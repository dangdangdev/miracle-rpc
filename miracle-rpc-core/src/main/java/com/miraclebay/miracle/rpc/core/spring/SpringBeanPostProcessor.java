package com.miraclebay.miracle.rpc.core.spring;

import com.miraclebay.miracle.rpc.common.extension.ExtensionLoader;
import com.miraclebay.miracle.rpc.common.factory.SingletonFactory;
import com.miraclebay.miracle.rpc.core.annotation.RpcReference;
import com.miraclebay.miracle.rpc.core.annotation.RpcService;
import com.miraclebay.miracle.rpc.core.config.RpcServiceConfig;
import com.miraclebay.miracle.rpc.core.provider.ServiceProvider;
import com.miraclebay.miracle.rpc.core.provider.impl.ZkServiceProviderImpl;
import com.miraclebay.miracle.rpc.core.proxy.RpcClientProxy;
import com.miraclebay.miracle.rpc.core.remoting.transport.RpcRequestTransport;
import io.protostuff.Rpc;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 *
 * @author dangyong
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)){
            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            //获取RpcService注解
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            //build RpcServiceProperties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
            System.out.println("发布了服务：" + rpcServiceConfig.getRpcServiceName() );
        }
        return bean;
    }

    /**
     * 给被RpcReference标注的接口注入动态代理后的实现类
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for(Field declaredField : declaredFields){
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null){
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try{
                    declaredField.set(bean, clientProxy);
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
