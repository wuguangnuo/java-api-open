package com.xyj.api.utils;

import com.google.common.io.BaseEncoding;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密
 *
 * @author WuGuangNuo
 */
@Component
public class EncryptUtil {
    /**
     * 用户账号密码加密
     *
     * @param str
     * @return
     */
    public static String EncryptString(String str) {
        String str2 = getMD5Str("XXXXXX");
        int num2 = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            /* * */
            加密算法
            /* * */
        }
        String str1 = builder.toString();

        String result = EncryptKey(builder.toString());
        BaseEncoding baseEncoding = BaseEncoding.base64();
        result = baseEncoding.encode(result.getBytes());
        return result;
    }

    /**
     * 加密Key
     */
    private static String EncryptKey(String str) {
        String str2 = getMD5Str("XXXXXX");
        int num2 = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            /* * */
            加密算法
            /* * */
        }
        return builder.toString();
    }

    /**
     * MD5 加密
     *
     * @param str str
     * @return md5encode
     */
    static String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (byte b : byteArray) {
            if (Integer.toHexString(0xFF & b).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & b));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & b));
        }

        return md5StrBuff.toString();
    }
}
