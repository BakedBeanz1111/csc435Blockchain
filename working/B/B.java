/*  Copy the previous code, and translate your entire 
	rudimentary blockchain into JSON format and write 
	it to disk.
*/

/*
# ToDo List:
# Create a subdirectory: Named "working" (Done)
# with a readme file called 0-readme.txt (Done)
# In a single process, create a blockchain with 4 nodes IE - Create 4 JSON Files (done)
# Convert Json to String (done)
# Concatenate each block(done)
# Hash Them(done)
# Do something to "verify" them
# Write JSON to disk(done)
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.io.*;

import java.util.*;
/* import java.util.Date;
   import java.util.Random;
   import java.util.regex.*;
   import java.util.StringTokenizer;
*/

import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.text.*;

//The following code was taken from BLockInputG.java but I have retyped everything to conform to my personal style guide for writing code

class BlockRecord {
  
	String fName;
	String lName;
	String dob;

	/* Examples of accessors for the BlockRecord fields: */
	public String getLname() {
		
		return lName;
	}
	public void setLname (String LN) {
		
		this.lName = LN;
	}
  
	public String getFname() {
		
		return fName;
	}
	public void setFname (String FN) {
		
		this.fName = FN;
	}
  
	public String getDOB() {
		
		return dob;
	}
	public void setDOB (String RS) {
		
		this.dob = RS;
	}
	
	public BlockRecord(String fName, String lName, String dob) {
		
		this.fName = fName;
		this.lName = lName;
		this.dob = dob;
	}
}

public class B {

    public static void main(String[] args)throws IOException {
		
		//Creating 4 JSON Objects
		BlockRecord blockRecord0 = new BlockRecord("Ali", "Amad", "1989.07.19");
		BlockRecord blockRecord1 = new BlockRecord("Ali", "Adil", "1988.05.16");
		BlockRecord blockRecord2 = new BlockRecord("Ali", "Amna", "1995.09.27");
		BlockRecord blockRecord3 = new BlockRecord("Fake", "Person", "1999.09.09");
		
        Gson gson = new Gson();

		//Going from Object to String
		String block0 = gson.toJson(blockRecord0);
		String block1 = gson.toJson(blockRecord1);
		String block2 = gson.toJson(blockRecord2);
		String block3 = gson.toJson(blockRecord3);
		
		//concatenate blocks
		String concatenatedBlocks = block0 + block1 + block2 + block3;
		
		//Check block output
		System.out.println(concatenatedBlocks);
		
		//Hash concatenatedBlocks
		System.out.println(concatenatedBlocks.hashCode());
		
		//Verify Blocks
		//Do a thing
		
		//Output to disk? Are we outputting single JSON files or a concatenated JSON?
		BufferedWriter writer = new BufferedWriter(new FileWriter("BlockChain.json"));
	    writer.write(concatenatedBlocks);
	    
	    writer.close();
		
    }

}
