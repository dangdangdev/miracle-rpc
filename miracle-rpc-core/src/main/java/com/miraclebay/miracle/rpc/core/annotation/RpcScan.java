package com.miraclebay.miracle.rpc.core.annotation;

import com.miraclebay.miracle.rpc.core.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描本地注解
 * @author miraclebay
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage();
}
