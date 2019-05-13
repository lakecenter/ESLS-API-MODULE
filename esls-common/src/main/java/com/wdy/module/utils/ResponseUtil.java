package com.wdy.module.utils;


import com.wdy.module.common.response.ResponseHelper;
import com.wdy.module.common.response.ResultBean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ResponseUtil {
    public static ResponseEntity<ResultBean> testListSize(String msg, List... args) {
        for (int i = 0; i < args.length; i++)
            if (CollectionUtils.isEmpty(args[i]))
                return ResponseHelper.buildBadRequestResultBean(msg);
        return null;
    }
}
