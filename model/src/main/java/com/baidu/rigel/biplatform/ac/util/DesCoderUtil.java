package com.baidu.rigel.biplatform.ac.util;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

  

/**
 * DesBase64加密类
 * 
 * @author luowenlei
 *
 */
public class DesCoderUtil {
  
    /**
     * keys
     */
    private static byte[] keys = { 1, -1, 1, -1, 1, -1, 1, -1 };

    /**
     * 对password进行MD5加密
     * 
     * @param source source
     * @return byte[] byte
     */
    private static byte[] getMD5(byte[] source) {
        byte tmp[] = null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source);
            tmp = md.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }
  
    /**
     * DES进行真正的加密操作
     * 
     * @param byteS byteS
     * @param password password
     * @return byte[] byte
     */
    private static byte[] encryptByte(byte[] byteS, byte password[]) {
        byte[] byteFina = null;
        try {// 初始化加密/解密工具
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(password);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keys);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return byteFina;
    }
  
    /**
     * DES进行解密操作
     * 
     * @param byteS byteS
     * @param password password
     * @return byte[]  byte
     */
    private static byte[] decryptByte(byte[] byteS, byte password[]) {
        byte[] byteFina = null;
        try {// 初始化加密/解密工具
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(password);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(keys);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return byteFina;
    }
  
    /**
     *
     * <p>
     * Des加密strMing，然后base64转换
     * @param strMing
     * @param md5key
     * @return String   
     * author: Heweipo
     */
    private static String encryptStr(String strMing, byte md5key[]) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        try {
            byteMing = strMing.getBytes("utf-8");
            byteMi = encryptByte(byteMing, md5key);
            Base64 base64=new Base64();  
            strMi = base64.encodeToString(byteMi);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }
  
    /**
     *
     * Base64转换strMi,然后进行des解密
     * @param strMi
     * @param md5key
     * @return String   
     * author: Heweipo
     */
    public static String decryptStr(String strMi, byte md5key[]) {
        byte[] bytes = null;
        String str = "";
        try {
        	Base64 base64=new Base64();  
        	bytes = base64.decode(strMi);
        	bytes = decryptByte(bytes, md5key);
        	str = new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        	bytes = null;
        }
        return str;
    }
    
    /**
     * 开放出来的base64+des加密
     * 
     * @param data data，需要加密的字符串
     * @param key key
     * @return String 加密后的字符串
     */
    public static String encrypt(String data, String key) {
    	try {
			return DesCoderUtil.encryptStr(data, DesCoderUtil.getMD5(key.getBytes("utf-8")));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * 开放出来的base64+des加密
     * 
     * @param data data，需要加密的字符串
     * @param key key
     * @return String 加密后的字符串
     */
    public static String decrypt(String data, String key) {
    	try {
    		byte[] keyByte =  DesCoderUtil.getMD5(key.getBytes("utf-8"));
			return DesCoderUtil.decryptStr(data, keyByte);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    } 
  
}