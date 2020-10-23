/*# Create a named subdirectory with a 0-readme.txt file in it. 
# In a single process, create a blockchain with four nodes in it: 
# a dummy block zero, and three other simple blocks with a small 
# amount of data in them. Demonstrate that you can convert the 
# Java data contained in the simple block you have designed into 
# string format, and that you can concatenate the "three elements" 
# together, hash them, and come up with a result that is used to 
# "verify" each block. In this simple version you can, e.g., make 
# the puzzle so easy that it is solved every time. We will worry 
# about work later. Use the Proof-of-Work field from the previous 
# block as part of the data in the subsequent block. 

# ToDo List:
# Create a subdirectory: Named "working" (Done)
# with a readme file called 0-readme.txt (Done)
# In a single process, create a blockchain with 4 nodes IE - Create 4 JSON Files (done)
# Convert Json to String (done)
# Concatenate each block(done)
# Hash Them(done)
# Do something to "verify" them
*/

// I looked at the following links to help me do this piece of code
// https://mkyong.com/java/how-to-parse-json-with-gson/ as introduced from BlockJ.java/how-to-parse-json-with-gson/
// https://www.java67.com/2017/05/how-to-convert-java-object-to-json-using-Gson-example-tutorial.html
// https://www.tutorialspoint.com/java/java_string_hashcode.htm


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

public class A {

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
		
    }

}
