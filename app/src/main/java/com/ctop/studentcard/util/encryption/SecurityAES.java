package com.ctop.studentcard.util.encryption;

import com.ctop.studentcard.util.LogUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密
 * <p>
 * Created by on 2018/5/3.
 */
public final class SecurityAES {

    private static class SecurityHolder {
        private static SecurityAES instance = new SecurityAES();
    }

    public static SecurityAES getInstance() {
        return SecurityHolder.instance;
    }

    /**
     * 加密
     *
     * @param src 明文
     * @return 返回密文
     */
    public String encryptT(String src, String key, String vector) {
        if (null == src || src.isEmpty()) {
            LogUtil.e("Request param is null");
            return "";
        }
        return encrypt(src, key, vector);
    }

    /**
     * 使用AES进行加密
     *
     * @param src 源字符串
     * @param key 加密KEY
     * @return 返回加密后的字符串
     */
    private String encrypt(String src, String key, String vector) {
        String res = "";
        try {
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

            //"算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            //使用CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(src.getBytes());

            //此处使用BASE64做转码功能，同时能起到2次加密的作用。
            res = new Base64Custom().encodeToString(encrypted);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
        return res.replaceAll("\\n", "");
    }

    /**
     * AES解密
     *
     * @param src 密文
     * @return 返回明文
     */
    public String decryptT(String src, String key, String vector) {
        if (null == src || src.isEmpty()) {
            LogUtil.e("Request param is null");
            return "";
        }
        return decrypt(src, key, vector);
    }

    /**
     * AES解密
     *
     * @param src 密文
     * @param key 密钥
     * @return 返回明文
     */
    private String decrypt(String src, String key, String vector) {
        String res = "";
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
            IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted1 = Base64Custom.decode(src.replaceAll("\\n", ""));//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original);
        } catch (Exception e) {
            res = "decryptError";
            LogUtil.e(e.getMessage());
        }
        return res;
    }

//    public static void main(String[] args) {
//        System.out.println("$_".getBytes());
//        String str = "260696040000202,3,LOGIN,20191212173140,4,1000";
//        System.out.println(SecurityAES.getInstance().encryptT(str, "JNS6960tvon&25ED", ""));
//
//    }
}
