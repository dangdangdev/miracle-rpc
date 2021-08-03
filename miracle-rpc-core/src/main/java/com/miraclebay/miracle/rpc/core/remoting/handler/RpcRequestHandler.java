package com.miraclebay.miracle.rpc.core.remoting.handler;

import com.miraclebay.miracle.rpc.common.exception.RpcException;
import com.miraclebay.miracle.rpc.common.factory.SingletonFactory;
import com.miraclebay.miracle.rpc.core.provider.ServiceProvider;
import com.miraclebay.miracle.rpc.core.provider.impl.ZkServiceProviderImpl;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 处理rpcRequest
     */
    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     *  得到方法执行结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            throw new RpcException(e.getMessage(),e);
        }
        return result;
    }
}
