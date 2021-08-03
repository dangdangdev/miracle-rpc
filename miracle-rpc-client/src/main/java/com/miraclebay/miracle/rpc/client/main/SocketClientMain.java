package com.miraclebay.miracle.rpc.client.main;

import com.miraclebay.miracle.rpc.api.Hello;
import com.miraclebay.miracle.rpc.api.HelloService;
import com.miraclebay.miracle.rpc.core.config.RpcServiceConfig;
import com.miraclebay.miracle.rpc.core.proxy.RpcClientProxy;
import com.miraclebay.miracle.rpc.core.remoting.transport.RpcRequestTransport;
import com.miraclebay.miracle.rpc.core.remoting.transport.socket.SocketRpcClient;

public class SocketClientMain {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //返回的helloService是一个经过加工后的代理对象，调用方法实际执行的是rpcClientProxy.invoke()方法
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
