pragma solidity ^0.4.0;

import "oraclize-api/contracts/usingOraclize.sol";
import "./ERC223_receiving_contract.sol";
import "zeppelin-solidity/contracts/ownership/Ownable.sol";
import "./PajCoin223.sol";

contract ExchangerOraclize is usingOraclize, ERC223ReceivingContract, Ownable {

    mapping(bytes32=>bool) validIds;

    uint public gasPerUpdate;
    uint public rate;

    PajCoin223 public token;

    event DataEvent(string comment);
    event DataEvent(uint value, string comment);
    event DataEvent(bytes32 value, string comment);
    event DataEvent(bool value, string comment);
    event DataEvent(address addr, string comment);

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
    event LogNewOraclizeQuery(string description);

    function ExchangerOraclize() public payable {
        oraclize_setCustomGasPrice(3000000000 wei);
        oraclize_setProof(proofType_TLSNotary | proofStorage_IPFS);
        gasPerUpdate = 90000;
    }

    function setGasPricePerUpdate(uint price) public onlyOwner {
        oraclize_setCustomGasPrice(price);
    }

    function getPricePerUpdate() public returns (uint) {
        return oraclize_getPrice("URL");
    }

    function setGasPerUpdate(uint amount) public onlyOwner {
        gasPerUpdate = amount;
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
            uint fee = oraclize_getPrice("URL");
            require(fee <= msg.value);
            DataEvent(msg.value, "Someone sent ether: amount");
            ethSended[ethSendedNumber++] = Deal({user: msg.sender, money: msg.value});
            updateRate();
        }
    }

    function tokenFallback(address _from, uint _value, bytes _data) {
        if (tx.origin != owner) {
            uint fee = oraclize_getPrice("URL");
            require(fee <= _value * 1e9 / rate);
            DataEvent(_value, "Someone sent coin: amount");
            coinSended[coinSendedNumber++] = Deal({user: _from, money: _value});
            updateRate();
        }
    }

    function __callback(bytes32 myid, string result, bytes proof) {
        require (msg.sender == oraclize_cbAddress());
        DataEvent(myid, "id");
        DataEvent(validIds[myid], "validId");
        if (!validIds[myid]) throw;
        rate = parseInt(result, 6);
        LogPriceUpdated(rate);

        uint fee = oraclize_getPrice("URL");
        DataEvent(fee, "Personal fee");

        proceedEtherDeals(rate, fee);
        proceedTokenDeals(rate, fee);

        validIds[myid] = false;

    }

    function proceedEtherDeals(uint rate, uint fee) internal {
        for (uint8 i = 0; i < ethSendedNumber; i++) {
            address user = ethSended[i].user;
            DataEvent(ethSended[i].money, "Someone sent ether: amount");
            DataEvent(fee, "Fee: amount");
            uint money = ethSended[i].money - fee;

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

    function proceedTokenDeals(uint rate, uint fee) internal {
        for (uint8 j = 0; j < coinSendedNumber; j++) {
            address user = coinSended[j].user;
            uint coin = coinSended[j].money;

            DataEvent(coin, "Someone sent tokens: amount");
            DataEvent(coin * 1e9 / rate, "Tokens to ether: amount");
            uint value = coin * 1e9 / rate - fee;
            DataEvent(fee, "Fee: amount");
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

    function updateRate() public {
        if (oraclize_getPrice("URL") > this.balance) {
            LogNewOraclizeQuery("Oraclize query was NOT sent, please add some ETH to cover for the query fee ");
        } else {
            LogNewOraclizeQuery("Oraclize query was sent, standing by for the answer..");
            DataEvent(oraclize_getPrice("URL"), "Oraclize fee");
            bytes32 queryId = oraclize_query("URL", "json(https://api.coinmarketcap.com/v1/ticker/ethereum/?convert=RUB).0.price_rub", gasPerUpdate);
            validIds[queryId] = true;
            DataEvent(queryId, "QueryId");
        }
    }
}