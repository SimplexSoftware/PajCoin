var PajCoin223 = artifacts.require("./PajCoin223.sol");
var Exchanger = artifacts.require("./Exchanger.sol");
var P2PExchanger = artifacts.require("./P2PExchanger.sol");

module.exports = function(deployer) {

    deployer.deploy(PajCoin223);
    deployer.deploy(Exchanger);
    deployer.deploy(P2PExchanger);

    var coin, changer, p2pchanger;
    deployer.then(function () {
        return PajCoin223.deployed();
    }).then(function (instance) {
        coin = instance;
        return Exchanger.deployed();
    }).then(function (instance) {
        changer = instance;
        changer.setToken(coin.address);
    });
    deployer.then(function () {
        return PajCoin223.deployed();
    }).then(function (instance) {
        coin = instance;
        return P2PExchanger.deployed();
    }).then(function (instance) {
        p2pchanger = instance;
        p2pchanger.setToken(coin.address);
    });
};
