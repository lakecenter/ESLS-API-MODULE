package com.wdy.module.common.response;

import lombok.Data;

@Data
public class ResponseBean {
    private int sum;
    private int successNumber;
    public ResponseBean(int sum, int successNumber) {
        this.sum = sum;
        this.successNumber = successNumber;
    }
    public boolean isError(){
        return successNumber==0;
    }
}
