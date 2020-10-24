/* 	D. Modify the utility code to create a standalone piece of 
	code that accepts a command line argument designating which
	process ID (0, 1, or 2) this process is using. Get all three
	processes starting up, and then printing out "Hello from Process 
	[N]" on the console.
 */
 
 /* To Do List
 #	1) Read BlockJ.java and get Process(done)
 #	2) Read in command line arguments(done)
 #	3) Output setance with process number(done)
 #	4) Press any key to continue(done)
 */
 
public class D {
    
	public static void main(String[] args) {
	
		int pNum; 

		/* Show how to set the process ID pNum from a command line argument: */
		if (args.length < 1) pNum = 0;
		else if (args[0].equals("0")) pNum = 0;
		else if (args[0].equals("1")) pNum = 1;
		else if (args[0].equals("2")) pNum = 2;
		else pNum = 0; /* Default for badly formed argument */

		System.out.println("Hello from Process " + pNum);
		
		System.in.read()
	}
}