package com.wdy.module.dto;

import com.wdy.module.entity.Dispms;
import lombok.Data;

@Data
public class ByteAndRegion {
    private byte[] regionBytes;
    private Dispms region;

    public ByteAndRegion(byte[] regionBytes, Dispms region) {
        this.regionBytes = regionBytes;
        this.region = region;
    }
}
