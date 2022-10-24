package top.corz.mini.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryUtil {

	public static class SIGN {
		/**
		 * 生成 MD5 签名
		 */
		public static final Map<String, String> encrypt(String data, String secret) {
			if (data == null)
				return null;
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data);
			map.put("nonce", uuid());
			map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

			map.put("signature", MD5.encrypt(preparePlainText(map, secret)));

			return map;
		}

		/**
		 * MD5 签名校验
		 */
		public static final boolean verify(String data, String secret, String nonce, String timestamp,
				String signature) {
			if (data == null || signature == null)
				return false;
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data);
			map.put("nonce", nonce);
			map.put("timestamp", timestamp);

			String sign = MD5.encrypt(preparePlainText(map, secret));
			return signature.equalsIgnoreCase(sign);
		}

		/**
		 * 生成 SHA 签名
		 */
		public static final Map<String, String> shaSign(String data, String secret) {
			if (data == null)
				return null;
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data);
			map.put("nonce", uuid());
			map.put("timestamp", String.valueOf(LocalDateTime.now().getLong(ChronoField.INSTANT_SECONDS)));

			map.put("signature", SHA.encrypt(preparePlainText(map, secret), secret));

			return map;
		}

		/**
		 * SHA 签名校验
		 */
		public static final boolean shaSignVerify(String data, String secret, String nonce, String timestamp,
				String signature) {
			if (data == null || signature == null)
				return false;
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", data);
			map.put("nonce", nonce);
			map.put("timestamp", timestamp);

			String sign = SHA.encrypt(preparePlainText(map, secret), secret);

			return signature.equalsIgnoreCase(sign);
		}
	}

	public static class MD5 {
		public final static String encrypt(String data) {
			try {
				return encrypt(data.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("getBytes UTF-8 error");
			}
		}
		public final static String encrypt(byte[] bytes) {
			if (bytes == null || bytes.length == 0)
				return null;
			try {
				byte[] target = MessageDigest.getInstance("md5").digest(bytes);
				return bytesToHex(target).toUpperCase();
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("none MD5 algorithm");
			}
		}
	}

	public static class SHA {
		public final static String encrypt(String data, String key) {
			try {
				Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
				SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
				sha256_HMAC.init(secret_key);
				byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
				return bytesToHex(array).toUpperCase();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static class DES {
		public static final String encrypt(String data, String key) {
			return desCipher(data, key, Cipher.ENCRYPT_MODE);
		}
		public static final String decrypt(String data, String key) {
			return desCipher(data, key, Cipher.DECRYPT_MODE);
		}
		/**
		 * DES 加解密
		 * @param mode Cipher.ENCRYPT_MODE / Cipher.DECRYPT_MODE
		 * @return
		 */
		private static final String desCipher(String data, String key, int mode) {
			try {
				SecureRandom random = new SecureRandom();
				DESKeySpec desKey = new DESKeySpec(getDESSercretKey(key));
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				SecretKey securekey = keyFactory.generateSecret(desKey);
				Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
				cipher.init(mode, securekey, random);

				if (Cipher.ENCRYPT_MODE == mode) {
					byte[] bs = cipher.doFinal(data.getBytes());
					return Base64.getEncoder().encodeToString(bs);
				} else {
					byte[] bs = cipher.doFinal(Base64.getDecoder().decode(data));
					return new String(bs, "UTF-8");
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}
		private static byte[] getDESSercretKey(String key) throws UnsupportedEncodingException {
			byte[] result = new byte[8];
			byte[] keys = key.getBytes(StandardCharsets.UTF_8);
			for (int i = 0; i < 8; i++) {
				if (i < keys.length)
					result[i] = keys[i];
				else
					result[i] = 0x01;
			}
			return result;
		}
	}

	public static class AES {

		public static final String cbcDecrypt(String data, String key, String... iv) throws UnsupportedEncodingException {
			byte[] target = null;
			if (null != iv && iv.length > 0) {
				byte[] ivByte = Base64.getDecoder().decode(iv[0]); // 偏移量
				target = cbcCipher(data, key, new IvParameterSpec(ivByte), Cipher.DECRYPT_MODE);
			}else {
				target = cbcCipher(data, key, null, Cipher.DECRYPT_MODE);
			}
			if( target!=null )
				return new String(target, "utf-8");
			return null;
		}
		public static final String cbcEncrypt(String data, String key, String... iv) {
			byte[] target = null;
			if (null != iv && iv.length > 0) {
				byte[] ivByte = Base64.getDecoder().decode(iv[0]); // 偏移量
				target = cbcCipher(data, key, new IvParameterSpec(ivByte), Cipher.ENCRYPT_MODE);
			}else {
				target = cbcCipher(data, key, null, Cipher.ENCRYPT_MODE);
			}
			if( target!=null )
				return Base64.getEncoder().encodeToString(target);
			return null;
		}
		
		/**
		 * AES CBC 加解密
		 * @param mode Cipher.ENCRYPT_MODE 1 / Cipher.DECRYPT_MODE 2
		 * @return
		 */
		public static final byte[] cbcCipher(String data, String key, IvParameterSpec iv, int mode) {
			try {
				byte[] aesKey = Base64.getDecoder().decode(key); // 加密秘钥
				if( iv==null )
					iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
				
				SecretKeySpec spec = new SecretKeySpec(aesKey, "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
				cipher.init(mode, spec, iv);// 初始化

				if (Cipher.DECRYPT_MODE == mode) {
					return cipher.doFinal(Base64.getDecoder().decode(data));
				} else {
					return cipher.doFinal(data.getBytes("utf-8"));
				}
				
				//AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
				//parameters.init(iv);
				//cipher.init(mode, spec, parameters);// 初始化
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public static final String ecbDecrypt(String data, String key) {
			return ecbCipher(data, key, Cipher.DECRYPT_MODE);
		}
		public static final String ecbEncrypt(String data, String key) {
			return ecbCipher(data, key, Cipher.ENCRYPT_MODE);
		}
		/**
		 * AES ECB 加解密
		 * @param mode Cipher.ENCRYPT_MODE 1 / Cipher.DECRYPT_MODE 2
		 * @return
		 */
		private static final String ecbCipher(String data, String key, int mode) {
			try {
				KeyGenerator keygen = KeyGenerator.getInstance("AES");
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				secureRandom.setSeed(key.getBytes());
				keygen.init(128, secureRandom);

				SecretKey skey = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
				Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
				cipher.init(mode, skey);

				if (Cipher.DECRYPT_MODE == mode) {
					byte[] byte_decode = cipher.doFinal(Base64.getDecoder().decode(data));
					return new String(byte_decode, "utf-8");
				} else {
					byte[] target = cipher.doFinal(data.getBytes("utf-8"));
					return Base64.getEncoder().encodeToString(target);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static final String preparePlainText(Map<String, String> data, String secret) {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if (data.get(k) != null && data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
				sb.append(k).append("=").append(data.get(k).trim()).append("&");
		}
		sb.append("secret=").append(secret);
		return sb.toString();
	}

	private static final String bytesToHex(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (byte b : bytes) {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() < 2)
				buff.append(0);
			buff.append(hex);
		}
		return buff.toString().toUpperCase();
	}

	public final static String uuid() {
		return UUID.randomUUID().toString().toUpperCase().replace("-", "");
	}

	public final static String uuid(int length) {
		String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		return uuid.substring(uuid.length() - length);
	}

	public final static String token(Object userId) {
		return uuid() + "_" + userId;
	}

//	public static void main(String[] args) {
//		String encryptedData = "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZMQmRzooG2xrDcvSnxIMXFufNstNGTyaGS9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+3hVbJSRgv+4lGOETKUQz6OYStslQ142dNCuabNPGBzlooOmB231qMM85d2/fV6ChevvXvQP8Hkue1poOFtnEtpyxVLW1zAo6/1Xx1COxFvrc2d7UL/lmHInNlxuacJXwu0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn/Hz7saL8xz+W//FRAUid1OksQaQx4CMs8LOddcQhULW4ucetDf96JcR3g0gfRK4PC7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns/8wR2SiRS7MNACwTyrGvt9ts8p12PKFdlqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYVoKlaRv85IfVunYzO0IKXsyl7JCUjCpoG20f0a04COwfneQAGGwd5oa+T8yO5hzuyDb/XcxxmK01EpqOyuxINew==";
//		String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
//		String iv = "r7BXXKkLb8qrSNn05n0qiA==";
//
//		String data = "{\"openId\":\"oGZUI0egBJY1zhBYw2KhdUfwVJJE\",\"nickName\":\"Band\",\"gender\":1,\"language\":\"zh_CN\",\"city\":\"Guangzhou\",\"province\":\"Guangdong\",\"country\":\"CN\",\"avatarUrl\":\"http://wx.qlogo.cn/mmopen/vi_32/aSKcBBPpibyKNicHNTMM0qJVh8Kjgiak2AHWr8MHM4WgMEm7GFhsf8OYrySdbvAMvTsw3mo8ibKicsnfN5pRjl1p8HQ/0\",\"unionId\":\"ocMvos6NjeKLIBqg5Mr9QjxrP1FA\",\"watermark\":{\"timestamp\":1477314187,\"appid\":\"wx4f4bc4dec97d474b\"}}";
//
//		System.out.println(AES.cbcDecrypt(encryptedData, sessionKey, iv));
//		System.out.println(AES.cbcEncrypt(data, sessionKey, iv).equalsIgnoreCase(encryptedData));
//	}
}
