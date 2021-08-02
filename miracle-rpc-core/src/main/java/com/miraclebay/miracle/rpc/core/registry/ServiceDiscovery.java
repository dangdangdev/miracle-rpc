package com.miraclebay.miracle.rpc.core.registry;

import com.miraclebay.miracle.rpc.common.extension.SPI;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现
 * @author dangyong
 */
@SPI
public interface ServiceDiscovery {
    /**
     *  查找服务
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);

}
