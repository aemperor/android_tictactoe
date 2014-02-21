package aemperor.tictactoe;

import aemperor.tictactoe.TicTacToeGame.DifficultyLevel;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToe extends Activity {

	private TicTacToeGame mGame;
	private BoardView mBoardView;
	private TextView mInfoTextView;
	private boolean mGameOver;
	private TextView mHumanWins;
	private TextView mAndroidWins;
	private TextView mTies;
	private int hWins = 0;
	private int aWins = 0;
	private int ties = 0;
	static final int DIALOG_DIFFICULTY_ID = 0;
	static final int DIALOG_QUIT_ID = 1;
	private OnTouchListener mTouchListener = new OnTouchListener();
	private SoundPool mSounds;
	private int mHumanMoveSoundID;
	private int mComputerMoveSoundID;
	private int mTieSoundID;
	private int mHumanWinSoundID;
	private int mComputerWinSoundID;
	// not yet implemented
	private char mTurn;
	private char mGoesFirst;
	private Handler mPauseHandler;
	private Runnable mRunnable;
	private SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mGame = new TicTacToeGame();
		mBoardView = (BoardView) findViewById(R.id.board);
		mBoardView.setGame(mGame);
		
		mInfoTextView = (TextView) findViewById(R.id.information);
		
		mHumanWins = (TextView) findViewById(R.id.humanWins);
		mAndroidWins = (TextView) findViewById(R.id.androidWins);
		mTies = (TextView) findViewById(R.id.ties);
		
		mBoardView.setOnTouchListener(mTouchListener);
		
		mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE); 
		 
		// Restore the scores 
		hWins = mPrefs.getInt("mHumanWins", 0); 
		aWins = mPrefs.getInt("mAndroidWins", 0); 
		ties = mPrefs.getInt("mTies", 0); 
		
//		if (mPrefs.contains("mDifficultyLevel")) {
//			int diffLevel = mPrefs.getInt("mDifficultyLevel", 0);
//			if (diffLevel == 0) 
//				mGame.setDifficultyLevel(DifficultyLevel.Easy);
//			else if (diffLevel == 1)
//				mGame.setDifficultyLevel(DifficultyLevel.Harder);
//			else if (diffLevel == 2)
//				mGame.setDifficultyLevel(DifficultyLevel.Expert);
//		}
		
		mTurn = TicTacToeGame.HUMAN_PLAYER;
//		mGoesFirst = TicTacToeGame.COMPUTER_PLAYER; 
//		mPauseHandler = new Handler();
		if (savedInstanceState == null) { 
			 mTurn = TicTacToeGame.HUMAN_PLAYER; 
//			 mGoesFirst = TicTacToeGame.COMPUTER_PLAYER; 
			 startNewGame(); 
		} 
		else { 
			 mGame.setBoardState(savedInstanceState.getCharArray("board")); 
			 mBoardView.invalidate();
			 mGameOver = savedInstanceState.getBoolean("mGameOver"); 
			 mTurn = savedInstanceState.getChar("mTurn"); 
//			 mGoesFirst = savedInstanceState.getChar("mGoesFirst"); 
			 mInfoTextView.setText(savedInstanceState.getCharSequence("info")); 
			 hWins = savedInstanceState.getInt("mHumanWins"); 
			 aWins = savedInstanceState.getInt("mAndroidWins"); 
			 ties = savedInstanceState.getInt("mTies"); 
			 
			 displayScores(); 
			 startComputerDelay(); 
		}
	}
	
	private void displayScores() { 
		 mHumanWins.setText("Human: " + Integer.toString(hWins)); 
		 mAndroidWins.setText("Android: " + Integer.toString(aWins)); 
		 mTies.setText("Ties: " + Integer.toString(ties)); 
	} 
	
	private void startComputerDelay() { 
		 if (!mGameOver && mTurn == TicTacToeGame.COMPUTER_PLAYER) { 
			 int move = mGame.getComputerMove(); 
			 setMove(TicTacToeGame.COMPUTER_PLAYER, move); 
		 } 
	} 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case (R.id.new_game):
			startNewGame();
			return true;
		case (R.id.ai_difficulty):
			showDialog(DIALOG_DIFFICULTY_ID);
			return true;
		case (R.id.reset_scores):
			resetScores();
			displayScores();
			return true;
		case (R.id.quit):
			showDialog(DIALOG_QUIT_ID);
			return true;
		default:
			break;
		}
		
		return false;
	}
	
	public void resetScores() {
		hWins = 0;
		aWins = 0;
		ties = 0;
	}
	
	@Override 
	protected void onStop() { 
		 super.onStop(); 
		 // Save the current scores 
		 SharedPreferences.Editor ed = mPrefs.edit(); 
		 String s = mHumanWins.getText().toString();
		 String[] splitt = s.split(" ");
		 int i = Integer.valueOf(splitt[1]);
		 ed.putInt("mHumanWins", i); 
		 s = mAndroidWins.getText().toString();
		 splitt = s.split(" ");
		 i = Integer.valueOf(splitt[1]);
		 ed.putInt("mAndroidWins", i);
		 s = mTies.getText().toString();
		 splitt = s.split(" ");
		 i = Integer.valueOf(splitt[1]);
		 ed.putInt("mTies", i); 
		 
//		 String difficulty = mGame.getDifficultyLevel().toString();
//		 Log.d("difficulty", difficulty);
//		 if (difficulty == getResources().getString(R.string.difficulty_easy)) 
//			 ed.putInt("mDifficultyLevel", 0);
//			else if (difficulty == getResources().getString(R.string.difficulty_harder))
//				ed.putInt("mDifficultyLevel", 1);
//			else if (difficulty == getResources().getString(R.string.difficulty_expert))
//				ed.putInt("mDifficultyLevel", 2);
		 
		 
		 ed.apply(); 
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(id) {
		case DIALOG_DIFFICULTY_ID:
			builder.setTitle(R.string.difficulty_choose);
			
			final CharSequence[] levels = {
					getResources().getString(R.string.difficulty_easy),
					getResources().getString(R.string.difficulty_harder),
					getResources().getString(R.string.difficulty_expert)
			};
			
			int selected = 0; 
			
			builder.setSingleChoiceItems(levels, selected, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss(); 
					
					if (levels[item] == getResources().getString(R.string.difficulty_easy)) 
						mGame.setDifficultyLevel(DifficultyLevel.Easy);
					else if (levels[item] == getResources().getString(R.string.difficulty_harder))
						mGame.setDifficultyLevel(DifficultyLevel.Harder);
					else if (levels[item] == getResources().getString(R.string.difficulty_expert))
						mGame.setDifficultyLevel(DifficultyLevel.Expert);
					
					
					Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();
				}
			}); 
			dialog = builder.create();
			break;
		case DIALOG_QUIT_ID:
			builder.setMessage(R.string.quit_message)
			.setCancelable(false)
			.setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AndroidTicTacToe.this.finish();
				}
			})
			.setNegativeButton(R.string.quit_no, null);
			
			
			dialog = builder.create();
			break;
		}
		return dialog;
		
	}
	
	@Override 
	protected void onSaveInstanceState(Bundle outState) { 
		 super.onSaveInstanceState(outState); 
		 
		 outState.putCharArray("board", mGame.getBoardState()); 
		 outState.putBoolean("mGameOver", mGameOver); 
		 String s = mHumanWins.getText().toString();
		 String[] splitt = s.split(" ");
		 int i = Integer.valueOf(splitt[1]);
		 outState.putInt("mHumanWins", i); 
		 s = mAndroidWins.getText().toString();
		 splitt = s.split(" ");
		 i = Integer.valueOf(splitt[1]);
		 outState.putInt("mAndroidWins", i);
		 s = mTies.getText().toString();
		 splitt = s.split(" ");
		 i = Integer.valueOf(splitt[1]);
		 outState.putInt("mTies", i); 
		 outState.putCharSequence("info", mInfoTextView.getText()); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		
		mHumanMoveSoundID = mSounds.load(this, R.raw.x_sound_scream, 1);
		mComputerMoveSoundID = mSounds.load(this, R.raw.o_sound_scream, 1);
		
		mHumanWinSoundID = mSounds.load(this, R.raw.human_win, 1);
		mComputerWinSoundID = mSounds.load(this, R.raw.android_win, 1);
		mTieSoundID = mSounds.load(this, R.raw.tie, 1);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Log.d("onPause", "In onPause()");
		if (mSounds != null) {
			mSounds.release();
			mSounds = null;
		}
		
	}
	
	
	
	private void startNewGame() {
		mGame.clearBoard();
		mBoardView.invalidate();
		
		mInfoTextView.setText(R.string.first_human);
		
		mHumanWins.setText("Human: " + hWins);
		mAndroidWins.setText("Android: " + aWins);
		mTies.setText("Ties: " + ties);
		
		mGameOver = false;
	}
	
	private boolean setMove(char player, int loc) {
		if (mGame.setMove(player, loc)) { 
			 mBoardView.invalidate(); // Redraw the board 
			 if (player == mGame.HUMAN_PLAYER)
				 mSounds.play(mHumanMoveSoundID, 1, 1, 1, 0, 1);
			 else if (player == mGame.COMPUTER_PLAYER)
				 mSounds.play(mComputerMoveSoundID, 1, 1, 1, 0, 1);
			 
			 return true; 
		} 
		return false;
	} 
	
	
	
	private class OnTouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// Determine which cell was touched 
			 int col = (int) event.getX() / mBoardView.getBoardCellWidth(); 
			 int row = (int) event.getY() / mBoardView.getBoardCellHeight(); 
			 int pos = row * 3 + col;
			 
			 if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) { 
				// if no winner yet, let computer go 
				int winner = mGame.checkForWinner(mGame.getBoard());
				
				if (winner == 0) {
					mInfoTextView.setText(R.string.turn_computer);
					int move = mGame.getComputerMove();
					setMove(TicTacToeGame.COMPUTER_PLAYER, move);
				}
				
				winner = mGame.checkForWinner(mGame.getBoard());
				mInfoTextView.setText(R.string.turn_human);
				
				if (winner == 0)
					mInfoTextView.setText(R.string.turn_human);
				else if (winner == 1) { 
					mInfoTextView.setText(R.string.result_tie);
					ties++;
					mTies.setText("Ties: " + ties);
					mSounds.play(mTieSoundID, 1, 1, 1, 0, 1);
					mGameOver = true;
				}
				else if (winner == 2) {
					mInfoTextView.setText(R.string.result_human_wins);
					hWins++;
					mHumanWins.setText("Human: " + hWins);
					mSounds.play(mHumanWinSoundID, 1, 1, 1, 0, 1);
					mGameOver = true;
				}
				else {
					mInfoTextView.setText(R.string.result_computer_wins);
					aWins++;
					mAndroidWins.setText("Android: " + aWins);
					mSounds.play(mComputerWinSoundID, 1, 1, 1, 0, 1);
					mGameOver = true;
				}
			 } 
			return false; 
		}

	}
}
