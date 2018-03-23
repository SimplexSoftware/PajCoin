pragma solidity 0.4.21;

import "./ERC223_receiving_contract.sol";
import "zeppelin-solidity/contracts/ownership/Ownable.sol";
import "./PajCoin223.sol";

contract Exchanger is ERC223ReceivingContract, Ownable {

    uint public rate = 30*1000000000;
    uint public fee = 100000*3e9;

    PajCoin223 public token = PajCoin223(0x1a85180ce3012e7715b913dd585afdf1a10f3025);

    // event DataEvent(string comment);
    event DataEvent(uint value, string comment);
    // event DataEvent(bytes32 value, string comment);
    // event DataEvent(bool value, string comment);
    // event DataEvent(address addr, string comment);

    // структ с юзером и суммой, которую он переслал
    struct Deal {
        address user;
        uint money;
    }
    // очередь "забронированных" переводов на покупку токенов
    mapping(uint => Deal) ethSended;
    mapping(uint => Deal) coinSended;

    // Счетчик людей, "забронировавших" токены.
    // "Бронирование" значит, что человек прислал деньги на покупку, но курс еще
    // не установлен. Соответственно, перевод средств добавляется в очередь и при
    // следующем обновлении курса будет обработан
    uint ethSendedNumber = 0;
    uint coinSendedNumber = 0;

    modifier allDealsArePaid {
        require(ethSendedNumber == 0);
        require(coinSendedNumber == 0);
        _;
    }

    event LogPriceUpdated(uint price);

    function Exchanger() public payable {
        updater = msg.sender;
    }

    function needUpdate() public view returns (bool) {
        return ethSendedNumber + coinSendedNumber > 0;
    }



    /**
     * @dev We use a single lock for the whole contract.
     */
    bool private reentrancy_lock = false;

    /**
     * @dev Prevents a contract from calling itself, directly or indirectly.
     * @notice If you mark a function `nonReentrant`, you should also
     * mark it `external`. Calling one nonReentrant function from
     * another is not supported. Instead, you can implement a
     * `private` function doing the actual work, and a `external`
     * wrapper marked as `nonReentrant`.
     */
    modifier nonReentrant() {
        require(!reentrancy_lock);
        reentrancy_lock = true;
        _;
        reentrancy_lock = false;
    }

    /**
     * @dev An account that commands to change a rate
     */
    address updater;

    modifier onlyUpdater() {
        require(msg.sender == updater);
        _;
    }

    function setUpdater(address _updater) public onlyOwner() {
        updater = _updater;
    }

    function setFee(uint _fee) public onlyOwner() {
        fee = _fee;
    }

    function setToken(address addr) public onlyOwner {
        token = PajCoin223(addr);
    }

    function getEth(uint amount) public onlyOwner allDealsArePaid {
        owner.transfer(amount);
    }

    function getTokens(uint amount) public onlyOwner allDealsArePaid {
        token.transfer(owner, amount);
    }

    function() public payable {
        if (msg.sender != owner) {
            require(fee <= msg.value);
            DataEvent(msg.value, "Someone sent ether: amount");
            ethSended[ethSendedNumber++] = Deal({user: msg.sender, money: msg.value});
        }
    }

    function tokenFallback(address _from, uint _value, bytes _data) {
        // DataEvent(msg.sender, "from");

        require(msg.sender == address(token));
        if (_from != owner) {
            require(fee <= _value * 1e9 / rate);
            DataEvent(_value, "Someone sent coin: amount");
            coinSended[coinSendedNumber++] = Deal({user: _from, money: _value});
        }
    }

    function updateRate(uint _rate) public onlyUpdater nonReentrant{

        rate = _rate;
        LogPriceUpdated(rate);

        uint personalFee = fee / (ethSendedNumber + coinSendedNumber);
        DataEvent(personalFee, "Personal fee");

        proceedEtherDeals(personalFee);
        proceedTokenDeals(personalFee);

    }

    function proceedEtherDeals(uint personalFee) internal {
        for (uint8 i = 0; i < ethSendedNumber; i++) {
            address user = ethSended[i].user;
            DataEvent(ethSended[i].money, "Someone sent ether: amount");
            DataEvent(personalFee, "Fee: amount");
            uint money = ethSended[i].money - personalFee;

            DataEvent(money, "Discounted amount: amount");
            uint value = money * rate / 1e9;
            DataEvent(value, "Ether to tokens: amount");
            if (money < 0) {
                // Скинуто эфира меньше, чем комиссия
            } else if (token.balanceOf(this) < value) {
                DataEvent(token.balanceOf(this), "Not enough tokens: owner balance");
                // Вернуть деньги, если токенов не осталось
                user.transfer(money);
            } else {
                token.transfer(user, value);
                DataEvent(value, "Tokens were sent to customer: amount");
            }
        }
        ethSendedNumber = 0;
    }

    function proceedTokenDeals(uint personalFee) internal {
        for (uint8 j = 0; j < coinSendedNumber; j++) {
            address user = coinSended[j].user;
            uint coin = coinSended[j].money;

            DataEvent(coin, "Someone sent tokens: amount");
            DataEvent(coin * 1e9 / rate, "Tokens to ether: amount");
            uint value = coin * 1e9 / rate - personalFee;
            DataEvent(personalFee, "Fee: amount");
            DataEvent(value, "Tokens to discounted ether: amount");

            if (value < 0) {
                // Скинуто токенов меньше, чем комиссия
            } else if (this.balance < value) {
                // Вернуть токены, если денег не осталось
                DataEvent(this.balance, "Not enough ether: contract balance");

                token.transfer(user, coin);
            } else {
                user.transfer(value);
                DataEvent(value, "Ether was sent to customer: amount");
            }
        }
        coinSendedNumber = 0;
    }
}
