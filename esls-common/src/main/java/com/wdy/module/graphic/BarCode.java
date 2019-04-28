// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BarCode.java

package com.wdy.module.graphic;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;

public class BarCode
{

    public BarCode()
    {
    }

    public static BufferedImage encode(String contents, int width, int height)
    {
        int codeWidth = 104;
        try
        {
            codeWidth = Math.max(codeWidth, width);
            com.google.zxing.common.BitMatrix bitMatrix = (new MultiFormatWriter()).encode(contents, BarcodeFormat.CODE_128, codeWidth, height, null);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
