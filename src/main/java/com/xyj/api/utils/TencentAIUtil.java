package com.xyj.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.xyj.api.dto.test.IDCardInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 腾讯AI开放平台 https://ai.qq.com
 *
 * @author WuGuangNuo
 */
@Slf4j
@Component
public class TencentAIUtil {

    private static final String appkey = appkey;
    private static final int app_id = app_id;

    // 身份证OCR
    private static final String ocr_idcardocr = "https://api.ai.qq.com/fcgi-bin/ocr/ocr_idcardocr";
    // 智能闲聊
    private static final String nlp_textchat = "https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat";
    // 文本翻译
    private static final String nlp_texttrans = "https://api.ai.qq.com/fcgi-bin/nlp/nlp_texttrans";

    /**
     * @return 请求时间戳（秒级）
     */
    private static long getTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * @return 随机字符串
     */
    private static String getNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成MD5 OCR签名信息
     *
     * @param param  param
     * @param appkey appkey
     * @return OCR签名
     */
    private static String getReqSign(Map<String, String> param, String appkey) {
        StringBuffer appendStr = new StringBuffer();
        for (String key : param.keySet()) {
            if (!Strings.isNullOrEmpty(param.get(key))) {
                appendStr.append(key).append("=").append(param.get(key)).append("&");
            }
        }
        appendStr.append("app_key=").append(appkey);
        return EncryptUtil.getMD5Str(appendStr.toString()).toUpperCase();
    }

    /**
     * 识别身份证
     *
     * @param base64Img 身份证正面base64编码(不含Data URI)
     * @param cardType  身份证图片类型，0-正面，1-反面
     * @return httpsPost String
     */
    public String ocrIdcard(String base64Img, Integer cardType) {
        try {
            Map<String, String> param = new TreeMap<>();
            param.put("app_id", URLEncoder.encode(String.valueOf(app_id), "UTF-8"));
            param.put("image", URLEncoder.encode(base64Img.replace("\r\n", ""), "UTF-8"));
            param.put("card_type", URLEncoder.encode(cardType + "", "UTF-8"));
            param.put("time_stamp", URLEncoder.encode(String.valueOf(getTimeStamp()), "UTF-8"));
            param.put("nonce_str", URLEncoder.encode(getNonceStr(), "UTF-8"));
            param.put("sign", getReqSign(param, URLEncoder.encode(appkey, "UTF-8")));
            String result = httpsPost(param, ocr_idcardocr);
            // 返回码; 0表示成功，非0表示出错
            int ret = Integer.valueOf(JSONObject.parseObject(result).get("ret").toString());
            if (ret == 0) {
                return result;
            } else {
                log.error("识别身份证失败:", cardType == 0 ? "正面" : "反面", JSONObject.parseObject(result).get("msg").toString());
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取身份证识别数据
     *
     * @param result   httpsPost String
     * @param cardType 返回码； 0表示成功，非0表示出错
     * @return IDCardInfo身份证信息封装类
     */
    public IDCardInfoDto getIDCardInfo(String result, int cardType) {
        IDCardInfoDto ocrInfo = new IDCardInfoDto();
        if (cardType == 0) { // 正面
            String data = JSONObject.parseObject(result).get("data").toString();
            JSONObject dataObject = JSONObject.parseObject(data);
            ocrInfo.setName(dataObject.get("name").toString());
            ocrInfo.setSex(dataObject.get("sex").toString());
            ocrInfo.setNation(dataObject.get("nation").toString());
            ocrInfo.setBirth(dataObject.get("birth").toString());
            ocrInfo.setAddress(dataObject.get("address").toString());
            ocrInfo.setId(dataObject.get("id").toString());
        } else if (cardType == 1) { // 反面
            String data = JSONObject.parseObject(result).get("data").toString();
            JSONObject dataObject = JSONObject.parseObject(data);
            ocrInfo.setAuthority(dataObject.get("authority").toString());
            ocrInfo.setValidDate(dataObject.get("valid_date").toString());
        }
        return ocrInfo;
    }

    /**
     * 智能闲聊
     *
     * @param session  会话id
     * @param question 问题
     * @return 回答
     */
    public String textChat(String session, String question) {
        try {
            Map<String, String> param = new TreeMap<>();
            param.put("app_id", URLEncoder.encode(String.valueOf(app_id), "UTF-8"));
            param.put("time_stamp", URLEncoder.encode(String.valueOf(getTimeStamp()), "UTF-8"));
            param.put("nonce_str", URLEncoder.encode(getNonceStr(), "UTF-8"));
            param.put("session", URLEncoder.encode(session, "UTF-8"));
            param.put("question", URLEncoder.encode(question, "UTF-8"));
            param.put("sign", getReqSign(param, URLEncoder.encode(appkey, "UTF-8")));
            String result = httpsPost(param, nlp_textchat);
            int ret = Integer.valueOf(JSONObject.parseObject(result).get("ret").toString());
            if (ret == 0) {
                String data = JSONObject.parseObject(result).get("data").toString();
                JSONObject dataObject = JSONObject.parseObject(data);
                return dataObject.get("answer").toString();
            } else {
                return JSONObject.parseObject(result).get("msg").toString();
            }
        } catch (Exception e) {
            log.error(e.toString());
            return e.toString();
        }
    }

    /**
     * 文本翻译（AI Lab）
     *
     * @param text 待翻译文本
     * @param type 翻译类型
     * @return 翻译后文本
     */
    public String textTranslate(String text, int type) {
        try {
            Map<String, String> param = new TreeMap<>();
            param.put("app_id", URLEncoder.encode(String.valueOf(app_id), "UTF-8"));
            param.put("time_stamp", URLEncoder.encode(String.valueOf(getTimeStamp()), "UTF-8"));
            param.put("nonce_str", URLEncoder.encode(getNonceStr(), "UTF-8"));
            param.put("type", URLEncoder.encode(String.valueOf(type), "UTF-8"));
            param.put("text", URLEncoder.encode(text, "UTF-8"));
            param.put("sign", getReqSign(param, URLEncoder.encode(appkey, "UTF-8")));
            String result = httpsPost(param, nlp_texttrans);
            int ret = Integer.valueOf(JSONObject.parseObject(result).get("ret").toString());
            if (ret == 0) {
                String data = JSONObject.parseObject(result).get("data").toString();
                JSONObject dataObject = JSONObject.parseObject(data);
                return dataObject.get("trans_text").toString();
            } else {
                return JSONObject.parseObject(result).get("msg").toString();
            }
        } catch (Exception e) {
            log.error(e.toString());
            return e.toString();
        }
    }

    /**
     * 发送post请求
     *
     * @param param  param
     * @param apiUrl api url
     * @return httpsPost String
     */
    private static String httpsPost(Map<String, String> param, String apiUrl) {
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
            sb.substring(0, sb.length() - 1);
        }
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
}