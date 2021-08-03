package com.miraclebay.miracle.rpc.core.spring;

import com.miraclebay.miracle.rpc.core.annotation.RpcScan;
import com.miraclebay.miracle.rpc.core.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 动态注册bean
 * 扫描和过滤指定注释
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    public static final String SPRING_BEAN_BASE_PACKAGE = "com.miraclebay.miracle.rpc";
    public static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取注解RpcScan的属性和值
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if (rpcScanAnnotationAttributes != null){
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcScanBasePackages.length == 0){
            rpcScanBasePackages =  new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }
        //扫描rpcService注解
        CustomScanner rpcServiveScanner = new CustomScanner(registry, RpcService.class);
        //扫描Component注解
        CustomScanner springBeanScanner = new CustomScanner(registry, Component.class);
        if (resourceLoader != null){
            rpcServiveScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量[{}]", springBeanAmount);
        int rpcServiceCount = rpcServiveScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner扫描的数量[{}]", rpcServiceCount);
    }
}
