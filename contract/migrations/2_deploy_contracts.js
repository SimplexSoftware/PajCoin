var PajCoin = artifacts.require("./PajCoin.sol");
var DealCenter = artifacts.require("./DealCenter.sol");

module.exports = function(deployer) {
    deployer.deploy(PajCoin);
};
