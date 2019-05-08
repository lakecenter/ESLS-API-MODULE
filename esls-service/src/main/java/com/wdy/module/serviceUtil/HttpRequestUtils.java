package com.wdy.module.serviceUtil;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @auther: dong_wu
 * @date: 2019/4/1 15:31
 * @description:
 */
@Component("httpRequestUtils")
public class HttpRequestUtils {
    private RestTemplate restTemplate;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity doHttpGetMethod(String url, Map headers, Map<String, Object> paramMapArgs) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(headers);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity result;
        if (paramMapArgs != null)
            result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class, paramMapArgs);
        else
            result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println(result);
        return result;
    }

    public ResponseEntity doHttpPostMethod(String url, Map headers, Map<String, Object> bodyMapArgs) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(headers);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.setAll(bodyMapArgs);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(bodyMap, httpHeaders);
        ResponseEntity result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        return result;
    }

}
