import java.io.*;
import java.net.*; 


public class MastermindServer {
	
	private static ServerSocket ss;

	
	public static void main ( String argv[] ) {
		
		try {

			ss = new ServerSocket(2140);
			System.out.println("Server started...");
			
			while(true) {
				Socket client = ss.accept() ;
				System.out.println("Accepted connection: " + client);
				Thread t = new Thread(new MastermindServerWorker(client));
				t.start();
			}

			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				ss.close();
			}
			catch(IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}


