package com.goodcom.pos.utils;

import android.util.Log;

import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.core.codec.Base16Codec;


public class DESUtil {
	private static final String ALGORITHM = "DESede";
	private static final String TRANSFORMATION = "DESede/CBC/PKCS5Padding";

	public static String encrypt(String data, String secretKeyHex, String ivhHex) {
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			byte[] secretKey = Base16Codec.CODEC_LOWER.decode(secretKeyHex);
			SecretKey key = new SecretKeySpec(secretKey, ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(
					Base16Codec.CODEC_LOWER.decode(ivhHex)); // DESede requires an 8 byte IV for CBC mode
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			byte[] encryptedBytes = cipher.doFinal(data.getBytes());
			return new String(Base16Codec.CODEC_LOWER.encode(encryptedBytes));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static String decrypt(String encryptedData, String secretKeyHex, String ivhHex) {
		try {
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			SecretKey key = new SecretKeySpec(Base16Codec.CODEC_LOWER.decode(secretKeyHex), ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(Base16Codec.CODEC_LOWER.decode(ivhHex)); // Use the same IV for
																								// decryption
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] decryptedBytes = cipher.doFinal(Base16Codec.CODEC_LOWER.decode(encryptedData));
			return new String(decryptedBytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static String DESedeMAC(String signContend, String secretKeyHex, String ivhHex) {
		Log.e("DESedeMAC","signContend:"+signContend);
		Log.e("DESedeMAC","secretKeyHex:"+secretKeyHex);
		Log.e("DESedeMAC","ivhHex:"+ivhHex);
		try {
			byte[] key = Base16Codec.CODEC_LOWER.decode(secretKeyHex);
			byte[] iv = Base16Codec.CODEC_LOWER.decode(ivhHex);
			KeyParameter keyParameter = new KeyParameter(key);
			DESedeEngine deSedeEngine = new DESedeEngine();
			CBCBlockCipherMac cbcBlockCipherMac = new CBCBlockCipherMac(deSedeEngine, deSedeEngine.getBlockSize() * 8);
			cbcBlockCipherMac.init(new ParametersWithIV(keyParameter, iv));
			// 计算消息的 MAC
			byte[] message = signContend.getBytes();
			cbcBlockCipherMac.update(message, 0, message.length);

			byte[] macBytes = new byte[cbcBlockCipherMac.getMacSize()];
			cbcBlockCipherMac.doFinal(macBytes, 0);
			return new String(Base16Codec.CODEC_LOWER.encode(macBytes));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private static final String TAG = "DESUtil";
	public static byte[] XOR(byte[] edata, byte[] temp) {
		byte[] result = new byte[edata.length];
		int i = 0;

		for(int j = result.length; i < j; ++i) {
			result[i] = (byte)(edata[i] ^ temp[i]);
		}

		return result;
	}

	public static byte[] encrypt(byte[] data, byte[] secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
			SecretKey key = new SecretKeySpec(secretKey, ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String DESedeMAC(String signContend, String secretKeyHex) {
		Log.e("DESedeMAC","signContend:"+signContend);
		Log.e("DESedeMAC","secretKeyHex:"+secretKeyHex);

		byte[] key = Base16Codec.CODEC_UPPER.decode(secretKeyHex);
		byte[] data = signContend.getBytes();
		// 进行分组
		int group = (data.length + (8 - 1)) / 8;
		// 偏移量
		int offset = 0;
		// 输入计算数据
		byte[] edata = null;
		byte[] temp;
		for (int i = 0; i < group; i++) {
			temp = new byte[8];
			if (i != group - 1) {
				System.arraycopy(data, offset, temp, 0, 8);
				offset += 8;
			} else {// 只有最后一组数据才进行填充0x00
				System.arraycopy(data, offset, temp, 0, data.length - offset);
			}

			if (i != 0) {// 只有第一次不做异或
				edata = XOR(edata, temp);
			} else {
				edata = temp;
			}
		}
		if(edata == null){
			return null;
		}
		byte[] M1 = new String(Base16Codec.CODEC_UPPER.encode(edata)).getBytes();
		byte[] M11 = new byte[8];
		byte[] M12 = new byte[8];
		System.arraycopy(M1,0,M11,0,8);
		System.arraycopy(M1,8,M12,0,8);
		byte[] M2 = encrypt(M11,key);
		byte[] M3 = XOR(M12, M2);
		byte[] M4 = encrypt(M3,key);
		String result = new String(Base16Codec.CODEC_UPPER.encode(M4));
		return new String(Base16Codec.CODEC_UPPER.encode(result.substring(0,8).getBytes()));
	}


}
