


public class HTMLCreator {

	private  enum colors {
		  red,
		  blue,
		  yellow,
		  green,
		  white,
		  black;
	}
	//START
	String start = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><title>Mastermind</title>";

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
		button.append(";}");

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
		result.append(";}");

		return result.toString();
	}


	public static String createAllButtonsCSS(int nbGuess){
		StringBuilder row = new StringBuilder();
		for(int i = 0; i < 4; i++){
			row.append(createBubbleCSS(nbGuess,i,color));
		}
		for(int i = 0; i < 4; i++){
			row.append(createResultCSS(nbGuess,i,color))
		}
	
		return row.toString();
	}

	
	private String createAllResultsCSS(){
		StringBuilder box = new StringBuilder();
		for(int nbGuess = 11,nbGuess >= 0, nbGuess--){
			box.append(createRow(nbGuess));
		}
	}
	


}