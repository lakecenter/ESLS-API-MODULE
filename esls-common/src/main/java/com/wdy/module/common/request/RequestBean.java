package com.wdy.module.common.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RequestBean {
    private List<RequestItem> items = new ArrayList<>();
}
