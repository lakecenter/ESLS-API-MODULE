package com.wdy.module.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QueryAllBean
 *
 * @author dongyang_wu
 * @date 2019/5/13 15:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryAllBean {
    private String query;
    private String queryString;
    private Integer page;
    private Integer pagecount;
    private String result;
    private String serviceName;
}
