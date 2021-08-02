package com.miraclebay.miracle.rpc.core.remoting.transport;


import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;

/**
 * 发送rpc请求
 * @author miraclebay
 */
public interface RpcRequestTransport {
    /**
     * 发送rpc请求，然后获取结果
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
