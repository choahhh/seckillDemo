package com.example.seckilldemo.util;

/**
 * CryptoJS加密辅助类
 *
 * @author lougf
 * @since 2023/3/6
 */
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptUtil {

    //这个密钥需要是16位
    public static final String KEY_DES = "07288DBBE50F1B65";

    public static void main(String[] args) throws Exception {
        String old = "da67391d468dfc8ea799e1da54b9eca7";
        String target ="RXt5WWBrmO1mSylA+NFGAwaW9CnB6lk+NrbZ1eqlwi3vq2yywuu/wauHTV4E2Z2h";

//解密
        System.out.println(EncryptUtil.aesDecryptForFront(target,EncryptUtil.KEY_DES));

        //加密
        System.out.println(EncryptUtil.aesEncryptForFront(old,EncryptUtil.KEY_DES));

    }


    /**
     * AES解密
     * @param encryptStr 密文
     * @param decryptKey 秘钥，必须为16个字符组成
     * @return 明文
     * @throws Exception
     */
    public static String aesDecryptForFront(String encryptStr, String decryptKey) {
        if (StringUtils.isEmpty(encryptStr) || StringUtils.isEmpty(decryptKey)) {
            return null;
        }
        try {
            byte[] encryptByte = Base64.getDecoder().decode(encryptStr);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
            byte[] decryptBytes = cipher.doFinal(encryptByte);
            return new String(decryptBytes);

        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }


    }

    /**
     * AES加密
     * @param content 明文
     * @param encryptKey 秘钥，必须为16个字符组成
     * @return 密文
     * @throws Exception
     */
    public static String aesEncryptForFront(String content, String encryptKey) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(encryptKey)) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

            byte[] encryptStr = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptStr);

        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }

    }

}




