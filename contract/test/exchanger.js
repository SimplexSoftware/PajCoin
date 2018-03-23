var PajCoin = artifacts.require("./PajCoin223.sol");
var Exchanger = artifacts.require("./Exchanger.sol");

var ten9=1000000000;
var ten18=ten9*ten9;

contract('Exchanger', function(accounts) {

    it("should change ether to tokens with no fee", function () {

        var changer;
        var coin;

        var admin = accounts[0];
        var buyer = accounts[1];

        var changer_eth_balance;
        var changer_new_eth_balance;
        var changer_token_balance;
        var changer_new_token_balance;
        var buyer_token_balance;
        var buyer_new_token_balance;
        var buyer_eth_balance;
        var buyer_new_eth_balance;
        var buyer_amount = 0.5 * ten18;
        var rate = 40*ten9;

        // INIT CHANGER
        return Exchanger.deployed().then(function (instance) { // save deployed exchanger contract and send 1 ether to it
            changer = instance;
            return web3.eth.sendTransaction({from:admin, to: changer.address, value: 1*ten18, gasPrice: 0})
        }).then(function () { // save deployed token contract
            return PajCoin.deployed();
        }).then(function (instance) { // transfer 1000 tokens to exchanger
            coin = instance;
            return instance.transfer(changer.address, 1000*ten18, {from: admin});
        }).then(function () { // save exchanger initial token balance
            return coin.balanceOf.call(changer.address);
        }).then(function (result) { // retrieve exchanger initial eth balance
            changer_token_balance = result.toNumber();
            return web3.eth.getBalance(changer.address);
        }).then(function (result) { // save exchanger initial eth balance
            changer_eth_balance = result.toNumber();
            return changer_eth_balance;
        }).then(function (no_matter) {
            return changer.setFee(0, {from:admin, gasPrice: 0});
        })
        // SAVE BUYER'S ETH BALANCE
        .then(function () {
            return web3.eth.getBalance(buyer);
        }).then(function (result) {
            buyer_eth_balance = result.toNumber();
            return buyer_eth_balance;
        })
        // SAVE BUYER'S TOKEN BALANCE
        .then(function (no_matter) {
            return coin.balanceOf.call(buyer);
        }).then(function (result) {
            buyer_token_balance = result.toNumber();
            return buyer_token_balance;
        })
        // SEND ETHER FROM BUYER TO EXCHANGER AND UPDATE RATE
        .then(function (no_matter) {
            return web3.eth.sendTransaction({from:buyer, to: changer.address, value: buyer_amount, gasPrice: 0});
        }).then(function () {
            return changer.updateRate(rate, {from: admin});
        })
        // SAVE BUYER'S NEW TOKEN BALANCE
        .then(function () { // get and check new balances
            return coin.balanceOf.call(buyer);
        }).then(function (result) {
            buyer_new_token_balance = result.toNumber();
            return buyer_new_token_balance;
        })
        // SAVE BUYER'S NEW ETH BALANCE
        .then(function (no_matter) {
            return web3.eth.getBalance(buyer);
        }).then(function (result) {
            buyer_new_eth_balance = result.toNumber();
            return buyer_new_eth_balance;
        })
        // SAVE EXCHANGER'S NEW TOKEN BALANCE
        .then(function (no_matter) {
            return coin.balanceOf.call(changer.address);
        }).then(function (result) {
            changer_new_token_balance = result.toNumber();
            return changer_new_token_balance;
        })
        // SAVE EXCHANGER'S NEW ETH BALANCE
        .then(function (no_matter) {
            return web3.eth.getBalance(changer.address);
        }).then(function (result) {
            changer_new_eth_balance = result.toNumber();
            return changer_new_eth_balance;
        })
        // ASSERT CHANGES
        .then(function (no_matter) {
            assert.equal(changer_token_balance - rate * buyer_amount / 1e9, changer_new_token_balance, "Exchanger: Not correct token amount after send");
            assert.equal(changer_eth_balance + buyer_amount, changer_new_eth_balance, "Exchanger: Not correct eth amount after send");
            assert.equal(buyer_token_balance + rate * buyer_amount / 1e9, buyer_new_token_balance, "Not correct token amount after send");
            assert.equal(buyer_eth_balance - buyer_amount, buyer_new_eth_balance, "Not correct ether amount after send");
        });
    });

    it("should change tokens to ether with no fee", function () {

        var changer;
        var coin;

        var admin = accounts[0];
        var seller = accounts[1];

        var changer_eth_balance;
        var changer_new_eth_balance;
        var changer_token_balance;
        var changer_new_token_balance;
        var seller_token_balance;
        var seller_new_token_balance;
        var seller_eth_balance;
        var seller_new_eth_balance;
        var seller_amount = 10 * ten18;
        var seller_token_budget = 20 * ten18;
        var rate = 40 * ten9;

        // SAVE CHANGER DATA
        Exchanger.deployed().then(function (instance) { // save deployed exchanger contract and send 1 ether to it
            changer = instance;
            // return instance.send(web3.toWei(1, "ether")).then(function(result) {});
            return changer;
        }).then(function (instance) { // save deployed token contract
            return PajCoin.deployed();
        }).then(function (instance) { // transfer 1000 tokens to exchanger
            coin = instance;
            // return instance.transfer(changer.address, 1000*ten18, {from: admin});
            return coin;
        }).then(function (instance) { // save exchanger initial token balance
            return coin.balanceOf.call(changer.address);
        }).then(function (result) { // retrieve exchanger initial eth balance
            changer_token_balance = result.toNumber();
            return web3.eth.getBalance(changer.address);
        }).then(function (result) { // save exchanger initial eth balance
            changer_eth_balance = result.toNumber();
            return changer_eth_balance;
        }).then(function (no_matter) {
            return changer.setFee(0, {from:admin, gasPrice: 0});
        })
        // GIVE SELLER 20 TOKENS
        .then(function () { // give 20 tokens to seller
            return coin.transfer(seller, seller_token_budget, {from:admin, gasPrice: 0});
        })
        // SAVE SELLER'S ETH BALANCE
        .then(function () {
            return web3.eth.getBalance(seller);
        }).then(function (result) {
            seller_eth_balance = result.toNumber();
            return seller_eth_balance;
        })
        // SAVE SELLER'S TOKEN BALANCE
        .then(function (no_matter) {
            return coin.balanceOf.call(seller);
        }).then(function (result) {
            seller_token_balance = result.toNumber();
            return seller_token_balance;
        })
        // SEND TOKENS FROM SELLER TO EXCHANGER AND UPDATE RATE
        .then(function (no_matter) {
            return coin.transfer(changer.address, seller_amount, {from: seller, gasPrice: 0});
        }).then(function () { // update rate
            return changer.updateRate(rate, {from: admin, gasPrice: 0});
        })
        // SAVE SELLER'S NEW TOKEN BALANCE
        .then(function () { // get and check new balances
            return coin.balanceOf.call(seller);
        }).then(function (result) {
            seller_new_token_balance = result.toNumber();
            return seller_new_token_balance;
        })
        // SAVE SELLER'S NEW ETH BALANCE
        .then(function (no_matter) {
            return web3.eth.getBalance(seller);
        }).then(function (result) {
            seller_new_eth_balance = result.toNumber();
            return seller_new_eth_balance;
        })
        // SAVE EXCHANGER'S NEW TOKEN BALANCE
        .then(function (no_matter) { // get and check new balances
            return coin.balanceOf.call(changer.address);
        }).then(function (result) {
            changer_new_token_balance = result.toNumber();
            return changer_new_token_balance;
        })
        // SAVE EXCHANGER'S NEW ETH BALANCE
        .then(function (no_matter) {
            return web3.eth.getBalance(changer.address);
        }).then(function (result) {
            changer_new_eth_balance = result.toNumber();
            return changer_new_eth_balance;
        })
        // ASSERT CHANGES
        .then(function (no_matter) {
            assert.equal(changer_token_balance + seller_amount, changer_new_token_balance, "Exchanger: Not correct token amount after send");
            assert.equal(changer_eth_balance - seller_amount * 1e9 / rate, changer_new_eth_balance, "Exchanger: Not correct eth amount after send");
            assert.equal(seller_token_balance - seller_amount, seller_new_token_balance, "Not correct token amount after send");
            assert.equal(seller_eth_balance + seller_amount * 1e9 / rate, seller_new_eth_balance, "Not correct ether amount after send");
        });
    });
});




