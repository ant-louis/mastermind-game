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

			//Shows the main page : Normal encoding
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


			if(/*THE REQUEST IS A GUESS*/false){
				String cookie = httpparser.getCookie();
				String guess = httpparser.getGuess();
				GameInterface.submitGuess(cookie,guess);
			}
			
			/*
			//Shows the main page : chunked encoding
			if(requestType.equals("GET") && path.equals("/play.html")){
				System.out.println("Showing Mastermind interface");

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Transfer-Encoding: chunked\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Set-Cookie: SESSID=rk64vvmhlbt6rsdfv4f02kc5g0; path=/play.html\r\n");
			    workerOut.print("\r\n");

			    //Body
				String line1 = "Voici les données du premier morceau\r\n";
				String hexLength1 = Integer.toHexString(line1.length());
			    workerOut.print(hexLength1);
			    workerOut.print(line1);
			    
			    String line2 = "et voici un second morceau\r\n";
				String hexLength2 = Integer.toHexString(line2.length());
			    workerOut.print(hexLength2);
			    workerOut.print(line2);

				String line3 = "et voici deux derniers morceaux\r\n";
				String hexLength3 = Integer.toHexString(line3.length());
			    workerOut.print(hexLength3);
			    workerOut.print(line3);

				String line4 = "sans saut de ligne\r\n";
				String hexLength4 = Integer.toHexString(line4.length());
			    workerOut.print(hexLength4);
			    workerOut.print(line4);
				
			    workerOut.print("0\r\n");
			    workerOut.print("\r\n");

			    workerOut.flush();
			}
			 
			*/


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