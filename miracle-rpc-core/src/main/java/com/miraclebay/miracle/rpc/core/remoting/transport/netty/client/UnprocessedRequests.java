package com.miraclebay.miracle.rpc.core.remoting.transport.netty.client;

import com.miraclebay.miracle.rpc.core.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端未处理请求
 * @author xiaodang
 */
@Slf4j
public class UnprocessedRequests {
    public static final Map<String, CompletableFuture<RpcResponse<Object>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future){
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(RpcResponse<Object> rpcResponse){
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (future != null){
            future.complete(rpcResponse);
        }else{
            throw new IllegalStateException();
        }
    }
}
