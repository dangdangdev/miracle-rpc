package com.miraclebay.miracle.rpc.core.remoting.dto;

import lombok.*;

/**
 * store and provide service object
 * @author miraclebay
 */
@AllArgsConstructor  //全参
@NoArgsConstructor //无参
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {
    //rpc message type
    private byte messageType;
    //serialization type
    private byte codec;
    //compress type
    private byte compress;
    //request id
    private int requestId;    //request id必须且唯一
    //request data
    private Object data;
}
