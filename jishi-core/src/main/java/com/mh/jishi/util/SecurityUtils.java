package com.mh.jishi.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Lizr
 * @Date: 2020-06-12 17:07
 * @Description: 加解密安全工具类
 */
public class SecurityUtils {

    /**
     * MD5 32位加密
     * @param plainText 明文
     * @return 32位密文
     */
    public static String encrypMD5(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer(32);
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            re_md5 = buf.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5签名
     * @param origin
     * @param charsetname
     * @return
     */
    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    // RSA公钥
    public static String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDI+L1K/QFZcN/PVdqsRUQgFpZsEMmbHkiwLJPWR6DApi54sMvutb0LQqBDIndaYKOuh7zEvMTFg+ORUPC6OE54GJdiwq3wPY6K36miqIBI8Wuh/3+WDxRKRD0M/rj3AGlZZQaHIRT6it4s4QAOksc/PQ70g99RZBSerz4c0qRUkQIDAQAB";
    // RSA私钥
    public static String rasPrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMj4vUr9AVlw389V2qxFRCAWlmwQyZseSLAsk9ZHoMCmLniwy+61vQtCoEMid1pgo66HvMS8xMWD45FQ8Lo4TngYl2LCrfA9jorfqaKogEjxa6H/f5YPFEpEPQz+uPcAaVllBochFPqK3izhAA6Sxz89DvSD31FkFJ6vPhzSpFSRAgMBAAECgYBSvo75W/f9Qkrzy65v9UjaSVJ/hd3r3ukgmAn6VJ1sFD/X8zrTusHt6+2eFoe7j0R1LG/dNLdzdxzwDLpAgcRUVUkRR7xXUy0MYjDAYjHVx8uHbiAPME8cGWtd3aBwfH65c0O7eu8RSffBoNcY/XkcTbVOREV/ML8RIrPYm0mKkQJBAOV2u1dcL6fcch4lADSBg28K87y2Fm5qY+oHARTXFbsEBNY6oC6V4XR7IVQIknCLr+RsrgfshMsjTqEaZZv9ao0CQQDgNn+LmWkTuTdqyFJ3AphfGbGEs4g4QFuEdr0hPR6n0RIDTPs8Dpf46x43X57tpIpu9inkm1bnOwzi4HwRQrMVAkAIO0UnMiBnhin12N/0Aj1jy2HJAEM3vMtOxueMBtc5uUAvKzU51pia5Bvi9tfB/9DUge2QTTTKUL7uWpMQkHu1AkBzKqsjCp7VmbqkZ9cr+DJKDwmG7yzWMACSLueA7kMT0ikddupJc/mNyz9PFBevW2gcDOeB3GSsC0HYO5SezRLtAkAdSIus7ASNBBa15JoCsHGpz74ykHL9Yr/2q2kZNspnu7K4Vbsa59z3M3ocizoSUf4Qskfd0I8w7oacbxCQvEFM";
    private static Map<Integer, String> keyMap = new HashMap<>(); // 用于封装随机产生的公钥与私钥

    //随机生成密钥对
    public static void genKeyPair() {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = null;

        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 初始化密钥对生成器，密钥大小为96-1024位
        assert keyPairGen != null;
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0, publicKeyString);  //0表示公钥
        keyMap.put(1, privateKeyString);  //1表示私钥
    }

    /** RSA公钥加密
     * @param str  加密字符串
     * @param publicKey  公钥
     * @return  密文
     */
    public static String encrypt(String str, String publicKey) {
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = null;
        String outStr = null;

        try {
            pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        //RSA加密
        return outStr;
    }

    /**  RSA私钥解密
     * @param str   加密字符串
     * @param privateKey  私钥
     * @return  铭文
     */
    public static String decrypt(String str, String privateKey) {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = null;
        //RSA解密
        Cipher cipher = null;
        String outStr = null;

        try {
            priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            outStr = new String(cipher.doFinal(inputByte));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return outStr;
    }
}