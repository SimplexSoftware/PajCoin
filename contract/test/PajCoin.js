var PajCoin = artifacts.require("../contracts/PajCoin.sol");

contract('PajCoin', function(accounts) {
    it("should put 150000000e18 PajCoin in the creators account", function() {
        var coin = PajCoin.deployed();
        return coin.then(function(instance) {
            return instance.balanceOf.call(accounts[0]);
        }).then(function(balance) {
            assert.equal(balance.valueOf(), 150000000e18, "150000000e18 isn't in the creators account");
        });
    });
    it("should send coin correctly", function() {
        var coin = PajCoin.deployed();

        var account_one = accounts[0];
        var account_two = accounts[1];

        var account_one_starting_balance;
        var account_two_starting_balance;
        var account_one_ending_balance;
        var account_two_ending_balance;

        var amount = 100;

        coin.then(function (instance) {
            account_one_starting_balance = instance.balanceOf.call(account_one)
        });
        coin.then(function (instance) {
            account_two_starting_balance = instance.balanceOf.call(account_two);
        });
        coin.then(function (instance) {
            instance.transfer(account_two, amount, {from: account_one});
        });
        coin.then(function (instance) {
            account_one_ending_balance = instance.balanceOf.call(account_one)
        });
        coin.then(function (instance) {
            account_two_ending_balance = instance.balanceOf.call(account_two);

            assert.equal(account_one_ending_balance, account_one_starting_balance - amount, "Amount wasn't correctly taken from the sender");
            assert.equal(account_two_ending_balance, account_two_starting_balance + amount, "Amount wasn't correctly sent to the receiver");
        });
    });
});
