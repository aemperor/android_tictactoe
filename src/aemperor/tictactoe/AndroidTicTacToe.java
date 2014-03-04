package aemperor.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

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
	private String mGoesFirst;
	private Handler mPauseHandler;
	private Runnable mRunnable;
	private SharedPreferences mPrefs;
	private boolean mSoundOn = false;
	private boolean soundsLoaded = false;
	
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
		
		mSoundOn = mPrefs.getBoolean("sound", true);
		mPauseHandler = new Handler();
		
		
		mTurn = TicTacToeGame.HUMAN_PLAYER;
		if (savedInstanceState == null) { 
			 mTurn = TicTacToeGame.HUMAN_PLAYER; 
			 startNewGame(); 
		} 
		else { 
			 mGame.setBoardState(savedInstanceState.getCharArray("board")); 
			 mBoardView.invalidate();
			 mGameOver = savedInstanceState.getBoolean("mGameOver"); 
			 mTurn = savedInstanceState.getChar("mTurn"); 
			 mGoesFirst = savedInstanceState.getString("mGoesFirst"); 
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
		case R.id.settings: 
			 startActivityForResult(new Intent(this, Settings.class), 0); 
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
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	 
		 if (requestCode == RESULT_CANCELED) { 
			 // Apply potentially new settings 
			 mSoundOn = mPrefs.getBoolean("sound", true); 
			 String[] levels = getResources().getStringArray(R.array.list_difficulty_level); 
			 
			 // set difficulty, or use hardest if not present, 
			 String difficultyLevel = mPrefs.getString("difficulty_level", levels[levels.length - 1]); 
			 int i = 0; 
			 while(i < levels.length) { 
				 if(difficultyLevel.equals(levels[i])) { 
					 mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[i]); 
				 	i = levels.length; // to stop loop 
				 } 
				 i++; 
			 } 
			 
			 String[] goesFirst = getResources().getStringArray(R.array.list_goes_first);
			 String first = mPrefs.getString("goes_first", goesFirst[0]); // set to X by default
			 i = 0;
			 while (i < goesFirst.length){
				 if (first.equals(goesFirst[i])) {
					 mGoesFirst = goesFirst[i];
					 i = goesFirst.length;
				 }
				 i++;
			 }
		 } 
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
		 ed.putString("mGoesFirst", mGoesFirst);
		 ed.apply(); 
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(id) {
		
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
		 
		 stopComputerDelay();
	}
	
	private void stopComputerDelay() {
		if(mRunnable != null) {
			mPauseHandler.removeCallbacks(mRunnable);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		mSounds.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	        @Override
	        public void onLoadComplete(SoundPool soundPool, int sampleId,
	                  int status) {
	           soundsLoaded = true;
	       }
	 });
		
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
		 if (player == mGame.COMPUTER_PLAYER) {
			 mRunnable = createRunnable(loc);
			 mPauseHandler.postDelayed(mRunnable, 1000);
			 return true;
		 }
		 else if (mGame.setMove(TicTacToeGame.HUMAN_PLAYER, loc)) {
			 mTurn = TicTacToeGame.COMPUTER_PLAYER;
			 mBoardView.invalidate();   // Redraw the board
			 if (mSoundOn)
				 mSounds.play(mHumanMoveSoundID, 1, 1, 1, 0, 1);	    	   	
			 return true;
		 }
		return false;
	} 
	
	private Runnable createRunnable(final int location) {
		return new Runnable() {
			public void run() {
				mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, location);
				if (mSoundOn)
					mSounds.play(mComputerMoveSoundID, 1, 1, 1, 0, 1);
				mBoardView.invalidate();   // Redraw the board

				int winner = mGame.checkForWinner();
				if (winner == 0) {
					mTurn = TicTacToeGame.HUMAN_PLAYER;	                                	
					mInfoTextView.setText(R.string.turn_human);
				}
				else 
					endGame(winner);
			}
		};
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
				else 
					endGame(winner);
			 } 
			return false; 
		}

	}
	
	public void endGame(int winner) {
		if (winner == 1) { 
			mInfoTextView.setText(R.string.result_tie);
			ties++;
			mTies.setText("Ties: " + ties);
			if (mSoundOn)
				mSounds.play(mTieSoundID, 1, 1, 1, 0, 1);
			mGameOver = true;
		}
		else if (winner == 2) {
			mInfoTextView.setText(R.string.result_human_wins);
			hWins++;
			mHumanWins.setText("Human: " + hWins);
			String defaultMessage = getResources().getString(R.string.result_human_wins); 
			mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
			if (mSoundOn)
				mSounds.play(mHumanWinSoundID, 1, 1, 1, 0, 1);
			mGameOver = true;
		}
		else {
			mInfoTextView.setText(R.string.result_computer_wins);
			aWins++;
			mAndroidWins.setText("Android: " + aWins);
			if (mSoundOn)
				mSounds.play(mComputerWinSoundID, 1, 1, 1, 0, 1);
			mGameOver = true;
		}
	}
}
