import java.io.*;
import java.net.*; 


public class WebServer {
	
	public static void main ( String argv[] ) {
		
		ServerSocket ss = null;

		try {	
			ss = new ServerSocket(8001);
			System.out.println("Server started...");
			
			Socket clientSocket = ss.accept();

		    PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream());


		    clientOut.println("HTTP/1.1 200 OK");
		    clientOut.println("Content-Type: text/html");
		    clientOut.println("\r\n");
		    clientOut.println("<p> Hello world </p>");
		    clientOut.flush();

			clientSocket.close();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				ss.close();
			}
			catch(IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
