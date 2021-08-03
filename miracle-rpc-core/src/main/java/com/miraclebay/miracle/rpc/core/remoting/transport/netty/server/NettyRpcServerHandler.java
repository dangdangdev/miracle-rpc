package com.miraclebay.miracle.rpc.core.remoting.transport.netty.server;

import com.miraclebay.miracle.rpc.common.enums.CompressTypeEnum;
import com.miraclebay.miracle.rpc.common.enums.RpcResponseCodeEnum;
import com.miraclebay.miracle.rpc.common.enums.SerializationTypeEnum;
import com.miraclebay.miracle.rpc.common.factory.SingletonFactory;
import com.miraclebay.miracle.rpc.core.remoting.constants.RpcConstants;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcMessage;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcRequest;
import com.miraclebay.miracle.rpc.core.remoting.dto.RpcResponse;
import com.miraclebay.miracle.rpc.core.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义ChannelHandler处理客户端发送的数据
 */
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter{
    private final RpcRequestHandler rpcRequestHandler;
    public NettyRpcServerHandler(){
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            if (msg instanceof RpcMessage){
                log.info("server receive msg: [{}]", msg);
                byte messageType =  ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                }else{
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    //执行方法返回方法的执行结果
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()){
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    }else{
                        RpcResponse<Object> rpcResponse  = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }finally {
            //释放ByteBuf
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE){
                log.info("idle check happen, close the connection");
                ctx.close();
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
