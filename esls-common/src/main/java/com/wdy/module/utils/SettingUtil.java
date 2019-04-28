package com.wdy.module.utils;


import com.wdy.module.entity.Router;
import com.wdy.module.entity.Tag;

import java.sql.Timestamp;

public class SettingUtil {
    public static Tag settingTagWithUpdate(Tag tag, long begin) {
        // 设置当前完成时间
        tag.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        long end = System.currentTimeMillis();
        // 0更新更新完毕
        tag.setWaitUpdate(0);
        // 设置命令执行时间
        tag.setExecTime((int) (end - begin));
        System.out.println("命令执行时间: " + (end - begin));
        return tag;
    }

    public static Tag settingTag(Tag tag, long begin) {
        // 设置当前完成时间
        tag.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        long end = System.currentTimeMillis();
        // 设置命令执行时间
        tag.setExecTime((int) (end - begin));
        System.out.println("命令执行时间: " + (end - begin));
        return tag;
    }

    public static Router settintRouter(Router router, long begin) {
        long end = System.currentTimeMillis();
        router.setExecTime((int) (end - begin));
        router.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        return router;
    }
}
