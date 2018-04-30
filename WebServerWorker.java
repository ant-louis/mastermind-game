import java.io.*;
import java.net.*; 


public class WebServerWorker implements Runnable {

	private Socket workerSock;
	private static int i;

	public WebServerWorker(Socket clientSocket){
		workerSock = clientSocket;
		System.out.println("Thread "+ i +" started");
		i++; 
	}


	public void run() {
		try{

			InputStreamReader istream = new InputStreamReader(workerSock.getInputStream());
			PrintWriter workerOut = new PrintWriter(workerSock.getOutputStream());

		    HttpParser httpparser = new HttpParser(istream);
		    String requestType = httpparser.getRequestType();
		    String path = httpparser.getPath();
		    String cookie = httpparser.getCookie();
		    System.out.print("Request type: ");
			System.out.println(requestType);
		    System.out.print("Path: ");
			System.out.println(path);
			//httpparser.getMap();

			

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
				//Starting the game
				if(!GameInterface.gameExists(cookie)){
					GameInterface.createGame(cookie);
				}

				//Headers
		    	workerOut.print("HTTP/1.1 200 OK\r\n");
			    workerOut.print("Content-Type: text/html\r\n");
			    workerOut.print("Connection: close\r\n");
			    workerOut.print("Set-Cookie: SESSID=rk64vvmhlbt6rsdfv4f02kc5g0; path=/play.html\r\n");
			    workerOut.print("\r\n");

			    //Body

			    //Printing base64 image --> WORKS

			    /*
			    workerOut.print("<!DOCTYPE html>"+
								"<html>"+
								"<body>"+
								"<img style=\'display:block;\' id=\'base64image\'"+               
       							"src=" + base64image + ">"+
								"</body>"+
								"</html>");
			    */

			    workerOut.print(webpage);
			    workerOut.flush();
			}



			

			if(/*THE REQUEST IS A GUESS*/false){
				/*
				//String guess = httpparser.getGuess();
				System.out.println("Sending 10 to Worker");
				String cookie = "1";
				String guess = "10";
				GameInterface.submitGuess(cookie,guess);
				String response = GameInterface.getResponse(cookie);
				analyseResponse(response);

				*/
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



	private void analyseResponse (String response){
		if(response.equals("11")){
			System.out.println("Game has started!");
		}
	}

	//J'ai utilisé le site https://www.willpeavy.com/minifier/ pour faire une seule ligne
	//Pour les images en base 64, j'ai utilisé https://www.base64-image.de/
	//Pour changer guillemets: selectionner tout le String, Ctrl+H, *sélection* , remplacer " par \"
	private static String base64image = "data:image/jpeg;base64,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";
	static String webpage = "<!DOCTYPE html><html> <head> <meta charset=\"utf-8\"/> <title>Mastermind</title> <style>body{font-family: \"Times New Roman\", Arial, serif;font-weight: normal;background-color: rgb(232,232,232);}.flexer{display: flex;}/********************GUESSES_AND_SCORES*********************/.mastermind-board{width: 30%;min-width:400px; height:650px; margin: 0 auto; margin-top: 20px; margin-bottom: 10px;}.title{width: 100%;height: 10%;}.mastermind-text{font-size: 3em;color: rgb(131,131,131);text-align: center;margin-top: 10px;}.guess-container{width:100%;height:90%;}.guess-row{box-sizing: border-box;height: 8.2%;width: 100%;}.guess-box{width: 70%;height: 100%;}.guess-bubble{height:70%;width:12%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin: 8px;background-color: rgb(240,240,240);}.result-box{width: 30%;height: 100%;}.result-bubble{height: 30%;width: 12%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin: 5px;background-color:rgb(240,240,240);margin-top: 15%;}.bubble-red{background-color: red;}.bubble-blue{background-color: blue;}.bubble-yellow{background-color: yellow;}.bubble-green{background-color: green;}.bubble-white{background-color: white;}.bubble-black{background-color: black;}/*************************SELECTION************************/.selection-board{width: 30%;min-width:400px;height: 50px;border: 1px solid rgb(161,161,161); border-radius: 10px; margin: 0 auto;}.button{width: 30%;height: 100%;}#submit-button{width: 80%;height: 70%;border: 1px solid rgb(161,161,161); border-radius: 10px; text-align: center; box-shadow: 2px 2px 2px rgb(161,161,161); margin:5%; font-size: 1.2em;color: rgb(111,111,111);}.selection-box{width: 70%;height: 90%;}#btn1, #btn2, #btn3, #btn4{height:80%;width:18%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin-left: 6%;margin-top: 2%;background-color: red;}</style> <script language=\"javascript\" type=\"text/javascript\"> var colorArray=new Array(); colorArray[0]=\"red\"; colorArray[1]=\"blue\"; colorArray[2]=\"yellow\"; colorArray[3]=\"green\"; colorArray[4]=\"white\"; colorArray[5]=\"black\"; var count_btn1=0; var count_btn2=0; var count_btn3=0; var count_btn4=0; function changeColorBtn1(){count_btn1=(count_btn1 + 1) % colorArray.length; document.getElementById('btn1').style.backgroundColor=colorArray[count_btn1];}function changeColorBtn2(){count_btn2=(count_btn2 + 1) % colorArray.length; document.getElementById('btn2').style.backgroundColor=colorArray[count_btn2];}function changeColorBtn3(){count_btn3=(count_btn3 + 1) % colorArray.length; document.getElementById('btn3').style.backgroundColor=colorArray[count_btn3];}function changeColorBtn4(){count_btn4=(count_btn4 + 1) % colorArray.length; document.getElementById('btn4').style.backgroundColor=colorArray[count_btn4];}</script> </head> <body> <div class=\"mastermind-board\"> <div class=\"title\"> <h2 class=\"mastermind-text\"> MASTERMIND </h2> </div><div class=\"guess-container\"> <div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div><div class=\"guess-row flexer\"> <div class=\"guess-box flexer\"> <div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div><div class=\"guess-bubble\"></div></div><div class=\"result-box flexer\"> <div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div><div class=\"result-bubble\"></div></div></div></div></div><div class=\"selection-board flexer\"> <div class=\"selection-box\"> <div class=\"guess-box flexer\"> <input type=\"button\" id=\"btn1\" onclick=\"changeColorBtn1()\";/> <input type=\"button\" id=\"btn2\" onclick=\"changeColorBtn2()\";/> <input type=\"button\" id=\"btn3\" onclick=\"changeColorBtn3()\";/> <input type=\"button\" id=\"btn4\" onclick=\"changeColorBtn4()\";/> </div></div><div class=\"button\"> <input type=\"button\" id=\"submit-button\" value=\"Submit\" onclick=\"\";/> </div></div></body></html>";
}