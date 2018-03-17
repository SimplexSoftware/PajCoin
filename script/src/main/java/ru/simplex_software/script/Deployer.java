package ru.simplex_software.script;

import com.github.openjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import ru.simplex_software.contract.PajCoin;
import ru.simplex_software.contract.PajCoin223;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * The class instance is created in Spring for constant listening to the block
 */
public class Deployer {

    private static final Logger LOG = LoggerFactory.getLogger(Deployer.class);

    public static void main(String[] args) {
        try {
            Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/naqTNN4B2QavbM4vZI3q"));
            Credentials credentials = Credentials.create("95b0fd8c91f44e6b47c590d0a0dc03f03ef51154e0959a14d3519208ea8c99f7");
            LOG.info("start deploying");
            PajCoin223 coin = PajCoin223.deploy(web3j, credentials, GAS_PRICE,GAS_LIMIT).send();
            LOG.info("coin deployed " + coin.getContractAddress());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
