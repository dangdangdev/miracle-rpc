package com.miraclebay.miracle.rpc.server.Main;

import com.miraclebay.miracle.rpc.api.HelloService;
import com.miraclebay.miracle.rpc.core.annotation.RpcScan;
import com.miraclebay.miracle.rpc.core.annotation.RpcService;
import com.miraclebay.miracle.rpc.core.config.RpcServiceConfig;
import com.miraclebay.miracle.rpc.core.remoting.transport.netty.server.NettyRpcServer;
import com.miraclebay.miracle.rpc.server.serviceImpl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.miraclebay.miracle.rpc"})
public class NettyServerMain {
    public static void main(String[] args) {
        //通过注解注册服务
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        //手动注册服务
        HelloService helloService2 = new HelloServiceImpl();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test_2").version("version_2").service(helloService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
