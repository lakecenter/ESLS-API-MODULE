// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   QRCode.java

package com.wdy.module.graphic;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class QRCode
{

    public QRCode()
    {
    }

    public static BufferedImage encode(String contents, int width, int height)
    {
        Map hints = new HashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, Integer.valueOf(0));
        try
        {
            com.google.zxing.common.BitMatrix bitMatrix = (new MultiFormatWriter()).encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
