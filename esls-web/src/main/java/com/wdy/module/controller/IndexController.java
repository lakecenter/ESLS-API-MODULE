package com.wdy.module.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Api(description = "首页")
@CrossOrigin(origins = "*", maxAge = 3600)
public class IndexController {
    @GetMapping(value = "/index/home")
    @ApiOperation("www.localhost进入主页")
    public String toHome(){
        return "/upload";
    }
}
