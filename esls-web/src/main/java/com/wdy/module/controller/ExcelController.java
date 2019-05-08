package com.wdy.module.controller;

import com.wdy.module.aop.Log;
import com.wdy.module.common.constant.PublicResultConstant;
import com.wdy.module.config.ResponseHelper;
import com.wdy.module.config.ResponseModel;
import com.wdy.module.serviceUtil.*;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: esls-parent
 * @description: Excel导入导出控制器
 * @author: dongyang_wu
 * @create: 2019-04-26 20:10
 */
@RestController
@RequestMapping("/excel")
@Api(description = "excel导入导出模块")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExcelController {

    @ApiOperation(value = "Excel导出")
    @GetMapping(value = "/excelExport")
    @Log("Excel导出")
    public ResponseModel excelExport(@RequestParam(required = false) @ApiParam("数据表表名集合按照sheet顺序以小写逗号隔开") String tableNames, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        System.out.println(iSmsVerifyService.findAll());
        ExcelUtil.excelExport(tableNames, "数据表", request, response);
        return ResponseHelper.buildResponseModel(PublicResultConstant.SUCCEED);
    }

    @ApiOperation(value = "Esv导出(仅支持单表)")
    @GetMapping(value = "/csvExport")
    public ResponseModel csvExport(@RequestParam(required = false) @ApiParam("表名") String tableName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExcelUtil.csvExport(tableName, request, response);
        return ResponseHelper.buildResponseModel(PublicResultConstant.SUCCEED);
    }

    @ApiOperation(value = "Excel导入")
    @PostMapping(value = "/excelImport")
    @Log("Excel导入")
    public ResponseModel excelImport(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam(required = false) @ApiParam("数据表表名集合按照sheet顺序以小写逗号隔开") String tableNames) throws Exception {
//        List<List<String>> lists = ExcelUtils.getInstance().readExcel2List(file.getInputStream(), 1, 2, 0);
        ExcelUtil.excelImport(tableNames, file, 0);
        return ResponseHelper.buildResponseModel(PublicResultConstant.SUCCEED);
    }

    @ApiOperation(value = "csv导入(仅支持单表)")
    @PostMapping(value = "/csvImport")
    public ResponseModel csvImport(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam(required = false) @ApiParam("表名") String tableName, @RequestParam @ApiParam("0添加导入1更新导入") Integer update) throws Exception {
//        List<List<String>> lists = ExcelUtils.getInstance().readExcel2List(file.getInputStream(), 1, 2, 0);
        ExcelUtil.csvImport(tableName, file, update);
        return ResponseHelper.buildResponseModel(PublicResultConstant.SUCCEED);
    }
}