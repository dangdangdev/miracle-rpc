package com.miraclebay.miracle.rpc.core.remoting.transport.netty.codec;

import com.miraclebay.miracle.rpc.common.enums.CompressTypeEnum;
import com.miraclebay.miracle.rpc.common.enums.SerializationTypeEnum;
import com.miraclebay.miracle.rpc.common.extension.ExtensionLoader;
import com.miraclebay.miracle.rpc.core.compress.Compress;
import com.miraclebay.miracle.rpc.core.remoting.constants.RpcConstants;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcMessage;
import com.miraclebay.miracle.rpc.core.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out){
        try{
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            out.writeInt(ATOMIC_INTEGER.getAndDecrement());

            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            if (messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE && messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE){
                //序列化对象
                String codeCName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}]", codeCName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codeCName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                //压缩字节
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null){
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        }catch (Exception e){
            log.error("Encode request error!", e);
        }
    }
}
