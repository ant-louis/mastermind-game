import java.io.*;
import java.net.*; 
import java.util.concurrent.TimeUnit;


public class WebServerWorker implements Runnable {

	private Socket workerSock;
	private static int newCookie = 0;


	public WebServerWorker(Socket clientSocket){
		workerSock = clientSocket;
	}


	public void run() {
		try{

			InputStreamReader istream = new InputStreamReader(workerSock.getInputStream());
			PrintWriter workerOut = new PrintWriter(workerSock.getOutputStream());

		    HttpParser httpparser = new HttpParser(istream);
		    String requestType = httpparser.getRequestType();
		    String path = httpparser.getPath();
		    int cookie;
		    System.out.print("Request type: ");
			System.out.println(requestType);
		    System.out.print("Path: ");
			System.out.println(path);
			//httpparser.getMap();

			

			//When the path requested is "/", we're redirecting to "play.html"
			if(path.equals("/")){
				System.out.println("Redirecting...");
				//Headers
				workerOut.print("HTTP/1.1 303 See Other\r\n");
				workerOut.print("Location: play.html\r\n");
				workerOut.print("Connection: close\r\n");
				workerOut.print("\r\n");
				workerOut.flush();
			}

			
			//Shows the main page : chunked encoding
			else if(requestType.equals("GET") && path.equals("play.html")){
				
				//Creating new game
				newCookie++;
				GameInterface.createGame(newCookie);

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Transfer-Encoding: chunked\r\n");
			    workerOut.print("Set-Cookie: SESSID=" + newCookie + "; path=play.html\r\n");
			    workerOut.print("\r\n");

				//Body					    
			    
			    //String previousexchanges = GameInterface.getPreviousExchanges(newCookie);
			    String previousexchanges = ("1123412");
			    HTMLCreator myhtmlcreator = new HTMLCreator(previousexchanges);
			    String createdwebpage = myhtmlcreator.createPage();
			    //System.out.println(createdwebpage);

			    int chunkSize = 128;
			    int startIndex = 0;
			    int endIndex = startIndex + chunkSize;
			    String pageChunk;

			    while(createdwebpage.length() >= endIndex){

			    	//Cut the webpage into chunks of size chunkSize
			    	pageChunk = createdwebpage.substring(startIndex,endIndex);
			    	startIndex += chunkSize;
			    	endIndex = startIndex + chunkSize;

			    	//Get chunkSize in hexadecimal
					String hexLength = Integer.toHexString(chunkSize);
			    	workerOut.println(hexLength);
			    	workerOut.println(pageChunk);
			    }


			    //If the wepage length is not a multiple of chunkSize, 
			    //there are some characters left
		    	pageChunk = createdwebpage.substring(startIndex,createdwebpage.length());
				String hexLength = Integer.toHexString(createdwebpage.length() - startIndex);
		    	workerOut.println(hexLength);
		    	workerOut.println(pageChunk);

		    	//End the chunked enconding
			    workerOut.print("0\r\n");
			    workerOut.print("\r\n");
			    

			    workerOut.flush();
			}
			

			/*
			//Shows the main page and starts a game : Normal encoding
			else if(requestType.equals("GET") && path.equals("play.html")){
				
				//Creating new game
				newCookie++;
				GameInterface.createGame(newCookie);

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Access-Control-Allow-Origin: *\r\n");
			    workerOut.print("Set-Cookie: SESSID=" + newCookie + "; path=play.html\r\n");
			    workerOut.print("\r\n");

			    //Body

			    

				//System.out.println(GameInterface.submitGuess(newCookie,"1234"));
				TimeUnit.SECONDS.sleep(1);
				GameInterface.submitGuess(newCookie,"1144");
				TimeUnit.SECONDS.sleep(1);
				GameInterface.submitGuess(newCookie,"1111");
				TimeUnit.SECONDS.sleep(1);
				GameInterface.submitGuess(newCookie,"3454");
				TimeUnit.SECONDS.sleep(1);
				GameInterface.submitGuess(newCookie,"0451");
				TimeUnit.SECONDS.sleep(1);
				



			    //String previousexchanges = GameInterface.getPreviousExchanges(newCookie);
			    String previousexchanges = ("1123412");
			    HTMLCreator myhtmlcreator = new HTMLCreator(previousexchanges);
			    String createdwebpage = myhtmlcreator.createPage();
			    //System.out.println(createdwebpage);

			    workerOut.print(createdwebpage);
			    workerOut.flush();

			}

			*/

			//AJAX Request 
			else if(requestType.equals("GET") && path.startsWith("play.html?")){
				cookie = httpparser.getCookie();
				String guess = httpparser.getGuess();
				GameInterface.submitGuess(cookie,guess);
			}

			//All others apths, these are wrong
			else if(requestType.equals("GET")){

				System.out.println("Redirecting...");
				//Headers
				workerOut.print("HTTP/1.1 404 Not Found\r\n");
				workerOut.print("\r\n");

				//Body
				workerOut.print("<!DOCTYPE html>\r\n");
				workerOut.print("<html lang=en>\r\n");
				workerOut.print("<meta charset=utf-8>\r\n");
  				workerOut.print("<title>Error 404 (Not Found)!!1</title>\r\n");
 				workerOut.print("<p><b>404.</b> <ins>That’s an error.</ins>\r\n");
  				workerOut.print("<p>The requested URL was not found on this server.\r\n");

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