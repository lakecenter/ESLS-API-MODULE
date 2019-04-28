package com.wdy.module.serviceUtil;

import com.github.crab2died.ExcelUtils;
import com.github.crab2died.sheet.wrapper.NoTemplateSheetWrapper;
import com.wdy.module.common.constant.SqlConstant;
import com.wdy.module.service.BaseDao;
import com.wdy.module.utils.ReflectUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @program: esls-parent
 * @description: Excel导入导出工具类
 * @author: dongyang_wu
 * @create: 2019-04-27 16:06
 */
public class ExcelUtil {
    public static void excelExport(String tableNames, String resultName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<NoTemplateSheetWrapper> sheets = new ArrayList<>();
        List<String> names = Arrays.asList(StringUtils.delimitedListToStringArray(tableNames, ","));
        if (StringUtils.isEmpty(tableNames)) {
            names = reflectFindBySqlMethod();
        }
        for (int i = 0; i < names.size(); i++) {
            if (SqlConstant.needIgnoreEntity.contains(names.get(i))) {
                continue;
            }
            String entityName = SqlConstant.EntityToSqlMap.get(names.get(i));
            List list = reflectFindAllMethod(entityName);
            if (SqlConstant.needVoEntity.contains(names.get(i))) {
                List resultList = CopyUtil.copyEntity(list, entityName);
                Class clazz = Class.forName("com.wdy.module.dto." + entityName + "Vo");
                sheets.add(new NoTemplateSheetWrapper(resultList, clazz, true, names.get(i)));
            } else {
                Class clazz = Class.forName("com.wdy.module.entity." + entityName);
                sheets.add(new NoTemplateSheetWrapper(list, clazz, true, names.get(i)));
            }
        }
        //ExcelUtils.getInstance().noTemplateSheet2Excel(sheets, "E://EE.xlsx");
        PoiUtil.writeToResponse(null, request, response, resultName);
        ExcelUtils.getInstance().noTemplateSheet2Excel(sheets, response.getOutputStream());
    }

    public static void csvExport(String tableName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String entityName = SqlConstant.EntityToSqlMap.get(tableName);
        List list = reflectFindAllMethod(entityName);
        Class clazz;
        if (SqlConstant.needVoEntity.contains(tableName)) {
            clazz = Class.forName("com.wdy.module.dto." + entityName + "Vo");
        } else {
            clazz = Class.forName("com.wdy.module.entity." + entityName);
        }
        PoiUtil.responseSetProperties(tableName, request, response);
        ExcelUtils.getInstance().exportObjects2CSV(list, clazz, response.getOutputStream());
    }

    public static void excelImport(String tableNames, MultipartFile file, Integer update) throws Exception {
        List<String> names = Arrays.asList(StringUtils.delimitedListToStringArray(tableNames, ","));
        if (StringUtils.isEmpty(tableNames)) {
            List<String> resultNames = new ArrayList<>();
            names = reflectFindBySqlMethod();
            for (String name : names) {
                if (SqlConstant.needIgnoreEntity.contains(name))
                    continue;
                resultNames.add(name);
            }
            names = resultNames;
        }
        for (int i = 0; i < names.size(); i++) {
            String entityName = SqlConstant.EntityToSqlMap.get(names.get(i));
            Class clazz;
            List list;
            if (SqlConstant.needVoEntity.contains(names.get(i))) {
                clazz = Class.forName("com.wdy.module.dto." + entityName + "Vo");
                list = ExcelUtils.getInstance().readExcel2Objects(file.getInputStream(), clazz, i);
            } else {
                clazz = Class.forName("com.wdy.module.entity." + entityName);
                list = ExcelUtils.getInstance().readExcel2Objects(file.getInputStream(), clazz, i);
            }
            for (Object st : list) {
                CopyUtil.copyVo(Arrays.asList(st), entityName, update, true);
            }
        }
    }

    public static void csvImport(String tableName, MultipartFile file, Integer update) throws Exception {
        String entityName = SqlConstant.EntityToSqlMap.get(tableName);
        Class clazz;
        if (SqlConstant.needVoEntity.contains(tableName)) {
            clazz = Class.forName("com.wdy.module.dto." + entityName + "Vo");
        } else {
            clazz = Class.forName("com.wdy.module.entity." + entityName);
        }
        List list = ExcelUtils.getInstance().readCSV2Objects(file.getInputStream(), clazz);
        for (Object st : list) {
            CopyUtil.copyVo(Arrays.asList(st), entityName, update, true);
        }
    }

    private static List reflectFindAllMethod(String entityName) throws Exception {
        String serviceName = entityName + "Service";
        Object serviceObj = SpringContextUtil.getBean(serviceName);
        Method findAll = serviceObj.getClass().getMethod("findAll");
        List list = (List) findAll.invoke(serviceObj);
        return list;
    }

    private static void reflectSaveOneMethod(String entityName, List list) throws Exception {
        Object serviceObj = SpringContextUtil.getBean(entityName + "Service");
        Method saveOneMethod = serviceObj.getClass().getMethod("saveOne", list.get(0).getClass());
        saveOneMethod.invoke(serviceObj, list.get(0));
    }

    private static List reflectFindBySqlMethod() {
        List<String> names;
        String sql = SqlConstant.QUERY_ALL_TABLE;
        BaseDao baseDao = (BaseDao) SpringContextUtil.getBean("BaseDao");
        names = baseDao.findBySql(sql);
        return names;
    }

    public static Object reflectFindById(Object item, String entityName) throws Exception {
        String serviceName = entityName + "Service";
        Object serviceObj = SpringContextUtil.getBean(serviceName);
        Method findById;
        findById = serviceObj.getClass().getMethod("findById", Long.class);
        return findById.invoke(serviceObj, Long.valueOf(ReflectUtil.getSourceData("id", item)));
    }
}