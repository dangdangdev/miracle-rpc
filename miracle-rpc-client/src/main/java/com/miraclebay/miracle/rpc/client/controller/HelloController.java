package com.miraclebay.miracle.rpc.client.controller;

import com.miraclebay.miracle.rpc.api.Hello;
import com.miraclebay.miracle.rpc.api.HelloService;
import com.miraclebay.miracle.rpc.core.annotation.RpcReference;
import org.springframework.stereotype.Component;

@Component
public class HelloController {

    @RpcReference(version = "version_1", group = "test_1")
    private HelloService helloService;

    public void test() throws InterruptedException{
        String hello = this.helloService.hello(new Hello("111", "222"));
        //使用断言，方便调试
        Thread.sleep(10000);
        for (int i = 0; i< 10;i++){
            System.out.println(helloService.hello(new Hello("111", "222")));
        }
    }
}
