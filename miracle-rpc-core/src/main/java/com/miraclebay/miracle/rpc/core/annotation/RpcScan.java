package com.miraclebay.miracle.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * 扫描本地注解
 * @author miraclebay
 */
@Documented
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcScan {

    String[] basePackage();
}
