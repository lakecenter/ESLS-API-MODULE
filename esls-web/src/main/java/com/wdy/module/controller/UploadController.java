package com.wdy.module.controller;

import com.wdy.module.common.constant.FileConstant;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.ResultBean;
import com.wdy.module.entity.*;
import com.wdy.module.service.*;
import com.wdy.module.serviceUtil.COSUtil;
import com.wdy.module.serviceUtil.RequestBeanUtil;
import com.wdy.module.utils.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@Api(description = "文件上传API")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class UploadController {
    @Value("${project.profile}")
    private String UPLOAD_FOLDER;
    @Autowired
    private GoodService goodService;
    @Autowired
    private DispmsService dispmsService;
    @Autowired
    private CycleJobService cycleJobService;
    @ApiOperation("上传单个文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "-1为商品基本信息 -2商品变价信息 0为商品特征图片 1为样式特征图片", dataType = "mode", paramType = "query", required = true)
    })
    @PostMapping("/uploadFile")
    @RequiresPermissions("上传单个文件")
    public ResponseEntity<ResultBean> singleFileUpload(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile file, @RequestParam Integer mode, @RequestParam(required = false) String query, @RequestParam(required = false)  String queryString ) {
        if (Objects.isNull(file) || file.isEmpty()) {
            log.error("文件为空");
            return new ResponseEntity<>(ResultBean.error("文件为空，请重新上传"), HttpStatus.NOT_ACCEPTABLE);
        }
        RequestBean requestBean = new RequestBean();
        RequestItem requestItem = new RequestItem(query, queryString);
        requestBean.getItems().add(requestItem);
        String fileName =  UUID.randomUUID().toString()+file.getOriginalFilename();
        try {
            if(mode == -1 ||  mode == -2) {
                CycleJob cycleJob = cycleJobService.findByMode(mode);
                String filePath = cycleJob.getArgs() + FileConstant.ModeMap.get(mode);
                if (FileUtil.judeFileExists(filePath, fileName))
                    return new ResponseEntity<>(ResultBean.error("文件已经存在，请重新上传"), HttpStatus.NOT_ACCEPTABLE);
                // 商品基本信息及变价信息
                FileUtil.uploadFile(file.getBytes(), cycleJob.getArgs(), fileName);
            }
            // 商品图片
            else if(mode == 0){
                fileName =  "/goods_image/"+fileName;
                List<Good> goods = RequestBeanUtil.getGoodsByRequestBean(requestBean);
                for (Good good : goods) {
                    String url = COSUtil.PutObjectRequest(file, fileName);
                    good.setImageUrl(url);
                    goodService.saveOne(good);
                }
            }
            // 样式图片
            else if(mode == 1){
                fileName =  "/dismps_image/"+fileName;
                List<Dispms> dispms = RequestBeanUtil.getDispmsByRequestBean(requestBean);
                for (Dispms dispm : dispms) {
                    String url = COSUtil.PutObjectRequest(file, fileName);
                    dispm.setImageUrl(url);
                    dispmsService.saveOne(dispm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("文件写入成功...");
        return new ResponseEntity<>(ResultBean.success("文件上传成功"), HttpStatus.OK);
    }

    @ApiOperation("上传多个文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mode", value = "0为商品基本信息 1商品变价信息 2为商品特征图片 3为样式特征图片", dataType = "mode", paramType = "query", required = true)
    })
    @PostMapping("/uploadFiles")
    public ResponseEntity<ResultBean> multiFileUpload(@ApiParam(value = "文件信息", required = true) @RequestParam("file") MultipartFile[] file, @RequestParam Integer mode, @RequestBody @ApiParam("图片对应实体信息集合") RequestBean requestBean) {
        int successNumber = 0;
        for (int i = 0; i < file.length; i++) {
            if (file[i] != null) {
                System.out.println(file[i].getName());
            }
        }
        return new ResponseEntity<>(ResultBean.success(new ResponseBean(file.length,successNumber)), HttpStatus.OK);
    }
}
