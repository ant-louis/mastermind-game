
public class HTMLCreator {

	private  enum colors {
		  red,
		  blue,
		  yellow,
		  green,
		  white,
		  black;
	}

	/******************************************CREATE PAGE***************************************/

	public static createPage(){

		StringBuilder page = new StringBuilder();

		//Headers
		page.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><title>Mastermind</title>");

		//CSS
		page.append("<style>");

		page.append("</style>");

		//HTML 
		page.append("<body>");

		page.append("</body>");
	}


	/************************************CREATING CSS**********************************************/

	//CSS style for the bubbles
	private static String createBubbleCSS(int nbGuess, int i, int color){
		StringBuilder button = new StringBuilder("#bub");
		button.append(nbGuess);
		button.append(i);
		button.append("{height:70%;");
		button.append("width:12%;");
		button.append("border-radius: 50%;");
		button.append("border: 1px solid rgb(50,50,50);");
		button.append("margin: 8px;");
		button.append("background-color:");
		button.append(colors.values()[color]);
		button.append(";}\n");

		return button.toString();
	}
	//CSS style for the results
	private static String createResultCSS(int nbGuess, int i, int color){
		StringBuilder result = new StringBuilder("#res");
		result.append(nbGuess);
		result.append(i);
		result.append("{height:30%;");
		result.append("width:12%;");
		result.append("border-radius: 50%;");
		result.append("border: 1px solid rgb(50,50,50);");
		result.append("margin: 5px;");
		result.append("background-color:");
		result.append(colors.values()[color]);
		result.append(";}\n");

		return result.toString();
	}


	//Creates the CSS template for one row of a given type of button
	public static String createButtonType(int nbGuess,String type){
		StringBuilder row = new StringBuilder();

		if(type.equals("bubble")){

			for(int i = 0; i < 4; i++){
				row.append(createBubbleCSS(nbGuess,i,color));
			}

		}else{

			for(int i = 0; i < 4; i++){
				row.append(createResultCSS(nbGuess,i,color));
			}
		}
	
		return row.toString();
	}

	//Creates the CSS template for all buttons
	private String createButtonsCSS(){

		StringBuilder buttonCSS = new StringBuilder();

		for(int nbGuess = 11; nbGuess >= 0; nbGuess--){
			buttonCSS.append(createButtonType(nbGuess,"bubble"));
		}

		for(int nbGuess = 11; nbGuess >= 0; nbGuess--){
			buttonCSS.append(createButtonType(nbGuess,"result"));
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
		selectionBoard.append("<div class=\"selection-board flexer\"> <div class=\"selection-box\">");

		//Guess buttons
		selectionBoard.append("<div class=\"guess-box flexer\">");
		for(int i=0; i<4; i++){
			selectionBoard.append("<button id=\"btn"+i+"\"></button>");
		}
		selectionBoard.append("</div></div>");

		//Submit button
		selectionBoard.append("<div class=\"button\"><button id=\"submit-button\"> Submit </button></div>");

		selectionBoard.append("</div>");

		return selectionBoard.toString();
	}

}