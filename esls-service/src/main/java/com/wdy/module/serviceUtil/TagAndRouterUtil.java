package com.wdy.module.serviceUtil;


import com.wdy.module.dto.TagsAndRouter;
import com.wdy.module.entity.*;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.*;
import com.wdy.module.utils.SettingUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
public class TagAndRouterUtil {

    public static List<TagsAndRouter> splitTagsByRouter(List<Tag> tags) {
        ArrayList<TagsAndRouter> tagsAndRouters = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getForbidState() == 0 || tag.getRouter() == null) continue;
            TagsAndRouter tagsAndRouter = new TagsAndRouter(tag.getRouter());
            if (tagsAndRouters.contains(tagsAndRouter)) {
                tagsAndRouter = getTagsAndRouter(tagsAndRouters, tagsAndRouter.getRouter().getId());
                tagsAndRouter.addTag(tag);
            } else {
                tagsAndRouter.addTag(tag);
                tagsAndRouters.add(tagsAndRouter);
            }
        }
        return tagsAndRouters;
    }

    public static TagsAndRouter getTagsAndRouter(ArrayList<TagsAndRouter> tagsAndRouters, Long routerId) {
        for (int i = 0; i < tagsAndRouters.size(); i++)
            if (tagsAndRouters.get(i).getRouter().getId() == routerId)
                return tagsAndRouters.get(i);
        return null;
    }

    public static synchronized String judgeResultAndSettingTag(String result, String contentType, long begin, Tag tag) {
        TagService tagService = ((TagService) SpringContextUtil.getBean("TagService"));
        log.info(tag.toString() + contentType + result);
        if ("成功".equals(result)) {
            Tag newTag = SettingUtil.settingTag(tag, begin);
            if (CommandConstant.needToSynchronize.contains(contentType)) {
                log.info(tag.toString() + "此命令需要同步" + result);
                synchronizeEntityToTag(tag, contentType);
            }
            tagService.saveOne(newTag);
        } else {
            // 变价超时
            tag.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            tag.setCompleteTime(null);
            tag.setExecTime(null);
            tagService.saveOne(tag);
        }
        return result;
    }

    public static String judgeResultAndSettingTagWaitUpdate(String result, long begin, Tag tag) {
        TagService tagService = ((TagService) SpringContextUtil.getBean("TagService"));
        log.info(tag.toString() + "改价" + result);
        if ("成功".equals(result)) {
            Tag newTag = SettingUtil.settingTag(tag, begin);
            newTag.setWaitUpdate(1);
            tagService.saveOne(newTag);
            updateGoodWaitUpdate(tag);
        } else {
            // 变价超时
            tag.setWaitUpdate(0);
            tag.setExecTime(null);
            tag.setCompleteTime(null);
            tagService.saveOne(tag);
        }
        return result;
    }

    public static String judgeResultAndSettingRouter(String result, long begin, Router router, byte[] message, String... contentType) {
        RouterService routerService = ((RouterService) SpringContextUtil.getBean("RouterService"));
        log.info(router.toString() + contentType + "命令" + result);
        if (message[8] == 5 && message[9] == 2) {
            return result;
        } else if ("成功".equals(result)) {
            if (contentType != null && CommandConstant.needToSynchronize.contains(contentType)) {
                log.info(router.toString() + "此命令需要同步" + result);
                synchronizeEntityToRouter(router, contentType[0]);
            }
            Router newRouter = SettingUtil.settintRouter(router, begin);
            routerService.saveOne(newRouter);
        } else {
            router.setCompleteTime(null);
            router.setExecTime(null);
            routerService.saveOne(router);
        }
        return result;
    }

    public static List<Tag> getTagsByRouters(List<Router> routers) {
        TagService tagService = (TagService) SpringContextUtil.getBean("TagService");
        List<Tag> result = new ArrayList<>();
        for (Router r : routers) {
            List<Tag> itemTags = tagService.findByRouterId(r.getId());
            result.addAll(itemTags);
        }
        return result;
    }

    public static void setTagIsNotWorking(List<Tag> tags) {
        TagService tagService = (TagService) SpringContextUtil.getBean("TagService");
        tags.forEach(item -> {
            item.setIsWorking((byte) 0);
            tagService.saveOne(item);
        });
    }

    public static void setRouterIsNotWorking(List<Router> routers) {
        RouterService routerService = (RouterService) SpringContextUtil.getBean("RouterService");
        for (Router r : routers) {
            r.setIsWorking((byte) 0);
            routerService.saveOne(r);
        }
    }

    public static boolean judgeTagMatchStyle(Tag tag, Style style) {
        String resolutionWidth = tag.getResolutionWidth();
        String styleNumber = style.getStyleNumber();
        if (resolutionWidth == null || styleNumber == null)
            return false;
        if (styleNumber.substring(0, 2).equals("21") && style.getStyleType().contains("黑白")) {
            if (resolutionWidth.substring(0, 2).equals("25"))
                return true;
            else
                return false;
        }
        if (styleNumber.substring(0, 2).equals("42") && resolutionWidth.substring(0, 2).equals("40")) {
            return true;
        }
        if (resolutionWidth.substring(0, 2).equals(styleNumber.substring(0, 2)))
            return true;
        return false;
    }

    public static void setBaseTagStyle(List<Tag> tags) {
        TagService tagService = (TagService) SpringContextUtil.getBean("TagService");
        for (Tag tag : tags) {
            Style style = getBaseStyleByTag(tag);
            if (style != null) {
                tag.setStyle(style);
                tagService.saveOne(tag);
            }
        }
    }

    private static Style getBaseStyleByTag(Tag tag) {
        StyleService styleService = (StyleService) SpringContextUtil.getBean("StyleService");
        Style style;
        switch (tag.getResolutionWidth()) {
            case "400":
                style = styleService.findByStyleNumberAndIsPromote("4201", (byte) 0);
                break;
            case "296":
                style = styleService.findByStyleNumberAndIsPromote("2901", (byte) 0);
                break;
            default:
                style = styleService.findByStyleNumberAndIsPromote("2101", (byte) 0);
                break;
        }
        return style;
    }

    private static void updateGoodWaitUpdate(Tag tag) {
        // 更新商品
        boolean flag = true;
        if (tag.getGood() != null) {
            TagService tagService = ((TagService) SpringContextUtil.getBean("TagService"));
            List<Tag> tags = tagService.findByGoodId(tag.getGood().getId());
            for (Tag label : tags) {
                if (label.getWaitUpdate() == 0) {
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            GoodService goodService = ((GoodService) SpringContextUtil.getBean("GoodService"));
            Good good = tag.getGood();
            good.setWaitUpdate(1);
            good.setRegionNames(null);
            goodService.save(good);
            log.info(tag.toString() + "更新对象商品状态");
        }
    }

    public static void synchronizeEntityToTag(Tag tag, String contenType) {
        TagService tagService = (TagService) SpringContextUtil.getBean("TagService");
        switch (contenType) {
            case CommandConstant.TAGREMOVE:
                tagService.saveOne(forbiddenTag(tag));
                break;
        }
    }

    public static void synchronizeEntityToRouter(Router router, String contenType) {
        RouterService routerService = (RouterService) SpringContextUtil.getBean("RouterService");
        switch (contenType) {
            case CommandConstant.ROUTERREMOVE:
                routerService.saveOne(forbiddenRouter(router));
                break;
        }
    }

    public static Tag forbiddenTag(Tag tag) {
        tag.setCompleteTime(null);
        tag.setExecTime(null);
        tag.setIsWorking((byte) 0);
        tag.setForbidState(0);
        tag.setWaitUpdate(1);
        tag.setState((byte) 0);
        tag.setGood(null);
        return tag;
    }

    public static Router forbiddenRouter(Router router) {
        router.setState((byte) 0);
        router.setExecTime(null);
        router.setCompleteTime(null);
        router.setIsWorking((byte) 0);
        return router;
    }
}
