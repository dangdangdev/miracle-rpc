package com.miraclebay.miracle.rpc.client.main;

import com.miraclebay.miracle.rpc.api.Hello;
import com.miraclebay.miracle.rpc.client.controller.HelloController;
import com.miraclebay.miracle.rpc.core.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.miraclebay.miracle.rpc"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException{
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
