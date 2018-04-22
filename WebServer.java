import java.io.*;
import java.net.*; 


public class WebServer {
	
	public static void main ( String argv[] ) {
		
		ServerSocket serverSocket = null;

		try {	

				serverSocket = new ServerSocket(8001);
				System.out.println("Server started...");
			
			//while(true) {
				Socket clientSocket = serverSocket.accept() ;
				Thread t = new Thread(new WebServerWorker(clientSocket));
				t.start();
			//}
			
				/*
				BufferedReader BR = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			    PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream());
			    

			    String header1;
			    
			    clientOut.println("HTTP/1.1 303 See Other");
			    clientOut.println("Location: /play.html");
			    clientOut.println("\r\n");
			    clientOut.flush();
		
			    while((header1 = BR.readLine()) != null){
			    	System.out.println(header1);
			    }

				clientSocket.close();

				
				clientSocket = serverSocket.accept();
				BR = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			   	clientOut = new PrintWriter(clientSocket.getOutputStream());
			    

			    String header2;
			    		
			    clientOut.println("HTTP/1.1 200 OK");
			    clientOut.println("Content-Type: text/html");
			    clientOut.println("Set-Cookie: SESSID=rk64vvmhlbt6rsdfv4f02kc5g0; path=/");
			    clientOut.println("\r\n");
			    clientOut.println("<p> Hello world </p>");
			    clientOut.flush();


			
			    while((header2 = BR.readLine()) != null){
			    	System.out.println(header2);
			    }
	
			    BR.close();
				clientSocket.close();
				*/

			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				serverSocket.close();
			}
			catch(IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
}
