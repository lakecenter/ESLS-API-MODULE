package com.wdy.module.netty.command;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "category")
@Data
public class CommandCategory {
    public final static Map<String, CategoryItem> COMMAND_CATEGORY = new HashMap<>();

    public Map<String, CategoryItem> getCOMMAND_CATEGORY() {
        return COMMAND_CATEGORY;
    }

    public static byte[] getResponse(byte[] classId, byte[] header,int type,byte[] address) {
        byte[] result = new byte[13];
        if(type == CommandConstant.COMMANDTYPE_TAG){
            // int length = address.length + message.length;
            // 通讯对象22对标签
            result[0] = 0x22;
            result[1] = 0x22;
            // 标签地址
            result[4] = address[0];
            result[5] = address[1];
            result[6] = address[2];
            result[7] = address[3];
        }
        else if(type == CommandConstant.COMMANDTYPE_ROUTER){
            // 通讯对象11对路由器
            result[0] = 0x11;
            result[1] = 0x11;
            // 地址
            result[4] = (byte) 0xff;
            result[5] = (byte) 0xff;
            result[6] = (byte) 0xff;
            result[7] = (byte) 0xff;
        }
        else if(type == CommandConstant.COMMANDTYPE_TAG_BROADCAST){
            // 通讯对象22标签
            result[0] = 0x22;
            result[1] = 0x22;
            // 地址
            result[4] = (byte) 0x0;
            result[5] = (byte) 0x0;
            result[6] = (byte) 0x0;
            result[7] = (byte) 0x0;
        }
        // 长度
        result[2] = 0;
        result[3] = 9 ;
        //数据段
        result[8] = 0x01;
        result[9] = 0x01;
        result[10] = 2;
        result[11] = header[0];
        result[12] = header[1];
        return result;
    }

    public static byte[] getResponse(String key) {
        byte[] result = new byte[4];
        CategoryItem categoryItem = COMMAND_CATEGORY.get(key);
        result[0] = categoryItem.getCommand_class();
        result[1] = categoryItem.getCommand_id();
        return result;
    }

    public static String getCommandCategory(byte[] header) {
        if (header[0] == COMMAND_CATEGORY.get(CommandConstant.ACK).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.ACK).getCommand_id()) {
            return CommandConstant.ACK;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.NACK).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.NACK).getCommand_id()) {
            return CommandConstant.NACK;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.OVERTIME).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.OVERTIME).getCommand_id()) {
            return CommandConstant.OVERTIME;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.TAGRESPONSE).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.TAGRESPONSE).getCommand_id()) {
            return CommandConstant.TAGRESPONSE;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.ROUTERRESPONSE).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.ROUTERRESPONSE).getCommand_id()) {
            return CommandConstant.ROUTERRESPONSE;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.ROUTERREGISTY).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.ROUTERREGISTY).getCommand_id()) {
            return CommandConstant.ROUTERREGISTY;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.TAGREGISTY).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.TAGREGISTY).getCommand_id()) {
            return CommandConstant.TAGREGISTY;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.APREAD).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.APREAD).getCommand_id()) {
            return CommandConstant.APREAD;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.BALANCEDATA).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.BALANCEDATA).getCommand_id()) {
            return CommandConstant.BALANCEDATA;
        } else if (header[0] == COMMAND_CATEGORY.get(CommandConstant.BALANCEPOWER).getCommand_class() && header[1] == COMMAND_CATEGORY.get(CommandConstant.BALANCEPOWER).getCommand_id()) {
            return CommandConstant.BALANCEPOWER;
        }else {
            return null;
        }
    }
}
