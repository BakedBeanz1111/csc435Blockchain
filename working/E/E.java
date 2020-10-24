/* 	Copy the previous code and extend it so that
	after each process settles (using sleep statements?),
	process 0 (or other) multicasts the message "Hello multicast
	message from Process 0" which is then printed on the console
	of each process. 
*/

/*
#	To-Do List
#	1) Copy code from D.java(done)
#	2) Insert a sleep command(done)
#	3) output message to console about process completion(done)
*/

/*
	I used the following links:
	https://mkyong.com/java/java-how-to-delay-few-seconds/
*/

import java.util.concurrent.TimeUnit;

public class E {
    
	public static void main(String[] args) {
	
		int pNum; 

		/* Show how to set the process ID pNum from a command line argument: */
		if (args.length < 1) pNum = 0;
		else if (args[0].equals("0")) pNum = 0;
		else if (args[0].equals("1")) pNum = 1;
		else if (args[0].equals("2")) pNum = 2;
		else pNum = 0; /* Default for badly formed argument */

		System.out.println("Hello from Process " + pNum);
		try{
			
			TimeUnit.SECONDS.sleep(10);
		}
		catch (InterruptedException e) {
			
			System.out.println(e);
		}
		
		System.out.println("Hello multicast message from Process " + pNum);
	}
}