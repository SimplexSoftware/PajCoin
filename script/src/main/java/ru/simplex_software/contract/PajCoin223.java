package ru.simplex_software.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class PajCoin223 extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b6100176100e9565b6a7c13bc4b2c133c560000006000818155600160a060020a0333168082526001602052604080832084905590927fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c169185905182815260406020820181815290820183818151815260200191508051906020019080838360005b838110156100a8578082015183820152602001610090565b50505050905090810190601f1680156100d55780820380516001836020036101000a031916815260200191505b50935050505060405180910390a3506100fb565b60206040519081016040526000815290565b6107088061010a6000396000f3006060604052600436106100695763ffffffff60e060020a60003504166306fdde03811461006e57806318160ddd146100f8578063313ce5671461011d57806370a082311461014657806395d89b4114610165578063a9059cbb14610178578063be45fd621461019c575b600080fd5b341561007957600080fd5b610081610201565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156100bd5780820151838201526020016100a5565b50505050905090810190601f1680156100ea5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561010357600080fd5b61010b610238565b60405190815260200160405180910390f35b341561012857600080fd5b61013061023e565b60405160ff909116815260200160405180910390f35b341561015157600080fd5b61010b600160a060020a0360043516610243565b341561017057600080fd5b61010b61025e565b341561018357600080fd5b61019a600160a060020a0360043516602435610282565b005b34156101a757600080fd5b61019a60048035600160a060020a03169060248035919060649060443590810190830135806020601f820181900481020160405190810160405281815292919060208401838380828437509496506104af95505050505050565b60408051908101604052600781527f50616a436f696e00000000000000000000000000000000000000000000000000602082015281565b60005481565b601281565b600160a060020a031660009081526001602052604090205490565b7f504a43000000000000000000000000000000000000000000000000000000000081565b600061028c6106ca565b600160a060020a033316600090815260016020526040812054853b93506102b9908563ffffffff61068f16565b600160a060020a0333811660009081526001602052604080822093909355908716815220546102ee908563ffffffff6106a316565b600160a060020a0386166000908152600160205260408120919091558311156103f4575083600160a060020a03811663c0ee0b8a3386856040518463ffffffff1660e060020a0281526004018084600160a060020a0316600160a060020a0316815260200183815260200180602001828103825283818151815260200191508051906020019080838360005b8381101561039257808201518382015260200161037a565b50505050905090810190601f1680156103bf5780820380516001836020036101000a031916815260200191505b50945050505050600060405180830381600087803b15156103df57600080fd5b6102c65a03f115156103f057600080fd5b5050505b84600160a060020a031633600160a060020a03167fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c16868560405182815260406020820181815290820183818151815260200191508051906020019080838360005b8381101561046d578082015183820152602001610455565b50505050905090810190601f16801561049a5780820380516001836020036101000a031916815260200191505b50935050505060405180910390a35050505050565b600160a060020a033316600090815260016020526040812054843b91906104dc908563ffffffff61068f16565b600160a060020a033381166000908152600160205260408082209390935590871681522054610511908563ffffffff6106a316565b600160a060020a038616600090815260016020526040812091909155821115610617575083600160a060020a03811663c0ee0b8a3386866040518463ffffffff1660e060020a0281526004018084600160a060020a0316600160a060020a0316815260200183815260200180602001828103825283818151815260200191508051906020019080838360005b838110156105b557808201518382015260200161059d565b50505050905090810190601f1680156105e25780820380516001836020036101000a031916815260200191505b50945050505050600060405180830381600087803b151561060257600080fd5b6102c65a03f1151561061357600080fd5b5050505b84600160a060020a031633600160a060020a03167fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c16868660405182815260406020820181815290820183818151815260200191508051906020019080838360008381101561046d578082015183820152602001610455565b600061069d838311156106bb565b50900390565b60008282016106b4848210156106bb565b9392505050565b8015156106c757600080fd5b50565b602060405190810160405260008152905600a165627a7a72305820174323fcf714fa1c2aba1171a86aa33bd2face244cbbfe9fdc655cdb03f638340029";

    protected PajCoin223(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PajCoin223(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.data = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<DynamicBytes>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.data = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> name() {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<byte[]> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value, byte[] _data) {
        Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value), 
                new org.web3j.abi.datatypes.DynamicBytes(_data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<PajCoin223> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PajCoin223.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<PajCoin223> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PajCoin223.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static PajCoin223 load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PajCoin223(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static PajCoin223 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PajCoin223(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public String from;

        public String to;

        public BigInteger value;

        public byte[] data;
    }
}
