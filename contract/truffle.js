// var HDWalletProvider = require("truffle-hdwallet-provider-privkey");
// var mnemonic = "part power which eternally wills evil works good johann wolfgang goethe faust";

var mnemonic = "";
const privKey = "716ecb2bd10128244c5fd0e0b26a34bf05e84e11f070040ec336a5c229c3f118";
var mainNode = "https://mainnet.infura.io/<INFURA_Access_Token>";
var rinkebyNode = "https://rinkeby.infura.io/naqTNN4B2QavbM4vZI3q";

module.exports = {
  // See <http://truffleframework.com/docs/advanced/configuration>
  // to customize your Truffle configuration!
    networks: {
        /*main: {
            provider: function() {
                return new HDWalletProvider(mnemonic, mainNode);
            },
            network_id: '1'
        },*/
        /*rinkeby: {
            provider: new HDWalletProvider(mnemonic, rinkebyNode),
            network_id: 4
        }*/
    }
};
