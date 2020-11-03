/*
	Copy and extend the previous code so that 
	EACH processes multicasts the message 
	"Hello multicast message from Process [N]" 
	and then print each of these three messages 
	on each console. 
*/

/*
#	To-Do List
#	1) Take previous code from EACH
#	2) Build a Listener so class F can connect to it
#	3) Everytime something connects to the listener, say where the process came from
*/

public class F {

	public static void main(Strings []args) {
		
		int pNum; 
		String serverName = "localhost";
		int port = 9001;
		Socket socket;
		ServerSocket serverSocket;

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
		
		while(true) {
			
			socket = serverSocket.accept();
			new Multicast(socket).start();
		}
	}
}

class Multicast extends Thread {

	Socket socket;
	
	Multicast(Socket socket) {
		
		socket = socket;
	}
	
	public void run() {
	
		PrintStream out = null;
		BufferedReader in = null;
		
		try {
		}
		catch (IOException ioe) {
			
			System.out.println(ioe);
		}
	}
}