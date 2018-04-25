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

		    HttpParser httpparser = new HttpParser(istream);
		    String requestType = httpparser.getRequestType();
		    String path = httpparser.getPath();
		    System.out.print("Request type: ");
			System.out.println(requestType);
		    System.out.print("Path: ");
			System.out.println(path);
			httpparser.getMap();

			//When the path requested is "/", we're redirecting to /play.html
			if(path.equals("/")){
				System.out.println("Redirecting...");
				//Headers
				workerOut.print("HTTP/1.1 303 See Other\r\n");
				workerOut.print("Location: /play.html\r\n");
				workerOut.print("Connection: close\r\n");
				workerOut.print("\r\n");
				workerOut.flush();
			}

			//Shows the main page
			if(requestType.equals("GET") && path.equals("/play.html")){
				System.out.println("Showing Mastermind interface");

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Set-Cookie: SESSID=rk64vvmhlbt6rsdfv4f02kc5g0; path=/\r\n");
			    workerOut.print("\r\n");
			    //Body
			    workerOut.print("<p> Hello world" + i + " </p>\r\n");
			    workerOut.flush();
			}

			if(requestType.equals("POST")){

			}

			istream.close();
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