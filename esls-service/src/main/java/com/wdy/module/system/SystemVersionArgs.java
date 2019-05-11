package com.wdy.module.system;

import com.wdy.module.cycleJob.DynamicTask;
import com.wdy.module.dao.*;
import com.wdy.module.entity.*;
import com.wdy.module.license.LicenseCheckListener;
import com.wdy.module.service.RouterService;
import com.wdy.module.serviceUtil.*;
import io.netty.channel.Channel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;

@Component("SystemVersionArgs")
@Data
public class SystemVersionArgs {
    @Autowired
    private SystemVersionDao systemVersionDao;
    @Autowired
    private CycleJobDao cycleJobDao;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserAndRoleDao userAndRoleDao;
    @Autowired
    private RoleAndPermissionDao roleAndPermissionDao;
    @Autowired
    private RouterService routerService;
    public static String softVersion;
    public static String productor;
    public static Timestamp date;

    public static String tokenAliveTime;
    public static String packageLength;
    public static String commandRepeatTime;
    public static String commandWaitingTime;
    public static String outNetIp;
    public static String recursionDepth;
    public static String timeGapAndTime;
    public static String basePermissions;
    public static String tagsLengthCommand;
    public static String goodDataFormat;

    public void init() {
        try {
            LicenseCheckListener licenseCheckListener = (LicenseCheckListener) SpringContextUtil.getBean("LicenseCheckListener");
            licenseCheckListener.installLicense();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            initCycleJob();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            initSystemArgs();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            initPermissions();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            initBaseRole();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            initManagerRole();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            initManagerUser();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            initRouter();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void initCycleJob() {
        // 加载默认定时任务
        CycleJob baseGoodJob = cycleJobDao.findByMode(-1);
        if (baseGoodJob == null) {
            CycleJob cycleJob = new CycleJob();
            cycleJob.setDescription("扫描商品基本数据");
            cycleJob.setId((long) 1);
            cycleJob.setMode(-1);
            cycleJob.setType(7);
            cycleJob.setCron("0 1/30 * * * ?");
            cycleJob.setArgs("/root/goodsData.csv");
            cycleJobDao.save(cycleJob);
        }
        CycleJob updateGoodJob = cycleJobDao.findByMode(-2);
        if (updateGoodJob == null) {
            CycleJob cycleJob = new CycleJob();
            cycleJob.setId((long) 2);
            cycleJob.setDescription("扫描商品变价数据");
            cycleJob.setMode(-2);
            cycleJob.setType(8);
            cycleJob.setCron("0 1/30 * * * ?");
            cycleJob.setArgs("/root/goodsData.csv");
            cycleJobDao.save(cycleJob);
        }

        // 加载数据库定时任务
        DynamicTask dynamicTask = (DynamicTask) SpringContextUtil.getBean("DynamicTask");
        dynamicTask.init();
    }

    public void initSystemArgs() {
        // 加载默认系统参数
        List<SystemVersion> systemVersions = systemVersionDao.findAll();
        if (systemVersions.size() > 0) {
            SystemVersion systemVersion = systemVersions.get(0);
            SystemVersionArgs.softVersion = systemVersion.getSoftVersion();
            SystemVersionArgs.productor = systemVersion.getProductor();
            SystemVersionArgs.date = systemVersion.getDate();
            SystemVersionArgs.tokenAliveTime = systemVersion.getTokenAliveTime();
            SystemVersionArgs.commandRepeatTime = systemVersion.getCommandRepeatTime();
            SystemVersionArgs.packageLength = systemVersion.getPackageLength();
            SystemVersionArgs.commandWaitingTime = systemVersion.getCommandWaitingTime();
            SystemVersionArgs.outNetIp = systemVersion.getOutNetIp();
            SystemVersionArgs.recursionDepth = systemVersion.getRecursionDepth();
            SystemVersionArgs.timeGapAndTime = systemVersion.getTimeGapAndTime();
            SystemVersionArgs.basePermissions = systemVersion.getBasePermissions();
            SystemVersionArgs.tagsLengthCommand = systemVersion.getTagsLengthCommand();
            SystemVersionArgs.goodDataFormat = systemVersion.getGoodDataFormat();
        } else {
            String softVersion = "V1.00";
            String productor = "数据库组";
            Timestamp date = new Timestamp(System.currentTimeMillis());
            String tokenAliveTime = "30000";
            String commandRepeatTime = "3";
            String packageLength = "220";
            String commandWaitingTime = "5000";
            String outNetIp = "39.108.106.167";
            String recursionDepth = "2";
            String timeGapAndTime = "15000 1";
            String tagsLengthCommand = "1000";
            StringBuffer basePermissions = new StringBuffer();
            basePermissions.append("标签闪灯 添加或修改信息 标签商品绑定 系统菜单 设置通讯命令时间参数 设置通讯命令时间参数 导入数据库表 导出数据库表 获取数据表信息 获取指定ID的信息 删除指定ID的信息 查询和搜索功能");
            String goodDataFormat = "id name barCode qrCode price promotePrice provider operator category origin spec stock unit shelfNumber promoteTimeGap promotionReason isPromote rfu01 rfu02 rfus01 rfus02";
            SystemVersion newSystemVersion = new SystemVersion();
            newSystemVersion.setId((long) 1);
            newSystemVersion.setSoftVersion(softVersion);
            newSystemVersion.setProductor(productor);
            newSystemVersion.setDate(date);
            newSystemVersion.setTokenAliveTime(tokenAliveTime);
            newSystemVersion.setCommandRepeatTime(commandRepeatTime);
            newSystemVersion.setPackageLength(packageLength);
            newSystemVersion.setCommandWaitingTime(commandWaitingTime);
            newSystemVersion.setOutNetIp(outNetIp);
            newSystemVersion.setRecursionDepth(recursionDepth);
            newSystemVersion.setTimeGapAndTime(timeGapAndTime);
            newSystemVersion.setBasePermissions(basePermissions.toString());
            newSystemVersion.setTagsLengthCommand(tagsLengthCommand);
            newSystemVersion.setGoodDataFormat(goodDataFormat);
            SystemVersionArgs.softVersion = softVersion;
            SystemVersionArgs.productor = productor;
            SystemVersionArgs.date = date;
            SystemVersionArgs.tokenAliveTime = tokenAliveTime;
            SystemVersionArgs.commandRepeatTime = commandRepeatTime;
            SystemVersionArgs.packageLength = packageLength;
            SystemVersionArgs.commandWaitingTime = commandWaitingTime;
            SystemVersionArgs.outNetIp = outNetIp;
            SystemVersionArgs.recursionDepth = recursionDepth;
            SystemVersionArgs.basePermissions = basePermissions.toString();
            SystemVersionArgs.tagsLengthCommand = tagsLengthCommand;
            SystemVersionArgs.goodDataFormat = goodDataFormat;
            systemVersionDao.save(newSystemVersion);
        }
    }

    public void initPermissions() throws IOException {
        // 加载默认权限
        File file = ResourceUtils.getFile("classpath:data/permission.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
        PoiUtil.importExcelDataFile(multipartFile, "permission");
    }

    public void initBaseRole() {
        // 加载基础角色
        Role role = roleDao.findByType("基础权限");
        Long roleId;
        if (role == null) {
            Role baseRole = new Role();
            baseRole.setType("基础权限");
            baseRole.setName("基本用户");
            baseRole.setId((long) 2);
            roleDao.saveAndFlush(baseRole);
            roleId = baseRole.getId();
        } else {
            roleId = role.getId();
        }
        // 加载基础角色权限
        String[] basePermission = basePermissions.split(" ");
        for (String permissionName : basePermission) {
            try {
                Permission permission = permissionDao.findByName(permissionName);
                RolePermission rolePermission = new RolePermission(permission.getId(), roleId);
                roleAndPermissionDao.save(rolePermission);
            } catch (Exception e) {
            }
        }
    }

    public void initManagerRole() {
        // 加载管理员角色
        Role role = roleDao.findByType("最高权限");
        Long roleId;
        if (role == null) {
            Role managerRole = new Role();
            managerRole.setType("最高权限");
            managerRole.setName("管理员");
            managerRole.setId((long) 1);
            roleDao.save(managerRole);
            roleId = managerRole.getId();
        } else {
            roleId = role.getId();
        }
        // 加载基础角色权限
        List<Permission> permissions = permissionDao.findAll();
        for (Permission permission : permissions) {
            try {
                RolePermission rolePermission = new RolePermission(permission.getId(), roleId);
                roleAndPermissionDao.save(rolePermission);
            } catch (Exception e) {
            }
        }
    }

    public void initManagerUser() {
        User admin = userDao.findByName("ESLS");
        User save = admin;
        if (admin == null) {
            User user = new User();
            user.setId((long) 1);
            user.setName("ESLS");
            user.setRawPasswd("123456");
            user.setPasswd("833c1e098a53575033c5e7a97875b9f5");
            user.setStatus((byte) 1);
            user.setActivateStatus((byte) 1);
            user.setCreateTime(new Timestamp(System.currentTimeMillis()));
            user.setDepartment("创协");
            user.setMail("13058142866@163.com");
            user.setTelephone("17722828134");
            user.setAddress("深圳大学");
            save = userDao.save(user);
        }
        Role managerRole = roleDao.findByType("最高权限");
        UserRole userRole = new UserRole(managerRole.getId(), save.getId());
        userAndRoleDao.save(userRole);
    }

    public void initRouter() {
        List<Router> routers = routerService.findAll();
        for (Router r : routers) {
            Channel channel = SocketChannelHelper.getChannelByRouter(r);
            if (channel == null) {
                r.setIsWorking((byte) 0);
                routerService.saveOne(r);
            }
        }
    }
}
