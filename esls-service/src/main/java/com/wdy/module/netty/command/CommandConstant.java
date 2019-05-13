package com.wdy.module.netty.command;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class CommandConstant {
    // 命令常量
    public static String ACK = "ACK";
    public static String NACK = "NACK";
    public static String OVERTIME = "OVERTIME";
    public static String TAGRESPONSE = "TAGRESPONSE";
    public static String ROUTERRESPONSE = "ROUTERRESPONSE";
    // 命令内容常量
    // 路由注册
    public static String ROUTERREGISTY = "ROUTERREGISTY";
    public static String TAGREGISTY = "TAGREGISTY";
    public static String MAC = "mac";
    // 路由器测试
    public static String APREAD = "APREAD";
    // 电子秤
    public static String BALANCEDATA = "BALANCEDATA";
    public static String BALANCEPOWER = "BALANCEPOWER";
    // 命令常量
    // 0对标签 1对路由器
    public static Integer COMMANDTYPE_TAG = 0;
    public static Integer COMMANDTYPE_ROUTER = 1;
    public static Integer COMMANDTYPE_TAG_BROADCAST = 2;

    public static Map<String, byte[]> COMMAND_BYTE = null;
    public static String TAGBIND = "标签绑定";
    public static String TAGBINDOVER = "标签取消绑定";
    public static String TAGBLING = "标签LED闪烁";
    public static String TAGBLINGOVER = "标签结束LED闪烁";
    public static String FLUSH = "墨水屏刷新";
    public static String QUERYTAG = "查询标签信息";
    public static String QUERYROUTER = "查询路由器信息";
    public static String SETTINGROUTER = "设置路由器信息";
    public static final String TAGREMOVE = "标签移除";
    public static String ROUTERAWAKEOVER = "路由器结束唤醒";
    public static final String ROUTERREMOVE = "路由器移除";
    public static String ROUTERHEARTBEAN = "心跳包";
    public static String APBYCHANNELIDSTOP = "AP停止发送无线帧";
    public static String APRECEIVEBYCHANNELIDSTOP = "AP停止接收无线帧";
    public static String DELETEIPRECORD = "删除历史连接记录";
    public static String GETIPRECORD = "获取历史连接记录";
    public static String GETBALANCE = "获取计量数据";
    public static String BALANCETOZERO = "电子秤置O";
    public static String BALANCETOFLAY = "电子秤去皮";
    public static String GETBALANCEPOWER = "获取电子秤电量";

    // 墨水瓶测试命令
    public static String INKSCREENCOMMAND1 = "墨水瓶测试命令1";
    public static String INKSCREENCOMMAND2 = "墨水瓶测试命令2";
    public static String INKSCREENCOMMAND3 = "墨水瓶测试命令3";
    public static String INKSCREENCOMMAND4 = "墨水瓶测试命令4";
    public static String INKSCREENCOMMAND5 = "墨水瓶测试命令5";


    // 路由器在线升级
    public static String ROUTER_UPDATE = "在线升级命令";
    public static String ROUTER_UPDATE_BEGIN = "开始发送升级程序数据命令";
    public static List<String> needToSynchronize = new ArrayList<>();

    @PostConstruct
    public static void init() {
        COMMAND_BYTE = new HashMap<>();
        // TAG命令
        // 通知标签绑定
        COMMAND_BYTE.put(TAGBIND, getBytes(0x04, 0x01));
        // 通知标签解绑
        COMMAND_BYTE.put(TAGBINDOVER, getBytes(0x04, 0x02));
        // 通知标签LED闪烁
        COMMAND_BYTE.put(TAGBLING, getBytes(0x04, 0x03));
        // 通知标签LED停止闪烁
        COMMAND_BYTE.put(TAGBLINGOVER, getBytes(0x04, 0x04));
        // 通知墨水瓶刷新
        COMMAND_BYTE.put(FLUSH, getBytes(0x04, 0x05));
        // 标签巡检
        COMMAND_BYTE.put(QUERYTAG, getBytes(0x05, 0x01));
        // 标签移除命令
        COMMAND_BYTE.put(TAGREMOVE, getBytes(0x02, 0x02));
        //Router命令（3个注册 查询 设置）
        // 路由器巡检
        COMMAND_BYTE.put(QUERYROUTER, getBytes(0x05, 0x02));
        // 路由器设置
        COMMAND_BYTE.put(SETTINGROUTER, getBytes(0x02, 0x05, COMMANDTYPE_ROUTER));
        // 路由器结束唤醒
        COMMAND_BYTE.put(ROUTERAWAKEOVER, getBytes(0x04, 0x07, COMMANDTYPE_ROUTER));
        // 路由器移除
        COMMAND_BYTE.put(ROUTERREMOVE, getBytes(0x02, 0x04));
        // 路由器心跳包
        COMMAND_BYTE.put(ROUTERHEARTBEAN, getBytes(0x0B, 0x01, COMMANDTYPE_ROUTER));
        // APRead9 12   APByChannelIdStop 9 16  APReceiveByChannelIdStop 9 17 deleteIpRecord 0A 02  getIpRecord 0A 03 GetBalance 08 01 BalanceToZero 08 02 BalanceToFlay  08 03
        // AP读取
        COMMAND_BYTE.put(APREAD, getBytes(0x09, 0x12));
        // AP停止发送无线帧
        COMMAND_BYTE.put(APBYCHANNELIDSTOP, getBytes(0x09, 0x12));
        // AP停止接收无线帧
        COMMAND_BYTE.put(APRECEIVEBYCHANNELIDSTOP, getBytes(0x09, 0x17));
        // 删除历史连接记录
        COMMAND_BYTE.put(DELETEIPRECORD, getBytes(0x0A, 0x02));
        // 获取历史连接记录
        COMMAND_BYTE.put(GETIPRECORD, getBytes(0x0A, 0x03));
        // 获取计量数据
        COMMAND_BYTE.put(GETBALANCE, getBytes(0x08, 0x01));
        // 电子秤置O
        COMMAND_BYTE.put(BALANCETOZERO, getBytes(0x08, 0x02));
        // 电子秤去皮
        COMMAND_BYTE.put(BALANCETOFLAY, getBytes(0x08, 0x03));
        // 获取电子秤电量GETBALANCEPOWER
        COMMAND_BYTE.put(GETBALANCEPOWER, getBytes(0x08, 0x04));
        // 墨水瓶测试命令
        COMMAND_BYTE.put(INKSCREENCOMMAND1, getBytes(0x09, 0x08));
        COMMAND_BYTE.put(INKSCREENCOMMAND2, getBytes(0x09, 0x09));
        COMMAND_BYTE.put(INKSCREENCOMMAND3, getBytes(0x09, 0x0A));
        COMMAND_BYTE.put(INKSCREENCOMMAND4, getBytes(0x09, 0x0B));
        COMMAND_BYTE.put(INKSCREENCOMMAND5, getBytes(0x09, 0x0C));
        // 路由器在线升级
        COMMAND_BYTE.put(ROUTER_UPDATE, getBytes(0x0C, 0x01));
        COMMAND_BYTE.put(ROUTER_UPDATE_BEGIN, getBytes(0x0C, 0x02, CommandConstant.COMMANDTYPE_ROUTER));
        needToSynchronize = Arrays.asList(TAGREMOVE);
    }

    private static byte[] getBytes(int _0, int _1, int type) {
        byte[] bytes = new byte[11];
        //100010 00100010
        if (type == COMMANDTYPE_TAG) {
            bytes[0] = 0x22;
            bytes[1] = 0x22;
        } else if (type == COMMANDTYPE_ROUTER) {
            // 临时修改
            bytes[0] = 0x11;
            bytes[1] = 0x11;
        }
        bytes[2] = 0;
        bytes[3] = 7;
        bytes[4] = (byte) 0xff;
        bytes[5] = (byte) 0xff;
        bytes[6] = (byte) 0xff;
        bytes[7] = (byte) 0xff;
        bytes[8] = (byte) _0;
        bytes[9] = (byte) _1;
        bytes[10] = 0;
        return bytes;
    }

//    public static byte[] getRouterUpdateByte(byte[] message) {
//        byte[] bytes = new byte[10 + message.length];
//        // 通讯对象
//        bytes[0] = 0x11;
//        bytes[1] = 0x11;
//        // 长度
//        int length = 4 + message.length;
//        bytes[2] = (byte) (length >> 8);
//        bytes[3] = (byte) (length >> 0);
//        // 地址
//        bytes[4] = (byte) 0xff;
//        bytes[5] = (byte) 0xff;
//        bytes[6] = (byte) 0xff;
//        bytes[7] = (byte) 0xff;
//        bytes[8] = (byte) 0x0C;
//        bytes[9] = (byte) 0x03;
//        bytes[10] = (byte) message.length;
//        System.arraycopy(message, 0, bytes, 10, message.length);
//        //数据段
//        return bytes;
//    }

    public static byte[] getBytesByType(byte[] address, byte[] message, int type) {
        byte[] bytes = new byte[8 + message.length];
        if (type == COMMANDTYPE_TAG) {
            // int length = address.length + message.length;
            int length = 4 + message.length;
            // 通讯对象
            bytes[0] = 0x22;
            bytes[1] = 0x22;
            // 长度
            bytes[2] = (byte) (length >> 8);
            bytes[3] = (byte) (length >> 0);
            // 标签地址
            for (int i = 0; i < address.length; i++)
                bytes[i + 4] = address[i];
            //数据段
            for (int i = 0; i < message.length; i++)
                bytes[i + 8] = message[i];

        } else if (type == COMMANDTYPE_ROUTER) {
            // 通讯对象
            bytes[0] = 0x11;
            bytes[1] = 0x11;
            // 长度
            int length = 4 + message.length;
            bytes[2] = (byte) (length >> 8);
            bytes[3] = (byte) (length >> 0);
            // 地址
            bytes[4] = (byte) 0xff;
            bytes[5] = (byte) 0xff;
            bytes[6] = (byte) 0xff;
            bytes[7] = (byte) 0xff;
            //数据段
            for (int i = 0; i < message.length; i++)
                bytes[i + 8] = message[i];
        } else if (type == COMMANDTYPE_TAG_BROADCAST) {
            // 通讯对象
            bytes[0] = 0x22;
            bytes[1] = 0x22;
            // 长度
            int length = 4 + message.length;
            bytes[2] = (byte) (length >> 8);
            bytes[3] = (byte) (length >> 0);
            // 地址
            bytes[4] = 0;
            bytes[5] = 0;
            bytes[6] = 0;
            bytes[7] = 0;
            //数据段
            for (int i = 0; i < message.length; i++)
                bytes[i + 8] = message[i];
        }
        return bytes;
    }

    private static byte[] getBytes(int _0, int _1) {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) _0;
        bytes[1] = (byte) _1;
        bytes[2] = 0;
        return bytes;
    }

    public static String getInkScreenType(Integer type) {
        String contentType;
        switch (type) {
            case 1:
                contentType = CommandConstant.INKSCREENCOMMAND1;
                break;

            case 2:
                contentType = CommandConstant.INKSCREENCOMMAND2;
                break;

            case 3:
                contentType = CommandConstant.INKSCREENCOMMAND3;
                break;

            case 4:
                contentType = CommandConstant.INKSCREENCOMMAND4;
                break;

            case 5:
                contentType = CommandConstant.INKSCREENCOMMAND5;
                break;

            default:
                contentType = CommandConstant.INKSCREENCOMMAND1;
        }
        return contentType;
    }

}
