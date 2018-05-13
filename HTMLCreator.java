import java.io.*;
import java.net.*; 
import java.util.zip.GZIPOutputStream;

//Class to dynamically create the HTML webpage
public class HTMLCreator {
	//ALl the different colors in the Mastermind game
	private enum colors {
		red,
		blue,
		yellow,
		green,
		white,
		black;
	}

	//Class variables
	String header;
	String previousExchanges;
	StringBuilder compress; //Used for gzip compressing
	OutputStream socketOut;
	int nbExchanges;
	int result;
	boolean gzipEnabled; 

	private static final int BLANK = 10;
	private static String staticCSS = "body{font-family: \"Times New Roman\", Arial, serif;font-weight: normal; background-image: radial-gradient(circle at center, rgb(180,255,160), rgb(10,50,0));}.flexer{display: flex;}/********************GUESSES_AND_SCORES*********************/.mastermind-board{width: 30%;min-width:400px; height:650px; margin: 0 auto; margin-top: 20px; margin-bottom: 20px;}.title{width: 100%;height: 12%;}.mastermind-text{font-size: 4em;color: rgb(10,50,0);text-align: center;margin-top: 10px;text-shadow: 1px 1px rgb(220,255,215);}.guess-container{width:100%;height:90%;}.guess-row{box-sizing: border-box;height: 8.2%;width: 100%;}.guess-box{width: 70%;height: 100%;}.result-box{width: 28%;height: 100%;}/*************************SELECTION************************/#js{display: none;}.selection-board{width: 30%;min-width:400px;height: 50px;border: 1px solid rgb(10,50,0); border-radius: 10px; margin: 0 auto;}.button{width: 30%;height: 100%;}.submit-button{width: 80%;height: 70%;border: 1px solid rgb(161,161,161); border-radius: 10px; text-align: center; box-shadow: 2px 2px 2px rgb(161,161,161); margin: 5%; font-size: 1.2em;background-color: rgb(240,240,240);color:rgb(10,50,0); cursor: pointer;}.selection-box{width: 70%;height: 90%;}#btn0, #btn1, #btn2, #btn3{height:80%;width:16.5%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin-left: 3%;margin-right: 4.5%;margin-top: 2%;background-color: red;cursor: pointer;}.list{margin-top: 4%;margin-left: 4%;}.list select{background-color: rgb(240,240,240);border-radius: 10px;}.list option{font-family: \"Times New Roman\", Arial, serif;font-size: 1.2em;text-align: center;}";
	private static String javascript = "<script type=\"text/javascript\">document.getElementById(\"js\").style.display=\"flex\"; var nbGuess=11; var colorArray=new Array(); colorArray[0]=\"red\"; colorArray[1]=\"blue\"; colorArray[2]=\"yellow\"; colorArray[3]=\"green\"; colorArray[4]=\"white\"; colorArray[5]=\"black\"; var countBtn=new Array(); countBtn[0]=0; countBtn[1]=0; countBtn[2]=0; countBtn[3]=0; var btn0=document.getElementById(\"btn0\"); var btn1=document.getElementById(\"btn1\"); var btn2=document.getElementById(\"btn2\"); var btn3=document.getElementById(\"btn3\"); var submit_btn=document.getElementById(\"submit-js\"); /*Change color btn0*/ btn0.addEventListener(\"click\", function(){countBtn[0]=(countBtn[0] + 1) % colorArray.length; btn0.style.backgroundColor=colorArray[countBtn[0]];}); /*Change color btn1*/ btn1.addEventListener(\"click\", function(){countBtn[1]=(countBtn[1] + 1) % colorArray.length; btn1.style.backgroundColor=colorArray[countBtn[1]];}); /*Change color btn2*/ btn2.addEventListener(\"click\", function(){countBtn[2]=(countBtn[2] + 1) % colorArray.length; btn2.style.backgroundColor=colorArray[countBtn[2]];}); /*Change color btn3*/ btn3.addEventListener(\"click\", function(){countBtn[3]=(countBtn[3] + 1) % colorArray.length; btn3.style.backgroundColor=colorArray[countBtn[3]];}); /*Click on submit button*/ submit_btn.addEventListener(\"click\", function(){/*New HTTP Request*/ var xhttp=new XMLHttpRequest(); /*Stock new guess*/ for (var i=0; i < 4; i++){var bub=document.getElementById(\"bub\" + nbGuess + i); bub.style.backgroundColor=colorArray[countBtn[i]];}/*Receive data*/ xhttp.onreadystatechange=function(){if (xhttp.readyState==4 && xhttp.status==200){/*Get the response*/ var response=xhttp.responseText; /*Parse the response*/ var nbWellPlaced=Number(response[0]); var nbNotWellPlaced=Number(response[1]); var len=nbWellPlaced + nbNotWellPlaced; /*Display the result*/ for (var i=0; i < len; i++){var res=document.getElementById(\"res\" + nbGuess + i); if (nbWellPlaced > 0){res.style.backgroundColor=\"red\"; nbWellPlaced--;}else{res.style.backgroundColor=\"white\";}}/*Decrease number of guesses left*/nbGuess--; /*Check if user won*/ if (response[0]==4){alert(\"YOU WIN !\\n\\n A new game is being launched...\"); setTimeout(function(){document.location=\"play.html\"}, 2000);} /*Check if user lost*/ else if (nbGuess < 0){alert(\"GAME OVER !\\n\\n A new game is being launched...\"); setTimeout(function(){document.location=\"play.html\"}, 2000);}}}; /*Send request with GET*/ xhttp.open(\"GET\", 'play.html?choice0=' + countBtn[0] + '&choice1=' + countBtn[1] + '&choice2=' + countBtn[2] + '&choice3=' + countBtn[3], true); xhttp.send();});</script>";

	
	//Constructor
	public HTMLCreator(String prevExchanges, OutputStream workerOut, String header, boolean gzipEnabled){

		//Enable compression or not
		this.gzipEnabled = gzipEnabled;
		if(gzipEnabled){
			compress = new StringBuilder();
		}
		//Get the header of the response
		this.header = header;

		//Get the socketOutputsream
		this.socketOut = workerOut;

		//Get the previous exchanges of the current game
		this.previousExchanges = prevExchanges;

		//Get either a single or double digit number of exchanges
		if(previousExchanges.length() > 0 && previousExchanges.length() <= 55){

			this.nbExchanges = Character.getNumericValue(previousExchanges.charAt(0));
		}
		else if(previousExchanges.length() > 55){

			this.nbExchanges = Integer.parseInt(previousExchanges.substring(0,2));
		}
		else{
			this.nbExchanges = 1;
		}

		//Get the result of the guess
		if(previousExchanges.length() != 0){
			this.result = Character.getNumericValue(prevExchanges.charAt(previousExchanges.length()-2));
		}
		else{
			this.result = 0;
		}
	}	


	/********************************************************************************
	 * Creates the entire HTML page
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	public void createPage(){

		try{
			//HTTP Header
			socketOut.write(header.getBytes("UTF-8"));

			//Generate page if user won
			if(result == 4){

				sendChunkOrCompress("<!DOCTYPE html><html>");
				sendChunkOrCompress("<head><meta charset=\"utf-8\"/><title>You won</title>");
				sendChunkOrCompress("<style>body{font-family: \"Times New Roman\", Arial, serif;font-weight: normal; background-image: radial-gradient(circle at center, rgb(180,255,160), rgb(10,50,0));}.message{height:100%; font-size: 5em; text-align: center; color: rgb(10,50,0);}.submit-btn{border-radius: 10px; background-color: rgb(10,50,0); color: white; text-align: center; font-size: 28px; padding: 20px; width: 200px; cursor: pointer; margin-left: 43%;}</style>");
				sendChunkOrCompress("</head>");
				sendChunkOrCompress("<body> <div class=\"message\"> <p> CONGRATULATIONS, YOU WON ! </p></div><form method=\"post\" action=\"replay.html\"> <input class=\"submit-btn\" type=\"submit\" value=\"Replay\"/> </form> </body>");
				sendChunkOrCompress("</html>");
			}

			//Generate page if user lost
			else if(nbExchanges > 11){
				sendChunkOrCompress("<!DOCTYPE html><html>");
				sendChunkOrCompress("<head><meta charset=\"utf-8\"/><title>Game Over</title>");
				sendChunkOrCompress("<style>body{font-family: \"Times New Roman\", Arial, serif;font-weight: normal; background-image: radial-gradient(circle at center, rgb(180,255,160), rgb(10,50,0));}.message{height:100%; font-size: 5em; text-align: center; color: rgb(10,50,0);}.submit-btn{border-radius: 10px; background-color: rgb(10,50,0); color: white; text-align: center; font-size: 28px; padding: 20px; width: 200px; cursor: pointer; margin-left: 43%;}</style>");
				sendChunkOrCompress("</head>");
				sendChunkOrCompress("<body> <div class=\"message\"> <p> GAME OVER ! </p></div><form method=\"post\" action=\"replay.html\"> <input class=\"submit-btn\" type=\"submit\" value=\"Replay\"/> </form> </body>");
				sendChunkOrCompress("</html>");
			}

			//Generate normal page
			else{
				//Headers
				sendChunkOrCompress("<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><title>Mastermind</title>");

				//CSS
				sendChunkOrCompress("<style>");
				sendChunkOrCompress(staticCSS);
				createAllButtons();
				sendChunkOrCompress("</style>");
				sendChunkOrCompress("</head>");

				//HTML 
				sendChunkOrCompress("<body>");
				createBoard();

				//Javascript
				sendChunkOrCompress(javascript);

				sendChunkOrCompress("</body></html>");
			}
			//If gzip is enabled, create a GZIPOutputStream and write to it
			if(gzipEnabled){
				GZIPOutputStream gzipOut = new GZIPOutputStream(socketOut);
				gzipOut.write(compress.toString().getBytes("UTF-8"),0,compress.toString().length());
				gzipOut.finish();
				gzipOut.close();
			}else{


		    	//End the chunked enconding
			    socketOut.write("0\r\n".getBytes("UTF-8"));
			    socketOut.write("\r\n".getBytes("UTF-8"));
			    socketOut.flush();

			}
		    
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				socketOut.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}


	/********************************************************************************
	 * Sends parts of HTML code by chunked encoding or append the string to a StringBuilder
	 * to later compress
	 *
	 * ARGUMENTS :
	 *	- line : a string to send or compress if gzipEnabled is true
	 *
	 * RETURNS : /
	 ********************************************************************************/
	public void sendChunkOrCompress(String line) throws IOException{ 

		//If compression is enabled, we only compress instead of chunking
		if(gzipEnabled){
			compress.append(line);

		}else{
			final int MAX_CHUNK_SIZE = 126; //128-2 for \r\n
		    int startIndex = 0;
		    int endIndex = startIndex + MAX_CHUNK_SIZE;
	    	String chunk;

		    if(line.length() > MAX_CHUNK_SIZE){
		    	while(line.length() >= endIndex){
			    	//Cut the webpage into chunks of size MAX_CHUNK_SIZE
			    	chunk = line.substring(startIndex,endIndex);
			    	startIndex += MAX_CHUNK_SIZE;
			    	endIndex = startIndex + MAX_CHUNK_SIZE;

			    	//Get MAX_CHUNK_SIZE in hexadecimal
					String hexLength = Integer.toHexString(MAX_CHUNK_SIZE);
					hexLength = hexLength + "\r\n";
					
	    			socketOut.write(hexLength.getBytes("UTF-8"));
    				chunk = chunk + "\r\n";
    				socketOut.write(chunk.getBytes("UTF-8"));

			    }
			    //If the line length is not a multiple of MAX_CHUNK_SIZE, 
			    //there are some characters left
		    	line = line.substring(startIndex,line.length());
			}



			//Getting the length of the String to send
			String hexLength = Integer.toHexString(line.length());
			
			hexLength = hexLength + "\r\n";
	    	socketOut.write(hexLength.getBytes("UTF-8"));
			line = line + "\r\n";
	    	socketOut.write(line.getBytes("UTF-8"));

		}
	}


	/*------------------------------------CREATING CSS-------------------------------------------*/
	

	/********************************************************************************
	 * CSS style for one bubble
	 *
	 * ARGUMENTS :
	 *	- nbGuess : integer representing the index of the row
	 *	- i : integer representing the index of the bubble in the row
	 *	- color: integer representing the color of the bubble
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createBubbleCSS(int nbGuess, int i, int color)throws IOException{

		sendChunkOrCompress("#bub"+Integer.toString(nbGuess)+Integer.toString(i));
		sendChunkOrCompress("{height:70%;");
		sendChunkOrCompress("width:12%;");
		sendChunkOrCompress("border-radius: 50%;");
		sendChunkOrCompress("border: 1px solid rgb(50,50,50);");
		sendChunkOrCompress("margin: 8px;");
		sendChunkOrCompress("background-color:");
		if(color == BLANK){
			sendChunkOrCompress("rgb(201,201,201)");
		}else{
			sendChunkOrCompress(colors.values()[color].toString());
		}
		sendChunkOrCompress(";}\n");

	}

	
	/********************************************************************************
	 * CSS style for one result
	 *
	 * ARGUMENTS :
	 *	- nbGuess : integer representing the index of the row
	 *	- i : integer representing the index of the bubble in the row
	 *	- color: integer representing the color of the bubble
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createResultCSS(int nbGuess, int i, int color) throws IOException{

		sendChunkOrCompress("#res"+Integer.toString(nbGuess)+Integer.toString(i));
		sendChunkOrCompress("{height:30%;");
		sendChunkOrCompress("width:12%;");
		sendChunkOrCompress("border-radius: 50%;");
		sendChunkOrCompress("border: 1px solid rgb(50,50,50);");
		sendChunkOrCompress("margin: 5px;");
		sendChunkOrCompress("background-color:");
		if(color == BLANK){
			sendChunkOrCompress("rgb(201,201,201)");

		}else{
			sendChunkOrCompress(colors.values()[color].toString());
		}		
		sendChunkOrCompress(";}\n");
	}


	/********************************************************************************
	 * Creates the CSS template for one row of bubble buttons
	 *
	 * ARGUMENTS :
	 *	- nbGuess : integer representing the index of the row
	 *	- combination : string representing the color combination of the guess
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createBubble(int nbGuess, String combination) throws IOException{

		//Creating buttons according to each color
		for(int j = 0; j < combination.length(); j++) {
			int color = Character.getNumericValue(combination.charAt(j));
			createBubbleCSS(nbGuess,j,color);
		}
	}


	/********************************************************************************
	 * Creates the CSS template for one row of result buttons
	 *
	 * ARGUMENTS :
	 *	- nbGuess : integer representing the index of the row
	 *	- placedright : integer representing the number of good colors well placed
	 *	- ispresent : integer representing the number of badly placed good colors
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createResult(int nbGuess, int placedright, int ispresent) throws IOException{

		int i;

		//Correctly placed bubbles
		for(i = 0; i < placedright; i++){
			createResultCSS(nbGuess,i,0);
		}

		//Bubbles in the sequence but not at the correct place
		for(;i < ispresent + placedright; i++){
			createResultCSS(nbGuess,i,4);
		}

		//Blank results
		for(; i < 4 ; i++) {
			createResultCSS(nbGuess,i,BLANK);
		}
	}


	/********************************************************************************
	 * Creates the CSS template for all buttons
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createAllButtons() throws IOException{

		int nbGuess = 11;
		int i;

		//All the previous exchanges of the game
		if(this.nbExchanges >= 10){
			i = 2;
		}else{
			i = 1;
		}
		for(; i < this.previousExchanges.length(); i += 6, nbGuess--){

			//Dividing into substrings
			String guess = this.previousExchanges.substring(i, i + 6);
			String combination = guess.substring(0,4);
			
			//Result of the guess
			int placedright = Character.getNumericValue(guess.charAt(4));
			int ispresent= Character.getNumericValue(guess.charAt(5));

			//Create the row of updated bubbles
			createBubble(nbGuess,combination);

			//Create the row of updated results
			createResult(nbGuess,placedright,ispresent);
		}

		//The rest of the buttons must remain blank
		for(; nbGuess >= 0; nbGuess--){
			for(i = 0; i < 4 ; i++) {
				createBubbleCSS(nbGuess,i,BLANK);
				createResultCSS(nbGuess,i,BLANK);
			}
		}
	}
	


	/*-------------------------------------CREATE HTML---------------------------------------*/

	/********************************************************************************
	 * Creates all the board
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createBoard() throws IOException{

		//Mastermind board
		createMastermindBoard();

		//Selection board
		createSelectionBoard();
	}


	/********************************************************************************
	 * Creates the mastermind board (guesses and results)
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createMastermindBoard() throws IOException{

		sendChunkOrCompress("<div class=\"mastermind-board\">");

		//Title
		sendChunkOrCompress("<div class=\"title\"><h2 class=\"mastermind-text\"> MASTERMIND</h2></div>");

		//Guess board
		sendChunkOrCompress("<div class=\"guess-container\">");

		for(int nbGuess = 0; nbGuess <= 11; nbGuess++){
			createRow(nbGuess);
		}

		sendChunkOrCompress("</div></div>");

	}


	/********************************************************************************
	 * Creates a row (one guess and its result)
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createRow(int index) throws IOException{

		sendChunkOrCompress("<div class=\"guess-row flexer\">");

		// Guess box
		createGuessBox(index);

		//Result box
		createResultBox(index);

		sendChunkOrCompress("</div>");

	}


	/********************************************************************************
	 * Creates a guess box
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createGuessBox(int index) throws IOException{

		sendChunkOrCompress("<div class=\"guess-box flexer\">");

		//Guess bubbles
		for(int i=0; i < 4; i++){
			sendChunkOrCompress("<div id=\"bub"+index+i+"\"></div>");
		}

		sendChunkOrCompress("</div>");

	}

	
	/********************************************************************************
	 * Creates a result box
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createResultBox(int index) throws IOException{

		sendChunkOrCompress("<div class=\"result-box flexer\">");

		//Result bubbles
		for(int i=0; i < 4; i++){
			sendChunkOrCompress("<div id=\"res"+index+i+"\"></div>");
		}

		sendChunkOrCompress("</div>");

	}

	
	/********************************************************************************
	 * Creates the selection board
	 *
	 * ARGUMENTS : /
	 *
	 * RETURNS : /
	 ********************************************************************************/
	private void createSelectionBoard() throws IOException{

		//-------If JS enabled-----------
		sendChunkOrCompress("<div class=\"selection-board flexer\" id=\"js\"> <div class=\"selection-box\">");

		//Guess buttons
		sendChunkOrCompress("<div class=\"guess-box flexer\">");
		for(int i=0; i<4; i++){
			sendChunkOrCompress("<button id=\"btn"+i+"\"></button>");
		}
		sendChunkOrCompress("</div></div>");

		//Submit button
		sendChunkOrCompress("<div class=\"button\"><button class=\"submit-button\" id=\"submit-js\"> Submit </button></div>");
		sendChunkOrCompress("</div>");
		//--------------------------------


		//-------If JS disabled-----------
		sendChunkOrCompress("<noscript>");
		sendChunkOrCompress("<form class=\"selection-board flexer\" method=\"post\" action=\"play.html\">");

		//Guess scrolling lists
		sendChunkOrCompress("<div class=\"selection-box flexer\">");

		for(int i=0; i < 4; i++){
			sendChunkOrCompress("<div class=\"list\">");
			sendChunkOrCompress("<select name=\"choice"+i+"\">");

			sendChunkOrCompress("<option value=\"0\">red</option>");	
			sendChunkOrCompress("<option value=\"1\">blue</option>");
			sendChunkOrCompress("<option value=\"2\">yellow</option>");
			sendChunkOrCompress("<option value=\"3\">green</option>");
			sendChunkOrCompress("<option value=\"4\">white</option>");
			sendChunkOrCompress("<option value=\"5\">black</option>");

			sendChunkOrCompress("</select>");
			sendChunkOrCompress("</div>");
		}
		sendChunkOrCompress("</div>");

		//Submit button
		sendChunkOrCompress("<div class=\"button\">");
		sendChunkOrCompress("<input class=\"submit-button\" type=\"submit\" value=\"Submit\"/>");
		sendChunkOrCompress("</div>");

		sendChunkOrCompress("</form>");
		sendChunkOrCompress("</noscript>");
		//--------------------------------
	}
}