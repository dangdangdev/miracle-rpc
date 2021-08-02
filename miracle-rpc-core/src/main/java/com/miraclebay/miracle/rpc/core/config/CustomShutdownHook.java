package com.miraclebay.miracle.rpc.core.config;

import com.miraclebay.miracle.rpc.common.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.miraclebay.miracle.rpc.core.registry.zk.util.CuratorUtils;
import com.miraclebay.miracle.rpc.core.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author dangyong
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook(){
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll(){
        log.info("addShutdownHook for clear");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try{
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            }catch (UnknownHostException ignored){
            }
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
