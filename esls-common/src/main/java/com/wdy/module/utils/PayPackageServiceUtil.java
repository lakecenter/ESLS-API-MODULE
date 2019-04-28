package com.wdy.module.utils;//package com.datagroup.ESLS.utils;
//
//import com.alibaba.fastjson.JSON;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import javax.security.auth.message.AuthException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class PayPackageServiceUtil {
//    @Autowired
//    private HttpRequestUtils httpRequestUtils;
//    private static String communityHost;
//    private static String  GETUSERECORDS = "/payPackage/getUseRecords";
//    private static String  ADDUSERECORD = "/payPackage/addUseRecord";
//    private static String  DELETEUSERECORD = "/payPackage/deleteUseRecord";
//    private static String  UPDATEUSERECORD = "/payPackage/updateUseRecord";
//    private static String  GETPACKAGES = "/payPackage/getPackages";
//    @Value("${yzj.community.inner.host}")
//    public void setCommunityHost(String communityHost) {
//        PayPackageServiceUtil.communityHost = communityHost;
//    }
//
//    public CommonQueryOutputVo<UseRecordOutputVo> getUseRecords(UseRecordQueryVo useRecordQueryVo) throws AdminException {
//        String url = communityHost.concat(GETUSERECORDS);
//
//        Map pageArgsMap = getPageArgsMap(useRecordQueryVo.getPage(), useRecordQueryVo.getPageSize());
//        ResponseEntity<String> result = httpRequestUtils.doHttpGetMethod(url, getHttpHeaders(), pageArgsMap);
//        if(HttpStatus.OK.equals(result.getStatusCode())) {
//            String body = result.getBody();
//            Map map = JSON.parseObject(body, Map.class);
//            List<UseRecordOutputVo> resultList = (List<UseRecordOutputVo>) map.get("list");
//            Integer total = (Integer) map.get("total");
//            CommonQueryOutputVo<UseRecordOutputVo> queryOutputVo =
//                    new CommonQueryOutputVo<>(resultList, total);
//            return queryOutputVo;
//        }
//        else
//            throw new AdminException(AdminErrCodeEnum.STATIC_LOG_TYPE_ERROR);
//    }
//    public String addUseRecord(UseRecordInputVo useRecordInputVo) throws AdminException {
//        String url = communityHost.concat(ADDUSERECORD);
//        Map<String,Object> beanMap;
//        ResponseEntity<Map> result;
//        try {
//            if (useRecordInputVo.getExpireTime() == null) {
//                throw new AuthException(AuthErrCodeEnum.USE_RECORD_EXPIRETIME_IS_NULL);
//            }
//            beanMap = BeansUtils.convertBeanToMap(useRecordInputVo,false);
//            if(useRecordInputVo.getOrderTime()!=null)
//                beanMap.put("orderTime",useRecordInputVo.getOrderTime().getTime());
//            beanMap.put("expireTime",useRecordInputVo.getExpireTime().getTime());
//            result = httpRequestUtils.doHttpPostMethod(url, getHttpHeaders(), beanMap);
//        }
//        catch (Exception e){
//            throw new AdminException(AdminErrCodeEnum.BAD_PARAM);
//        }
//        if(HttpStatus.OK.equals(result.getStatusCode())){
//            return String.valueOf(result.getBody().get("id"));
//        }
//        else
//            throw new AdminException(AdminErrCodeEnum.BAD_PARAM);
//    }
//    public boolean deleteUseRecord(IdVo idVo) throws AdminException {
//        String url = communityHost.concat(DELETEUSERECORD);
//        Map<String, Object> beanMap;
//        ResponseEntity<Map> result;
//        try {
//            beanMap = BeansUtils.convertBeanToMap(idVo, false);
//            result = httpRequestUtils.doHttpPostMethod(url, getHttpHeaders(), beanMap);
//        }
//        catch (Exception e){
//            throw new AdminException(AdminErrCodeEnum.BAD_PARAM);
//        }
//        if(HttpStatus.OK.equals(result.getStatusCode()))
//            return true;
//        else
//            return false;
//    }
//    public boolean updateUseRecord(UseRecordInputVo useRecordInputVo) throws AdminException {
//        String url = communityHost.concat(UPDATEUSERECORD);
//        Map<String,Object> beanMap;
//        ResponseEntity<Map> result;
//        try {
//            if (useRecordInputVo.getExpireTime() == null) {
//                throw new AuthException(AuthErrCodeEnum.USE_RECORD_EXPIRETIME_IS_NULL);
//            }
//            beanMap = BeansUtils.convertBeanToMap(useRecordInputVo,false);
//            if(useRecordInputVo.getOrderTime()!=null)
//                beanMap.put("orderTime",useRecordInputVo.getOrderTime().getTime());
//            beanMap.put("expireTime",useRecordInputVo.getExpireTime().getTime());
//            result = httpRequestUtils.doHttpPostMethod(url, getHttpHeaders(), beanMap);
//        }
//        catch (Exception e){
//            throw new AdminException(AdminErrCodeEnum.BAD_PARAM);
//        }
//        if(HttpStatus.OK.equals(result.getStatusCode()))
//            return true;
//        else
//            return false;
//    }
//    public CommonQueryOutputVo<PayPackageOutputVo> getPackages() throws AdminException {
//        String url = communityHost.concat(GETPACKAGES);
//        ResponseEntity<String> result = httpRequestUtils.doHttpGetMethod(url, getHttpHeaders(), null);
//        if(HttpStatus.OK.equals(result.getStatusCode())) {
//            String body = result.getBody();
//            List<PayPackageOutputVo> lists = JSON.parseArray(body, PayPackageOutputVo.class);
//            return new CommonQueryOutputVo<>(lists,lists.size());
//        }
//        else
//            throw new AdminException(AdminErrCodeEnum.STATIC_LOG_TYPE_ERROR);
//    }
//
//    private Map getHttpHeaders() throws AdminException {
//        HashMap<String, Object> headers = new HashMap<>();
//        headers.put("X-Requested-userId", ContextUtils.getUserId());
//        headers.put("X-Requested-networkId", ContextUtils.getNetworkId());
//        String jwtToken = JWTTokenUtil.createJWTToken(headers, 50);
//        headers.put("X-Requested-jwttoken",jwtToken);
//        return headers;
//    }
//
//    private Map getPageArgsMap(Integer page, Integer pageSize) {
//        HashMap<String, Object> paramMapArgs = new HashMap<>();
//        paramMapArgs.put("page", page);
//        paramMapArgs.put("pageSize", pageSize);
//        return paramMapArgs;
//    }
//
//}
