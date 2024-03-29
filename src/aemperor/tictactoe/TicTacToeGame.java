package aemperor.tictactoe;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 * 
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is X and the computer is O. 
 */

import java.util.Random;

import android.util.Log;
import android.view.View.OnTouchListener;

public class TicTacToeGame {

	private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
	// the computer's difficulty level
	public enum DifficultyLevel {Easy, Harder, Expert};
	// current difficulty level
	private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
	public static final int BOARD_SIZE = 9;
	
	public static final char HUMAN_PLAYER = 'X';
	public static final char COMPUTER_PLAYER = 'O';
	public static final char OPEN_SPOT = ' ';
	
	private Random mRand; 
	
	public TicTacToeGame() {
		
		// Seed the random number generator
		mRand = new Random(); 
		clearBoard();
	}
	
	private void displayBoard(char[] board)	{
		System.out.println();
		System.out.println(board[0] + " | " + board[1] + " | " + board[2]);
		System.out.println("-----------");
		System.out.println(board[3] + " | " + board[4] + " | " + board[5]);
		System.out.println("-----------");
		System.out.println(board[6] + " | " + board[7] + " | " + board[8]);
		System.out.println();
	}
	
	public char[] getBoard() {
		return mBoard;
	}
	
	public DifficultyLevel getDifficultyLevel() {
		return mDifficultyLevel;
	}
	
	public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
		mDifficultyLevel = difficultyLevel;
	}
	
	private int getRandomMove() {
		// Generate random move
		int move = -1;
		do {
			move = mRand.nextInt(BOARD_SIZE);
		} while (mBoard[move] != OPEN_SPOT);

		return move;
		
	}
	
	private int getWinningMove() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			if (mBoard[i] == OPEN_SPOT) {
				mBoard[i] = COMPUTER_PLAYER;
				if (checkForWinner() == 3) {
					mBoard[i] = OPEN_SPOT; // restore
					return i;
				}
				else
					mBoard[i] = OPEN_SPOT;
			}
		}
		return -1; // never found a winning move
	}
	
	private int getBlockingMove() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			if (mBoard[i] == OPEN_SPOT) {
				mBoard[i] = HUMAN_PLAYER;
				if (checkForWinner() == 2) {
					mBoard[i] = OPEN_SPOT; // restore
					return i;
				}
				else
					mBoard[i] = OPEN_SPOT;
			}
		}
		return -1; // never found a blocking move
	}
	
	
	
	// Check for a winner.  Return
	//  0 if no winner or tie yet
	//  1 if it's a tie
	//  2 if X won
	//  3 if O won
	/** 
	 * Check for a winner and return a status value indicating who has won. 
	 * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won, 
	 * or 3 if O won. 
	 */ 
	public int checkForWinner(char[] board) {
		
		// Check horizontal wins
		for (int i = 0; i <= 6; i += 3)	{
			if (board[i] == HUMAN_PLAYER && 
				board[i+1] == HUMAN_PLAYER &&
				board[i+2]== HUMAN_PLAYER)
				return 2;
			if (board[i] == COMPUTER_PLAYER && 
				board[i+1]== COMPUTER_PLAYER && 
				board[i+2] == COMPUTER_PLAYER)
				return 3;
		}
	
		// Check vertical wins
		for (int i = 0; i <= 2; i++) {
			if (board[i] == HUMAN_PLAYER && 
				board[i+3] == HUMAN_PLAYER && 
				board[i+6]== HUMAN_PLAYER)
				return 2;
			if (board[i] == COMPUTER_PLAYER && 
				board[i+3] == COMPUTER_PLAYER && 
				board[i+6]== COMPUTER_PLAYER)
				return 3;
		}
	
		// Check for diagonal wins
		if ((board[0] == HUMAN_PLAYER &&
			 board[4] == HUMAN_PLAYER && 
			 board[8] == HUMAN_PLAYER) ||
			(board[2] == HUMAN_PLAYER && 
			 board[4] == HUMAN_PLAYER &&
			 board[6] == HUMAN_PLAYER))
			return 2;
		if ((board[0] == COMPUTER_PLAYER &&
			 board[4] == COMPUTER_PLAYER && 
			 board[8] == COMPUTER_PLAYER) ||
			(board[2] == COMPUTER_PLAYER && 
			 board[4] == COMPUTER_PLAYER &&
			 board[6] == COMPUTER_PLAYER))
			return 3;
	
		// Check for tie
		for (int i = 0; i < BOARD_SIZE; i++) {
			// If we find a number, then no one has won yet
			if (board[i] != HUMAN_PLAYER && board[i] != COMPUTER_PLAYER)
				return 0;
		}
	
		// If we make it through the previous loop, all places are taken, so it's a tie
		return 1;
	}
	
	
	
	/** Return the best move for the computer to make. You must call setMove() 
	 * to actually make the computer move to that location. 
	 * @return The best move for the computer to make (0-8). 
	 */ 
	public int getComputerMove() 
	{
		int move = -1;
	
		if (mDifficultyLevel == DifficultyLevel.Easy)
			move = getRandomMove();
		else if (mDifficultyLevel == DifficultyLevel.Harder) {
			move = getWinningMove();
			if (move == -1)
				move = getRandomMove();
		}
		else if (mDifficultyLevel == DifficultyLevel.Expert) {
			move = getWinningMove();
			if (move == -1)
				move = getBlockingMove();
			if (move == -1)
				move = getRandomMove();
		}

		return move;
	}	
	
	/** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
	public void clearBoard() {
		for (int i = 0; i < mBoard.length; i++) {
			mBoard[i] = OPEN_SPOT;
		}
	}
	
	/** Set the given player at the given location on the game board. 
	 * The location must be available, or the board will not be changed. 
	 * 
	 * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER 
	 * @param location - The location (0-8) to place the move 
	 */ 
	public boolean setMove(char player, int location) {
		if (mBoard[location] == OPEN_SPOT) {
			mBoard[location] = player;
			return true;
		}
		return false;
	}
	
	public char getBoardOccupant(int pos) {
		return mBoard[pos];
	}
	
	public char[] getBoardState() {
		return this.mBoard;
	}
	
	public void setBoardState(char[] board) {
		this.mBoard = board;
	}
	
	public int checkForWinner() {

		// Check horizontal wins
		for (int i = 0; i <= 6; i += 3)	{
			if (mBoard[i] == HUMAN_PLAYER && 
					mBoard[i+1] == HUMAN_PLAYER &&
					mBoard[i+2]== HUMAN_PLAYER)
				return 2;
			if (mBoard[i] == COMPUTER_PLAYER && 
					mBoard[i+1]== COMPUTER_PLAYER && 
					mBoard[i+2] == COMPUTER_PLAYER)
				return 3;
		}

		// Check vertical wins
		for (int i = 0; i <= 2; i++) {
			if (mBoard[i] == HUMAN_PLAYER && 
					mBoard[i+3] == HUMAN_PLAYER && 
					mBoard[i+6]== HUMAN_PLAYER)
				return 2;
			if (mBoard[i] == COMPUTER_PLAYER && 
					mBoard[i+3] == COMPUTER_PLAYER && 
					mBoard[i+6]== COMPUTER_PLAYER)
				return 3;
		}

		// Check for diagonal wins
		if ((mBoard[0] == HUMAN_PLAYER &&
				mBoard[4] == HUMAN_PLAYER && 
				mBoard[8] == HUMAN_PLAYER) ||
				(mBoard[2] == HUMAN_PLAYER && 
				mBoard[4] == HUMAN_PLAYER &&
				mBoard[6] == HUMAN_PLAYER))
			return 2;
		if ((mBoard[0] == COMPUTER_PLAYER &&
				mBoard[4] == COMPUTER_PLAYER && 
				mBoard[8] == COMPUTER_PLAYER) ||
				(mBoard[2] == COMPUTER_PLAYER && 
				mBoard[4] == COMPUTER_PLAYER &&
				mBoard[6] == COMPUTER_PLAYER))
			return 3;

		// Check for tie
		for (int i = 0; i < BOARD_SIZE; i++) {
			// If we find a number, then no one has won yet
			if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
				return 0;
		}

		// If we make it through the previous loop, all places are taken, so it's a tie
		return 1;
	}
	
	
}
