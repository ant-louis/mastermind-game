


public class HTMLCreator {

	private  enum colors {
		  RED,
		  BLUE,
		  YELLOW,
		  GREEN,
		  WHITE,
		  BLACK;
	}
	//START
	String start = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /><title>Mastermind</title>";


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


	public static String createRow(){
		return createResultCSS(1,2,3);
	}

	/*
	private String createBox(){
		for(var i=0; i<4; i++){
			createBubble(i,nbGuess,color);


	}
	*/


}