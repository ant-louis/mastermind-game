import java.io.*;
import java.net.*; 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
	
	public static void main ( String argv[] ) {
		
		ServerSocket serverSocket = null;

		try {	

				serverSocket = new ServerSocket(8001);
				System.out.println("Server started...");
				//Creating a threadpool of 5 threads
		        ExecutorService executor = Executors.newFixedThreadPool(5);

				while(true) {
					Socket clientSocket = serverSocket.accept() ;
					Thread t = new Thread(new WebServerWorker(clientSocket));
					executor.execute(t);			
					}
					
				//executor.shutdown();
			
				/*
				
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
