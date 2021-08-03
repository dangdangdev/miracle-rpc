package com.miraclebay.miracle.rpc.core.proxy;

import com.miraclebay.miracle.rpc.common.enums.RpcErrorMessageEnum;
import com.miraclebay.miracle.rpc.common.enums.RpcResponseCodeEnum;
import com.miraclebay.miracle.rpc.common.exception.RpcException;
import com.miraclebay.miracle.rpc.core.config.RpcServiceConfig;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcResponse;
import com.miraclebay.miracle.rpc.core.remoting.transport.RpcRequestTransport;
import com.miraclebay.miracle.rpc.core.remoting.transport.netty.client.NettyRpcClient;
import com.miraclebay.miracle.rpc.core.remoting.transport.socket.SocketRpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author dangyong
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    //发送请求到服务端，有两个实现socket和netty
    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = new RpcServiceConfig();
    }

    //获取代理对象
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 用代理对象执行方法时，实际执行的是本方法
     *
     * @param proxy： 用getProxy方法获取的对象
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoke method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            //阻塞直到返回结果
            rpcResponse = completableFuture.get();
        } else if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest){
        if (rpcResponse == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
