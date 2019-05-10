package com.wdy.module.serviceUtil;

import com.wdy.module.common.constant.StyleType;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.response.ByteResponse;
import com.wdy.module.dto.ByteAndRegion;
import com.wdy.module.entity.*;
import com.wdy.module.graphic.BarCode;
import com.wdy.module.graphic.QRCode;
import com.wdy.module.service.*;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ImageHelper {
    public static ByteResponse getRequest(List<Dispms> dispms, String styleNumber, Good good) {
        StyleService styleService = (StyleService) SpringContextUtil.getBean("StyleService");
        Style style = styleService.findByStyleNumberAndIsPromote(styleNumber, good.getIsPromote() == null ? 0 : good.getIsPromote());
        if (dispms.size() == 0)
            return null;
        List<byte[]> allbyteList = new ArrayList<>();
        byte[] firstByte = new byte[3 + 6 + dispms.size() * 12];
        int i, j;
        firstByte[0] = 0x03;
        firstByte[1] = 0x01;
        firstByte[2] = (byte) (6 + dispms.size() * 12);
        for (j = 0; j < styleNumber.length(); j++)
            firstByte[j + 3] = (byte) styleNumber.charAt(j);
        firstByte[7] = '\0';
        firstByte[8] = (byte) dispms.size();
//        FileUtils.deleteDirectory(new File("D:\\styles\\"+styleNumber+"("+dispms.get(0).getStyle().getId()+")"));
        for (i = 0; i < dispms.size(); i++) {
            try {
                Dispms region = dispms.get(i);
                ByteAndRegion byteAndRegion = getRegionImage(region, styleNumber, good);
                region = byteAndRegion.getRegion();
                byte[] regionImage = byteAndRegion.getRegionBytes();
                // 区域编号
                firstByte[(i * 12) + 9] = Byte.parseByte(String.valueOf(region.getRegionId()));
                // 颜色
                firstByte[(i * 12) + 10] = ColorUtil.getColorByte(region.getBackgroundColor(), region.getFontColor());
                byte[] x, y, height, width;
                if (ImageHelper.getTypeByStyleNumber(styleNumber).equals(StyleType.StyleType_42)) {
                    width = int2ByteArr(region.getWidth());
                    height = int2ByteArr(region.getHeight());
                    x = int2ByteArr(region.getX());
                    y = int2ByteArr(region.getY());
                    // x
                    firstByte[(i * 12) + 11] = x[1];
                    firstByte[(i * 12) + 12] = x[0];
                    // y
                    firstByte[(i * 12) + 13] = y[1];
                    firstByte[(i * 12) + 14] = y[0];
                } else {
                    width = int2ByteArr(region.getWidth());
                    height = int2ByteArr(region.getHeight());
                    x = int2ByteArr(region.getX());
                    y = int2ByteArr(style.getHeight() - region.getY() - region.getHeight());
                    // x
                    firstByte[(i * 12) + 11] = y[1];
                    firstByte[(i * 12) + 12] = y[0];
                    // y
                    firstByte[(i * 12) + 13] = x[1];
                    firstByte[(i * 12) + 14] = x[0];
                }
                byte[] length = int2ByteArr(regionImage.length);
                // 长度
                firstByte[(i * 12) + 15] = width[1];
                firstByte[(i * 12) + 16] = width[0];
                // 宽度
                firstByte[(i * 12) + 17] = height[1];
                firstByte[(i * 12) + 18] = height[0];
                // 显示存储字节数
                firstByte[(i * 12) + 19] = length[1];
                firstByte[(i * 12) + 20] = length[0];
                List<byte[]> byteList = getByteList(regionImage, region.getRegionId());
                allbyteList.addAll(byteList);
            } catch (Exception e) {
                System.out.println("getRequest - " + e);
            }
        }
        byte[] bytes = allbyteList.get(allbyteList.size() - 1);
        bytes[4] = 0x01;
        return new ByteResponse(firstByte, allbyteList);
    }

    public static ByteResponse getRegionRequest(List<Dispms> dispms, String styleNumber, Good good) {
        if (dispms.size() == 0)
            return null;
        List<byte[]> allbyteList = new ArrayList<>();
        for (int i = 0; i < dispms.size(); i++) {
            Dispms region = dispms.get(i);
            ByteAndRegion byteAndRegion = getRegionImage(region, styleNumber, good);
            region = byteAndRegion.getRegion();
            byte[] regionImage = byteAndRegion.getRegionBytes();
            List<byte[]> byteList = getByteList(regionImage, region.getRegionId());
            allbyteList.addAll(byteList);
        }
        byte[] bytes = allbyteList.get(allbyteList.size() - 1);
        bytes[4] = 0x01;
        return new ByteResponse(null, allbyteList);
    }

    public static ByteAndRegion getRegionImage(Dispms dispM, String styleNumber, Good good) {
        try {
//            FileUtil.createFileIfNotExist("D:\\styles\\",styleNumber+"("+dispM.getStyle().getId()+")");
            String columnType = dispM.getColumnType();
            if (!ImageHelper.getImageType(columnType))
                return ImageHelper.getImageByType1(dispM, styleNumber, good);
            else
                return ImageHelper.getImageByType2(dispM, styleNumber, good);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static ByteAndRegion getImageByType1(Dispms dispM, String styleNumber, Good good) throws Exception {
        Dispms returnDispms = new Dispms();
        BeanUtils.copyProperties(dispM, returnDispms);
        returnDispms.setId((long) 0);
        // 宽 高 columnType backgroundColor fontColor（非文字）
        String columnType = dispM.getColumnType();
        int imageWidth = dispM.getWidth(), imageHeight = dispM.getHeight();
        if (getTypeByStyleNumber(styleNumber).equals(StyleType.StyleType_42)) {
            imageWidth = get8Number(imageWidth);
        } else {
            imageHeight = get8Number(imageHeight);
        }
        returnDispms.setWidth(imageWidth);
        returnDispms.setHeight(imageHeight);
        BufferedImage bufferedImage = createBufferedImage(imageWidth, imageHeight);
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
        // 背景
        g2d.setColor(ColorUtil.getColorByInt(dispM.getBackgroundColor()));
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        // 二维码
        if (columnType.contains(StringUtil.QRCODE)) {
            String result = SpringContextUtil.getSourceData(dispM.getSourceColumn(), good);
            int value = Math.min(get8Number(imageWidth), get8Number(imageHeight));
            BufferedImage image = QRCode.encode(result, value, value);
            g2d.drawImage(image, 0, 0, null);
        }
        // 条形码
        else if (dispM.getColumnType().contains(StringUtil.BARCODE)) {
            String result = SpringContextUtil.getSourceData(dispM.getSourceColumn(), good);
            BufferedImage image = BarCode.encode(result, imageWidth, imageHeight);
            g2d.drawImage(image, 0, 0, null);
        }
        // 线段
        else if (columnType.contains(StringUtil.LINE)) {
//            String result = SpringContextUtil.getSourceData("name", good);
//            Dispms name = dispmsService.findByStyleIdAndColumnTypeAndSourceColumn(dispM.getStyle().getId(), StringUtil.Str, "name");
//            int fontType = ColorUtil.getFontType(name.getFontType());
//            String[] leftArgs = ImageHelper.getWidthAndHeight(name.getFontFamily(), fontType, name.getFontSize(),StringUtil.Str, result);
//            returnDispms.setY(Integer.valueOf(leftArgs[1]));
            bufferedImage = createBackgroundImage(imageWidth, imageHeight, returnDispms.getBackup() == null ? 4 : Integer.valueOf(returnDispms.getBackup()));
            g2d = (Graphics2D) bufferedImage.getGraphics();
            if (imageWidth > imageHeight)
                g2d.drawLine(0, imageHeight, imageWidth, imageHeight);
            else
                g2d.drawLine(0, imageWidth, 0, imageHeight);
        }
        // 背景
        else if (columnType.contains(StringUtil.BACKGROUND)) {
            bufferedImage = createBackgroundImage(imageWidth, imageHeight, 4);
        }
        // 图片
        else if (columnType.contains(StringUtil.PHOTO)) {
            URL url = new URL(dispM.getImageUrl());
            bufferedImage = ImageIO.read(url);
            // image转bytes
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "bmp", out);
            byte[] bytes = ImageHelper.ChangeImgSize(out.toByteArray(), imageWidth, imageHeight);
            // bytes转image
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            bufferedImage = ImageIO.read(in);
        }
        //ImageIO.write(bufferedImage, "BMP", new File("D:\\styles\\"+styleNumber+"("+dispM.getStyle().getId()+")"+"\\"+dispM.getId()+columnType+" (x"+returnDispms.getX()+" y"+returnDispms.getY()+" w"+returnDispms.getWidth()+" h"+returnDispms.getHeight()+").bmp"));
        return new ByteAndRegion(changeImage(bufferedImage, styleNumber), returnDispms);
    }

    public static ByteAndRegion getImageByType2(Dispms dispM, String styleNumber, Good good) throws Exception {
        DispmsService dispmsService = (DispmsService) SpringContextUtil.getBean("DispmsService");
        Dispms returnDispms = new Dispms();
        BeanUtils.copyProperties(dispM, returnDispms);
        returnDispms.setId((long) 0);
        // 文本宽 高 columnType backgroundColor fontColor（非文字）
        String columnType = dispM.getColumnType();
        int fontType = ColorUtil.getFontType(dispM.getFontType());
        int imageWidth, imageHeight, imageAscent;
        Boolean flag = ColorUtil.isRedAndBlack(dispM.getBackgroundColor(), dispM.getFontColor());
        // 以下为含字符串或数字
        String s = StringUtil.getRealString(dispM, good);
        //获得最大宽
        String[] args = ImageHelper.getWidthAndHeight(dispM.getFontFamily(), fontType, dispM.getFontSize(), columnType, s);
        imageWidth = dispM.getWidth();
        imageHeight = Integer.valueOf(args[1]);
        imageAscent = Integer.valueOf(args[2]);
        if (getTypeByStyleNumber(styleNumber).equals(StyleType.StyleType_42)) {
            imageWidth = get8Number(imageWidth);
        } else {
            imageHeight = get8Number(imageHeight);
        }
        returnDispms.setWidth(imageWidth);
        returnDispms.setHeight(imageHeight);
        // 存高
        dispM.setHeight(imageHeight);
        BufferedImage bufferedImage = createBufferedImage(imageWidth, imageHeight);
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
        // (红黑) 0红黑 1白 背景色
        g2d.setColor(ColorUtil.getColorByInt(dispM.getBackgroundColor()));
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        Font font = new Font(dispM.getFontFamily(), fontType, dispM.getFontSize());
        g2d.setFont(font);
        g2d.setStroke(new BasicStroke(2));
        g2d = ImageHelper.getGraphiceByColor(g2d, dispM, flag);
        //  数字
        if (columnType.equals(StringUtil.NUMBER)) {
            String left = s.substring(0, s.indexOf(".") + 1);
            String right = s.substring(s.indexOf(".") + 1);
            String[] leftArgs = ImageHelper.getWidthAndHeight(dispM.getFontFamily(), fontType, dispM.getFontSize(), columnType, left);
            String[] backup = dispM.getBackup().split("/");
            String[] rightArgs = ImageHelper.getWidthAndHeight(dispM.getFontFamily(), fontType, Integer.valueOf(backup[1]), columnType, right);
            // 左侧数字直接画
            g2d.drawString(left, 0, imageAscent);
            // 0/fontsize/left的宽我存
            // 画划线
            if (backup[0].equals("1")) {
                g2d.drawLine(0, Integer.valueOf(args[1]) / 2, Integer.valueOf(leftArgs[0]), Integer.valueOf(args[1]) / 2);
                g2d.drawLine(Integer.valueOf(leftArgs[0]), Integer.valueOf(rightArgs[1]) / 2, Integer.valueOf(leftArgs[0]) + Integer.valueOf(rightArgs[0]), Integer.valueOf(rightArgs[1]) / 2);
            }
            font = new Font(dispM.getFontFamily(), fontType, Integer.valueOf(backup[1]));
            g2d.setFont(font);
            g2d.setStroke(new BasicStroke(2));
            // 画右侧数字 改变画笔
            g2d.drawString(right, Integer.valueOf(leftArgs[0]), Integer.valueOf(rightArgs[2]));
            String textLeft = dispM.getText();
            if (dispM.getText().contains("."))
                textLeft = dispM.getText().substring(0, textLeft.indexOf("."));
            String[] textArgs = ImageHelper.getWidthAndHeight(dispM.getFontFamily(), fontType, dispM.getFontSize(), columnType, textLeft);
            // 还是要存fontSize
            if (backup.length == 2)
                dispM.setBackup(dispM.getBackup() + "/" + textArgs[0]);
            else {
                String[] backup1 = dispM.getBackup().split("/");
                dispM.setBackup(backup1[0] + "/" + backup1[1] + "/" + textArgs[0]);
            }
        }
        // 字符串
        else if (columnType.contains(StringUtil.Str)) {
            g2d.drawString(s, 0, imageAscent);
            // 字符串的宽
            //dispM.setBackup(args[0]);
        }
        dispmsService.saveOne(dispM);
//        ImageIO.write(bufferedImage, "BMP", new File("D:\\styles\\"+styleNumber+"("+dispM.getStyle().getId()+")"+"\\"+dispM.getId()+columnType+" (x"+returnDispms.getX()+" y"+returnDispms.getY()+" w"+returnDispms.getWidth()+" h"+returnDispms.getHeight()+").bmp"));
        return new ByteAndRegion(changeImage(bufferedImage, styleNumber), returnDispms);
    }

    public static synchronized ByteResponse getByteResponse(Tag tag) {
        StyleService styleService = (StyleService) SpringContextUtil.getBean("StyleService");
        List<Dispms> dispmsesList = new ArrayList<>();
        Good good = tag.getGood();
        List<Dispms> dispmses = (List<Dispms>) styleService.findByStyleNumberAndIsPromote(tag.getStyle().getStyleNumber(), good.getIsPromote() == null ? 0 : good.getIsPromote()).getDispmses();
        String regionNames = good.getRegionNames();
        boolean isRegion = !StringUtil.isEmpty(regionNames) && !regionNames.contains("isPromote") ? true : false;
        ByteResponse byteResponse;
        // 改价只更改区域
        if (isRegion) {
            for (Dispms dispms : dispmses) {
                if (dispms.getStatus() != null && dispms.getStatus() == 1 && regionNames.contains(dispms.getSourceColumn())) {
                    dispmsesList.add(dispms);
                }
            }
            log.info("区域:" + dispmsesList.size());
            byteResponse = getRegionRequest(dispmsesList, tag.getStyle().getStyleNumber(), good);
        } else {
            for (int i = 0; i < dispmses.size(); i++) {
                Dispms dispms = dispmses.get(i);
                if (dispms.getStatus() == 1)
                    dispmsesList.add(dispmses.get(i));
            }
            log.info("全局:" + dispmsesList.size());
            byteResponse = getRequest(dispmsesList, tag.getStyle().getStyleNumber(), good);
        }
        return byteResponse;
    }

    // 分包发送
    public static List<byte[]> getByteList(byte[] regionImage, long number) {
        List<byte[]> byteList = new ArrayList<>();
        int i, j;
        int packageLength = Integer.valueOf(SystemVersionArgs.packageLength);
        int len = regionImage.length / packageLength;
        int remainder = regionImage.length % packageLength;
        if (regionImage.length > packageLength) {
            for (i = 0; i < len; i++) {
                byte[] bytes = new byte[8 + packageLength];
                bytes[0] = 0x03;
                bytes[1] = 0x02;
                // 长度
                bytes[2] = (byte) (5 + packageLength);
                // 编号
                bytes[3] = (byte) number;
                // 是否刷新
                bytes[4] = 0x00;
                byte[] loc = int2ByteArr(i * packageLength);
                // 起始位置
                bytes[5] = loc[1];
                bytes[6] = loc[0];
                // 数据长度
                bytes[7] = (byte) (packageLength);
                int begin = 8;
                // 区域显示数据
                for (j = i * packageLength; j < (i + 1) * packageLength; j++)
                    bytes[begin++] = regionImage[j];
                byteList.add(bytes);
            }
            byte[] bytes = new byte[8 + remainder];
            bytes[0] = 0x03;
            bytes[1] = 0x02;
            // 长度
            bytes[2] = (byte) (5 + remainder);
            // 编号
            bytes[3] = (byte) number;
            bytes[4] = 0x00;
            byte[] loc = int2ByteArr(i * packageLength);
            // 起始位置
            bytes[5] = loc[1];
            bytes[6] = loc[0];
            // 数据长度
            bytes[7] = (byte) (remainder);
            int begin = 8;
            // 区域显示数据
            for (j = i * packageLength; j < i * packageLength + remainder; j++)
                bytes[begin++] = regionImage[j];
            byteList.add(bytes);
        } else {
            byte[] bytes = new byte[8 + regionImage.length];
            bytes[0] = 0x03;
            bytes[1] = 0x02;
            // 长度
            bytes[2] = (byte) (5 + regionImage.length);
            // 编号
            bytes[3] = (byte) number;
            // 是否刷新
            bytes[4] = 0x00;
            // 起始位置
            bytes[5] = 0;
            bytes[6] = 0;
            // 数据长度
            bytes[7] = (byte) (regionImage.length);
            // 区域显示数据
            for (i = 0; i < regionImage.length; i++)
                bytes[i + 8] = regionImage[i];
            byteList.add(bytes);
        }
        return byteList;
    }

    public static Dispms getText(Dispms dispM, Good good) {
        if (dispM.getSourceColumn().equalsIgnoreCase("0")) {
            return dispM;
        }
        StringBuilder sqlBuilder = new StringBuilder("select ");
        sqlBuilder.append(dispM.getSourceColumn()).append(" ");
        sqlBuilder.append("from ").append(TableConstant.TABLE_GOODS).append(" ");
        sqlBuilder.append("where id=").append(good.getId());
        List list = ((GoodService) SpringContextUtil.getBean("GoodService")).findBySql(sqlBuilder.toString());
        if (list != null && list.size() > 0) {
            Object obj = list.get(0);
            if (dispM.getColumnType().contains("数字")) {
                dispM.setText(String.format("%.2f", new Object[]{
                        obj
                }));
            } else if (dispM.getColumnType().equals("条形码")) {
                dispM.setText(good.getBarCode());
            }
            // 字符串
            else {
                dispM.setText(String.valueOf(obj));
            }
        }
        return dispM;
    }

    public static byte[] ChangeImgSize(byte[] data, int nw, int nh) {
        byte[] newdata = null;
        try {
            BufferedImage bis = ImageIO.read(new ByteArrayInputStream(data));
            int w = bis.getWidth();
            int h = bis.getHeight();
            double sx = (double) nw / w;
            double sy = (double) nh / h;
            AffineTransform transform = new AffineTransform();
            transform.setToScale(sx, sy);
            AffineTransformOp ato = new AffineTransformOp(transform, null);
            //原始颜色
            BufferedImage bid = new BufferedImage(nw, nh, 12);
            ato.filter(bis, bid);
            //转换成byte字节
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bid, "bmp", baos);
            newdata = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newdata;
    }

    public static String[] getWidthAndHeight(String name, int style, int size, String columnType, String realString) {
        StringBuffer sb = new StringBuffer();
        Font font = new Font(name, style, size);
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        sb.append(metrics.stringWidth(realString) + " ");
        sb.append(metrics.getHeight() + " ");
        sb.append(metrics.getAscent());
        return sb.toString().split(" ");
    }

    public static Graphics2D getGraphiceByColor(Graphics2D g2d, Dispms dispM, boolean flag) {
        if (flag) {
            if (dispM.getFontColor() == 0 || dispM.getFontColor() == 2)
                g2d.setColor(ColorUtil.getColorByInt(1));
            else
                g2d.setColor(ColorUtil.getColorByInt(dispM.getFontColor()));
        } else
            g2d.setColor(ColorUtil.getColorByInt(dispM.getFontColor()));
        return g2d;
    }

    public static boolean getImageType(String columnType) {
        if (columnType.contains(StringUtil.QRCODE) || columnType.contains(StringUtil.BARCODE) || columnType.contains(StringUtil.BACKGROUND) || columnType.contains(StringUtil.PHOTO) || columnType.contains(StringUtil.LINE))
            return false;
        return true;
    }

    public static byte[] changeImage(BufferedImage bimg, String styleNumber) {
        // 212 104   296 128
        // 400 300
        int[][] data = new int[bimg.getWidth()][bimg.getHeight()];
        byte result[] = new byte[bimg.getWidth() * bimg.getHeight() / 8];
        int sum = 0;
        if (getTypeByStyleNumber(styleNumber).equals(StyleType.StyleType_42)) {
            for (int i = 0; i < bimg.getHeight(); i++) {
                int time = 0;
                StringBuffer byteString = new StringBuffer();
                for (int j = 0; j < bimg.getWidth(); j++) {
                    data[j][i] = bimg.getRGB(j, i);
                    if (data[j][i] == -1 && time < 8) {
                        byteString.append("1");
                        time++;
                    } else if (data[j][i] == -16777216 && time < 8) {
                        byteString.append("0");
                        time++;
                    } else if (time == 8) {
                        byte b = (byte) Integer.parseInt(byteString.toString(), 2);
                        result[sum++] = b;
                        time = 0;
                        byteString = new StringBuffer();
                        j--;
                    }
                    if (j == bimg.getWidth() - 1) {
                        byte b = (byte) Integer.parseInt(byteString.toString(), 2);
                        result[sum++] = b;
                    }
                }
            }
        } else {
            for (int i = 0; i < bimg.getWidth(); i++) {
                int time = 0;
                StringBuffer byteString = new StringBuffer();
                //-16777216 -1
                for (int j = bimg.getHeight() - 1; j >= 0; j--) {
                    data[i][j] = bimg.getRGB(i, j);
                    if (data[i][j] == -1 && time < 8) {
                        byteString.append("1");
                        time++;
                    } else if (data[i][j] == -16777216 && time < 8) {
                        byteString.append("0");
                        time++;
                    } else if (time == 8) {
                        byte b = (byte) Integer.parseInt(byteString.toString(), 2);
                        result[sum++] = b;
                        time = 0;
                        byteString = new StringBuffer();
                        j++;
                    }
                    if (j == 0) {
                        byte b = (byte) Integer.parseInt(byteString.toString(), 2);
                        result[sum++] = b;
                    }
                }
            }
        }
//        System.out.println("全部区域数据开始");
//        for (int i = 0; i < result.length; i++) {
//            byte b = result[i];
//            System.out.print (SpringContextUtil.toHex(b)+"    ");
//            if(i%12==0 && i!=0)
//                System.out.println();
//        }
//        System.out.println();
//        System.out.println("全部区域数据结束");
        return result;
    }

    public static Integer getTypeByStyleNumber(String styleNumber) {
        if (styleNumber.substring(0, 2).equals("21"))
            return StyleType.StyleType_21;
        else if (styleNumber.substring(0, 2).equals("29"))
            return StyleType.StyleType_29;
        else if (styleNumber.substring(0, 2).equals("42"))
            return StyleType.StyleType_42;
        return 0;
    }

    // 将数字转换为8的整数倍
    public static int get8Number(int a) {
        if (a % 8 == 0)
            return a;
        // 47  57  29
        int remainder = a % 8;
        return a + (8 - remainder);
    }

    public static BufferedImage createBufferedImage(int width, int height) throws IOException {
        // 单色位图
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++)
                bufferedImage.setRGB(i, j, Color.WHITE.getRGB());
        }
        return bufferedImage;
    }

    public static BufferedImage createBackgroundImage(int width, int height, int realHeight) throws IOException {
        // 单色位图
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < realHeight; i++) {
            for (int j = 0; j < width; j++)
                bufferedImage.setRGB(j, i, Color.BLACK.getRGB());
        }
        //后面画白色
        for (int i = realHeight; i < height; i++) {
            for (int j = 0; j < width; j++)
                bufferedImage.setRGB(j, i, Color.WHITE.getRGB());
        }
        return bufferedImage;
    }

    public static byte[] int2ByteArr(int i) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (i >> 8);
        bytes[1] = (byte) (i >> 0);
        return bytes;
    }
}
