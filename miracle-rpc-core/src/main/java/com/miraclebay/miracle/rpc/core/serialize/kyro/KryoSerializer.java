package com.miraclebay.miracle.rpc.core.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcResponse;
import com.miraclebay.miracle.rpc.core.serialize.Serializer;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

@Slf4j
public class KryoSerializer implements Serializer {
    /**
     * kryo非线程安全，因此用threadlocal存储kryo对象
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object ob) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            //将对象序列化为byte数组
            kryo.writeObject(output,ob);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            throw new SerializationException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            //从byte数组中反序列化得到对象
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        }catch (Exception e){
            throw new SerializationException("Deserialization failed");
        }
    }
}
