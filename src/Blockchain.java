/*
    Name: Amad Ali
    Date Started: 	10/16/2020
    Date Due: 		11/04/2020
	
	GitHub Repo: 	https://github.com/BakedBeanz1111/csc435Blockchain
	
	Java Version: 	1.8.0_211
    
	Instructions:
	
    To Compile:
					javac -cp "gson-2.8.4.jar" Blockchain.java
	
	To use:
					java BlockChain [ProcessNumber]			where [ProcessNumber] is [0,1,2]
    
	Also:
					You can use the provided batch script to run the project. Please read the batch script before you run it!
*/

/*
#	To-Do List
#	1) Generate RSA Public/Private Keys(done)
#	2) Define Data Block Object(done)
#	3) Define Block Record(done)
#	4) Define Blockchain(Started)
#	5) More to be defined!
*/

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.security.*;

// To Generate RSA Public/Private Keys, I will be following the procedure outlined in this tutorial: https://mkyong.com/java/java-asymmetric-cryptography-example/
class GenerateKeys{
	
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
	
		file f = new File(path);
		FileOutputStream fos = new FileOutputStream(f);
		
		fos.write(key);
		fos.flush();
		fos.close();
	}
	
	public void testGenerateKeys() {
	
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
	}
}

//To do AsymmetricCryptography, I will be following the procedure outlined in this tutorial: https://mkyong.com/java/java-asymmetric-cryptography-example/
class AsymmetricCryptography{
	
	private Cipher cipher;
	
	public AsymmetricCryptography() throws NoSuchAlgorithmException, NoSuchPaddingException {
		
		this.cipher = Cipher.getInstance("RSA");
	}
	
	public PrivateKey getPrivate(String filename) throws Exception {
	
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		return keyFactory.generatePrivate(spec);
	}
	
	public PublicKey getPublic(String filename) throws Exception {
		
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		return keyFactory.generatePublic(spec);
	}
	
	public void encryptFile(byte[] input, File output, PrivateKey key) throws IOException, GeneralSecurityException {
		
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		writeToFile(output, this.cipher.doFinal(input));
	}
	
	public void decryptFile(byte[] input, File output, PublicKey key) throws IOException, GeneralSecurityException {
		
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		writeToFile(output, this.cipher.doFinal(input));
	}
	
	private void writeToFile(File output, byte[] toWrite) throws IllegalBlockSizeException, BadPaddingException, IOException {
		
		FileOutputStream fos = new FileOutputStream(output);
		
		fos.write(toWrite);
		fos.flush();
		fos.close();
	}
	
	public String encryptText(String msg, PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
	}
	
	public String decryptText(String msg, PublicKey key) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
	}
	
	public byte[] getFileInBytes(File f) throws IOException {
		
		FileInputStream fis = new FileInputStream(f);
		byte[] fbytes = new byte[(int) f.length()];
		
		fis.read(fbytes);
		fis.close();
		return fbytes;
	}
	
	public void testAsymmetricCryptography() throws Exception {
	
		AsymmetricCryptography ac = new AsymmetricCryptography();
		PrivateKey privateKey = ac.getPrivate("id_rsa");
		PublicKey publicKey = ac.getPublic("id_rsa.pub");
		
		String msg = "If you can read this, it worked!";
		String encrypted_msg = ac.encryptText(msg, privateKey);
		String decrypted_msg = ac.decryptText(encrypted_msg, publicKey);
		
		System.out.println("Original Message: " + msg);
		System.out.println("Encrypted Message: " + encrypted_msg);
		System.out.println("Decrypted Message: " + decrypted_msg);

		ac.encryptFile(ac.getFileInBytes(new File("text.txt")), new File("text_encrypted.txt"),privateKey); //text.txt is generated during the build process of my batch script
		ac.decryptFile(ac.getFileInBytes(new File("text_encrypted.txt")), new File("text_decrypted.txt"), publicKey);
	}
}

// Define Data Block Object from input
// I am basing this object off of the BlockInputX.txt files
// I based the inspiration for this class from BlockInputG.java
class DataBlock {
	
	//Default Values for object
	private String firstName = "";
	private String lastName = "";
	private String ssn = "";
	private String dob = "";
	private String diagnosis = "";
	private String treatment = "";
	private String medication = "";
	
	//Getters
	public String getFirstName() {
		
		return this.firstName;
	}
	public String getLastName() {
		
		return this.lastName;
	}
	public String getSSN() {
		
		return this.ssn;
	}
	public String getDOB() {
		
		return this.dob;
	}
	public String getDiagnosis() {
		
		return this.diagnosis;
	}
	public String getTreatment() {
		
		return this.getTreatment;
	}
	public String getMedication() {
		
		return this.medication;
	}
	
	//Setters
	public void setFirstName(String firstName) {
	
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
	
		this.lastName = lastName;
	}
	public void setSSN(String ssn) {
	
		this.ssn = ssn;
	}
	public void setDOB(String dob) {
	
		this.dob = dob;
	}
	public void setDiagnosis(String diagnosis) {
	
		this.diagnosis = diagnosis;
	}
	public void setTreatment(String treatment) {
	
		this.treatment = treatment;
	}
	public void setMedication(String medication) {
	
		this.medication = medication;
	}
}

// Define Block Record
//This was taken from BlockInputG.java
class BlockRecord {
	
	String blockID;
	String timeStamp;
	String verificationProcessID;
	String previousHash;
	UUID uuid;
	
	//getters
	public String getBlockID() {
		
		return this.blockID;
	}
	public String getTimeStamp() {
		
		return this.timeStamp;
	}
	public String getVerificationProcessID() {
		
		return this.verificationProcessID;
	}
	public String getPreviousHash() {
		
		return this.previousHash;
	}
	public UUID getUUID() {
		
		return this.uuid;
	}
	
	//setters
	public void setBlockID(String blockID) {
	
		this.blockID = blockID;
	}
	public void setTimeStamp(String timeStamp) {
	
		this.timeStamp = timeStamp;
	}
	public void setVerificationProcessID(String verificationProcessID) {
	
		this.verificationProcessID = verificationProcessID;
	}
	public void setPreviousHash(String previousHash) {
	
		this.previousHash = previousHash;
	}
	public void setUUID(UUID uuid) {
	
		this.uuid = uuid;
	}
}

// Define BlockChain
public class Blockchain {
	
	public static final int processCount = 3;
	public static int pid = 0;
	public static final String serverName = "localhost";
	public static final ArrayList<BlockRecord> ledger = new ArrayList<BlockRecord>();
	
	public static void main(String []args){
	
		String inputFile;
		
		if(args.length == 0) {
			
			System.out.println("Since no Process ID was provided, defaulting to pid = 0");
		}
		else {
			
			pid = Integer.parseInt(args[0]);
		}
		
		switch(pid) {
		
			case 0: {
				
				inputFile = "BlockInput0.txt";
				break;
			}
			case 1: {
				
				inputFile = "BlockInput1.txt";
				break;
			}
			case 2: {
				
				inputFile = "BlockInput2.txt";
				break;
			}
			default: {
			
				inputFile = "BlockInput0.txt";
				break;
			}
		}
	}
}
