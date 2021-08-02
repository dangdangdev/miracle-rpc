package com.miraclebay.miracle.rpc.core.loadbalance;

import com.miraclebay.miracle.rpc.common.extension.SPI;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author dangyong
 */
@SPI
public interface LoadBalance {

    /**
     * 从当前服务列表中选择一个服务
     * @param serviceAddresses
     * @param rpcRequest
     * @return
     */
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);
}
