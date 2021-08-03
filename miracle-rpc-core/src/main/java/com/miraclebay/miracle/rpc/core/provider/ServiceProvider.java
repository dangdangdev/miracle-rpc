package com.miraclebay.miracle.rpc.core.provider;

import com.miraclebay.miracle.rpc.core.config.RpcServiceConfig;

/**
 * @author dangyong
 */
public interface ServiceProvider {

    void addService(RpcServiceConfig rpcServiceConfig);

    Object getService(String rpcServiceName);

    void publishService(RpcServiceConfig rpcServiceConfig);
}
