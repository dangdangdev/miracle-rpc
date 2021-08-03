package com.miraclebay.miracle.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * 作用于类，标识服务实现类
 * @author miraclebay
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

    /**
     * 服务的版本号，默认是""
     */
    String version() default "";

    /**
     * 服务分组，默认是""
     */
    String group() default "";
}
