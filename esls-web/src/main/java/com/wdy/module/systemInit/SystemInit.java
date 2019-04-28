package com.wdy.module.systemInit;

import com.wdy.module.serviceUtil.SpringContextUtil;
import com.wdy.module.system.SystemVersionArgs;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 系统启动初始化配置
 *
 * @author dongyang_wu
 * @date 2019/4/25 13:46
 */
@Component
public class SystemInit implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args)  {
        SystemVersionArgs systemVersionArgs = (SystemVersionArgs) SpringContextUtil.getBean("SystemVersionArgs");
        systemVersionArgs.init();
    }
}
