package com.miraclebay.miracle.rpc.core.registry.zk;

import com.miraclebay.miracle.rpc.common.enums.RpcErrorMessageEnum;
import com.miraclebay.miracle.rpc.common.exception.RpcException;
import com.miraclebay.miracle.rpc.common.extension.ExtensionLoader;
import com.miraclebay.miracle.rpc.core.loadbalance.LoadBalance;
import com.miraclebay.miracle.rpc.core.registry.ServiceDiscovery;
import com.miraclebay.miracle.rpc.core.registry.zk.util.CuratorUtils;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author dangyong
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl(){
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size()==0){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host,port);
    }


}
