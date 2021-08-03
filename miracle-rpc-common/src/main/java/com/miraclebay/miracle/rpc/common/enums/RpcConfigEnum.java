package com.miraclebay.miracle.rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dangyong
 */

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");
    private final String propertyvalue;
}
