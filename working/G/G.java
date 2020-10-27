/*
	Write some standalone code similar to [C]
	above that generates a public key / private 
	key pair, converts the public key to a string
	and then to a JSON string, writes it to disk
	as JSON, reads it back in, coverts it back to
	a binary-format valid public key, and checks
	that you can still encrypt with the secret key
	(sign some data) and decrypt with the public key
	(veryify the signature) that you previously wrote
	out to disk in JSON format. 
*/

/*
#	To-Do List	
#	1) Generate Public/Private Key(done)
#	2) Convert Keys to Strings(done)
#	3) Convert key strings to JSON on disk(done)
#	4) Read JSON file back in(done)
#	5) Convert back to binary format?
#	6) Check to see if you can still encrypt with the secret key(done)
#	7) Decrypt with public key stored on disk(done)
*/

/*
	I used the following links to help with this part:
	https://www.novixys.com/blog/how-to-generate-rsa-keys-java/#2_Generating_a_Key_Pair
	https://docs.oracle.com/javase/8/docs/api/java/security/KeyPairGenerator.html
	https://kodejava.org/how-do-i-generate-public-and-private-keys/
	https://mkyong.com/java/java-asymmetric-cryptography-example/
	
*/

import java.security.*;
import java.util.Base64;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.io.FileOutputStream;

import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.*;


class AsymmetricCryptography {
	
	private Cipher cipher;
	
	public AsymmetricCryptography() throws NoSuchAlgorithmException, NoSuchPaddingException {
		
		this.cipher = Cipher.getInstance("RSA");
	}
	
	//getPrivateKey method
	public PrivateKey getPrivate(String filename) throws Exception {
		
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(spec);
	}
	
	//getPublicKey method
	public PublicKey getPublic(String filename) throws Exception {
		
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(spec);
	}
	
	public byte[] getFileInBytes(File f) throws IOException {
		
		FileInputStream fis = new FileInputStream(f);
		byte[] fbytes = new byte[(int) f.length()];
		fis.read(fbytes);
		fis.close();
		return fbytes;
	}
	
	private void writeToFile(File output, byte[] toWrite) throws IllegalBlockSizeException, BadPaddingException, IOException {
		
		FileOutputStream fos = new FileOutputStream(output);
		fos.write(toWrite);
		fos.flush();
		fos.close();
	}
	
	public void encryptFile(byte[] input, File output, PrivateKey key) throws IOException, GeneralSecurityException {
		
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		writeToFile(output, this.cipher.doFinal(input));
	}
	
	public void decryptFile(byte[] input, File output, PublicKey key) throws IOException, GeneralSecurityException {
		
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		writeToFile(output, this.cipher.doFinal(input));
	}
}

class GenerateKeys {
	
	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	public GenerateKeys(int keyLength) throws NoSuchAlgorithmException, NoSuchProviderException {
	
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(keyLength);
	}
	
	public void createKeys() {
		
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}
	
	public PrivateKey getPrivateKey() {
	
		return this.privateKey;
	}
	
	public PublicKey getPublicKey() {
	
		return this.publicKey;
	}
	
	public void writeToFile(String path, byte[] key) throws IOException {
		
		File f = new File(path);
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}
}

public class G {

	public static void main(String []args) throws IOException, Exception {
		
		/*
		Gson gson = new Gson();
		
		//Generating Files
		try {
		
			//Instantiate Key Pair Generator
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		
			//Initialize KPG
			kpg.initialize(2048); //2048 is the bit size provided in the documentation, but for simplicity I will drop down to 256
		
			//Generate KP
			KeyPair kp = kpg.generateKeyPair();
		
			//Instantiate Keys
			PublicKey pubKey = kp.getPublic();
			PrivateKey pvtKey = kp.getPrivate();
		
			//Parse Keys to Strings
			Base64.Encoder encoder = Base64.getEncoder();
			//Debug information to confirm keys are generated properly
			//System.out.println("privateKey: " + encoder.encodeToString(pvtKey.getEncoded()));
			//System.out.println(" ");
			//System.out.println("publicKey: " + encoder.encodeToString(pubKey.getEncoded()));
			String pub = encoder.encodeToString(pubKey.getEncoded());
			String pvt = encoder.encodeToString(pvtKey.getEncoded());
			
			//Convert String to JSON with GSON
			
			//Debug information
			//System.out.println(gson.toJson(pub));
			//System.out.println("");
			//System.out.println(gson.toJson(pvt));
			
			String pubJson = gson.toJson(pub);
			String pvtJson = gson.toJson(pvt);
			
			//Write JSON keys to disk
			//FileOutputStream writerPublic = new FileOutputStream("public.pub");
			Writer writerPublic = new FileWriter("public.pub");
			writerPublic.write(pubJson);
			writerPublic.close();
			
			//FileOutputStream writerPrivate = new FileOutputStream("private.key");
			Writer writerPrivate = new FileWriter("private.key");
			writerPrivate.write(pvtJson);
			writerPrivate.close();
			
			System.out.println("Public and Private Keys generated and outputted to files!");
		}
		catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		}
		
		//Reading in Files
		try {
			
			
			//Create a reader
			Reader readerPublic = Files.newBufferedReader(Paths.get("public.pub"));
			
			//convert JSON string to BlockRecord object
			String publicKeyString = gson.fromJson(readerPublic, String.class);
			
			//print KeyString Object
			//System.out.println(publicKeyString);
			
			//close reader
			readerPublic.close();
			
			//Debug space line
			//System.out.println("");
			
			//Create a reader
			Reader readerPrivate = Files.newBufferedReader(Paths.get("private.key"));
			
			//convert JSON string to BlockRecord object
			String privateKeyString = gson.fromJson(readerPrivate, String.class);
			
			//print KeyString Object
			//System.out.println(privateKeyString);
			
			//close reader
			readerPrivate.close();
			
			System.out.println("Public and Private Keys read back in from file!");
		}
		catch (IOException e) {
		
			e.printStackTrace();
		}*/
		
		GenerateKeys gk;
		
		try {
			
			gk = new GenerateKeys(1024);
			gk.createKeys();
			gk.writeToFile("id_rsa.pub", gk.getPublicKey().getEncoded());
			gk.writeToFile("id_rsa", gk.getPrivateKey().getEncoded());
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			
			System.err.println(e.getMessage());
		} 
		catch (IOException e) {
			
			System.err.println(e.getMessage());
		}
		
		AsymmetricCryptography ac = new AsymmetricCryptography();
		
		PrivateKey privateKey = ac.getPrivate("id_rsa");
		PublicKey publicKey = ac.getPublic("id_rsa.pub");
		
		ac.encryptFile(ac.getFileInBytes(new File("encrypt_me.txt")), new File("encrypt_me_encrypted.txt"), privateKey);
		ac.decryptFile(ac.getFileInBytes(new File("encrypt_me_encrypted.txt")),new File("encrypt_me_decrypted.txt"), publicKey);
		
	}
	
	
}