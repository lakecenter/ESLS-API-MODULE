package com.wdy.module.common.request;

import lombok.Data;

@Data
public class RequestItem {
    private String query;
    private String queryString;
    private String cron;
    public RequestItem(){}
    public RequestItem(String query,String queryString){
        this.query = query;
        this.queryString = queryString;
    }
    public RequestItem(String cron,String query,String queryString){
        this.cron = cron;
        this.query = query;
        this.queryString = queryString;
    }
}
