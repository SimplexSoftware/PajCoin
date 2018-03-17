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
public class RateUpdateScript {

    private static final Logger LOG = LoggerFactory.getLogger(RateUpdateScript.class);

    private final static String RATE_URL = "https://api.coinmarketcap.com/v1/ticker/ethereum/?convert=RUB";

    /**
     * Token/rub exchange rate.
     */
    private static final int TOKEN_RATE = 1000;

    @Resource
    @Value("${tokenAddress}")
    private String tokenAddress;

    @Resource
    @Value("${nodeUrl}")
    private String ethereumNodeUrl;

    @Resource
    @Value("${updaterWalletPath}")
    private String path;

    @Resource
    @Value("${updaterWalletPassword}")
    private String password;

    private PajCoin coin;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx =
            new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }

    @PostConstruct
    public void init() {
        try {
            Web3j web3j = Web3j.build(new HttpService(ethereumNodeUrl));
//            Credentials credentials = WalletUtils.loadCredentials(password, path);
            Credentials credentials = Credentials.create("95b0fd8c91f44e6b47c590d0a0dc03f03ef51154e0959a14d3519208ea8c99f7");
            coin = PajCoin.load(tokenAddress, web3j, credentials, GAS_PRICE,  BigInteger.valueOf(4_300_00));
            LOG.info("RateUpdateScript initialized");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

//    public void proceed() {
//        try {
//            LOG.info("Contract check started");
//            if (coin.needsUpdate().send().booleanValue()) {
//                BigInteger rate = getExchangeRate();
//                LOG.info("Push rate " + rate.doubleValue()/1e9);
//                coin.updateRate(rate).send();
//            }
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//        }
//    }

    /**
     * Returns a price of one ether  in tokens that is multiplied by 10^9
     * (for accuracy). Method returns an integer value because solidity
     * contracts operate only with non-decimal numbers.
     *
     * @return a PajCoin/eth exchange rate * 10^9
     */
    public BigInteger getExchangeRate() throws IOException {
            String json = IOUtils.toString(new URL(RATE_URL), Charset.forName("UTF-8"));
            //remove array brackets and parse3 json.
            JSONObject jsonObject = new JSONObject(json.substring(1, json.length()-1));

            BigDecimal ethRate = BigDecimal.valueOf(jsonObject.getDouble("price_rub") * 1e9);
            BigDecimal coinRate = ethRate.divide(BigDecimal.valueOf(TOKEN_RATE), BigDecimal.ROUND_DOWN);
            return coinRate.toBigInteger();
    }
}
