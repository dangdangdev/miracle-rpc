package com.miraclebay.miracle.rpc.common.utils;

/**
 * @author dangyong
 */
public class RuntimeUtil {
    /**
     * 获取核心数
     * */
    public static int cpus(){
        return Runtime.getRuntime().availableProcessors();
    }
}
