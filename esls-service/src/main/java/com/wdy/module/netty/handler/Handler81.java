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

@Component("handler81")
@Slf4j
public class Handler81 implements ServiceHandler {
    @Override
    public byte[] executeRequest(byte[] header, byte[] message, Channel channel) {
        log.info("获取电子秤计量数据（应答包）-----处理器执行！");
        String tagAddress = ByteUtil.getTagAddress(ByteUtil.splitByte(message, 0, 4));
        String weight = ByteUtil.getDigitalMessage(ByteUtil.splitByte(message, 4, 4));
        String weightTips = ByteUtil.getWeightTipsMessage(ByteUtil.splitByte(message, 8, 1));
        String powerInteger = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 9, 1));
        String powerDecimal = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 10, 1));
        System.out.println("weight:"+weight);
        System.out.println("weightTips:"+weightTips);
        System.out.println("powerInteger:"+powerInteger);
        System.out.println("powerDecimal:"+powerDecimal);
        System.out.println("tagAddress:"+tagAddress);
        TagService tagService = ((TagService) SpringContextUtil.getBean("TagService"));
        Tag tag = tagService.findByTagAddress(tagAddress);
        BalanceService balanceService = ((BalanceService) SpringContextUtil.getBean("BalanceService"));
        Balance balance = new Balance();
        balance.setWeight(weight);
        balance.setPowerInterger(powerInteger);
        balance.setPowerDecimal(powerDecimal);
        // 00000000
        System.out.println(weightTips.substring(6,7));
        balance.setSteady(Byte.parseByte(weightTips.substring(6,7)));
        balance.setFlay(Byte.parseByte(weightTips.substring(5,6)));
        balance.setZero(Byte.parseByte(weightTips.substring(4,5)));
        balance.setOverWeight(Byte.parseByte(weightTips.substring(3,4)));
        balance.setNetWeight(Byte.parseByte(weightTips.substring(2,3)));
        balance.setTag(tag);
        balanceService.saveOne(balance);
        return null;
        //return CommandCategory.getResponse(null,header,CommandConstant.COMMANDTYPE_ROUTER,null);
    }
}
