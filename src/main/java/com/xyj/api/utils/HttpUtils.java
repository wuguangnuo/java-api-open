package com.xyj.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.xyj.api.model.ApiRes;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 发送请求
 *
 * @author WuGuangNuo
 */
@Component
public class HttpUtils {
    /**
     * 发送post请求 MAP
     *
     * @param param  param
     * @param apiUrl api url
     * @return httpPost String
     */
    public String httpPostMap(Map<String, String> param, String apiUrl) {
        try {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                entry.setValue(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> e : param.entrySet()) {
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
            sb.append("&");
        }
        sb.substring(0, sb.length() - 1);
        // 尝试发送请求
        try {
            URL u = new URL(apiUrl);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            // con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            osw.write(sb.toString());
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 发送post请求 JSON
     *
     * @param param param
     * @param url   api url
     * @return httpPost String
     */
    public String httpPostJson(String param, String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String responseInfo = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            ContentType contentType = ContentType.create("application/json", CharsetUtils.get("UTF-8"));
            httpPost.setEntity(new StringEntity(param, contentType));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                if (entity != null) {
                    responseInfo = EntityUtils.toString(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseInfo;
    }

    /**
     * API 输出格式化
     *
     * @param str JsonString
     * @return ApiRes
     */
    public ApiRes<String> smoothString(String str) {
        String meta = JSONObject.parseObject(str).get("Meta").toString();
        int state = Integer.valueOf(JSONObject.parseObject(meta).get("State").toString());
        String msg = JSONObject.parseObject(meta).get("Msg").toString();
        String data = JSONObject.parseObject(str).get("Data") == null ? "" : JSONObject.parseObject(str).get("Data").toString();

        return ApiRes.res(state, msg, data);
    }
}
