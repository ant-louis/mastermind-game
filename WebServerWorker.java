import java.io.*;
import java.net.*; 


public class WebServerWorker implements Runnable {

	private Socket workerSock;
	private static int i;

	public WebServerWorker(Socket clientSocket){
		workerSock = clientSocket;
		System.out.println("Thread "+ i +"started");
		i++; 
	}


	public void run() {
		try{

			InputStreamReader istream = new InputStreamReader(workerSock.getInputStream());
			PrintWriter workerOut = new PrintWriter(workerSock.getOutputStream());

			/*
			System.out.println("Redirecting...");
			workerOut.print("HTTP/1.1 303 See Other\r\n");
			workerOut.print("Location: /play.html\r\n");
		    workerOut.print("Connection: close\r\n");

			workerOut.print("\r\n");
			workerOut.flush();
			*/

	    	workerOut.print("HTTP/1.1 200 OK\r\n");
		    workerOut.print("Content-Type: text/html\r\n");
		    workerOut.print("Connection: close\r\n");
		    workerOut.print("Set-Cookie: SESSID=rk64vvmhlbt6rsdfv4f02kc5g0; path=/\r\n");
		    workerOut.print("\r\n");
		    workerOut.print("<p> Hello world" + i + " </p>\r\n");
		    workerOut.flush();

		    HttpParser httpparser = new HttpParser(istream);

		    System.out.print("Request type: ");
			System.out.println(httpparser.getRequestType());
		    System.out.print("Path: ");
			System.out.println(httpparser.getPath());
			httpparser.getMap();

			workerOut.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				workerSock.close();
			}
			catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
}