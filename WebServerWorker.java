import java.io.*;
import java.net.*; 
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;	

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

			//Parses the request and stores the important information
		    HttpParser httpparser = new HttpParser(istream);
		    //Gets the type of the request (GET or POST here)
		    String requestType = httpparser.getRequestType();
		    //Gets the path that is requested
		    String path = httpparser.getPath();
		    //boolean acceptGzip = httpparser.acceptGzipEncoding();
		    //Get the cookie associated with the request
		    int cookie = httpparser.getCookie();
		    System.out.println("Cookie: " + cookie);


			
			
			//When the path requested is "/", we're redirecting to "/play.html"
			if(path.equals("/")){
				System.out.println("Redirecting...");
				//Headers
				workerOut.print("HTTP/1.1 303 See Other\r\n");
				workerOut.print("Location: /play.html\r\n");
				workerOut.print("Connection: close\r\n");
				workerOut.print("\r\n");
				workerOut.flush();
			}

			
			//Shows the main page and create new game : chunked encoding
			else if(requestType.equals("GET") && path.equals("/play.html")){
				
				//Creating new game
				newCookie++;
				GameInterface.createGame(newCookie);

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Transfer-Encoding: chunked\r\n");
			    workerOut.print("Set-Cookie: SESSID=" + newCookie + "; path=/\r\n");
			    workerOut.print("\r\n");

				//Body					    
			    System.out.println("Main Cookie: " + cookie);

			    String previousexchanges = ""; //Empty previous exchanges to create blank page
	    		boolean gzipEnabled = true;
    			HTMLCreator myhtmlcreator = new HTMLCreator(previousexchanges,workerOut,gzipEnabled);
				myhtmlcreator.createPage();			
			}
			


			//AJAX Request 
			else if(requestType.equals("GET") && path.startsWith("/play.html?")){

				//Get the guess from the header and submit it
				cookie = httpparser.getCookie();
				String guess = httpparser.getGuess_GET();
				String result = GameInterface.submitGuess(cookie,guess);

				//Extract the correct number of well placed colors
				int wellPlacedColor = Character.getNumericValue(result.charAt(0));
				int numberOfGuesses = 0;

				//Get the result of the guess and all the exchanges ,
				// including the number of total exchanges
				System.out.println("Result of GET:" +result);
			   	String previousexchanges = GameInterface.getPreviousExchanges(cookie);

	   			if(previousexchanges.length() > 0 && previousexchanges.length() <= 55){

					numberOfGuesses = Character.getNumericValue(previousexchanges.charAt(0));
				}
				else if(previousexchanges.length() > 55){
					numberOfGuesses = Integer.parseInt(previousexchanges.substring(0,2));
				}


		   		System.out.println("Number of gueses : " + numberOfGuesses);
		   		System.out.println("wellPlacedColor : " + wellPlacedColor);


				/**************HTTP Header**************/

		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
		   		//If we won or lost, we must delete the cookie and delete the game
			   	if(numberOfGuesses == 12 || wellPlacedColor == 4){
			    	workerOut.print("Set-Cookie: SESSID=deleted; path=/;expires=Thu, 01 Jan 1970 00:00:00 GMT\r\n");
			    	GameInterface.deleteGame(cookie);
			   	}
			    workerOut.print("\r\n");
				/*************************************/

				/**************HTTP Body**************/
				//Body consists only of the result, no need to chunk or compress
			    workerOut.print(result); 
			    workerOut.flush();
			}




			//POST request - may need to separate normal post and guess POST
			else if(requestType.equals("POST") && path.equals("/play.html")){

				//Submit the guess received in the body
				cookie = httpparser.getCookie();
				String guess = httpparser.getGuess_POST();
				String result = GameInterface.submitGuess(cookie,guess); 

				//Extract the correct number of well placed colors
				int wellPlacedColor = Character.getNumericValue(result.charAt(0));
				int numberOfGuesses = 0;

				//Get the result of the guess and all the exchanges ,
				//including the number of total exchanges
				System.out.println("Result of POST:" +result);
			   	String previousexchanges = GameInterface.getPreviousExchanges(cookie);

	   			if(previousexchanges.length() > 0 && previousexchanges.length() <= 55){

					numberOfGuesses = Character.getNumericValue(previousexchanges.charAt(0));
				}
				else if(previousexchanges.length() > 55){
					numberOfGuesses = Integer.parseInt(previousexchanges.substring(0,2));
				}


		   		System.out.println("Number of gueses : " + numberOfGuesses);
		   		System.out.println("wellPlacedColor : " + wellPlacedColor);

				/**************HTTP Header**************/
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Transfer-Encoding: chunked\r\n");
		   		//If we won or lost, we must delete the cookie.
			   	if(numberOfGuesses == 12 || wellPlacedColor == 4){
			   		System.out.println("deleted cookie");
			    	workerOut.print("Set-Cookie: SESSID=deleted; path=/;expires=Thu, 01 Jan 1970 00:00:00 GMT\r\n");
			   	}
			    workerOut.print("\r\n");
			    /**************************************/

				/**************HTTP Body**************/

			    //POST request needs to recreate the whole page, so we're passing 
			    //all the previous guesses as argument
			    System.out.println("POST Cookie: " + cookie);
	    		boolean gzipEnabled = false;
	    		HTMLCreator myhtmlcreator = new HTMLCreator(previousexchanges,workerOut,gzipEnabled);
				myhtmlcreator.createPage();			
			}




			//All others paths, these are wrong
			else if(requestType.equals("GET")){

				System.out.println("Redirecting...");
				//Headers
				workerOut.print("HTTP/1.1 404 Not Found\r\n");
				workerOut.print("\r\n");

				//Body
				StringBuilder page = new StringBuilder();

				page.append("<!DOCTYPE html><html>");
				page.append("<head><meta charset=\"utf-8\"/><title>Error 404</title>");
				page.append("<style>body{font-family: \"Times New Roman\", Arial, serif;font-weight: normal; background-image: radial-gradient(circle at center, rgb(180,255,160), rgb(10,50,0));} .message{font-size: 5em; text-align: center; color: rgb(10,50,0);} .explain{margin: 10%; font-size: 2em; text-align: center; color: rgb(10,50,0);}</style>");
				page.append("</head>");
				page.append("<body><div class=\"message\"><p> <b> 404 NOT FOUND ! b></</p></div> <div class=\"explain\"> <p>The requested URL was not found on this server.</p></div></body>");
				page.append("</html>");

  				workerOut.print(page.toString());
				workerOut.flush();

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

	/*
	private void encodeChunks(PrintWriter workerOut,String previousexchanges){
		System.out.println("Encoding chunks");

		HTMLCreator myhtmlcreator = new HTMLCreator(previousexchanges);
	    String createdwebpage = myhtmlcreator.createPage(workerOut);

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

	*/

}