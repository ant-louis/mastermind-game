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
			    workerOut.print("<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Mastermind</title>
        <style>
        	body {
				font-family: "Times New Roman", Arial, serif;
				font-weight: normal;
				background-color: rgb(255,230,200);
			}


			.flexer {
				display: flex;
			}


			/********************GUESSES_AND_SCORES*********************/
			.mastermind-board {
				width: 30%;
				min-width:400px;
			    height:650px;
			    border: 1px solid rgba(50,160,200,0.7);
			    border-radius: 10px;
			    margin: 0 auto;
			    margin-top: 20px;
			    margin-bottom: 10px;
			}

			.title {
				width: 100%;
				height: 10%;
			}

			.mastermind-text {
				font-size: 3em;
				color: rgba(50,160,200,0.7);
				text-align: center;
				margin: 0;
				margin-top: 10px;
			}


			.guess-container{
				width:100%;
				height:90%;
			}


			.guess-row {
				box-sizing: border-box;
				height: 8.2%;
				width: 100%;
				
			}

			.guess-box{
				width: 70%;
				height: 100%;
			}

			.guess-bubble {
				height:70%;
				width:12%;
				border-radius: 50%;
				border: 1px solid rgb(50,50,50);
				margin: 8px;
				background-color: rgb(200,200,200); 
			}

			.result-box{
				width: 30%;
				height: 100%;
			}

			.result-bubble {
				height: 30%;
				width: 20%;
				border-radius: 50%;
				border: 1px solid rgb(50,50,50);
				margin: 5px;
				background-color:rgb(200,200,200);
				margin-top: 15%;
			}



			/*************************SELECTION************************/
			.selection-board {
				width: 30%;
				min-width:400px;
				height: 50px;
				border: 1px solid rgba(50,160,200,0.7);
			    border-radius: 10px;
			    margin: 0 auto;
			}

			.button {
				width: 30%;
				height: 100%;
			}

			.submit-button {
				width: 80%;
				height: 70%;
				border: 1px solid rgba(50,160,200,0.7);
			    border-radius: 10px;
			    text-align: center;
			    box-shadow: 2px 2px 2px rgba(50,160,200,0.7);
			    margin:5%;
			}

			.submit-text {
				font-size: 1.2em;
				color: rgba(50,160,200,0.7);
				margin: 3%;
			}

			.selection-box {
				width: 70%;
				height: 90%;
			}

			.guess{
				height:80%;
				width:18%;
				border-radius: 50%;
				border: 1px solid rgb(50,50,50);
				margin-left: 6%;
				margin-top: 2%;
				background-color: rgb(200,200,200); 
			}

			.red{
				background-color: red;
			}

			.blue{
				background-color: blue;
			}

			.yellow{
				background-color: yellow;
			}

			.green{
				background-color: green;
			}

			.white{
				background-color: white;
			}

			.black{
				background-color: black;
			}
        </style>

    </head>

    <body>

    	<div class="mastermind-board">

    		<div class="title">
    			<h2 class="mastermind-text"> MASTERMIND </h2>
    		</div>
    		
    		<div class="guess-container">

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    		<div class="guess-row flexer">
	    			<div class="guess-box flexer">
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    				<div class="guess-bubble"></div>
	    			</div>

	    			<div class="result-box flexer">
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    				<div class="result-bubble"></div>
	    			</div>
	    		</div>

	    	</div>

		</div>


		<div class="selection-board flexer">
		    	<div class="selection-box">
	    			<div class="guess-box flexer">
	    				<div class="guess"></div>
	    				<div class="guess"></div>
	    				<div class="guess"></div>
	    				<div class="guess"></div>
	    			</div>
		    	</div>

		    	<div class="button">
			    	<div class="submit-button">
			    		<h3 class="submit-text"> Submit </h3>
			    	</div>
			    </div>
		    </div>
    
    </body>
</html>");
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