package com.wdy.module.netty.handler;

import com.wdy.module.entity.Balance;
import com.wdy.module.entity.Tag;
import com.wdy.module.service.BalanceService;
import com.wdy.module.service.TagService;
import com.wdy.module.serviceUtil.ByteUtil;
import com.wdy.module.serviceUtil.SpringContextUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("handler84")
@Slf4j
public class Handler84 implements ServiceHandler {
    @Override
    public byte[] executeRequest(byte[] header, byte[] message, Channel channel) {
        log.info("获取电子秤电量（应答包）-----处理器执行！");
        String tagAddress = ByteUtil.getTagAddress(ByteUtil.splitByte(message, 0, 4));
        String powerInteger = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 4, 1));
        String powerDecimal = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 5, 1));
        System.out.println("powerInteger:"+powerInteger);
        System.out.println("powerDecimal:"+powerDecimal);
        System.out.println("tagAddress:"+tagAddress);
        TagService tagService = ((TagService) SpringContextUtil.getBean("TagService"));
        System.out.println("标签地址："+tagAddress);
        Tag tag = tagService.findByTagAddress(tagAddress);
        BalanceService balanceService = ((BalanceService) SpringContextUtil.getBean("BalanceService"));
        Balance balance = new Balance();
        balance.setPowerInterger(powerInteger);
        balance.setPowerDecimal(powerDecimal);
        balance.setTag(tag);
        balanceService.saveOne(balance);
        return null;
       // return CommandCategory.getResponse(null,header,CommandConstant.COMMANDTYPE_ROUTER,null);
    }
}
