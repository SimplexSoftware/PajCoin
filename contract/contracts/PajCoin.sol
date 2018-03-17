pragma solidity ^0.4.0;

import "zeppelin-solidity/contracts/token/ERC20/StandardToken.sol";

contract PajCoin is StandardToken {

    string public constant name = "PajCoin";
    string public constant symbol = "PJC";
    uint32 public constant decimals = 18;
    uint constant INITIAL_SUPPLY = 150000000e18;

    function PajCoin() public payable {
        totalSupply_ = INITIAL_SUPPLY;
        balances[msg.sender] = totalSupply_;
        Transfer(0x0, msg.sender, totalSupply_);
    }
}