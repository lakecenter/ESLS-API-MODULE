package com.wdy.module.common.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class ByteResponse {

    private byte[] firstByte;
    private List<byte[]> byteList;

    public ByteResponse(byte[] firstByte, List<byte[]> byteList) {
        this.firstByte = firstByte;
        this.byteList = byteList;
    }
}
