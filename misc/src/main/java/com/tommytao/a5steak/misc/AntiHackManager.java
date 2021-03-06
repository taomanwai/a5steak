package com.tommytao.a5steak.misc;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Responsible for security operations
 * 
 * @author tommytao
 * 
 */
public class AntiHackManager extends Foundation {

	private static AntiHackManager instance;

	public static AntiHackManager getInstance() {

		if (instance == null)
			instance = new AntiHackManager();

		return instance;
	}

	private AntiHackManager() {  
		
		super();
		
		log( "anti_hack: " + "create"); 
		

	}

	// --

	public static final String ENCRYPTION_DECRYPTION_PROTOCOL = "AES";
	public static final String SECRET_KEY = "uvjnvdsoljfdsapj"; // Must be 16 bytes for AES; 8 bytes for DES. More random / unpredictable is better

	private Cipher cipher;
	
	
	@Override
	public boolean init(Context context) {
		
		if (!super.init(context)){
			log( "anti_hack: " + "init REJECTED: already initialized"); 
			return false;
		}

		log( "anti_hack: " + "init"); 
		
		return true;

	}

	@Deprecated
	public boolean init(Context context, RequestQueue requestQueue) {
		return super.init(context, requestQueue);
	}

    @Override
	public String md5(String input) {

		return super.md5(input);

	}

	public String sha1(String input) {
		return super.sha1(input);
	}

	private Cipher getCipher() {

		if (cipher == null) {

			try {
				cipher = Cipher.getInstance(ENCRYPTION_DECRYPTION_PROTOCOL);
			} catch (Exception e) {
                e.printStackTrace();
			}
		}

		return cipher;

	}

	public String encrypt(String input, String secretKey) {
		
		if (input.isEmpty())
			return "";
		
		if (secretKey.isEmpty())
			return input;
		

		String result = "";

		SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(), ENCRYPTION_DECRYPTION_PROTOCOL);
		try {
			getCipher().init(Cipher.ENCRYPT_MODE, spec);
			result = byteArrayToHexRepresentation(getCipher().doFinal(input.getBytes()));
		} catch (Exception e) {

		}
		return result;

	}

	public String decrypt(String input, String secretKey) {

		if (input.isEmpty())
			return "";
		
		if (secretKey.isEmpty())
			return input;
		
		String result = "";

		byte[] inputNonHexBytes;
		inputNonHexBytes = hexRepresentationToByteArray(input);

		SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(), ENCRYPTION_DECRYPTION_PROTOCOL);

		try {
			getCipher().init(Cipher.DECRYPT_MODE, spec);
			result = new String(getCipher().doFinal(inputNonHexBytes), "UTF-8");
		} catch (Exception e) {

		}

		return result;

	}

	public KeyPair genPublicSecretKeyPair(){
		KeyPairGenerator keyPairGenerator = null;

		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		if (keyPairGenerator==null)
			return null;

		keyPairGenerator.initialize(1024);

		return keyPairGenerator.generateKeyPair();

	}

	@Override
	public int genUniqueId() {
		return super.genUniqueId();
	}

	@Override
	public String genUniqueIdInMd5() {
		return super.genUniqueIdInMd5();
	}
}
