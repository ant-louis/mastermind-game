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
		    /*System.out.print("Request type: ");
			System.out.println(requestType);
		    System.out.print("Path: ");
			System.out.println(path);
			*/
			

			//When the path requested is "/", we're redirecting to "/play.html"
			if(requestType.equals("GET") && path.equals("/")){
				System.out.println("Redirecting...");
				//Headers
				workerOut.print("HTTP/1.1 303 See Other\r\n");
				workerOut.print("Location: /play.html\r\n");
				workerOut.print("Connection: close\r\n");
				workerOut.print("\r\n");
				workerOut.flush();
			}

			
			//Shows the main page : chunked encoding
			else if(requestType.equals("GET") && path.equals("/play.html")){
				
				//Creating new game
				newCookie++;
				GameInterface.createGame(newCookie);

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Transfer-Encoding: chunked\r\n");
			    workerOut.print("Set-Cookie: SESSID=" + newCookie + "; path=/play.html\r\n");
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
			else if(requestType.equals("GET") && path.equals("/play.html")){
				
				//Creating new game
				newCookie++;
				GameInterface.createGame(newCookie);

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Access-Control-Allow-Origin: *\r\n");
			    workerOut.print("Set-Cookie: SESSID=" + newCookie + "; path=/play.html\r\n");
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

			    workerOut.print(createdwebpage);
			    workerOut.flush();

			}

			*/

			//AJAX Request -- sending result
			else if(requestType.equals("GET") && path.startsWith("/play.html?")){
				cookie = httpparser.getCookie();
				String guess = httpparser.getGuess();
				String result = GameInterface.submitGuess(cookie,guess);


		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Access-Control-Allow-Origin: *\r\n");
			    workerOut.print("Set-Cookie: SESSID=" + newCookie + "; path=/play.html\r\n");
			    workerOut.print("\r\n");

			    // Body - Consists only of the result
			    workerOut.print(result);

			    workerOut.flush();
			}




			//POST request - may need to separate normal post and guess POST
			else if(requestType.equals("POST") && path.equals("/play.html")){
				String body = httpparser.getBody();
				System.out.println(body);
			}






			//All others paths, these are wrong
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



	//J'ai utilisé le site https://www.willpeavy.com/minifier/ pour faire une seule ligne
	//Pour les images en base 64, j'ai utilisé https://www.base64-image.de/
	//Pour changer guillemets: selectionner tout le String, Ctrl+H, *sélection* , remplacer " par \"
	static String webpage = "<!DOCTYPE html><html> <head> <meta charset=\"utf-8\"/> <title>Mastermind</title> <style>body{font-family: \"Times New Roman\", Arial, serif;font-weight: normal; background-image: radial-gradient(ellipse at center, rgb(180,255,160), rgb(10,50,0));}.flexer{display: flex;}/********************GUESSES_AND_SCORES*********************/.mastermind-board{width: 30%;min-width:400px; height:650px; margin: 0 auto; margin-top: 20px; margin-bottom: 20px;}.title{width: 100%;height: 12%;}.mastermind-text{font-size: 4em;color: rgb(10,50,0);text-align: center;margin-top: 10px;text-shadow: 1px 1px rgb(220,255,215);}.guess-container{width:100%;height:90%;}.guess-row{box-sizing: border-box;height: 8.2%;width: 100%;}.guess-box{width: 70%;height: 100%;}#bub00,#bub01,#bub02,#bub03,#bub10,#bub11,#bub12,#bub13,#bub20,#bub21,#bub22,#bub23,#bub30,#bub31,#bub32,#bub33,#bub40,#bub41,#bub42,#bub43,#bub50,#bub51,#bub52,#bub53,#bub60,#bub61,#bub62,#bub63,#bub70,#bub71,#bub72,#bub73,#bub80,#bub81,#bub82,#bub83,#bub90,#bub91,#bub92,#bub93,#bub100,#bub101,#bub102,#bub103,#bub110,#bub111,#bub112,#bub113{height:70%;width:11%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin: 8px;background-color: rgb(240,240,240);}.result-box{width: 28%;height: 100%;}#res00,#res01,#res02,#res03,#res10,#res11,#res12,#res13,#res20,#res21,#res22,#res23,#res30,#res31,#res32,#res33,#res40,#res41,#res42,#res43,#res50,#res51,#res52,#res53,#res60,#res61,#res62,#res63,#res70,#res71,#res72,#res73,#res80,#res81,#res82,#res83,#res90,#res91,#res92,#res93,#res100,#res101,#res102,#res103,#res110,#res111,#res112,#res113{height: 30%;width: 12%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin: 5px;background-color:rgb(240,240,240);margin-top: 15%;}/*************************SELECTION************************/.selection-board{width: 30%;min-width:400px;height: 50px;border: 1px solid rgb(10,50,0); border-radius: 10px; margin: 0 auto;}.button{width: 30%;height: 100%;}#submit-button{width: 80%;height: 70%;border: 1px solid rgb(161,161,161); border-radius: 10px; text-align: center; box-shadow: 2px 2px 2px rgb(161,161,161); margin:5%; font-size: 1.2em;background-color: rgb(240,240,240);color:rgb(10,50,0);}.selection-box{width: 70%;height: 90%;}#btn0, #btn1, #btn2, #btn3{height:80%;width:16.5%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin-left: 3%;margin-right: 4.5%;margin-top: 2%;background-color: red;}</style> </head> <body> <div class=\"mastermind-board\"> <div class=\"title\"> <h2 class=\"mastermind-text\"> MASTERMIND</h2> </div><div class=\"guess-container\"> <div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub00\"></div><div id=\"bub01\"></div><div id=\"bub02\"></div><div id=\"bub03\"></div></div><div class=\"result-box flexer\"> <div id=\"res00\"></div><div id=\"res01\"></div><div id=\"res02\"></div><div id=\"res03\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub10\"></div><div id=\"bub11\"></div><div id=\"bub12\"></div><div id=\"bub13\"></div></div><div class=\"result-box flexer\"> <div id=\"res10\"></div><div id=\"res11\"></div><div id=\"res12\"></div><div id=\"res13\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub20\"></div><div id=\"bub21\"></div><div id=\"bub22\"></div><div id=\"bub23\"></div></div><div class=\"result-box flexer\"> <div id=\"res20\"></div><div id=\"res21\"></div><div id=\"res22\"></div><div id=\"res23\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub30\"></div><div id=\"bub31\"></div><div id=\"bub32\"></div><div id=\"bub33\"></div></div><div class=\"result-box flexer\"> <div id=\"res30\"></div><div id=\"res31\"></div><div id=\"res32\"></div><div id=\"res33\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub40\"></div><div id=\"bub41\"></div><div id=\"bub42\"></div><div id=\"bub43\"></div></div><div class=\"result-box flexer\"> <div id=\"res40\"></div><div id=\"res41\"></div><div id=\"res42\"></div><div id=\"res43\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub50\"></div><div id=\"bub51\"></div><div id=\"bub52\"></div><div id=\"bub53\"></div></div><div class=\"result-box flexer\"> <div id=\"res50\"></div><div id=\"res51\"></div><div id=\"res52\"></div><div id=\"res53\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub60\"></div><div id=\"bub61\"></div><div id=\"bub62\"></div><div id=\"bub63\"></div></div><div class=\"result-box flexer\"> <div id=\"res60\"></div><div id=\"res61\"></div><div id=\"res62\"></div><div id=\"res63\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub70\"></div><div id=\"bub71\"></div><div id=\"bub72\"></div><div id=\"bub73\"></div></div><div class=\"result-box flexer\"> <div id=\"res70\"></div><div id=\"res71\"></div><div id=\"res72\"></div><div id=\"res73\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub80\"></div><div id=\"bub81\"></div><div id=\"bub82\"></div><div id=\"bub83\"></div></div><div class=\"result-box flexer\"> <div id=\"res80\"></div><div id=\"res81\"></div><div id=\"res82\"></div><div id=\"res83\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub90\"></div><div id=\"bub91\"></div><div id=\"bub92\"></div><div id=\"bub93\"></div></div><div class=\"result-box flexer\"> <div id=\"res90\"></div><div id=\"res91\"></div><div id=\"res92\"></div><div id=\"res93\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub100\"></div><div id=\"bub101\"></div><div id=\"bub102\"></div><div id=\"bub103\"></div></div><div class=\"result-box flexer\"> <div id=\"res100\"></div><div id=\"res101\"></div><div id=\"res102\"></div><div id=\"res103\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div id=\"bub110\"></div><div id=\"bub111\"></div><div id=\"bub112\"></div><div id=\"bub113\"></div></div><div class=\"result-box flexer\"> <div id=\"res110\"></div><div id=\"res111\"></div><div id=\"res112\"></div><div id=\"res113\"></div></div></div></div></div><div class=\"selection-board flexer\"> <div class=\"selection-box\"> <div class=\"guess-box flexer\"> <button id=\"btn0\"></button> <button id=\"btn1\"></button> <button id=\"btn2\"></button> <button id=\"btn3\"></button> </div></div><div class=\"button\"> <button id=\"submit-button\"> Submit </button> </div></div><script type=\"text/javascript\">var nbGuess=11;var colorArray=new Array(); colorArray[0]=\"red\"; colorArray[1]=\"blue\"; colorArray[2]=\"yellow\"; colorArray[3]=\"green\"; colorArray[4]=\"white\"; colorArray[5]=\"black\"; var countBtn=new Array(); countBtn[0]=0; countBtn[1]=0; countBtn[2]=0; countBtn[3]=0; var btn0=document.getElementById(\"btn0\");var btn1=document.getElementById(\"btn1\");var btn2=document.getElementById(\"btn2\");var btn3=document.getElementById(\"btn3\");var submit_btn=document.getElementById(\"submit-button\");btn0.addEventListener(\"click\", function(){countBtn[0]=(countBtn[0] + 1) % colorArray.length; btn0.style.backgroundColor=colorArray[countBtn[0]];});btn1.addEventListener(\"click\", function(){countBtn[1]=(countBtn[1] + 1) % colorArray.length; btn1.style.backgroundColor=colorArray[countBtn[1]];});btn2.addEventListener(\"click\", function(){countBtn[2]=(countBtn[2] + 1) % colorArray.length; btn2.style.backgroundColor=colorArray[countBtn[2]];});btn3.addEventListener(\"click\", function(){countBtn[3]=(countBtn[3] + 1) % colorArray.length; btn3.style.backgroundColor=colorArray[countBtn[3]];});submit_btn.addEventListener(\"click\", function(){/*Stock new guess*/for(var i=0; i<4; i++){var bub=document.getElementById(\"bub\"+nbGuess+i);bub.style.backgroundColor=colorArray[countBtn[i]]}/*New HTTP Request*/ var xhttp=new XMLHttpRequest(); /*---------------Send request----------------*/ var value0=encodeURIComponent(countBtn[0]); var value1=encodeURIComponent(countBtn[1]); var value1=encodeURIComponent(countBtn[2]); var value3=encodeURIComponent(countBtn[3]); /* GET */ alert(value0); xhttp.open(\"GET\", 'localhost:8001/play.html?param1='+value1+'&param2='+value2+'&param3='+value3+'&param4='+value4); alert(\"Opened\"); xhttp.send(null); alert(\"Sent\"); /* POST xhttp.open('POST', '/play.html'); xhttp.send('param1='+value1 + '&param2='+value2 + '&param3='+value3 + '&param4='+value4); */ /*---------------Receive data--------------*/ xhttp.onreadystatechange=function(){/*Get the response*/var response=xhttp.responseText;/*Parse the response*/var nbWellPlaced=Number(response[0]);var nbNotWellPlaced=Number(response[1]);var len=nbWellPlaced + nbNotWellPlaced;/*Display the result*/for(var i=0; i < len; i++){var res=document.getElementById(\"res\"+nbGuess+i);if(nbWellPlaced > 0){res.style.backgroundColor=\"red\";nbWellPlaced--;}else{res.style.backgroundColor=\"black\";}}}; alert(\"Onreadystate\"); nbGuess--;if(nbGuess < 0){alert(\"GAME OVER\");setTimeout(function(){document.location=\"play.html\"}, 3000);}});</script> </body></html>";
}