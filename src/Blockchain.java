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
#	1)  Generate RSA Public/Private Keys(done and tested)
#	2)  Define Data Block Object(done)
#	3)  Define Block Record(done)
#	4)  Define Blockchain(Started)
#	5)  Get 3 Blockchain processes to connect together
#	6)  Setup public key listener and runner(listener started, runner not startd))
#	7)  Setup unverified Blocks listener and runner(listener started, runner not started)
#	8)  Setup update blockchain listener and runner(listener started, runner not started)
#	9)  Define Work
#	10) Setup AsymmetricCryptography (done and tested)
#	11) Setup data manager(done but not tested)
#	12) Write Port Manager(done, not tested)
#	13)	
*/

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.security.*;

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

class KeyGeneratorRunner extends Thread {

	private Socket socket;
	private KeyGenerator keyGen;
	
	KeyGeneratorRunner(Socket socket, KeyGenerator keyGen) {
	
		this.socket = socket;
		this.keyGen = keyGen;
	}
	
	public void run() {
		
		System.out.println("Key Generator Thread running!");
		
		PrintStream out;
		BufferedReader in;
		
		try {
			
			
			//Write Psuedocode here
		}
		catch (IOException ex) {
			
			System.out.println("KeyGen Runner Error! ", ex);
		}
		finally {
			
			this.socket.close();
			in.close();
		}
	}
}

// Sets up a server to listen for Public Key Authentication
// This is based on code we've been writing all term. I based it off what was written for the JokeServer
// Steps:
// 1) Setup listener for a single thread(say thread 0)
// 2) If public key is detected, load into key manager(done)
// Done
class PublicKeyRunner extends Thread {

	private Socket socket;
	
	PublicKeyRunner(Socket socket) {
		
		this.socket = socket;
	}
	
	public void run() {
		
		ObjectInputStream input;

		try {
		
			in = new ObjectInputStream(this.socket.getInputStream());
			
			try {
				
				PublicKey publicKey = (PublicKey) in.readObject();
				
				System.out.println("publicKey is: " + publicKey.toString());
				
				//Check to see if Public Key is setup for this thread
				//If Key isn't setup, generate key
				if(DataManager.getKeyGenerator() == null) {

					DataManager.setKeyGenerator(new KeyGenerator(publicKey);
				}
			}
			catch (Exception e) {
			
				System.out.println("PublicKeyRunner error: " + e);
			}
			finally {
			
				in.close();
				this.socket.close();
			}
		}
		catch (IOException ex) {
		
			System.out.println("PublicKeyRunner socket error: " + ex);
		}
	}
}

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
		
			System.out.println("Failed to start listener for public keys ", ex);
		}
	}
}

class KeyGeneratorListener implements Runnable {
	
	private int port = 1234;
	
	public void run() {
		
		int queueLength = 6;
		Socket socket;
		
		try {
			
			//Setup Listener
			ServerSocket serverSocket = new ServerSocket(port, queueLength);
			
			//Create Key
			KeyGenerator keyGen = new KeyGenerator();
			keyGen.generateKeyPair();
			
			//Set Key
			DataManager.setKeyGenerator(keyGen);
			DataManager.SendKeys(PortManager.getKeyServerPortsInUse());
			
			while (true) {
			
				socket = serverSocket.accept();
				
				new KeyGeneratorRunner(socket, keyGen).start();
			}				
			
		}
		catch (IOException ex) {
		
			System.out.println("Failed to started Key Generator Listener ", ex);
		}
}

// The following class handles the following:
//	1) Reading input files(done not tested)
//	2) Serialize Datablock(done not tested)
//	3) Serialize BlockRecord(done not tested)
//	4) Deserialize Datablock (done not tested)
//	5) Deserialize BlockRecord (done not tested)
//	6) Sending Data/blocks/keys(done not tested)
class DataManager {

	private static KeyGenerator keyGenerator;
	
	public static KeyGenerator getKeyGenerator() {
		
		return keyGenerator;
	}
	
	public static void setKeyGenerator(KeyGenerator keyGenerator) {
		
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
	
	public static String SerializeRecord(ArrayList<BlockRecord> blockRecords) {
	
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		
		return gson.toJson(blockRecords);
	}
	
	public static String SerializeDataBlock(DataBlock dataBlock) {
	
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		
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
				
				socket = new Socket(Blockchain.serverName, serverPorts[i];
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
				
				if(unverifiedBlock == null) {
				
					BlockRecord blockRecord = new BlockRecord();
					
					blockRecord.setBlockID("0");
					//Set Process # for block process
					
					System.out.println("Send unverified block to be signed");
					
					out.println(SerializeRecord(record));
					out.flush();
				}
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
//A Block Record is a collection of data blocks
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
	public String getBlockNumber() {
		
		return this.blockNumber;
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
	public void setSignedDataBlock(string signedDataBlock) {
	
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

//UnverifiedBlockRunner class
class UnverifiedBlockRunner extends Thread {

	private Socket socket;
	
	UnverifiedBlockRunner(Socket socket) {
		
		this.socket = s;
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
				
				//Add block to queue and deserialize the block
			}
			catch (Exception ex) {
				
				System.out.println("Error receiving Unverified Block", ex);
			}
			finally {
			
				in.close();
				this.socket.close();
			}
		}
		catch (IOException ex) {
			
			System.out.println("Error reading in unverified block runner", ex);
		}
	}
}

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
			
			System.out.println("Failed to read data from UnverifiedBlockListener ", ex);
		}
	}
}

class UnverifiedBlockWorker implements Runnable {
	
	//Write Psuedocode here
}

// Define BlockChain
public class Blockchain {
	
	//public static final int processCount = 3;
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
		
		PortManager.setPortsForProcess(pid);
		
		//Start all threads for current pid
		try {
			
			//new Thread().start();
		}
		catch (Exception ex) {
			
			System.out.println("Failed to start all threads in main ", ex);
		}
		
		System.out.println("Using file: " + inputFile);
		
		//Start Threads for this process
		try{
		
				new Thread(new PublicKeyRunner(PortManager.getKeyServerPortUsed())).start();
				new Thread(new UnverifiedBlockRunner(PortManager.getKeyServerPortUsed())).start();
				new Thread(new UpdateBlockchainRunner(PortManager.getKeyServerPortUsed())).start();
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
		catch{Exception ex) {
			
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
			DataManager.SendUnverifiedBlocks(record);
		}
	}
}

//UpdateBlockchainRunner class
class UpdateBlockchainRunner extends Thread {

	private Socket socket;
	
	UpdateBlockchainRunner(Socket socket) {
		
		this.socket = s;
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

class UpdateBlockchainListener extends Runnable {

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
			
				System.out.println("Failed to read data from socket on UpdateBlockchainListener ", ex);
			}
		}
}

//Class for managing ports used for each process
/* As per what was stated in the document

	Ports and servers
	Because we will have multiple participating processes running on the same machine we will need flexibility to avoid port conflicts. For each process:

    Port 4710+process number receives public keys (4710, 4711, 4712)

    Port 4820+process number receives unverified blocks (4820, 4821, 4822)

    Port 4930+process number receives updated blockchains (4930, 4931, 4932)

    Other ports at your discretion, but please use the same scheme: base+process number.

    Feel free to use ten or twenty ports / servers if for some reason you need them, or not. This is entirely up to you. 

*/
class PortManager{

	public static final int keyServerPort = 4710;
	public static final int unverifiedBlockServerPort = 4820;
	public static final int blockchainServerPort = 4930;
	public static final int keyGenPort = 1234;
	
	private static int[] keyServerPortsInUse = new int[3];
	private static int[] unverifiedBlockServerPortsInUse = new int[3];
	private static int[] blockchainServerPortsInUse = new int[3];
	
	private static int keyServerPortUsed;
	private static int unverifiedBlockServerPortUsed;
	private static int blockchainServerPortUsed;
	
	//Getters
	public static int getKeyServerPortUsed() {
	
		return keyServerPortUsed;
	}
	public static int getUnverifiedBlockServerPortUsed() {
	
		return unverifiedBlockServerPortUsed;
	}
	public static int getBlockchainServerPortUsed() {
	
		return blockchainServerPortUsed;
	}
	public static int[] getKeyServerPortsInUse() {
	
		return keyServerPortsInUse;
	}
	public static int[] getUnverifiedBlockServerPortsInUse() {
	
		return unverifiedBlockServerPortsInUse;
	}
	public static int[] getBlockchainServerPortsInUse() {
	
		return blockchainServerPortsInUse;
	}
	
	//Setters
	public static void setPortsForProcess(int processNumber) {
		
		keyServerPortUsed = keyServerPort + processNumber;
		unverifiedBlockServerPortUsed = unverifiedBlockServerPort + processNumber;
		blockchainServerPortUsed = blockchainServerPortUsed + processNumber;
	}
}