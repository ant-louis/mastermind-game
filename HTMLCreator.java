
public class HTMLCreator {

	private  enum colors {
		  red,
		  blue,
		  yellow,
		  green,
		  white,
		  black;
	}

	String previousexchanges;
	int nbofexchanges;

	public HTMLCreator(String prevExchanges){
		this.previousexchanges = prevExchanges;
		this.nbofexchanges = Character.getNumericValue(prevExchanges.charAt(0));



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

	


	private String blankbubble ;

	//CSS style for the bubbles
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
		button.append(colors.values()[color]);
		button.append(";}\n");

		return button.toString();
	}
	//CSS style for the results
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
		result.append(colors.values()[color]);
		result.append(";}\n");

		return result.toString();
	}


	//Creates the CSS template for one row of bubbles buttons
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
		
		for(i = 0; i < placedright; i++){
			row.append(createResultCSS(nbGuess,i,0));
		}

		for(;i < ispresent; i++){
			row.append(createResultCSS(nbGuess,i,5));
		}

		return row.toString();
	}

	//Creates the CSS template for all buttons
	private String createButtonsCSS(){
		int nbGuess = 11;
		StringBuilder buttonCSS = new StringBuilder();

		//All the previous exchanges of the game
		for(int i = 1; i < this.previousexchanges.length(); i += 6, nbGuess--){

			//Dividing into substrings
			String guess = this.previousexchanges.substring(i, i + 6);
			String combination = guess.substring(0,4);

			int placedright = Character.getNumericValue(guess.charAt(4));
			int ispresent= Character.getNumericValue(guess.charAt(5));

			//Create the row of updated bubbles
			buttonCSS.append(createBubble(nbGuess,combination));

			//Create the row of updated results
			buttonCSS.append(createResult(nbGuess,placedright,ispresent));

		}
		//The rest of the button must remain blank
		for(; nbGuess >= 0; nbGuess--){

		}


		System.out.println(buttonCSS.toString());
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