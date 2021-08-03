package com.miraclebay.miracle.rpc.core.provider.impl;

import com.miraclebay.miracle.rpc.common.enums.RpcErrorMessageEnum;
import com.miraclebay.miracle.rpc.common.exception.RpcException;
import com.miraclebay.miracle.rpc.common.extension.ExtensionLoader;
import com.miraclebay.miracle.rpc.core.config.RpcServiceConfig;
import com.miraclebay.miracle.rpc.core.provider.ServiceProvider;
import com.miraclebay.miracle.rpc.core.registry.ServiceRegistry;
import com.miraclebay.miracle.rpc.core.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaodang
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {
    /**
     * key: rpc服务名(接口名 + version + group)
     * value: service object
     */
    private final Map<String, Object> serviceMap;
    private final Set<String> registeredSerivce;
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProviderImpl() {
        this.serviceMap = new ConcurrentHashMap<>();
        registeredSerivce = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }


    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredSerivce.contains(rpcServiceName)){
            return;
        }
        registeredSerivce.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces: {}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());;
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (service == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try{
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, NettyRpcServer.PORT));
        }catch (UnknownHostException e){
            log.error("occur exception when getHostAddress", e);
        }
    }
}
