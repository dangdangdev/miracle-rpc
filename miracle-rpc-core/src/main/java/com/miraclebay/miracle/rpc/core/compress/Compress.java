package com.miraclebay.miracle.rpc.core.compress;

import com.miraclebay.miracle.rpc.common.extension.SPI;

/**
 * @author dangyong
 */
@SPI
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
