/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import java.util.*;

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
		upperScores = new int[nPlayers];
		lowerScores = new int[nPlayers];
		utilizedCats = new boolean[nPlayers][YahtzeeConstants.N_CATEGORIES];
		totalScores = new int[nPlayers];
		for(int catsUsed = 0; catsUsed < YahtzeeConstants.N_SCORING_CATEGORIES; catsUsed++){ // runs only for number of valid categories
			for(int player = 1; player <= nPlayers; player++){ // cycles through the number of players
				initialRoll(player, catsUsed); //rolls the dice for the first time and displays them.
				reRollDiceOrDontIdc(); //the second and/ or third rolls if they so choose to reroll any at all.
				pickCategory(player);	
			}
		}
		upperAndLower();
		determineWinner();
	}
	
	private void initialRoll(int player, int category){
		int turn = category+1;
		display.printMessage(playerNames[player-1] + "'s turn! Click " + "\"Roll Dice\"" + " button to roll the dice. "
				+ "This is turn " + turn + " of " + YahtzeeConstants.N_SCORING_CATEGORIES);
		display.waitForPlayerToClickRoll(player);
		for(int i = 0; i < YahtzeeConstants.N_DICE; i++){
			diceValues[i] = rgen.nextInt(1,6);
		}
		display.displayDice(diceValues);
	}
	
	private void reRollDiceOrDontIdc(){
		for(int rolls = 1; rolls <= 2; rolls++){ //allows for 2 re-rolls or gets bypassed if no dice selected
			display.printMessage("Select the dice you wish to re-roll and click " + "\"Roll Again\"" + ".");
			display.waitForPlayerToSelectDice();
			for(int die = 0; die < YahtzeeConstants.N_DICE; die++){// for every die
				if(display.isDieSelected(die)){// if the die is selected
					diceValues[die] = rgen.nextInt(1,6);// assign it a random integer between 1 and 6 (inclusive)
				}
			}
			display.displayDice(diceValues);// display the new dice values
		}
	}
	
	private void pickCategory(int player){
		display.printMessage("Select a category for this roll.");
		while(true){
			int myCategory = display.waitForPlayerToSelectCategory(); // gets the category the player has selected
			if (utilizedCats[player-1][myCategory] == false){// If that player hasn't already utilized the selected category
					int score = categories(myCategory, player);// determine the score for the diceValues based on that category
					display.updateScorecard(myCategory, player, score);// put that score on the score card in the right spot
					display.updateScorecard(YahtzeeConstants.TOTAL, player, score+totalScores[player-1]);// update the total score
					totalScores[player-1] += score;// log the updated total score
					utilizedCats[player-1][myCategory] = true;// log that the category has been used for that player
					break;// move on
			} else if (utilizedCats[player-1][myCategory] == true){
				display.printMessage("Please select a category that you haven't already selected.");
			}
		}
	}
		
	private int categories(int index, int player){
		// The initial "if" statement assumes YahtzeeConstants."#" = # (e.g. TWOS == 2, THREES == 3, etc.)
		if(index == YahtzeeConstants.ONES || index == YahtzeeConstants.TWOS ||
				index == YahtzeeConstants.THREES || index == YahtzeeConstants.FOURS ||
				index == YahtzeeConstants.FIVES || index == YahtzeeConstants.SIXES){
			int sum = 0;
			for (int i = 0; i < YahtzeeConstants.N_DICE; i++){
				if(diceValues[i]==index){
					sum+=index;
				}
			}
			upperScores[player-1] += sum;
			return sum;
		} else if(index == YahtzeeConstants.THREE_OF_A_KIND){
			int sum = 0;
			for(int dieToMatch = 0; dieToMatch < YahtzeeConstants.N_DICE-2; dieToMatch++){
				int count = 1; // dieToMatch will always match itself
				/*The logic in the for loop below is that after the first die
				 * has been determined to either match or not match dice 2 through N_DICE,
				 * comparing the first die to the other dice again is superfluous.
				 * 
				 * For example, the total possible matching pairs for 6-sided dice are:
				 * 12, 13, 14, 15, 16
				 * 23, 24, 25, 26 --> to check 21 would give the same result as 12 and is therefore unnecessary.
				 * 34, 35, 36
				 * 45, 46 --> if 45 and 46 are a match, 34, 24, and/ or 14 would have already determined 3 of a kind if true 
				 * 56 --> therefore checking these combinations is also superfluous & explains the "N_DIC-2" condition in 
				 * the "dieToMatch" for loop.
				 */
				for(int i = dieToMatch+1; i < YahtzeeConstants.N_DICE; i++){
					if(diceValues[i] == diceValues[dieToMatch]){
						count++;	
					}
				}
				if(count >= 3){
					for(int i = 0; i < YahtzeeConstants.N_DICE ; i++){
						sum+=diceValues[i];
					}
				break;
				}
			}
			lowerScores[player-1] += sum;
			if(sum != 0){
				return sum;
			} else {
				return 0;
			} 
		} else if(index == YahtzeeConstants.FOUR_OF_A_KIND){
				int sum = 0;
				for(int dieToMatch = 0; dieToMatch < YahtzeeConstants.N_DICE-3; dieToMatch++){
					int count = 1;
					for(int i = dieToMatch+1; i < YahtzeeConstants.N_DICE; i++){
						if(diceValues[i] == diceValues[dieToMatch]){
							count++;	
						}
					}
					if(count >= 4){
						for(int i = 0; i < YahtzeeConstants.N_DICE ; i++){
							sum+=diceValues[i];
						}
					break;
					}
				}
				lowerScores[player-1] += sum;
				if(sum != 0){
					return sum;
				} else {
					return 0;
				} 			
 		} else if(index == YahtzeeConstants.FULL_HOUSE){
 			Arrays.sort(diceValues);
 			//There will either be 3 matching dice followed by a pair, or a pair followed by 3 matching dice
 			//in order for this category to produce 25 points. 			
 			if(diceValues[0] == diceValues[1] && diceValues[0] == diceValues[2] && diceValues[3] == diceValues[4]){//3+2
 				lowerScores[player-1] += 25;
 				return 25;
 			} else if(diceValues[0] == diceValues[1] && diceValues[2] == diceValues[3] && diceValues[2] == diceValues[4]){//2+3
 				lowerScores[player-1] += 25;
 				return 25;
 			} else {
 				return 0;
 			}
		} else if(index == YahtzeeConstants.SMALL_STRAIGHT){//
			Arrays.sort(diceValues);//1 1 1 2 3
			ArrayList<Integer> removeRepeats = new ArrayList<Integer>();
			for(int i = 0; i < YahtzeeConstants.N_DICE; i++){
				if(removeRepeats.contains(diceValues[i])==false){
					removeRepeats.add(diceValues[i]);
				}
			}
			for(int i = 0; i < 2; i++){
				if(removeRepeats.size() >= 4 &&
						removeRepeats.get(i+1) - removeRepeats.get(i) == 1 && 
						removeRepeats.get(i+2) - removeRepeats.get(i+1) == 1 &&
						removeRepeats.get(i+3) - removeRepeats.get(i+2) == 1){
					lowerScores[player-1] += 30;
					return 30;
				}
			} 
			return 0;	
		} else if(index == YahtzeeConstants.LARGE_STRAIGHT){
			Arrays.sort(diceValues);
			ArrayList<Integer> removeRepeats = new ArrayList<Integer>();
			for(int i = 0; i < YahtzeeConstants.N_DICE; i++){
				if(removeRepeats.contains(diceValues[i])==false){
					removeRepeats.add(diceValues[i]);
				}
			}
			if(removeRepeats.size() == 5 && 
					removeRepeats.get(1) - removeRepeats.get(0) == 1 && 
					removeRepeats.get(2) - removeRepeats.get(1) == 1 &&
					removeRepeats.get(3) - removeRepeats.get(2) == 1 &&
					removeRepeats.get(4) - removeRepeats.get(3) == 1){
				lowerScores[player-1] += 40;
				return 40;
			} 
			return 0;
		} else if(index == YahtzeeConstants.YAHTZEE){
				int count = 1; // since the first die will always match itself
				for(int i = 1; i < YahtzeeConstants.N_DICE; i++){// For every die except the first one
					if(diceValues[i] == diceValues[0]){// If die 2 through N_DICE matches the first die
						count++;	
					}
				}
				if(count == YahtzeeConstants.N_DICE){ // If the number of dice that match the first die equals # of all dice
					return 50;
				} else {
					return 0;
				}
		} else if(index == YahtzeeConstants.CHANCE){
			/*Simply adds up all of the face values on 
			 * each die and displays that total.
			 */
			int sum = 0;
			for(int i = 0; i < YahtzeeConstants.N_DICE ; i++){
				sum+=diceValues[i];
			}
				lowerScores[player-1] += sum;
				return sum;
		} else {
			return -1;
		}
	
	}

	private void upperAndLower(){
		/* If the upper score is higher than 63, the current 
		 * player gets a 35 point bonus
		 */
		for(int player = 1; player <= nPlayers; player++){
			if(upperScores[player-1] > 63){
				totalScores[player-1]+=35;
				display.updateScorecard(YahtzeeConstants.UPPER_BONUS, player, 35);
			} else {
				display.updateScorecard(YahtzeeConstants.UPPER_BONUS, player, 0);
			}
			display.updateScorecard(YahtzeeConstants.UPPER_SCORE, player, upperScores[player-1]);
			display.updateScorecard(YahtzeeConstants.LOWER_SCORE, player, lowerScores[player-1]);
		}
	}
	
	private void determineWinner(){
		int winningScore = totalScores[0]; // if only 1 player, they're obviously the winner no matter what.
		int winner = 0;
		
		if(nPlayers > 1){
			for(int player = 1; player < nPlayers; player++){
				if(totalScores[player] > totalScores[winner]){
					winner = player;
					winningScore = totalScores[player];
				}
			}	
		}
		
		display.printMessage("Congratulations, " + playerNames[winner] + ", you're the winner with a total "
				+ "score of " + winningScore + "!"); 
	}
	
/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private int[] diceValues = new int[YahtzeeConstants.N_DICE];
	
	/*In order to keep track of the categories each player has used, an array with "nPlayers" rows 
	 * and "YahtzeeDisplay.N_Categories" columns has been made. Only the scorable categories will be used.
	 * (i.e. each subarray's positions that do not correspond to a "N_SOCRING_CATEGORY" will always be null).
	 */
	private boolean[][] utilizedCats;// tracks which categories have already been used
	private int[] totalScores;// sums the 3 categories in divisionScores for each player
	private int[] upperScores;// will track the total score in the upper division for each player
	private int[] lowerScores;// will track the total score in the lower division for each player
	
}
