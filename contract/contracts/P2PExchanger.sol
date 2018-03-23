pragma solidity ^0.4.0;

import "./ERC223_receiving_contract.sol";
import "zeppelin-solidity/contracts/ownership/Ownable.sol";
import "./PajCoin223.sol";
import "./SafeMath.sol";

contract P2PExchanger is ERC223ReceivingContract, Ownable {
    using SafeMath for uint256;

    uint public rate = 30e9;
    uint public fee = 100000*3e9;

    PajCoin223 public token = PajCoin223(0x1a85180ce3012e7715b913dd585afdf1a10f3025);

    // event DataEvent(string comment);
    event DataEvent(uint value, string comment);
    // event DataEvent(bytes32 value, string comment);
    // event DataEvent(bool value, string comment);
    // event DataEvent(address addr, string comment);


    struct Deal {
        address ethSender;
        address coinSender;
        uint eth;
        uint coin;
    }

    mapping(uint => Deal) deals;
    uint dealsNum;

    // очередь "забронированных" переводов на покупку токенов
    mapping(address => mapping(address => uint)) ethSended;
    mapping(address => mapping(address => uint)) coinSended;

    event LogPriceUpdated(uint price);

    function P2PExchanger() public payable {
        updater = msg.sender;
    }

    /**
     * @dev An account that commands to change a rate
     */
    address updater;

    modifier onlyUpdater() {
        require(msg.sender == updater);
        _;
    }

    function needUpdate() public view returns (bool) {
        return dealsNum > 0;
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

    modifier allDealsArePaid {
        require(dealsNum == 0);
        _;
    }

    function getEth(uint amount) public onlyOwner allDealsArePaid {
        owner.transfer(amount);
    }

    function getTokens(uint amount) public onlyOwner allDealsArePaid {
        token.transfer(owner, amount);
    }

    function() public payable {

        DataEvent(msg.value, "Someone sent ether: amount");
        address _from = msg.sender;
        uint _eth = msg.value;
        bytes memory addr = bytes(msg.data);

        // No data was called. Accept money and do nothing.
        if (addr.length == 4) return;

        address _to = bytesToAddress(addr);
        // DataEvent(_from, "sent ether from");
        // DataEvent(_to, "sent ether to");
        DataEvent(coinSended[_from][_to], "Coin deal current balance");
        // Someone wanted to open P2P with sender (already sended coins)
        if (coinSended[_from][_to] > 0) {
            DataEvent(_eth, "Creating a deal");
            uint _coin = coinSended[_from][_to];
            coinSended[_from][_to] = 0;
            deals[dealsNum++] = Deal({ethSender: _from, coinSender: _to, eth: _eth, coin: _coin});
        } else {
            // Create a query for a deal or add money if user previously already sent eth to _to
            DataEvent(_eth, "Open new P2P query");
            uint ethBalance = ethSended[_to][_from];
            // Pay fee
            if (ethBalance == 0) {
                _eth = _eth.sub(fee);
            }
            DataEvent(_eth, "Query eth after fee");
            ethSended[_to][_from] = ethBalance.add(_eth);
            DataEvent(ethSended[_to][_from], "Ether on map");
        }
    }

    function tokenFallback(address _from, uint _value, bytes _data) {
        require(msg.sender == address(token));
        DataEvent(_value, "Someone sent tokens: amount");
        address _to = bytesToAddress(_data);
        uint _coin = _value;
        // DataEvent(_from, "sent tokens from");
        // DataEvent(_to, "sent tokens to");

        // Someone wanted to open P2P with sender (already sended ether)
        DataEvent(ethSended[_from][_to], "Ether deal current balance");
        if (ethSended[_from][_to] > 0) {
            DataEvent(_coin, "Creating a deal");
            uint _eth = ethSended[_from][_to];
            ethSended[_from][_to] = 0;
            deals[dealsNum++] = Deal({ethSender: _to, coinSender: _from, eth: _eth, coin: _coin});
        } else {
            // Create a query for a deal or add money if user previously already sent eth to _to
            DataEvent(_coin, "Open new P2P query");
            uint coinBalance = coinSended[_to][_from];
            // Pay fee
            if (coinBalance == 0) {
                uint feeInTokens = fee * rate / 1e9;
                _coin = _coin.sub(feeInTokens);
            }
            DataEvent(_coin, "Query tokens after fee");
            coinSended[_to][_from] = coinSended[_to][_from].add(_coin);
            DataEvent(coinSended[_to][_from], "Tokens on map");
        }
    }

    function updateRate(uint _rate) onlyUpdater {

        rate = _rate;
        LogPriceUpdated(rate);

        proceedDeals();
    }

    function proceedDeals() internal {
        for (uint8 i = 0; i < dealsNum; i++) {
            address _ethSender = deals[i].ethSender;
            address _coinSender = deals[i].coinSender;
            uint _eth = deals[i].eth;
            uint _coin = deals[i].coin;

            uint coinsInEther = _coin * 1e9 / rate;
            // Reminder to ethSender
            uint reminder;
            if (_eth > coinsInEther) {
                reminder = _eth - coinsInEther;
                _eth = _eth - reminder;
                DataEvent(reminder, "Ether reminder");
                _coinSender.transfer(_eth);
                _ethSender.transfer(reminder);
                token.transfer(_ethSender, _coin);
            } else {
                uint ethInCoins = _eth * rate / 1e9;
                reminder = _coin - ethInCoins;
                _coin = _coin - reminder;
                DataEvent(reminder, "Token reminder");
                _coinSender.transfer(_eth);
                token.transfer(_coinSender, reminder);
                token.transfer(_ethSender, _coin);
            }
        }
        dealsNum = 0;
    }

    function bytesToAddress(bytes source) internal pure returns(address parsedAddr) {
        assembly {
            parsedAddr := mload(add(source,0x14))
        }
        return parsedAddr;
    }
}