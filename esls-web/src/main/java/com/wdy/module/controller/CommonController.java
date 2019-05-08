package com.wdy.module.controller;

import com.wdy.module.aop.Log;
import com.wdy.module.common.constant.SqlConstant;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.dao.SystemVersionDao;
import com.wdy.module.entity.SystemVersion;
import com.wdy.module.service.BaseDao;
import com.wdy.module.serviceUtil.PoiUtil;
import com.wdy.module.system.SystemVersionArgs;
import io.swagger.annotations.*;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.*;

@Api(description = "通用工具类")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Validated
public class CommonController {
    @Autowired
    private BaseDao baseDao;
    @Autowired
    private SystemVersionDao systemVersionDao;
    @Autowired
    private SystemVersionArgs systemVersionArgs;

    @ApiOperation("获取数据库表信息（0）或获取数据表的所有字段（表名,1）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0获取所有数据库表 1获取相应表的所有字段信息", dataType = "int", paramType = "query")
    })
    @GetMapping("/common/database")
    @RequiresPermissions("获取数据表信息")
    public ResponseEntity<ResultBean> getTableColumn(@RequestParam(required = false) @ApiParam("表名") String tableName, @RequestParam Integer mode) {
        String sql = SqlConstant.QUERY_ALL_TABLE;
        if (mode == 1)
            sql = SqlConstant.QUERY_TABLIE_COLUMN + "\'" + tableName + "\'";
        List results = baseDao.findBySql(sql);
        List<HashMap> mapToList = new ArrayList<>();
        for(Object str : results){
            HashMap<Object,Object> resultMap = new HashMap<>();
            resultMap.put("keyName",str);
            mapToList.add(resultMap);
        }
        return new ResponseEntity<>(ResultBean.success(mapToList), HttpStatus.OK);
    }

    @ApiOperation(value = "导出指定条件数据库excel报表(连接符可取 =  或  like),mode为1则导出全部数据表")
    @GetMapping("/common/database/exportExcelDataFile")
    @RequiresPermissions("导出数据库表")
    public ResponseEntity<ResultBean> getExcelByTableName(@RequestParam(required = false) String tableName, @RequestParam(required = false) String query, @RequestParam(required = false) String connection, @RequestParam(required = false) String queryString, @RequestParam Integer mode, HttpServletRequest request, HttpServletResponse response) {
        HSSFWorkbook hssfWorkbook;
        if(mode==0) {
            List dataColumnList = baseDao.findBySql(SqlConstant.QUERY_TABLIE_COLUMN + "\'" + tableName + "\'");
            List dataList;
            try {
                if (query == null || query == null || connection == null) {
                    dataList = baseDao.findBySql("select * from " + tableName, Class.forName("com.wdy.module.entity." + SqlConstant.EntityToSqlMap.get(tableName)));
                } else {
                    if (!"=".equals(connection) && !"like".equalsIgnoreCase(connection) && !"is".equalsIgnoreCase(connection))
                        return new ResponseEntity<>(ResultBean.error("connecttion参数出错"), HttpStatus.BAD_REQUEST);
                    if (connection.equalsIgnoreCase("like"))
                        queryString = "%" + queryString + "%";
                    dataList = baseDao.findBySql(SqlConstant.getQuerySql(tableName, query, connection, queryString), Class.forName("com.datagroup.ESLS.entity." + SqlConstant.EntityToSqlMap.get(tableName)));
                }
            } catch (ClassNotFoundException e) {
                return new ResponseEntity<>(ResultBean.error("导出excel出错" + e.toString()), HttpStatus.BAD_REQUEST);
            }
            hssfWorkbook = PoiUtil.exportData2Excel(dataList, dataColumnList, tableName);
            PoiUtil.writeToResponse(hssfWorkbook, request, response, tableName);
        }
        else {
            hssfWorkbook = PoiUtil.exportData2ExcelBatch();
            PoiUtil.writeToResponse(hssfWorkbook, request, response, "总表");
        }
        //以流输出到浏览器
        return new ResponseEntity<>(ResultBean.success("导出excel成功"), HttpStatus.OK);
    }

    @ApiOperation("导出指定条件数据库csv文件(连接符可取 =  或  like)")
    @GetMapping("/common/database/exportCsvDataFile")
    @RequiresPermissions("导出数据库表")
    public ResponseEntity<ResultBean> getCsvByTableName(@RequestParam @ApiParam("数据库表名") String tableName, @RequestParam(required = false) String query, @RequestParam(required = false) String connection, @RequestParam(required = false) String queryString, HttpServletRequest request, HttpServletResponse response) {
        List dataColumnList = baseDao.findBySql(SqlConstant.QUERY_TABLIE_COLUMN + "\'" + tableName + "\'");
        List dataList ;
        try (final OutputStream os = response.getOutputStream()) {
            if(query==null || query==null  || connection==null){
                dataList = baseDao.findBySql("select * from "+ tableName , Class.forName("com.wdy.module.entity." + SqlConstant.EntityToSqlMap.get(tableName)));
            }
            else {
                if (!"=".equals(connection) && !"like".equalsIgnoreCase(connection) && !"is".equalsIgnoreCase(connection))
                    return new ResponseEntity<>(ResultBean.error("connecttion参数出错"), HttpStatus.BAD_REQUEST);
                if (connection.equalsIgnoreCase("like"))
                    queryString = "%" + queryString + "%";
                dataList = baseDao.findBySql(SqlConstant.getQuerySql(tableName, query, connection, queryString), Class.forName("com.wdy.module.entity." + SqlConstant.EntityToSqlMap.get(tableName)));
            }
            PoiUtil.responseSetProperties(tableName, request,response);
            PoiUtil.exportData2Csv(dataList, dataColumnList, os);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultBean.error("导出csv出错"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ResultBean.success("导出csv成功"), HttpStatus.OK);
    }
    @ApiOperation("导入Excel数据库表,mode为1则导入全部数据表")
    @PostMapping("/common/database/importExcelDataFile")
    @RequiresPermissions("导入数据库表")
    public ResponseEntity<ResultBean> importExcelDataFile(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam(required = false) @ApiParam("数据库表名") String tableName, @RequestParam Integer mode)        {
            if (Objects.isNull(file) || file.isEmpty()) {
            return new ResponseEntity<>(ResultBean.error("文件为空，请重新上传"), HttpStatus.NOT_ACCEPTABLE);
        }
        if(mode == 0) {
            PoiUtil.importExcelDataFile(file, tableName);
        }
        else
            PoiUtil.importExcelDataFileBatch(file);
        return new ResponseEntity<>(ResultBean.success("文件上传成功"), HttpStatus.OK);
    }
    @ApiOperation("导入Csv数据库表")
    @PostMapping("/common/database/importCsvDataFile")
    @RequiresPermissions("导入数据库表")
    public ResponseEntity<ResultBean> importCsvDataFile(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam @ApiParam("数据库表名") String tableName) {
        if (Objects.isNull(file) || file.isEmpty()) {
            return new ResponseEntity<>(ResultBean.error("文件为空，请重新上传"), HttpStatus.NOT_ACCEPTABLE);
        }
        List dataColumnList = baseDao.findBySql(SqlConstant.QUERY_TABLIE_COLUMN + "\'" + tableName + "\'");
        try {
            PoiUtil.importCsvDataFile(file.getInputStream(),dataColumnList,tableName,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(ResultBean.success("文件上传成功"), HttpStatus.OK);
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = " 0设置命令等待时间(单位为毫秒) 1设置token存活时间(单位为毫秒) 2设置命令重发次数 3设置命令包的最大长度（不得超过220字节）4设置外网IP 5设置递归深度(对失败的标签重发次数) 6设置调用发送样式接口时间间隔与次数(15000 1代表15秒1次) 7设置基础权限集合名 8设置标签通讯数量", dataType = "int", paramType = "query")
    })
    @ApiOperation("设置系统参数")
    @PutMapping("/common/systemArgs")
    @RequiresPermissions("设置系统参数")
    @Log("设置系统参数")
    public ResponseEntity<ResultBean> setCommandTime(@ApiParam("参数值") @RequestParam String time, @RequestParam Integer mode) {
        SystemVersion systemVersion = null;
        List<SystemVersion> systemVersions = systemVersionDao.findAll();
        if(systemVersions.size()>0)
            systemVersion = systemVersions.get(0);
        switch (mode) {
            case 0:
                systemVersion.setCommandWaitingTime(time);
                break;
            case 1 :
                systemVersion.setTokenAliveTime(time);
                break;
            case 2 :
                systemVersion.setCommandRepeatTime(time);
                break;
            case 3 :
                systemVersion.setPackageLength(time);
                break;

            case 4 :
                systemVersion.setOutNetIp(time);
                break;
            case 5 :
                systemVersion.setRecursionDepth(time);
                break;
            case 6 :
                systemVersion.setTimeGapAndTime(time);
                break;
            case 7 :
                systemVersion.setBasePermissions(time);
                break;
            case 8 :
                systemVersion.setTagsLengthCommand(time);
                break;
            default:
                break;
        }
        systemVersion.setDate(new Timestamp(System.currentTimeMillis()));
        SystemVersion result = systemVersionDao.save(systemVersion);
        systemVersionArgs.init();
        return new ResponseEntity<>(ResultBean.success(result), HttpStatus.OK);
    }
    @ApiOperation("设置系统版本号和开发人员")
    @PutMapping("/common/system")
    @RequiresPermissions("设置命令参数")
    @Log("设置命令参数")
    public ResponseEntity<ResultBean> setSystemArgs(@ApiParam("版本号") @RequestParam String softVersion, @ApiParam("开发人员")String productor) throws IOException {
        SystemVersion systemVersion = systemVersionDao.findById((long) 1).get();
        systemVersion.setSoftVersion(softVersion);
        systemVersion.setProductor(productor);
        systemVersion.setDate(new Timestamp(System.currentTimeMillis()));
        SystemVersion result = systemVersionDao.save(systemVersion);
        systemVersionArgs.init();
        return new ResponseEntity<>(ResultBean.success(result), HttpStatus.OK);
    }
    @ApiOperation("获得系统参数")
    @GetMapping("/common/systemArgs")
    @RequiresPermissions("获得系统参数")
    public ResponseEntity<ResultBean> getSystemArgs() {
        List<SystemVersion> results = systemVersionDao.findAll();
        return new ResponseEntity<>(ResultBean.success(results), HttpStatus.OK);
    }
}
