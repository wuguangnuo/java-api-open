package com.xyj.api.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * 百度AI开放平台 http://ai.baidu.com
 *
 * @author WuGuangNuo
 */
@Component
public class BaiduAIUtil {

    // 人像分割
    private static final String body_seg = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_seg";
    // 增值税发票识别
    private static final String vat_invoice = "https://aip.baidubce.com/rest/2.0/ocr/v1/vat_invoice";

    /**
     * 获取权限token
     *
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    private static String getAuth() {
        final String clientId = "WxieMa2Wplpw7w69QdG5mrXx"; // API Key
        final String clientSecret = "8YPi6OkQ8cD1qSuaYuw4tbmC6sETEDbU"; // Secret Key
        return getAuth(clientId, clientSecret);
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    private static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
//             获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//             遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.err.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
//            System.err.println("result:" + result);
            JSONObject jsonObject = JSON.parseObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * 人像分割
     *
     * @param img base64原图片 (无uri)
     * @return 生成图片 (无uri)
     */
    public String classify(String img) {
        try {
            Map<String, String> param = new TreeMap<>();
            param.put("image", URLEncoder.encode(img.replace("\r\n", ""), "UTF-8"));
            param.put("type", "foreground");
            String result = httpsPost(param, body_seg);

            String base64 = JSONObject.parseObject(result).get("foreground").toString();
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 增值税发票识别
     *
     * @param img base64图片 (无uri)
     * @return 识别结果
     */
    public String vatInvoice(String img) {
        try {
            Map<String, String> param = new TreeMap<>();
            param.put("image", URLEncoder.encode(img.replace("\r\n", ""), "UTF-8"));
            param.put("accuracy", "normal");
            String result = httpsPost(param, vat_invoice);

            String base64 = JSONObject.parseObject(result).get("words_result").toString();
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * POST发送
     *
     * @param param 参数
     * @param url   链接
     * @return 响应结果
     */
    private static String httpsPost(Map<String, String> param, String url) {
        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        if (param != null) {
            for (Map.Entry<String, String> e : param.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
            sb = new StringBuffer(sb.substring(0, sb.length() - 1));
        }
        // 尝试发送请求
        try {
            URL u = new URL(url + "?access_token=" + getAuth());
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
}
