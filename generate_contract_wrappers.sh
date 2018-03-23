#!/usr/bin/env bash
#className=P2PExchanger
className=Exchanger
cwd=$(pwd)
cd contract/contracts
rm -rf ../compiled_contracts
solc zeppelin-solidity/=${cwd}/contract/node_modules/zeppelin-solidity/ ${className}.sol --bin --abi --optimize -o ../compiled_contracts
cd ../compiled_contracts
web3j solidity generate ${className}.bin ${className}.abi -o ../../script/src/main/generated/ -p ru.simplex_software.pajcoin.contract
