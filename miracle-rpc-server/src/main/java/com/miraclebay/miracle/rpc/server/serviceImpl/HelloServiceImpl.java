package com.miraclebay.miracle.rpc.server.serviceImpl;

import com.miraclebay.miracle.rpc.api.Hello;
import com.miraclebay.miracle.rpc.api.HelloService;
import com.miraclebay.miracle.rpc.core.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RpcService(group = "test_1", version = "version_1")
public class HelloServiceImpl implements HelloService {

    static{
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到:{}", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回:{}", result);
        return result;
    }
}
