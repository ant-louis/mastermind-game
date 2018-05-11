
public class HTMLCreator {


	private enum colors {
		  red,
		  blue,
		  yellow,
		  green,
		  white,
		  black;
	}
	//Class variables
	int nbExchanges;
	String previousexchanges;
	private static final int BLANK = 10;
	private static String staticCSS = "body{font-family: \"Times New Roman\", Arial, serif;font-weight: normal; background-image: radial-gradient(ellipse at center, rgb(180,255,160), rgb(10,50,0));}.flexer{display: flex;}/********************GUESSES_AND_SCORES*********************/.mastermind-board{width: 30%;min-width:400px; height:650px; margin: 0 auto; margin-top: 20px; margin-bottom: 20px;}.title{width: 100%;height: 12%;}.mastermind-text{font-size: 4em;color: rgb(10,50,0);text-align: center;margin-top: 10px;text-shadow: 1px 1px rgb(220,255,215);}.guess-container{width:100%;height:90%;}.guess-row{box-sizing: border-box;height: 8.2%;width: 100%;}.guess-box{width: 70%;height: 100%;}.result-box{width: 28%;height: 100%;}/*************************SELECTION************************/#js{display: none;}.selection-board{width: 30%;min-width:400px;height: 50px;border: 1px solid rgb(10,50,0); border-radius: 10px; margin: 0 auto;}.button{width: 30%;height: 100%;}.submit-button{width: 80%;height: 70%;border: 1px solid rgb(161,161,161); border-radius: 10px; text-align: center; box-shadow: 2px 2px 2px rgb(161,161,161); margin: 5%; font-size: 1.2em;background-color: rgb(240,240,240);color:rgb(10,50,0);}.selection-box{width: 70%;height: 90%;}#btn0, #btn1, #btn2, #btn3{height:80%;width:16.5%;border-radius: 50%;border: 1px solid rgb(50,50,50);margin-left: 3%;margin-right: 4.5%;margin-top: 2%;background-color: red;}.list{margin-top: 3%;margin-left: 4%;}.list select{border-radius: 10%;}.list option{font-family: \"Times New Roman\", Arial, serif;font-size: 1.2em;text-align: center;}";
	private static String javascript = "<script type=\"text/javascript\">document.getElementById(\"js\").style.display=\"flex\";var nbGuess=11;var colorArray=new Array(); colorArray[0]=\"red\"; colorArray[1]=\"blue\"; colorArray[2]=\"yellow\"; colorArray[3]=\"green\"; colorArray[4]=\"white\"; colorArray[5]=\"black\"; var countBtn=new Array(); countBtn[0]=0; countBtn[1]=0; countBtn[2]=0; countBtn[3]=0; var btn0=document.getElementById(\"btn0\");var btn1=document.getElementById(\"btn1\");var btn2=document.getElementById(\"btn2\");var btn3=document.getElementById(\"btn3\");var submit_btn=document.getElementById(\"submit-js\");btn0.addEventListener(\"click\", function(){countBtn[0]=(countBtn[0] + 1) % colorArray.length; btn0.style.backgroundColor=colorArray[countBtn[0]];});btn1.addEventListener(\"click\", function(){countBtn[1]=(countBtn[1] + 1) % colorArray.length; btn1.style.backgroundColor=colorArray[countBtn[1]];});btn2.addEventListener(\"click\", function(){countBtn[2]=(countBtn[2] + 1) % colorArray.length; btn2.style.backgroundColor=colorArray[countBtn[2]];});btn3.addEventListener(\"click\", function(){countBtn[3]=(countBtn[3] + 1) % colorArray.length; btn3.style.backgroundColor=colorArray[countBtn[3]];});submit_btn.addEventListener(\"click\", function(){/*Stock new guess*/for(var i=0; i<4; i++){var bub=document.getElementById(\"bub\"+nbGuess+i);bub.style.backgroundColor=colorArray[countBtn[i]]}/*New HTTP Request*/ var xhttp=new XMLHttpRequest(); /*---------------Receive data--------------*/ xhttp.onreadystatechange=function(){if (xhttp.readyState==4 && xhtpp.status==200){alert(\"Get a response !\");/*Get the response*/var response=xhttp.responseText;/*Parse the response*/var nbWellPlaced=Number(response[0]);var nbNotWellPlaced=Number(response[1]);var len=nbWellPlaced + nbNotWellPlaced;/*Display the result*/for(var i=0; i < len; i++){var res=document.getElementById(\"res\"+nbGuess+i);if(nbWellPlaced > 0){res.style.backgroundColor=\"red\";nbWellPlaced--;}else{res.style.backgroundColor=\"black\";}}/*Check if user wins*/if(nbWellPlaced==4 && nbNotWellPlaced==0){alert(\"YOU WIN !\");setTimeout(function(){document.location=\"play.html\"}, 3000);}}}; /*---------------Send request----------------*/ var value0=encodeURIComponent(countBtn[0]); var value1=encodeURIComponent(countBtn[1]); var value1=encodeURIComponent(countBtn[2]); var value3=encodeURIComponent(countBtn[3]); /* GET */ xhttp.open(\"GET\", 'play.html?choice0='+value0+'&choice1='+value1+'&choice2='+value2+'&choice3='+value3, true); alert(\"Open method passed\"); xhttp.send(); alert(\"Send method passed\"); nbGuess--;if(nbGuess < 0){alert(\"GAME OVER\");setTimeout(function(){document.location=\"play.html\"}, 3000);}});</script>";

	
	//Constructor
	public HTMLCreator(String prevExchanges){
		this.previousexchanges = prevExchanges;
		//this.nbExchanges = Character.getNumericValue(prevExchanges.charAt(0));
	}


	//Create the page
	public String createPage(){

		StringBuilder page = new StringBuilder();

		//Headers
		page.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><title>Mastermind</title>");

		//CSS
		page.append("<style>");
		page.append(staticCSS);
		page.append(createAllButtons());
		page.append("</style>");
		page.append("</head>");

		//HTML 
		page.append("<body>");
		page.append(createBoard());

		//Javascript
		page.append(javascript);

		page.append("</body></html>");

		return page.toString();
	}


	/************************************CREATING CSS**********************************************/
	//CSS style for one bubble 
	private String createBubbleCSS(int nbGuess, int i, int color){
		StringBuilder button = new StringBuilder("#bub");
		button.append(nbGuess);
		button.append(i);
		button.append("{height:70%;");
		button.append("width:12%;");
		button.append("border-radius: 50%;");
		button.append("border: 1px solid rgb(50,50,50);");
		button.append("margin: 8px;");
		button.append("background-color:");
		if(color == BLANK){
			button.append("rgb(240,240,240)");
		}else{
			button.append(colors.values()[color]);
		}
		button.append(";}\n");

		return button.toString();
	}

	//CSS style for one result
	private String createResultCSS(int nbGuess, int i, int color){
		StringBuilder result = new StringBuilder("#res");
		result.append(nbGuess);
		result.append(i);
		result.append("{height:30%;");
		result.append("width:12%;");
		result.append("border-radius: 50%;");
		result.append("border: 1px solid rgb(50,50,50);");
		result.append("margin: 5px;");
		result.append("background-color:");
		if(color == BLANK){
			result.append("rgb(240,240,240)");
		}else{
			result.append(colors.values()[color]);
		}		
		result.append(";}\n");

		return result.toString();
	}


	//Creates the CSS template for one row of bubble buttons
	private String createBubble(int nbGuess, String combination){

		StringBuilder row = new StringBuilder();

		//Creating buttons according to each color
		for(int j = 0; j < combination.length(); j++) {
			int color = Character.getNumericValue(combination.charAt(j));
			row.append(createBubbleCSS(nbGuess,j,color));
		}
	
		return row.toString();
	}

	//Creates the CSS template for one row of result buttons
	private String createResult(int nbGuess, int placedright, int ispresent){

		StringBuilder row = new StringBuilder();
		int i;
		

		//Correctly placed bubbles
		for(i = 0; i < placedright; i++){
			row.append(createResultCSS(nbGuess,i,0));
		}

		//Bubbles in the sequence but not at the correct place
		for(;i < ispresent + placedright; i++){
			row.append(createResultCSS(nbGuess,i,5));
		}

		//Blank results
		for(; i < 4 ; i++) {
			row.append(createResultCSS(nbGuess,i,BLANK));
		}

		return row.toString();
	}

	//Creates the CSS template for all buttons
	private String createAllButtons(){
		int nbGuess = 11;
		StringBuilder buttonCSS = new StringBuilder();

		//All the previous exchanges of the game
		for(int i = 1; i < this.previousexchanges.length(); i += 6, nbGuess--){

			//Dividing into substrings
			String guess = this.previousexchanges.substring(i, i + 6);
			String combination = guess.substring(0,4);
			
			//Result of the guess
			int placedright = Character.getNumericValue(guess.charAt(4));
			int ispresent= Character.getNumericValue(guess.charAt(5));

			//Create the row of updated bubbles
			buttonCSS.append(createBubble(nbGuess,combination));

			//Create the row of updated results
			buttonCSS.append(createResult(nbGuess,placedright,ispresent));

		}

		//The rest of the buttons must remain blank
		for(; nbGuess >= 0; nbGuess--){
			for(int i = 0; i < 4 ; i++) {
				buttonCSS.append(createBubbleCSS(nbGuess,i,BLANK));
				buttonCSS.append(createResultCSS(nbGuess,i,BLANK));
			}
		}

		return buttonCSS.toString();

	}
	


	/**************************************CREATE HTML******************************************/
	private String createBoard(){

		StringBuilder board = new StringBuilder();

		//Mastermind board
		String mastermindBoard = createMastermindBoard();
		board.append(mastermindBoard);

		//Selection board
		String selectionBoard = createSelectionBoard();
		board.append(selectionBoard);

		return board.toString();
	}


	private String createMastermindBoard(){

		StringBuilder mastermindBoard = new StringBuilder("<div class=\"mastermind-board\">");

		//Title
		mastermindBoard.append("<div class=\"title\"><h2 class=\"mastermind-text\"> MASTERMIND</h2></div>");

		//Guess board
		mastermindBoard.append("<div class=\"guess-container\">");

		for(int nbGuess = 0; nbGuess <= 11; nbGuess++){
			String row = createRow(nbGuess);
			mastermindBoard.append(row);
		}
		mastermindBoard.append("</div></div>");

		return mastermindBoard.toString();
	}


	private String createRow(int index){

		StringBuilder row = new StringBuilder();

		row.append("<div class=\"guess-row flexer\">");

		// Guess box
		String guessBox = createGuessBox(index);
		row.append(guessBox);

		//Result box
		String resultBox = createResultBox(index);
		row.append(resultBox);

		row.append("</div>");

		return row.toString();
	}


	private String createGuessBox(int index){

		StringBuilder guessBox = new StringBuilder("<div class=\"guess-box flexer\">");

		//Guess bubbles
		for(int i=0; i < 4; i++){
			guessBox.append("<div id=\"bub"+index+i+"\"></div>");
		}

		guessBox.append("</div>");

		return guessBox.toString();
	}


	private String createResultBox(int index){

		StringBuilder resultBox = new StringBuilder("<div class=\"result-box flexer\">");

		//Result bubbles
		for(int i=0; i < 4; i++){
			resultBox.append("<div id=\"res"+index+i+"\"></div>");
		}

		resultBox.append("</div>");

		return resultBox.toString();
	}


	private String createSelectionBoard(){

		StringBuilder selectionBoard = new StringBuilder();

		//-------If JS enabled-----------
		selectionBoard.append("<div class=\"selection-board flexer\" id=\"js\"> <div class=\"selection-box\">");

		//Guess buttons
		selectionBoard.append("<div class=\"guess-box flexer\">");
		for(int i=0; i<4; i++){
			selectionBoard.append("<button id=\"btn"+i+"\"></button>");
		}
		selectionBoard.append("</div></div>");

		//Submit button
		selectionBoard.append("<div class=\"button\"><button class=\"submit-button\" id=\"submit-js\"> Submit </button></div>");
		selectionBoard.append("</div>");
		//--------------------------------

		//-------If JS disabled-----------
		selectionBoard.append("<noscript>");
		selectionBoard.append("<form class=\"selection-board flexer\" method=\"post\" action=\"play.html\">");

		//Guess scrolling lists
		selectionBoard.append("<div class=\"selection-box flexer\">");

		for(int i=0; i < 4; i++){
			selectionBoard.append("<div class=\"list\">");
			selectionBoard.append("<select name=\"choice"+i+"\">");

			selectionBoard.append("<option value=\"0\">red</option>");	
			selectionBoard.append("<option value=\"1\">blue</option>");
			selectionBoard.append("<option value=\"2\">yellow</option>");
			selectionBoard.append("<option value=\"3\">green</option>");
			selectionBoard.append("<option value=\"4\">white</option>");
			selectionBoard.append("<option value=\"5\">black</option>");

			selectionBoard.append("</select>");
			selectionBoard.append("</div>");
		}
		selectionBoard.append("</div>");

		//Submit button
		selectionBoard.append("<div class=\"button\">");
		selectionBoard.append("<input class=\"submit-button\" type=\"submit\" value=\"Submit\"/>");
		selectionBoard.append("</div>");

		selectionBoard.append("</form>");
		selectionBoard.append("</noscript>");
		//--------------------------------

		return selectionBoard.toString();
	}

}