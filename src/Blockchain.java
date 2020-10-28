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
#	2) Define Data Block Object
#	3) Define Block Record
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

// Define Data Block Object
class DataBlock {
}

// Define Block Record
class BlockRecord {
}

// Define BlockChain
public class Blockchain {
}
