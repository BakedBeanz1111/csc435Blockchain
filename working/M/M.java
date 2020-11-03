/*
	M. In a standalone program, implement a priority queue. Create a couple
	of fake unverified blocks with timestamps. Insert the blocks into the
	priority queue, sorted (queued) by timestamp priority. 
*/

/*
#	To-Do List
#	1) Write Priority Queue in Java(done sorta, the compare doesn't work)
#	2) Feed data into algorithm(done)
#	3) Add Time Stamps to all fed in data(manually added one)
#	4) Sorta data by order of time stamp
*/

/*
	I use the following links to help in my development of this task:
	https://www.callicoder.com/java-priority-queue/
*/

import java.util.PriorityQueue;
import java.io.StringWriter;
import java.io.StringReader;

/* CDE: The encryption needed for signing the hash: */

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import java.security.spec.*;
// Ah, heck:
import java.security.*;
import java.util.UUID;
import java.util.Objects;

// Produces a 64-bye string representing 256 bits of the hash output. 4 bits per character
import java.security.MessageDigest; // To produce the SHA-256 hash.

//BlockRecord was taken directly from BlockJ.java sample code provided in class
//I just fixed the coding style of how everything looks
class BlockRecord implements Comparable<BlockRecord>{
	
	/* Examples of block fields. You should pick, and justify, your own set: */
	String BlockID;
	String VerificationProcessID;
	String PreviousHash; // We'll copy from previous block
	String uuid; // Just to show how JSON marshals this binary data.
	String Fname;
	String Lname;
	String SSNum;
	String DOB;
	String Diag;
	String Treat;
	String Rx;
	String RandomSeed; // Our guess. Ultimately our winning guess.
	String WinningHash;
	int timeStamp;
	
	public BlockRecord(String BlockID, String VerificationProcessID, String PreviousHash, String uuid, String Fname, String Lname, String SSNum, String DOB, String Diag, String Treat, String Rx, String RandomSeed, String WinningHash, int timeStamp) {
	
		this.BlockID = BlockID;
		this. VerificationProcessID = VerificationProcessID;
		this.PreviousHash = PreviousHash;
		this.uuid = uuid;
		this.Fname = Fname;
		this.Lname = Lname;
		this.SSNum = SSNum;
		this.Diag = Diag;
		this.Treat = Treat;
		this.Rx = Rx;
		this.RandomSeed = RandomSeed;
		this.WinningHash = WinningHash;
		this.timeStamp = timeStamp;
	}
  
	/* Examples of accessors for the BlockRecord fields: */
	public String getBlockID() {return BlockID;}
	public void setBlockID(String BID){this.BlockID = BID;}
	
	public String getVerificationProcessID() {return VerificationProcessID;}
	public void setVerificationProcessID(String VID){this.VerificationProcessID = VID;}
	
	public String getPreviousHash() {return this.PreviousHash;}
	public void setPreviousHash (String PH){this.PreviousHash = PH;}
  
	public String getUUID() {return uuid;} // Later will show how JSON marshals as a string. Compare to BlockID.
	public void setUUID (String ud){this.uuid = ud;}
	
	public String getLname() {return Lname;}
	public void setLname (String LN){this.Lname = LN;}
	
	public String getFname() {return Fname;}
	public void setFname (String FN){this.Fname = FN;}
  
	public String getSSNum() {return SSNum;}
	public void setSSNum (String SS){this.SSNum = SS;}
  
	public String getDOB() {return DOB;}
	public void setDOB (String RS){this.DOB = RS;}

	public String getDiag() {return Diag;}
	public void setDiag (String D){this.Diag = D;}

	public String getTreat() {return Treat;}
	public void setTreat (String Tr){this.Treat = Tr;}

	public String getRx() {return Rx;}
	public void setRx (String Rx){this.Rx = Rx;}

	public String getRandomSeed() {return RandomSeed;}
	public void setRandomSeed (String RS){this.RandomSeed = RS;}
  
	public String getWinningHash() {return WinningHash;}
	public void setWinningHash (String WH){this.WinningHash = WH;}
	
	public int getTimeStamp() { return timeStamp;}
	public void setTimeStamp(int timeStamp) { this.timeStamp = timeStamp;}
	
	public int compareTo(BlockRecord blockRecord) {
	
		if(this.getTimeStamp() > blockRecord.getTimeStamp()) {
		
			return 1;
		}
		else if (this.getTimeStamp() < blockRecord.getTimeStamp()) {
		
			return -1;
		}
		else {
		
			return 0;
		}
	}
}

public class M {

	public static void main(String []args) {
	
		PriorityQueue<BlockRecord> blockRecordsQueue = new PriorityQueue<>();
		
		//Add entries to PriorityQueue
		blockRecordsQueue.add(new BlockRecord("1", "2", "3", "uuid", "Amad", "Ali", "SocialSecurityNumber", "DOBString", "Diag", "treat", "rx123", "fasdfsda", "winninghash", 4));
		blockRecordsQueue.add(new BlockRecord("4", "5", "6", "uuid", "Adil", "Ali", "SocialSecurityNumber", "DOBString", "Diag", "treat", "rx123", "dfasfasd", "winninghash", 3));
		blockRecordsQueue.add(new BlockRecord("9", "8", "7", "uuid", "Amna", "Ali", "SocialSecurityNumber", "DOBString", "Diag", "treat", "rx123", "kjhljklj", "winninghash", 2));
		blockRecordsQueue.add(new BlockRecord("10", "11", "12", "uuid", "Syed", "Ali", "SocialSecurityNumber", "DOBString", "Diag", "treat", "rx123", "ertyyter", "winninghash", 1));
		
		while (!blockRecordsQueue.isEmpty()) {
		
			System.out.println(blockRecordsQueue.remove());
		}
	}
}