pragma solidity ^0.4.0;

import "./ERC223_token.sol";

contract PajCoin223 is ERC223Token {

    string public constant name = "PajCoin";
    bytes32 public constant symbol = "PJC";
    uint8 public constant decimals = 18;

    function PajCoin223() public {
        bytes memory empty;
        totalSupply = 150000000e18;
        balances[msg.sender] = totalSupply;
        Transfer(0x0, msg.sender, totalSupply, empty);
    }
}