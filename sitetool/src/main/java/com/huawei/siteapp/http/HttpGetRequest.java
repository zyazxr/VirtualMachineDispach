package com.huawei.siteapp.http;

import com.huawei.siteapp.cache.CacheCenter;
import com.huawei.siteapp.common.Bean.RestBean;
import com.huawei.siteapp.common.constats.ParamKey;
import com.huawei.siteapp.common.util.JSONUtils;
import com.huawei.siteapp.common.util.PropertiesUtils;
import com.huawei.siteapp.common.util.ServiceContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z00390414 on 2017/6/15.
 *
 * @version 1.0
 * @date 2017/6/15
 */

public class HttpGetRequest {
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public ServiceContext sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        ServiceContext cxt = new ServiceContext();
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Accept", "application/json;version=5.1;charset=UTF-8");
            connection.setRequestProperty("X-Auth-Token", (String) CacheCenter.getInstance().getRestBeanResponse("FcLogin"));
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (Object o : map.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                List<String> val = (List<String>) entry.getValue();

                cxt.put(key, val);
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        cxt.put(ParamKey.REST_RESPONSE, result.toString());
        return cxt;
    }

    public void fcGetSitesRest() {
        SiteLoginHttpRequest siteLoginHttpRequest = new SiteLoginHttpRequest();
        RestBean restInfo = siteLoginHttpRequest.getTestRest();
        String[] urlParm = new String[]{restInfo.getVrmIp(), restInfo.getRestPort()};
        String url = PropertiesUtils.getUrl("FcGetSites", urlParm);
        ServiceContext sr = sendGet(url, "");
        String restResponse = (String) sr.get(ParamKey.REST_RESPONSE);

        Map<String, Object> responseMap = JSONUtils.jsonToMap(restResponse);
        String urlSites = ((HashMap<String, String>) (((List) responseMap.get(ParamKey.SITES)).get(0))).get(ParamKey.URI);
        CacheCenter.getInstance().addUrlResponse(ParamKey.SITE_ID, urlSites);
        System.out.println(sr);
    }

    public void fcGetSitesClustersRest() {
        SiteLoginHttpRequest siteLoginHttpRequest = new SiteLoginHttpRequest();
        RestBean restInfo = siteLoginHttpRequest.getTestRest();

        String[] urlParm = new String[]{restInfo.getVrmIp(), restInfo.getRestPort(), (String) CacheCenter.getInstance().getRestBeanResponse(ParamKey.SITE_ID)};
        String url = PropertiesUtils.getUrl("FcGetClusters", urlParm);
        ServiceContext sr = sendGet(url, "");
        String restResponse = (String) sr.get(ParamKey.REST_RESPONSE);
        ((List) (JSONUtils.jsonToMap(restResponse).get("clusters"))).get(0);

        for (Object clusterTemp : ((List<Object>) (JSONUtils.jsonToMap(restResponse).get("clusters")))) {
            String uri = ((Map<String, String>) clusterTemp).get(ParamKey.URI);

            Object memResource = ((Map<String, String>) clusterTemp).get("memResource");
            setResponseInCache(memResource);

            Object cpuResource = ((Map<String, String>) clusterTemp).get("cpuResource");
            setResponseInCache(cpuResource);
        }

        System.out.println(sr);
    }

    private void setResponseInCache(Object response) {
        for (Object o : ((Map<String, String>) response).entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            Object val = entry.getValue();
            CacheCenter.getInstance().addUrlResponse((String) key, val);
        }
    }
}