package com.miraclebay.miracle.rpc.common.exception;

import com.miraclebay.miracle.rpc.common.enums.RpcErrorMessageEnum;

/**
 * @author dangyong
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum,  String message) {
        super(rpcErrorMessageEnum.getMessage() + ":" + message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum){
        super(rpcErrorMessageEnum.getMessage());
    }
}
