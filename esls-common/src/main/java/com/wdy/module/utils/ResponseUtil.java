package com.wdy.module.utils;


import com.wdy.module.common.response.ResultBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseUtil {
    public static ResponseEntity<ResultBean> testListSize(String msg, List... args) {
        for (int i = 0; i < args.length; i++)
            if (args.length <= 0)
                return new ResponseEntity<>(ResultBean.error(msg), HttpStatus.BAD_REQUEST);
        return null;
    }
}
