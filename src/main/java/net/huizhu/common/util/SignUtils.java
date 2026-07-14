package net.huizhu.common.util;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Random;


@Slf4j
public class SignUtils {

    public static String getSign(Integer time,String nonce,String appSecret) {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("time:"+time).append(",").append("nonce:"+nonce).append(",").append("appSecret:"+appSecret);
            System.out.println("签名原文："+stringBuffer.toString());
            String argPreSign = stringBuffer.toString();
            String signStr = DigestUtil.md5Hex(argPreSign);
            System.out.println("RSA签名结果:"+signStr);
            return signStr;
        }catch (Exception e){
            e.printStackTrace();
            log.error("签名出错",e);
        }
        return null;
    }


    /**
     * 获取精确到秒的时间戳
     * @param date
     * @return
     */
    public static int getSecondTimestampTwo(Date date){
        if (null == date) {
             return 0;
        }
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.valueOf(timestamp);
    }

    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i ++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }



}
