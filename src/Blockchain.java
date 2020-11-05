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
					You can use the provided batch script to run the project. Please read the batch script before you run it! EDIT!!! No Batch script provided since I didn't get this thing working :(
					The batch script would have done the following:
					1) Clean and sanitize the build environment(delete .class/.txt/keys)
					2) Compile Java Code
					3) Start 3 processes like this:		java Blockchain 0/java Blockchain 1/java Blockchain 2
					
*/

/*
#	To-Do List
#	1)  Generate RSA Public/Private Keys(done and tested)
#	2)  Define Data Block Object(done)
#	3)  Define Block Record(done)
#	4)  Define Blockchain(Started)
#	5)  Get 3 Blockchain processes to connect together
#	6)  Setup public key listener and runner(listener started, runner startd))
#	7)  Setup unverified Blocks listener and runner(listener started, runner started)
#	8)  Setup update blockchain listener and runner(listener started, runner started)
#	9)  Define Work
#	10) Setup AsymmetricCryptography (done)
#	11) Setup data manager(done)
#	12) Write Port Manager(done)
#	13)	
*/

//Import Statements
import com.google.gson.*;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.*;
import java.nio.file.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.lang.reflect.Type;

// To Generate RSA Public/Private Keys, I will be following the procedure outlined in this tutorial: https://mkyong.com/java/java-asymmetric-cryptography-example/
// This generates and writes keys
// Not Tested within Blockchain but tested as part of Development Guide
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
	
	//getters
	public PrivateKey getPrivateKey() {
	
		return this.privateKey;
	}
	
	public PublicKey getPublicKey() {
	
		return this.publicKey;
	}
	
	//No Setters for Generating Keys
	
	public void writeToFile(String path, byte[] key) throws IOException {
	
		File f = new File(path);
		FileOutputStream fos = new FileOutputStream(f);
		
		fos.write(key);
		fos.flush();
		fos.close();
	}
	
	//This function was written very early on in the assignment's development to test functionality for this bit of code
	//This function never gets called
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

// To do AsymmetricCryptography, I will be following the procedure outlined in this tutorial: https://mkyong.com/java/java-asymmetric-cryptography-example/
// This handles encryption/decryption
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
	
	//The following was commented out because I was getting build issues with this segment of code that is actually superfluous to the assignment. But rule of thumb is I never delete anything, this needs to be cleaned up eventually
	/*
	public String encryptText(String msg, PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
	}
	
	public String decryptText(String msg, PublicKey key) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
	}*/
	
	public byte[] getFileInBytes(File f) throws IOException {
		
		FileInputStream fis = new FileInputStream(f);
		byte[] fbytes = new byte[(int) f.length()];
		
		fis.read(fbytes);
		fis.close();
		return fbytes;
	}
	
	//This function was written very early on in the assignment's development to test functionality for this bit of code
	//This function never gets called
	public void testAsymmetricCryptography() throws Exception {
	
		AsymmetricCryptography ac = new AsymmetricCryptography();
		PrivateKey privateKey = ac.getPrivate("id_rsa");
		PublicKey publicKey = ac.getPublic("id_rsa.pub");
		
		String msg = "If you can read this, it worked!";
		//String encrypted_msg = ac.encryptText(msg, privateKey);
		//String decrypted_msg = ac.decryptText(encrypted_msg, publicKey);
		
		System.out.println("Original Message: " + msg);
		//System.out.println("Encrypted Message: " + encrypted_msg);
		//System.out.println("Decrypted Message: " + decrypted_msg);

		ac.encryptFile(ac.getFileInBytes(new File("text.txt")), new File("text_encrypted.txt"),privateKey); //text.txt is generated during the build process of my batch script
		ac.decryptFile(ac.getFileInBytes(new File("text_encrypted.txt")), new File("text_decrypted.txt"), publicKey);
	}
}

// The idea behind KeyGeneratorRunner was to have the KeyGeneratorListener to trigger the runner to sign with the private key but I didn't get that far in the implementation
// I based this work off of what we did all quarter with javing a Server Listener and a Worker thread, just like in the joke server assignment
class KeyGeneratorRunner extends Thread {

	private Socket socket;
	private GenerateKeys keyGen;
	
	KeyGeneratorRunner(Socket socket, GenerateKeys keyGen) {
	
		this.socket = socket;
		this.keyGen = keyGen;
	}
	
	public void run() {
		
		System.out.println("Key Generator Thread running!");
		
		PrintStream out = null;
		BufferedReader in = null;
		
		/*
		try {
			
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			//Write Psuedocode here
			
			try {
				
				
			}
			catch (Exception ex) {
			
				System.out.println("KeyGenRunner error: " + ex);
			}
		}
		catch (IOException ex) {
			
			System.out.println("KeyGen Runner Error! " + ex);
		}
		finally {
			
			this.socket.close();
			in.close();
		}*/
	}
}

// This is based on code we've been writing all term. I based it off what was written for the JokeServer
// The idea behind PublicKeyRunner was to have the PublicKeyListener to trigger the runner to make sure a key is set for this thread. I didn't get far enough into the project to test this functionality
class PublicKeyRunner extends Thread {

	private Socket socket;
	
	PublicKeyRunner(Socket socket) {
		
		this.socket = socket;
	}
	
	public void run() {
		
		ObjectInputStream input;

		try {
		
			input = new ObjectInputStream(this.socket.getInputStream());
			
			try {
				
				PublicKey publicKey = (PublicKey) input.readObject();
				
				System.out.println("publicKey is: " + publicKey.toString());
				
				//Check to see if Public Key is setup for this thread
				//If Key isn't setup, generate key
				if(DataManager.getKeyGenerator() == null) {

					//Create Key
					GenerateKeys keyGen = new GenerateKeys(1024);
					keyGen.createKeys();

					DataManager.setKeyGenerator(new KeyGenerator(publicKey));
				}
			}
			catch (Exception e) {
			
				System.out.println("PublicKeyRunner error: " + e);
			}
			finally {
			
				input.close();
				this.socket.close();
			}
		}
		catch (IOException ex) {
		
			System.out.println("PublicKeyRunner socket error: " + ex);
		}
	}
}

//This sets up a listener on the appropriate port 
//It waits for incoming connections on a port and spawns the PublicKeyRunner when it gets a connection
//Did not get to test this due to time limit constraints
class PublicKeyListener implements Runnable {

	private int port;
	
	PublicKeyListener(int port) {
	
		this.port = port;
	}
	
	public void run() {
	
		int queueLength = 6;
		Socket socket;
		
		try {
		
			//Starting listener
			ServerSocket serverSocket = new ServerSocket(port, queueLength);
			
			while (true) {
			
				socket = serverSocket.accept();
				
				new PublicKeyRunner(socket).start();
			}
		}
		catch (IOException ex) {
		
			System.out.println("Failed to start listener for public keys " + ex);
		}
	}
}
//This sets up a listener on the appropriate port 
//It waits for incoming connections on a port and spawns the KeyGeneratorRunner when it gets a connection
//Did not get to test this due to time limit constraints
class KeyGeneratorListener implements Runnable {
	
	private int[] ports = new int[] {1234, 1235, 1236}; //Just pulled some random port out of thin air as per the project spec under "Porst and Servers" on https://condor.depaul.edu/elliott/435/hw/programs/Blockchain/program-block.html
	
	public void run() {
		
		int queueLength = 6;
		Socket socket;
		
		try {
			
			//Setup Listener
			ServerSocket serverSocket = new ServerSocket(1234, queueLength);
			
			//Create Key
			GenerateKeys keyGen = new GenerateKeys(1024);
			keyGen.createKeys();
			
			//Set Key
			DataManager.setKeyGenerator(keyGen);
			DataManager.SendKeys(ports);
			
			while (true) {
			
				socket = serverSocket.accept();
				
				new KeyGeneratorRunner(socket, keyGen).start();
			}				
			
		}
		catch (IOException ex) {
		
			System.out.println("Failed to started Key Generator Listener " + ex);
		}
	}
}

// The following class handles the following:
//	1) Reading input files(done not tested)
//	2) Serialize Datablock(done not tested)
//	3) Serialize BlockRecord(done not tested)
//	4) Deserialize Datablock (done not tested)
//	5) Deserialize BlockRecord (done not tested)
//	6) Sending Data/blocks/keys(done not tested)

//The idea behind this class was to have a be all class for dealing with data. I did not get far enough into the project to test this
// I used the following document as the guidance for handling all gson operations: https://mkyong.com/java/how-to-parse-json-with-gson/
class DataManager {

	private static GenerateKeys keyGenerator;
	
	public static GenerateKeys getKeyGenerator() {
		
		return keyGenerator;
	}
	
	public static void setKeyGenerator(GenerateKeys keyGenerator) {
		
		keyGenerator = keyGenerator;
	}
	
	public static ArrayList<BlockRecord> ReadInputFile(String filename, int pid) {
	
		ArrayList<BlockRecord> blockRecords = new ArrayList<BlockRecord>();
		String currentPID = Integer.toString(pid);
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
		
			String input = br.readLine();
			
			while(input != null) {
			
				BlockRecord record = new BlockRecord();
				
				String[] inputData = input.split(" +");
				
				record.getDataBlock().setFirstName(inputData[0]);
				record.getDataBlock().setLastName(inputData[1]);
				record.getDataBlock().setDOB(inputData[2]);
				record.getDataBlock().setSSN(inputData[3]);
				record.getDataBlock().setDiagnosis(inputData[4]);
				record.getDataBlock().setTreatment(inputData[5]);
				record.getDataBlock().setMedication(inputData[6]);
				
				blockRecords.add(record);
			}
		}
		catch (Exception e) {
		
			System.out.println("Data Manager error reading input file :" + e);
		}
		//Debug information about the records being collected
		//System.out.println("Block Record Size: " + blockRecords.size());
		
		return blockRecords;
	}
	
	public static String SerializeRecord(BlockRecord blockRecord) {
	
		Gson gson = new GsonBuilder().create();
		
		return gson.toJson(blockRecord);
	}
	
	public static String SerializeDataBlock(DataBlock dataBlock) {
	
		Gson gson = new GsonBuilder().create();
		
		return gson.toJson(dataBlock);
	}
	
	public static BlockRecord DeserializeRecord(String recordString) {
	
		return new Gson().fromJson(recordString, BlockRecord.class);
	}
	
	public static ArrayList<BlockRecord> DeserializeDataBlock(String ledgerString) {
	
		Type listType = new TypeToken<ArrayList<BlockRecord>>(){}.getType();
		
		return new Gson().fromJson(ledgerString, listType);
	}
	
	public static void SendKeys(int[] serverPorts) {
	
		Socket socket;
		ObjectOutputStream toServer;
		
		for(int i = 0; i < serverPorts.length; i++) {
		
			try {
				
				socket = new Socket(Blockchain.serverName, serverPorts[i]);
				toServer = new ObjectOutputStream(socket.getOutputStream());
				
				//Debug Information about keys
				System.out.println("Sending keys to process " + i);
				toServer.writeObject(keyGenerator.getPublicKey());
				
				toServer.flush();
				toServer.close();
				socket.close();
			}
			catch (IOException ex) {
				
				System.out.println("Error sending public keys from Data Manager: " + ex);
				
				return;
			}
		}
	}
	
	//Sending unverified Block
	public static void SendUnverifiedBlocks(BlockRecord unverifiedBlock) {
	
		try {
			
			Socket socket;
			PrintStream out;
			
			try {
				
				//socket = new Socket(server name, port);
				out = new PrintStream(socket.getOutputStream());
				
				//If block does not exist, create the block
				if(unverifiedBlock == null) {
				
					BlockRecord blockRecord = new BlockRecord();
					
					blockRecord.setBlockID("0");
					//Set Process # for block process
					
					System.out.println("Send unverified block to be signed");
					
					out.println(SerializeRecord(blockRecord));
					out.flush();
				}
				//else 
				//increment block id
				//Serialize blockrecord
			}
			catch (IOException ex) {
			
				System.out.println("error sending unverified block " + ex);
			}
			finally {
		
				out.close();
				socket.close();
			}
		}
		catch(IOException ex) {
		
			System.out.println("Send Unverified Block error: " + ex);
		}
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
		
		return this.treatment;
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
//A Block Record is a collection of data blocks
// I found the inspiration for the comparator from this article while doing development Guide assignment M: https://www.callicoder.com/java-priority-queue/
class BlockRecord implements Comparable<BlockRecord> {
	
	private DataBlock dataBlock = new DataBlock();
	private int blockNumber = 0;
	private String hashedDataBlock = "";
	private String signedDataBlock = "";
	private Date spawnTime = new Date();
	private String blockID = "";
	private String signedBlockID = "";
	private String verificationProcessID = "";
	private String previousHash = "";
	
	//getters
	public int getBlockNumber() {
		
		return blockNumber;
	}
	public String getHashedDataBlock() {
	
		return hashedDataBlock;
	}
	public String getSignedDataBlock() {
	
		return signedDataBlock;
	}
	public Date getSpawnTime() {
	
		return spawnTime;
	}
	public String getBlockID() {
	
		return blockID;
	}
	public String getSignedBlockID() {
	
		return signedBlockID;
	}
	public String getVerificationProcessID() {
	
		return verificationProcessID;
	}
	public String getPreviousHash() {
		
		return previousHash;
	}
	public DataBlock getDataBlock() {
		
		return this.dataBlock;
	}
	
	//setters
	public void setBlockNumber(int blockNumber) {
	
		this.blockNumber = blockNumber;
	}
	public void setHashedDataBlock(String hashedDataBlock) {
	
		this.hashedDataBlock = hashedDataBlock;
	}
	public void setSignedDataBlock(String signedDataBlock) {
	
		this.signedDataBlock = signedDataBlock;
	}
	public void setSpawnTime(Date spawnTime) {
	
		this.spawnTime = spawnTime;
	}
	public void setBlockID(String blockID) {
	
		this.blockID = blockID;
	}
	public void setVerificationProcessID(String verificationProcessID) {
	
		this.verificationProcessID = verificationProcessID;
	}
	public void setPreviousHash(String previousHash) {
	
		this.previousHash = previousHash;
	}
	
	//Comparable
	public int compareTo(BlockRecord blockRecord) {
	
		return this.spawnTime.compareTo(blockRecord.spawnTime);
	}

}

// This is based on code we've been writing all term. I based it off what was written for the JokeServer
// The idea behind UnverifiedBlockRunner was to have the UnverifiedBlockListener to trigger the runner to start. I didn't get far enough into the project to test this functionality
class UnverifiedBlockRunner extends Thread {

	private Socket socket;
	
	UnverifiedBlockRunner(Socket socket) {
		
		this.socket = socket;
	}
	
	public void run() {
		
		System.out.println("Unverified Block Runner Started");
		
		BufferedReader in = null;
		
		try {
			
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			
			try {
			
				System.out.println("Reading in an unverified block!");
				
				String nextBlock = "";
				String incomingBlock = in.readLine();
				
				while (incomingBlock != null) {
				
					nextBlock += incomingBlock;
				}
				
				//deserialize block
			}
			catch (Exception ex) {
				
				System.out.println("Error receiving Unverified Block " + ex);
			}
			finally {
			
				in.close();
				this.socket.close();
			}
		}
		catch (IOException ex) {
			
			System.out.println("Error reading in unverified block runner: " + ex);
		}
	}
}

//This sets up a listener on the appropriate port 
//It waits for incoming connections on a port and spawns the PublicKeyRunner when it gets a connection
//Did not get to test this due to time limit constraints
class UnverifiedBlockListener implements Runnable {

	private int port;
	
	UnverifiedBlockListener (int port) {
	
		this.port = port;
	}
	
	public void run() {
		
		int queueLength = 6;
		Socket socket;
		
		try {
			
			//Starting listener
			ServerSocket serverSocket = new ServerSocket(port, queueLength);
			
			while (true) {
			
				socket = serverSocket.accept();
				
				new UnverifiedBlockRunner(socket).start();
			}
			
		}
		catch (IOException ex) {
			
			System.out.println("Failed to read data from UnverifiedBlockListener " + ex);
		}
	}
}

//This sets up a listener on the appropriate port 
//It waits for incoming connections on a port and spawns the PublicKeyRunner when it gets a connection
//Did not get to test this due to time limit constraints
class UnverifiedBlockWorker implements Runnable {
	
	public void run() {
	
		System.out.println("Write Psuedocode here");
	}
	
}

// Define BlockChain
public class Blockchain {
	
	public static int pid = 0;
	public static final String serverName = "localhost";
	public static final ArrayList<BlockRecord> ledger = new ArrayList<BlockRecord>();

	public static void main(String []args){
	
		String inputFile;
		
		//Set command line arguments about thread/process
		if(args.length == 0) {
			
			System.out.println("Since no Process ID was provided, defaulting to pid = 0");
		}
		else {
			
			pid = Integer.parseInt(args[0]);
		}
		
		//Define input file
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
		
		//Start all threads for current pid
		try {
			
			//new Thread().start();
		}
		catch (Exception ex) {
			
			System.out.println("Failed to start all threads in main " + ex);
		}
		
		System.out.println("Using file: " + inputFile);
		
		//Start Threads for this process
		try{
			System.out.println("Launch all the threads and set all the ports!");
		}
		catch (Exception ex) {
		
			System.out.println("Main loop failed in starting threads: " + ex);
		}
		
		//Block threads til Public Key available
		try {
			
			while(DataManager.getKeyGenerator() == null) {
			
				Thread.sleep(3000);
			}
		}
		catch (Exception ex) {
			
			System.out.println("Error while waiting for Public Key: " + ex);
		}
		
		//Wait for all threads to start, when process 2 starts, send out the initial unverified blockID
		if (pid == 2) {
			
			//Setup DataManager to send Unverified blocks
			DataManager.SendUnverifiedBlocks(null);
		}
		
		//Initial process has completed, now serialize blocks from file
		ArrayList<BlockRecord> blockRecords = DataManager.ReadInputFile(inputFile, pid);
		
		//Send all the blocks!
		for (BlockRecord blockRecord : blockRecords) {
		
			//Send over unverified blocks
			DataManager.SendUnverifiedBlocks(blockRecord);
		}
	}
}

//UpdateBlockchainRunner class
class UpdateBlockchainRunner extends Thread {

	private Socket socket;
	
	UpdateBlockchainRunner(Socket socket) {
		
		this.socket = socket;
	}
	
	public void run() {
		
		System.out.println("Updating BLockchain Runner");
		BufferedReader in;
		
		try {
			
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			
			try {
				
				String ledger = "";
				String incomingBlock = in.readLine();
				
				while(incomingBlock != null) {
				
					ledger += incomingBlock;
				}
			}
			catch (Exception ex) {
			
				System.out.println("error reading in the data");
			}
			finally {
			
				this.socket.close();
				in.close();
			}
		}
		catch (IOException ex) {
		
			System.out.println("Error reading from socket");
		}
	}
}

//This sets up a listener on the appropriate port 
//It waits for incoming connections on a port and spawns the PublicKeyRunner when it gets a connection
//Did not get to test this due to time limit constraints
class UpdateBlockchainListener implements Runnable {

		private int port;
		
		UpdateBlockchainListener(int port) {
		
			this.port = port;
		}
		
		public void run() {
		
			int queueLength = 6;
			Socket socket;
			
			try {
				
				ServerSocket serverSocket = new ServerSocket(port, queueLength);
				
				while (true) {
				
					socket = serverSocket.accept();
					
					new UpdateBlockchainRunner(socket).start();
				}
			
			}
			catch (IOException ex) {
			
				System.out.println("Failed to read data from socket on UpdateBlockchainListener " + ex);
			}
		}
}