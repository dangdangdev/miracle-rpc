package com.miraclebay.miracle.rpc.core.annotation;

import java.lang.annotation.*;

/**
 * rpc引用注解，注入服务实现类
 * @author miraclebay
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface RpcReference {

    /**
     * 服务的版本号，默认是""
     */
    String version() default "";

    /**
     * 服务分组，默认是""
     */
    String group() default "";
}
