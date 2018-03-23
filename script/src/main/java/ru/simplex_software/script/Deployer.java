package ru.simplex_software.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import ru.simplex_software.pajcoin.contract.Exchanger;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * The class instance is created in Spring for constant listening to the block
 */
public class Deployer {

    private static final Logger LOG = LoggerFactory.getLogger(Deployer.class);

    public static void main(String[] args) {
        try {
            Web3j web3j = Web3j.build(new HttpService(""));
            Credentials credentials = Credentials.create("");
            BigInteger balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
            LOG.info("deployer address  {}",credentials.getAddress());
            LOG.info("deployer balance {}", new BigDecimal(balance,18));
            LOG.info("start deploying");
            Exchanger coin = Exchanger.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT, BigInteger.ONE).send();
            LOG.info("contract deployed " + coin.getContractAddress());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
