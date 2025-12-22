package club.beenest.sso.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * 加密工具类
 * 提供多种加密算法支持
 */
@Component
public class EncryptUtil {

    @Autowired
    private AesUtil aesUtil;

    /**
     * 对密码进行加密 (使用 SHA-256)
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public String encryptPassword(String password) {
        return sha256(password);
    }

    /**
     * 对敏感数据进行加密 (使用 AES)
     *
     * @param content 原始内容
     * @return 加密后的内容
     */
    public String encryptContent(String content) {
        if (content == null) {
            return null;
        }
        return aesUtil.encrypt(content);
    }

    /**
     * 对敏感数据进行解密 (使用 AES)
     *
     * @param content 加密后的内容
     * @return 原始内容
     */
    public String decryptContent(String content) {
        if (content == null) {
            return null;
        }
        return aesUtil.decrypt(content);
    }

    /**
     * SHA-256 加密
     *
     * @param str 字符串
     * @return Hex 字符串
     */
    public static String sha256(String str) {
        return hash(str, "SHA-256");
    }

    /**
     * MD5 加密
     *
     * @param str 字符串
     * @return Hex 字符串
     */
    public static String md5(String str) {
        return hash(str, "MD5");
    }

    private static String hash(String str, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] encodedhash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException(algorithm + " 加密失败", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
