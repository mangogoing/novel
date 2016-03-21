package com.ushaqi.zhuishushenqi.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
//import com.xm.gorilla.util.g;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {
	private static final ZhuiShu zhuishu = new ZhuiShu();
	//
	static {
		// System.load("data/data/net.tatans.coeus.novel/libs/libzh.so");
		System.loadLibrary("zh");
	}

	public static void showtxt() {
		Log.d("TTTTTTT", "nihaoa");
	}

	public static String getKey_t(String paramString) {
		String str = a(
				"559351a450fe459a3d9f78f1",
				a("559351b7cbe6a8573bc03a90",
						"au12woN24YCUPkc8lgjcTTUXYxHp3+CX+IsqL6KGPInK6LtdYPm7MYLkIlUNBRaNuJDIWnqr4rmVazLY4uu6Fqs4HsepNbBnGVv07j0ORrJ+xWRRzkbLy7WwWJoyJpUT"));
		if (str != null)
			str = str.substring(0, 20);
		return getNewAdvert(str, paramString, zhuishu);
	}

	public static String a(String paramString1, String paramString2) {
		try {
			Key localKey = b(paramString1);
			byte[] arrayOfByte1 = Base64.decode(paramString2, 0);
			byte[] arrayOfByte2 = ByteUtil.getArrayByte(arrayOfByte1, 0, 16);
			byte[] arrayOfByte3 = ByteUtil.getArrayByte(arrayOfByte1, 16,
					arrayOfByte1.length);
			IvParameterSpec localIvParameterSpec = new IvParameterSpec(
					arrayOfByte2);
			Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			localCipher.init(2, localKey, localIvParameterSpec);
			String str = new String(localCipher.doFinal(arrayOfByte3));
			return str;
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			while (true) {
				localNoSuchAlgorithmException.printStackTrace();
				String str = null;
			}
		} catch (NoSuchPaddingException localNoSuchPaddingException) {
			while (true)
				localNoSuchPaddingException.printStackTrace();
		} catch (InvalidKeyException localInvalidKeyException) {
			while (true)
				localInvalidKeyException.printStackTrace();
		} catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException) {
			while (true)
				localInvalidAlgorithmParameterException.printStackTrace();
		} catch (IllegalBlockSizeException localIllegalBlockSizeException) {
			while (true)
				localIllegalBlockSizeException.printStackTrace();
		} catch (BadPaddingException localBadPaddingException) {
			while (true)
				localBadPaddingException.printStackTrace();
		}
	}

	private static Key b(String paramString) {
		byte[] arrayOfByte = paramString.getBytes();
		return new SecretKeySpec(arrayOfByte, 0, arrayOfByte.length, "AES");
	}

	public static native String getNewAdvert(String paramString1,
			String paramString2, Context paramContext);
}

/*
 * Location:
 * C:\Users\newre\Desktop\onekey-decompile-apk-1.0.1\cyzs.1.9.1_signed.apk.jar
 * Qualified Name: com.ushaqi.zhuishushenqi.util.CipherUtil JD-Core Version:
 * 0.6.1
 */