package com.miraclebay.miracle.rpc.core.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author dangyong
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {
    public static final long serialVersionUID = 1905122041950251208L;
    public String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public String getRpcServiceName(){
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
