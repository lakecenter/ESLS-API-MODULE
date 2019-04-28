package com.wdy.module.netty.command;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "acme")
@Data
public class ProtocolConstant {
    public final static Map<String,Map<String, CommandItem>> COMMAND = new HashMap<>();
    public Map<String,Map<String, CommandItem>> getCOMMAND(){
        return COMMAND;
    }
    public static CommandItem getCommandItem(String key, String attr){
        Map<String, CommandItem> stringCommandItemMap = COMMAND.get(key);
        CommandItem commandItem = stringCommandItemMap.get(attr);
        return commandItem;
    }
}
