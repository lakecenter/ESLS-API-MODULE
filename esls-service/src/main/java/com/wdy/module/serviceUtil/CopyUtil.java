package com.wdy.module.serviceUtil;

import com.wdy.module.common.constant.SqlConstant;
import com.wdy.module.dto.*;
import com.wdy.module.entity.*;
import com.wdy.module.service.ShopService;
import com.wdy.module.service.UserService;
import com.wdy.module.utils.ReflectUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.util.*;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.*;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.o;

public class CopyUtil {

    public static List<TagVo> copyTag(List<Tag> content) {
        List<TagVo> resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    TagVo tagVo = new TagVo();
                    BeanUtils.copyProperties(item, tagVo);
                    tagVo.setGoodId(item.getGood() != null ? item.getGood().getId() : 0);
                    tagVo.setRouterId(item.getRouter() != null ? item.getRouter().getId() : 0);
                    tagVo.setStyleId(item.getStyle() != null ? item.getStyle().getId() : 0);
                    resultList.add(tagVo);
                }
        );
        return resultList;
    }

    public static List<GoodVo> copyGood(List<Good> content) {
        List<GoodVo> resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    boolean flag = true;
                    if (item == null) flag = false;
                    if (flag) {
                        GoodVo goodVo = new GoodVo();
                        BeanUtils.copyProperties(item, goodVo);
                        List<Long> tagIdList = goodVo.getTagIdList();
                        Collection<Tag> tags = item.getTags();
                        tags.forEach(itemTag -> {
                            if (itemTag != null)
                                tagIdList.add(itemTag.getId());
                        });
                        resultList.add(goodVo);
                    }
                }
        );
        return resultList;
    }

    public static List<StyleVo> copyStyle(List<Style> content) {
        List<StyleVo> resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    boolean flag = true;
                    if (item == null) flag = false;
                    if (flag) {
                        StyleVo styleVo = new StyleVo();
                        BeanUtils.copyProperties(item, styleVo);
                        List<Long> tagIdList = styleVo.getTagIdList();
                        Collection<Tag> tags = item.getTags();
                        tags.forEach(itemTag -> {
                            if (itemTag != null)
                                tagIdList.add(itemTag.getId());
                        });
                        resultList.add(styleVo);
                    }
                }
        );
        return resultList;
    }

    public static List<DispmsVo> copyDispms(List<Dispms> content) {
        List<DispmsVo> resultList = new ArrayList<>();
        content.forEach(item -> {
            boolean flag = true;
            if (item == null) flag = false;
            if (flag) {
                DispmsVo dispmsVo = new DispmsVo();
                BeanUtils.copyProperties(item, dispmsVo);
                resultList.add(dispmsVo);
            }
        });
        return resultList;
    }

    public static List<RouterVo> copyRouter(List<Router> content) {
        List<RouterVo> resultList = new ArrayList<>();
        content.forEach(item -> {
            boolean flag = true;
            if (item == null) flag = false;
            if (flag) {
                RouterVo routerVo = new RouterVo();
                BeanUtils.copyProperties(item, routerVo);
                if (item.getShop() != null) {
                    BeanUtils.copyProperties(item.getShop(), routerVo);
                    routerVo.setId(item.getId());
                    routerVo.setShopId(item.getShop().getId());
                } else
                    routerVo.setShopId(0);
                resultList.add(routerVo);
            }
        });
        return resultList;
    }

    public static List<UserVo> copyUser(List<User> content) {
        List<UserVo> resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(item, userVo);
                    userVo.setShopId(item.getShop() != null ? item.getShop().getId() : 0);
                    List<Role> roleList = item.getRoleList();
                    StringBuffer sb = new StringBuffer();
                    for (Role role : roleList) {
                        sb.append(role.getId() + " ");
                    }
                    userVo.setRoleList(sb.toString());
                    resultList.add(userVo);
                }
        );
        return resultList;
    }

    public static List<User> copyUserVo(List<UserVo> content, Boolean update) {
        List<User> resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    User user = new User();
                    if (update) {
                        UserService userService = (UserService) SpringContextUtil.getBean("UserService");
                        user = userService.findById(item.getId());
                    }
                    BeanUtils.copyProperties(item, user);
                    Shop shop = new Shop();
                    if (item.getShopId() != 0)
                        shop.setId(item.getShopId());
                    user.setShop(shop);
                    resultList.add(user);
                }
        );
        return resultList;
    }

    public static List copyEntity(List content, String entityName) {
        List resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    try {
                        Class clazz = Class.forName("com.wdy.module.dto." + entityName + "Vo");
                        Object o = clazz.newInstance();
                        BeanUtils.copyProperties(item, o);
                        List<String> filedType = ReflectUtil.getFiledType(item);
                        for (String type : filedType) {
                            if (type.contains("List") || type.contains("list"))
                                continue;
                            if (type.contains("entity")) {
                                String fieldName = StringUtil.toLowerCaseFirstOne(type.substring(type.lastIndexOf(".") + 1));
                                String sourceData = ReflectUtil.getSourceData(fieldName, item);
                                if (sourceData != null) {
                                    String id = sourceData.substring(sourceData.indexOf("id") + 3, sourceData.indexOf(","));
                                    ReflectUtil.setFiledAttrValue(o, fieldName + "Id", Long.valueOf(id));
                                }
                            }
                        }
                        resultList.add(o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        return resultList;
    }

    public static List copyVo(List content, String entityName, Integer update, Boolean saveOne) {
        List resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    try {
                        Object o;
                        if (update == 1) {
                            String serviceName = entityName + "Service";
                            Object serviceObj = SpringContextUtil.getBean(serviceName);
                            if (NONEEDOPTIONALENTITY.contains(entityName)) {
                                Method findById = serviceObj.getClass().getMethod("findById", Long.class);
                                o = findById.invoke(serviceObj, Long.valueOf(ReflectUtil.getSourceData("id", item)));
                            } else {
                                Method findById = serviceObj.getClass().getMethod("findById", Long.class);
                                Object oo = findById.invoke(serviceObj, Long.valueOf(ReflectUtil.getSourceData("id", item)));
                                Method get = oo.getClass().getMethod("get");
                                o = get.invoke(oo);
                            }
                        } else {
                            Class clazz = Class.forName("com.wdy.module.entity." + entityName);
                            o = clazz.newInstance();
                        }
                        BeanUtils.copyProperties(item, o);
                        String[] filedName = ReflectUtil.getFiledName(item);
                        for (String propertyName : filedName) {
                            if (NEEDIGNOREPROPERTY.contains(propertyName)) {
                                String id = ReflectUtil.getSourceData(propertyName, item);
                                if (!"channelId".equals(propertyName))
                                    ReflectUtil.setFiledAttrValue(o, propertyName, Long.valueOf(id));
                                else
                                    ReflectUtil.setFiledAttrValue(o, propertyName, id);
                                continue;
                            }
                            if (propertyName.contains("shopId")) {
                                ShopService shopService = (ShopService) SpringContextUtil.getBean("ShopService");
                                String id = ReflectUtil.getSourceData(propertyName, item);
                                Shop shop = shopService.findById(Long.valueOf(id)).get();
                                ReflectUtil.setFiledAttrValue(o, "shop", shop);
                            } else if (propertyName.contains("Id")) {
                                String propertyTargetName = propertyName.substring(0, propertyName.length() - 2);
                                Class clazz = Class.forName("com.wdy.module.entity." + StringUtil.captureName(propertyTargetName));
                                Object innerTarget = clazz.newInstance();
                                String id = ReflectUtil.getSourceData(propertyName, item);
                                if (!StringUtils.isEmpty(id)) {
                                    ReflectUtil.setFiledAttrValue(innerTarget, "id", Long.valueOf(id));
                                    ReflectUtil.setFiledAttrValue(o, propertyTargetName, innerTarget);
                                }
                            }
                        }
                        if (saveOne) {
                            if ("Good".equals(entityName)) {
                                Object serviceObj = SpringContextUtil.getBean(entityName + "Service");
                                Method saveOneMethod = serviceObj.getClass().getMethod("saveOne", o.getClass(), Integer.class);
                                saveOneMethod.invoke(serviceObj, o, 1);
                            } else {
                                Object serviceObj = SpringContextUtil.getBean(entityName + "Service");
                                Method saveOneMethod = serviceObj.getClass().getMethod("saveOne", o.getClass());
                                saveOneMethod.invoke(serviceObj, o);
                            }
                        }
                        resultList.add(o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        return resultList;
    }

    public static List<ShopVo> copShop(List<Shop> content) {
        List<ShopVo> resultList = new ArrayList<>();
        content.forEach(
                item -> {
                    ShopVo shopVo = new ShopVo();
                    BeanUtils.copyProperties(item, shopVo);
                }
        );
        return resultList;
    }

    private static String NEEDIGNOREPROPERTY = "openId smsId channelId userId permissionId roleId regionId";
    private static String NONEEDOPTIONALENTITY = "Balance Good User CycleJob";

}