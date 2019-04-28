package com.wdy.module.mybatis.mybatisService.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.response.SmsSendResponse;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.mybatis.mapper.SmsVerifyMapper;
import com.wdy.module.entity.SmsVerify;
import com.wdy.module.mybatis.mybatisService.ISmsVerifyService;
import com.wdy.module.utils.GenerationSequenceUtil;
import com.wdy.module.utils.RegexUtil;
import com.wdy.module.utils.SmsSendUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 验证码发送记录 服务实现类
 * </p>
 *
 * @author wdy
 * @since 2019-04-26
 */
@Service("SmsVerifyService")
public class SmsVerifyServiceImpl extends ServiceImpl<SmsVerifyMapper, SmsVerify> implements ISmsVerifyService {
    @Override
    public List<SmsVerify> getByMobileAndCaptchaAndType(String mobile, String captcha, int type) {
        EntityWrapper<SmsVerify> smsQuery = new EntityWrapper<>();
        smsQuery.where("mobile={0} and sms_verify={1} and  sms_type = {2}",
                mobile, captcha, type);
        smsQuery.orderBy("create_time", false);
        return this.selectList(smsQuery);
    }

    @Override
    public SmsVerify addAndGetMobileAndCaptcha(String smsType, String mobile) {
        if (RegexUtil.checkPhoneNumber(mobile)) {
            throw new ServiceException(ResultEnum.USER_PHONE_ERROR);
        }
        String randNum = GenerationSequenceUtil.getRandNum(4);
        SmsSendResponse smsSendResponse = SmsSendUtil.sendMessage(mobile,
                "校验码: " + randNum + "。您正在进行" + SmsSendUtil.SMSType.getSMSType(smsType).toString() + "的操作,请在5分钟内完成验证，注意保密哦！");
        SmsVerify smsVerify = SmsVerify.builder().smsId(smsSendResponse.getMsgId()).mobile(mobile).smsVerify(randNum)
                .smsType(SmsSendUtil.SMSType.getType(smsType)).createTime(System.currentTimeMillis()).build();
        this.insert(smsVerify);
        return smsVerify;
    }

    @Override
    public void captchaCheck(String mobile, String smsType, String captcha) throws Exception {
        if (!RegexUtil.checkPhoneNumber(mobile)) {
            throw new ServiceException(ResultEnum.USER_PHONE_ERROR);
        }
        List<SmsVerify> smsVerifies = this.getByMobileAndCaptchaAndType(mobile,
                captcha, SmsSendUtil.SMSType.getType(smsType));
        if (CollectionUtils.isEmpty(smsVerifies)) {
            throw new ServiceException(ResultEnum.VERIFY_PARAM_ERROR);
        }
        if (SmsSendUtil.isCaptchaPassTime(smsVerifies.get(0).getCreateTime())) {
            throw new ServiceException(ResultEnum.VERIFY_PARAM_PASS);
        }
    }

    @Override
    public List<SmsVerify> findAll() {
        return this.baseMapper.findAll();
    }

    @Override
    public SmsVerify findById(Long id) {
        return this.selectById(id);
    }

    @Override
    public Boolean saveOne(SmsVerify s) {
        return this.insertOrUpdate(s);
    }
}
