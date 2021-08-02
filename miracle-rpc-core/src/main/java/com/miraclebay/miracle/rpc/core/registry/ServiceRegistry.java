package com.miraclebay.miracle.rpc.core.registry;

import com.miraclebay.miracle.rpc.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @author dangyong
 */
@SPI
public interface ServiceRegistry {
    /**
     * 注册服务
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
