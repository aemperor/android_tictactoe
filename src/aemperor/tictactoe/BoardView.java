package aemperor.tictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BoardView extends View {
	
	public static final int GRID_LINE_WIDTH = 6;
	private Bitmap mHumanBitmap;
	private Bitmap mComputerBitmap;
	private Paint mPaint;
	private TicTacToeGame mGame; 
	

	
	public BoardView(Context context) { 
		 super(context); 
		 initialize(); 
	} 
	 
	public BoardView(Context context, AttributeSet attrs, int defStyle) { 
	 super(context, attrs, defStyle); 
	 initialize(); 
	} 
	 
	public BoardView(Context context, AttributeSet attrs) { 
	 super(context, attrs); 
	 initialize(); 
	}
	 
	public void setGame(TicTacToeGame game) { 
	 mGame = game; 
	} 


	public void initialize() {
		mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
		mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int boardWidth = getWidth();
		int boardHeight = getHeight();
		int topLineOffset = boardHeight  - boardHeight/3;
		int bottomLineOffset = boardHeight - (2*(boardHeight/3));
		int leftLineOffset = boardWidth - boardWidth/3;
		int rightLineOffset = boardWidth - (2*(boardWidth/3));
		
		mPaint.setColor(Color.CYAN);
		mPaint.setStrokeWidth(GRID_LINE_WIDTH);
		
		canvas.drawLine(0, boardHeight-topLineOffset, boardWidth, boardHeight-topLineOffset, mPaint);
		canvas.drawLine(0, boardHeight-bottomLineOffset, boardWidth, boardHeight-bottomLineOffset, mPaint);
		canvas.drawLine(boardWidth-leftLineOffset, 0, boardWidth-leftLineOffset, boardHeight, mPaint);
		canvas.drawLine(boardWidth-rightLineOffset, 0, boardWidth-rightLineOffset, boardHeight, mPaint);
		
		for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
			int col = i % 3;
			int row = i / 3;
			
			int[] dimensions = getCellDimensions(row, col);
			
			if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
				canvas.drawBitmap(mHumanBitmap, null, new Rect(dimensions[0], 
						dimensions[1], dimensions[2], dimensions[3]), null);
			}
			else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
				canvas.drawBitmap(mComputerBitmap, null, new Rect(dimensions[0], 
						dimensions[1], dimensions[2], dimensions[3]), null);
			}

			
		}
		
	}
	
	
	public int getBoardCellWidth() {
		return getWidth() / 3;
	}
	
	public int getBoardCellHeight() {
		return getHeight() / 3;
	}

	
	public int[] getCellDimensions(int row, int col) {
		int xTopLeft = 0; 
		int yTopLeft = 0;
		int xBottomRight = 0;
		int yBottomRight = 0;
		int width = getWidth();
		int cellWidth = getBoardCellWidth();
		int height = getHeight();
		int cellHeight = getBoardCellHeight();
		
		if (col == 0) {
			if (row == 0) {
				xTopLeft = 0;
				yTopLeft = 0;
				xBottomRight = cellWidth;
				yBottomRight = cellHeight;
			}
			else if (row == 1) {
				xTopLeft = 0;
				yTopLeft = cellHeight;
				xBottomRight = cellWidth; 
				yBottomRight = cellHeight*2;
			}
			else if (row == 2) {
				xTopLeft = 0;
				yTopLeft = cellHeight*2;
				xBottomRight = cellWidth;
				yBottomRight = cellHeight*3;
			}
		}
		else if (col == 1) {
			if (row == 0) {
				xTopLeft = cellWidth;
				yTopLeft = 0;
				xBottomRight = cellWidth*2;
				yBottomRight = cellHeight;
			}
			else if (row == 1) {
				xTopLeft = cellWidth;
				yTopLeft = cellHeight; 
				xBottomRight = cellWidth*2;
				yBottomRight = cellHeight*2;
			}
			else if (row == 2) {
				xTopLeft = cellWidth;
				yTopLeft = cellHeight*2;
				xBottomRight = cellWidth*2;
				yBottomRight = cellHeight*3;
			}
		}
		else if (col == 2) {
			if (row == 0) {
				xTopLeft = width - (cellWidth);
				yTopLeft = 0;
				xBottomRight = width;
				yBottomRight = cellHeight;
			}
			else if (row == 1) {
				xTopLeft = width - (cellWidth);
				yTopLeft = cellHeight;
				xBottomRight = width;
				yBottomRight = cellHeight*2;
			}
			else if (row == 2) {
				xTopLeft = width - (cellWidth);
				yTopLeft = cellHeight*2;
				xBottomRight = width;
				yBottomRight = cellHeight*3;
			}
		}
		int[] result = {xTopLeft, yTopLeft, xBottomRight, yBottomRight};
		
		return result;
	}

}
