package com.miraclebay.miracle.rpc.core.serialize;

import com.miraclebay.miracle.rpc.common.extension.SPI;

@SPI
public interface Serializer {

    /**
     * 序列化
     * @param ob 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object ob);

    /**
     * 反序列化
     * @param bytes 要发序列化的字节数组
     * @param clazz 反序列化的目标类
     * @param <T> 类的类型。
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
