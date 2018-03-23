package ru.simplex_software.script;

import com.github.openjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import ru.simplex_software.pajcoin.contract.Exchanger;
import ru.simplex_software.pajcoin.contract.P2PExchanger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;


/**
 * The class instance is created in Spring for constant listening to the block
 */
public class RateUpdateScript {

    private static final Logger LOG = LoggerFactory.getLogger(RateUpdateScript.class);

    private final static String RATE_URL = "https://api.coinmarketcap.com/v1/ticker/ethereum/?convert=RUB";

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(200000L);

    // 3 GWei
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(3000000000L);
    /**
     * Token/rub exchange rate.
     */
    private static final int TOKEN_RATE = 1000;

    @Resource
    @Value("${exchangerAddress}")
    private String exchangerAddress;

    @Resource
    @Value("${p2pexchangerAddress}")
    private String p2pExchangerAddress;

    @Resource
    @Value("${nodeUrl}")
    private String nodeUrl;

    @Resource
    @Value("${updaterWalletPath}")
    private String path;

    @Resource
    @Value("${updaterWalletPassword}")
    private String password;

    @Resource
    @Value("${updaterPrivateKey}")
    private String updaterPK;

    private Exchanger changer;
    private P2PExchanger p2pChanger;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx =
            new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }

    @PostConstruct
    public void init() {
        try {
            Web3j web3j = Web3j.build(new HttpService(nodeUrl));
//            Credentials credentials = WalletUtils.loadCredentials(password, path);
            Credentials credentials = Credentials.create(updaterPK);
            changer = Exchanger.load(exchangerAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
//            p2pChanger = P2PExchanger.load(p2pExchangerAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
            LOG.info("RateUpdateScript initialized");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void proceed() {
        try {
            LOG.info("Contracts check started");
            if (changer.needUpdate().send()) {
                BigInteger rate = getExchangeRate();
                LOG.info("Push rate to changer " + rate.doubleValue() / 1e9);
                changer.updateRate(rate).send();
            }
//            if (p2pChanger.needUpdate().send()) {
//                BigInteger rate = getExchangeRate();
//                LOG.info("Push rate to p2p " + rate.doubleValue() / 1e9);
//                p2pChanger.updateRate(rate).send();
//            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

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
