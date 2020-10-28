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
#	4) Define Blockchain
#	5) More to be defined!
*/

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
	public static final int pid = 0;
	public static final String serverName = "localhost";
	public static final ArrayList<BlockRecord> ledger = new ArrayList<BlockRecord>();
	
	public static void main(String []args){
	
		String inputFile;
		
		if
	}
}
