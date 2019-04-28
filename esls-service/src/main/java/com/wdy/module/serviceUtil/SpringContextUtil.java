package com.wdy.module.serviceUtil;

import com.wdy.module.entity.Dispms;
import com.wdy.module.entity.Tag;
import com.wdy.module.netty.ServerChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Component
@Slf4j
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    // 把byte 转化为两位十六进制数
    public static String toHex(byte b) {
        String result = Integer.toHexString(b & 0xFF);
        if (result.length() == 1) {
            result = '0' + result;
        }
        return result;
    }

    // 将数字转为数组
    public static byte[] int2ByteArr(int i, int n) {
        byte[] bytes = new byte[n];
        int begin = n;
        for (int j = 0; j < n; j++) {
            begin = begin - 1;
            bytes[j] = (byte) (i >> 8 * begin);
        }
        return bytes;
    }

    // 十进制整数转点分IP地址
    public static String longToIP(long longIp) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    public static void sleepSomeTime(Long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 获取指定对象的指定属性值
    public static String getSourceData(String name, Object source) {
        String sourceData = null;
        try {
            Field field = source.getClass().getDeclaredField(name);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            sourceData = field.get(source).toString();
        } catch (Exception e) {
        }
        return sourceData;
    }

    // 切换大小端
    public static byte[] changeBytes(byte[] a) {
        if (a == null) return null;
        byte[] b = new byte[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[b.length - i - 1];
        }
        return b;
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        byte[] result = new byte[4];
        int index = bytes.length - 1;
        for (int i = 3; i >= 0; i--, index--) {
            if (index >= 0)
                result[i] = bytes[index];
            else
                result[i] = 0;
        }
        return result;
    }

    public static byte hexStringtoByte(String str) {
        return (byte) Integer.parseInt(str, 16);
    }

    public static byte[] getAddressByBarCode(String barCode) {
        String substring = barCode.substring(3, 12);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 9; i++)
            sb.append(substring.charAt(i) - '0');
        substring = Long.toHexString(Long.valueOf(sb.toString()));
        if (substring.length() % 2 != 0)
            substring = "0" + substring;
        if (barCode != null && barCode.length() == 12)
            return changeBytes(hexStringToBytes(substring));
        return new byte[0];
    }

    public static List<byte[]> getAwakeBytes(List<Tag> tags) {
        List<byte[]> addressList = new ArrayList<>();
        for (Tag tag : tags) {
            byte[] addressByBarCode = getAddressByBarCode(tag.getBarCode());
            if (addressByBarCode != null)
                addressList.add(addressByBarCode);
        }
        int i = 0, j = 0, length = 4 * addressList.size();
        List<byte[]> byteList = new ArrayList<>();
        int len = length / 200;
        int remainder = length % 200;
        if (length > 200) {
            for (i = 0; i < len; i++) {
                byte[] bytes = new byte[5 + 200];
                bytes[0] = 0x04;
                bytes[1] = 0x06;
                // 长度
                bytes[2] = (byte) (2 + 200);
                // 地址数量
                bytes[3] = (byte) 50;
                // 是否刷新
                bytes[4] = 0x00;
                int begin = 5;
                for (j = i * 50; j < (i + 1) * 50; j++) {
                    byte[] address = addressList.get(j);
                    for (byte b : address)
                        bytes[begin++] = b;
                }
                byteList.add(bytes);
            }
            byte[] bytes = new byte[5 + remainder];
            bytes[0] = 0x04;
            bytes[1] = 0x06;
            // 长度
            bytes[2] = (byte) (2 + remainder);
            // 地址数量
            bytes[3] = (byte) (remainder / 4);
            bytes[4] = 0x01;
            int begin = 5;
            for (j = i * 50; j < i * 50 + remainder / 4; j++) {
                byte[] address = addressList.get(j);
                for (byte b : address)
                    bytes[begin++] = b;
            }
            byteList.add(bytes);
        } else {
            byte[] bytes = new byte[5 + length];
            bytes[0] = 0x04;
            bytes[1] = 0x06;
            // 长度
            bytes[2] = (byte) (2 + length);
            // 地址数量
            bytes[3] = (byte) (length / 4);
            // 是否刷新
            bytes[4] = 0x01;
            int begin = 5;
            for (i = 0; i < addressList.size(); i++) {
                byte[] address = addressList.get(i);
                for (byte b : address)
                    bytes[begin++] = b;
            }
            byteList.add(bytes);
        }
        return byteList;
    }

    public static String bytesToString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes)
            sb.append(b);
        return sb.toString();
    }

    public static void printBytes(String comment, byte[] message) {
        System.out.print(comment + " ");
        for (byte b : message)
            System.out.print(toHex(b) + " ");
        //System.out.print(b+" ");
        System.out.println();
    }

    public static HashMap<String, Integer> getRegionIdList(String regionName, List<Dispms> dispms) {
        HashMap<String, Integer> map = new HashMap<>();
        String[] regionNames = regionName.split(" ");
        String styleRegionNames = getStyleRegionNames(dispms);
        // 对改动的部分进行匹配  获得最终的区域信息
        regionNames = getRealRegionNames(regionNames, styleRegionNames).split(" ");
        for (String name : regionNames) {
            Integer idByRegionName = getIdByRegionName(dispms, name);
            if (idByRegionName != null) {
                map.put(name, idByRegionName);
            }
        }
        return map;
    }

    private static String getStyleRegionNames(List<Dispms> dispms) {
        StringBuffer sb = new StringBuffer();
        for (Dispms dispm : dispms) {
            sb.append(dispm.getSourceColumn() + " ");
        }
        return sb.toString();
    }

    private static String getRealRegionNames(String[] regionNames, String styleRegionNames) {
        StringBuffer sb = new StringBuffer();
        for (String name : regionNames) {
            if (styleRegionNames.contains(name))
                sb.append(name + " ");
        }
        return sb.toString();
    }

    public static Integer getIdByRegionName(List<Dispms> dispms, String name) {
        for (int i = 0; i < dispms.size(); i++) {
            if (dispms.get(i).getSourceColumn().equals(name))
                return (i + 1);
        }
        return 0;
    }

    public static ServerChannelHandler serverChannelHandler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtil.applicationContext == null) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public SpringContextUtil() {
    }
}
