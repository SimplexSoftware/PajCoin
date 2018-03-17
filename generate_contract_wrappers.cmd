chcp 65001
set className=PajCoin
cd contract/contracts
del /S /Q "../compiled_contracts"
solc zeppelin-solidity/=%~dp0/contract/node_modules/zeppelin-solidity/ %className%.sol --bin --abi --optimize -o ../compiled_contracts
cd ../compiled_contracts
web3j solidity generate %className%.bin %className%.abi -o ../../script/src/main/generated/ -p ru.simplex_software.pajcoin.contract
