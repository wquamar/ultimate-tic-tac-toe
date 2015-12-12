package com.wquamar.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
	
	int r = 9;
	int c = 9;
	private String[][] gameBoard = new String[r][c];
	private String[][]boardsWon = new String[3][3];
	private String X = "X";
	private String O = "O";
	
	private void resetGame()
	{
		for (int i=0; i < 9; i++)
		{
			String [] x = gameBoard[i];
			for (String y : x)
			{
				y = null;
			}
		}
	}
	
	private void printGameBoard(int n)
	{
		for (int i=0; i < n; i++)
		{
			for (int j=0; j<n; j++)
			{
				System.out.print(gameBoard[i][j]+ "\t");
			}
			System.out.println();
		}
	}
	
	private void printGameBoard(String[][] board, int n)
	{
		for (int i=0; i < n; i++)
		{
			for (int j=0; j<n; j++)
			{
				System.out.print( board[i][j]+ "\t");
			}
			System.out.println();
		}
	}

	private void prettyPrintGameBoard(String[][] miniBoard, int k)
	{
		System.out.println("-------------------------------------------------------------------------------------------");
		for (int i=0; i < 9; i++)
		{
			for (int j=0; j<9; j++)
			{
				if (j % 3 ==2)
					System.out.print(gameBoard[i][j]+ "\t|\t");
				else
					System.out.print(gameBoard[i][j]+ "\t");
			}
			System.out.println();
			if (i%3 == 2)
				System.out.println("-------------------------------------------------------------------------------------------");
		}
		System.out.println("-------------------------------------------------------------------------------------------");
	}

	private void insertIntoBoard(int r, int c, String entry)
	{
		if (gameBoard[r][c] == null)
		{
			//Get MiniBoard from the main GameBoard
			String[][] miniBoard = getMiniBoard(r, c);
			
			//Get Miniboard Position in the main Board
			int[] position = getMiniBoardPosition(r, c);
			System.out.println("Miniboard Position : "+position[0] + " * "+position[1]);
			
			//Insert into Miniboard
			List alignPositions = insertIntoMiniBoard(miniBoard, position[0], position[1], entry, true);
			printGameBoard(miniBoard, 3);

			//Insert into Game Board
			gameBoard[r][c] = entry;
			
			//There has been an alignment
			if (alignPositions != null)
			{
				//Convert the miniboard positions to the main game board
				List<String> gameBoardAlignPositions = convertMiniBoardToGameBoard(r, c, alignPositions);
				System.out.println("To strike gameBoardAlignPositions : "+gameBoardAlignPositions.toString());
				
				//Make the entry into the 3*3 boardswon and check if there is any alignment.
				//If there is an alignment , it means the the board has won.
				List boardsWonAlignPositions = insertIntoMiniBoard(boardsWon, r/3, c/3, entry, true);
				if (boardsWonAlignPositions != null)
				{
					printGameBoard(boardsWon, 3);
					System.out.println("To strike boardsWonAlignPositions : "+boardsWonAlignPositions.toString());
					System.out.println("Boards won by: "+entry);
				}
			}else
			{
				//TOTEST : Check if there are any empty cells?
				boolean emptyCells = false;
				for (int i=0;i<3;i++)
				{
					for (int j=0;j<3;j++)
					{
						if (miniBoard[i][j] == null)
						{
							emptyCells = true;
							break;
						}
					}
				}
				if (!emptyCells) 
				{
					// then Tie
					if (boardsWon[r/3][c/3] == null)
						boardsWon[r/3][c/3] = "T";
				}
			}
			
			//Get NextMiniBoardPsotions to highlight
			int[] nextPosition = getNextMiniBoardPosition(r,c);
			System.out.println("Next Positons: "+Arrays.toString(nextPosition));
		}
		else
			System.out.println("The cell is not empty");
	}
	
	//Let the machine play now by passing previous row, column and entry 
	private void machinePlay(int prevR, int prevC, String prevEntry)
	{
		//Get the current entry which Machine will be playing with
		String entry = null;
		if (prevEntry.equalsIgnoreCase(X))
		{
			entry = O;
		}else if (prevEntry.equalsIgnoreCase(O))
		{
			entry = X;
		}
		
		//Verify if the last entry is present in row and column given
		if (gameBoard[prevR][prevC]!= null && gameBoard[prevR][prevC].equalsIgnoreCase(prevEntry))
		{
			//Get NextMiniBoardPositions to play
			int[] nextPosition = getNextMiniBoardPosition(prevR,prevC);
			System.out.println("Next Positons||: "+Arrays.toString(nextPosition));
			
			int boardWonPositionR = nextPosition[0] /3;
			int boardWonPositionC = nextPosition[1] /3;
			if (boardsWon[boardWonPositionR][boardWonPositionC] == null)
			{
				machinePlayBoardsWon(nextPosition[0], nextPosition[1], entry);
			}else
			{
				//ANALYZE which next cell in boardsWon should be picked up
				//if difficult
				//machinePlay(boardsWon, entry, 3);
				
				int[] boardsWonCoord = machinePlay(boardsWon, entry, 3);
				if (boardsWonCoord[0] != -1 && boardsWonCoord[1] != -1)
				{
					int nextR = boardsWonCoord[0] * 3;
					int nextC = boardsWonCoord[1] * 3;
					machinePlayBoardsWon(nextR, nextC, entry);
				}else
				{
					//There is no more empty cells remaining in boardsWon. It has either X or O or T 
					System.out.println("There are no more Empty Cells remaining in the board."); 
				}
				
			}
			
		}
		
	}
	
	private void machinePlayBoardsWon(int r, int c, String entry)
	{
		String[][] nextMiniBoard = getMiniBoard(r, c);
		System.out.println("machinePlayBoardsWon| Next MiniBoard");
		printGameBoard(nextMiniBoard, 3);
		
		int[] miniBoardCoord = machinePlay(nextMiniBoard, entry, 3);
		
		
		if (miniBoardCoord[0] != -1 && miniBoardCoord[1] != -1)
		{
			//Get co-ordinates in 9*9 board
			int row = r + miniBoardCoord[0];
			int column = c + miniBoardCoord[1];
			
			insertIntoBoard(row, column, entry);
		}else
		{
			//Possible that there was no empty cells in that miniboard, hence we need to get the next board.
			//Send boardsWon to the machinePlay
			int[] boardsWonCoord = machinePlay(boardsWon, entry, 3);
			if (boardsWonCoord[0] != -1 && boardsWonCoord[1] != -1)
			{
				int nextR = boardsWonCoord[0] * 3;
				int nextC = boardsWonCoord[1] * 3;
				machinePlayBoardsWon(nextR, nextC, entry);
			}else
			{
				//There is no more empty cells remaining in boardsWon. It has either X or O or T 
				System.out.println("There are no more Empty Cells remaining in the board.");
			}
		}

	}
	
	private int[] machinePlay(String[][] miniBoard, String entry, int n)
	{
		System.out.println("Entry : "+entry);
		int row = -1;
		int column = -1;
		
		//Check if there is any empty cell
		boolean isCellEmpty = false; 
		List emptyCellsR = new ArrayList();
		List emptyCellsC = new ArrayList();
		for (int i = 0; i<n; i++)
		{
			for (int j=0; j<n; j++)
			{
				if (miniBoard[i][j] == null || miniBoard[i][j].equals(""))
				{
					isCellEmpty = true;
					
					emptyCellsR.add(i);
					emptyCellsC.add(j);
				}
			}
		}
		
		//If their is empty Cell
		if (isCellEmpty)
		{
			
			//Insert into location and check if there is any alignment - Machine playing
			int emptyCellsSize = emptyCellsR.size();
			boolean machineAligned = false;
			List machineAligedPositions = null;
			for (int i=0;i<emptyCellsSize; i++)
			{
				int r = (Integer)emptyCellsR.get(i);
				int c = (Integer)emptyCellsC.get(i);
				
				System.out.println(r+" - "+ c);
				List alignedPositions = insertIntoMiniBoard(miniBoard, r, c, entry, false);
				
				//If no Alignment then remove the entry from board
				if (alignedPositions != null)
				{
					machineAligned = true;
					machineAligedPositions = alignedPositions;
					row = r;
					column = c;
					
					break;
				}
			}
			
			if (machineAligned == true)
			{
				//miniboard won by machine
				//and positions are machineAligedPositions
				//If this is smaller miniboard, then make an entry in boardsWon and check for alignments to check if boardsWon is won or not
				System.out.println("Machine Aligned "+ machineAligedPositions.toString() + " r-c : "+row+" - "+column);
				
			}else
			{
				//this.printGameBoard(miniBoard, 3);
				
				//Check for other player and see if there are any cells where he can make an entry and win the board.
				String nextEntry = null;
				if (entry.equalsIgnoreCase(X))
				{
					nextEntry = O;
				}else if (entry.equalsIgnoreCase(O))
				{
					nextEntry = X;
				}
				
				boolean otherAligned = false;
				List otherAlignedPositions = null;
				for (int i=0;i<emptyCellsSize; i++)
				{
					int r = (Integer)emptyCellsR.get(i);
					int c = (Integer)emptyCellsC.get(i);
					
					List alignedPositions = insertIntoMiniBoard(miniBoard, r, c, nextEntry, false);
					
					
					//If no Alignment then remove the entry from board
					if (alignedPositions != null)
					{
						//miniBoard[r][c] = entry;
						
						otherAligned = true;
						otherAlignedPositions = alignedPositions;
						
						row = r;
						column = c;
						break;
					}
				}
				
				if (otherAligned)
				{
					//Make an exit as entry is made
					System.out.println("Other player aligned: "+otherAlignedPositions.toString() + " r-c : "+row+" - "+column);
					
				}else
				{
					//Put a random generator to get a valid number in empty cells and insert it there.
					//Generate a random number between 0 and (emptyCellsSize-1)
					int  rand = new Random().nextInt(emptyCellsSize);
					int r = (Integer)emptyCellsR.get(rand);
					int c = (Integer)emptyCellsC.get(rand);
					
					row = r;
					column = c;
					//miniBoard[r][c] = entry;
					
					//Now check for boardsWon if there is any cross there
					System.out.println("Random Row - "+row + " Column - "+column);
				}
			}
			
//			if (row != -1 && column != -1)
//			{
//				System.out.println("Inserted at Row : "+row + " column: "+column);
//				miniBoard[row][column] = entry;
//				
//			}
			
				
				
		}else // try to get another miniboard to play with
		{
			//Put a tie entry in boardsWon
			System.out.println("There is no empty cells in the miniBoard");
			
		}
		
		int[] miniBoardCoord = new int[2];
		miniBoardCoord[0] = row;
		miniBoardCoord[1] = column;
		
		return miniBoardCoord;
	}
	
	//Get the miniBoard based on co-ordinates from the bigger gameBoard
	private String[][] getMiniBoard(int r, int c)
	{
		String[][] miniBoard = new String[3][3];
		
		int startR = (r/3) *3;
		int startC = (c/3) *3;
		

		
		for (int x=startR,i=0; x< (startR+3) && i<3; x++, i++)
		{
			//System.out.println("["+x+"]["+i+"]");
			for (int y=startC,j=0; y< (startC+3) && j<3; y++, j++)
			{
				//System.out.println("["+i+"]["+j+"]"+" - ["+x+"]["+y+"] -- "+gameBoard[x][y]);
				miniBoard[i][j] = gameBoard[x][y];
			}
		}

		//System.out.println(startR+ " - "+startC);
		return miniBoard;
	}
	
	//convert Miniboard co-ordinates based on r, c and miniboard co-ordinates
	private List<String> convertMiniBoardToGameBoard(int r, int c, List<String> alignPositions)
	{
		if (alignPositions != null)
		{
			List<String> newPositions = new ArrayList<String>();
			int startR = (r/3) *3;
			int startC = (c/3) *3;
			
			for (String xy: alignPositions)
			{
				String[] xyArray = xy.split("-");
				int x = Integer.parseInt(xyArray[0]);
				int y = Integer.parseInt(xyArray[1]);
				newPositions.add((startR+x)+ "-" + (startC+y));
			}
			return newPositions;
		}
		else 
		{
			return null;
		}
	}
	
	//Get the miniBoard based on co-ordinates from the bigger gameBoard
	private int[] getMiniBoardPosition(int r, int c)
	{
		int x = r % 3;
		int y = c % 3;
		int[] position = new int[2];
		position[0] = x;
		position[1] = y;
		
		return position;
	}
	
	//TODO Get the next miniBoard to highlight based on co-ordinates from the bigger gameBoard
	private String[][] getNextMiniBoard(int r, int c)
	{
		return new String[3][3];
	}
	
	//TODO Get the next miniBoard positions to highlight based on co-ordinates from the bigger gameBoard
	private int[] getNextMiniBoardPosition(int r, int c)
	{
		int[] position = getMiniBoardPosition(r, c);
		int[] nextPositions = new int[2];
		nextPositions[0] = position[0] * 3;
		nextPositions[1] = position[1] * 3;
		
		return nextPositions;
	}

	//TODO If you want to provide board number and then row and column
	private void insertIntoBoard(int boardNumber, int r, int c, String entry)
	{
	}
	
	//Insert into 3*3 mini board
	private List insertIntoMiniBoard(String[][] board, int r, int c, String entry, boolean insert)
	{
		//This is for a 3*3 board
		int length = 3;
		
		if (r < 0 && r > (length-1))
		{
			System.out.println("Invalid Row entered");
		}
		if (c < 0 && c > (length-1))
		{
			System.out.println("Invalid Column entered");
		}
		
		//enter at the board
		if (board[r][c] == null)
		{
			board[r][c] = entry;
			
			boolean rightDown = false, rightUp = false;
			List rightDownList = new ArrayList();
			List rightUpList = new ArrayList();
			//If entered at the corners or r==c case
			if ((r == 0 && c == 0) || (r==0 && c == (length -1)) || (r==(length -1 ) && c==0) || (r==(length -1 ) && c==(length -1)) || (r==c))
			{

				//right down diagonal
				if ((r==0 && c==0) || (r==(length -1 ) && c==(length -1)) || (r==c))
				{
					rightDown = true;
					for (int x=0; x<(length); x++)
					{
						rightDownList.add(new String(x+"-"+x));
						if (board[x][x] == null || !board[x][x].equalsIgnoreCase(entry))
						{
							rightDown = false;
							break;
						}
					}
				}
				
				//right up diagonal
				if ((r==(length -1 ) && c==0) || (r==0 && c == (length -1)) || (r==c))
				{	
					rightUp = true;
					for (int x=(length - 1), y =0; x >= 0 && y<(length); x--, y++)
					{
						rightUpList.add(new String(x+"-"+y));
						if (board[x][y] == null || !board[x][y].equalsIgnoreCase(entry))
						{
							rightUp = false;
							break;
						}
					}
				}
				
			}//end of corners and x==y cases
			
			//For all other cases
			//vertical
			boolean vertical = true;
			List verticalList = new ArrayList();
			for (int x = 0; x<length; x++)
			{
				verticalList.add(new String(x+"-"+c));
				if (board[x][c] == null || !board[x][c].equalsIgnoreCase(entry))
				{
					vertical = false;
					break;
				}
			}
			
			//horizontal
			boolean horizontal = true;
			List horizontalList = new ArrayList();
			for (int y = 0; y<length; y++)
			{
				horizontalList.add(new String(r+"-"+y));
				if (board[r][y] == null || !board[r][y].equalsIgnoreCase(entry))
				{
					horizontal = false;
					break;
				}
			}

			if (!insert)
			{
				board[r][c] = null;
			}
			
			if (rightDown)
			{
				System.out.println("RightDown - "+rightDownList.toString());
				return rightDownList;
			}
			
			if (rightUp)
			{
				System.out.println("RightUp - "+rightUpList.toString());
				return rightUpList;
			}
			
			if (vertical)
			{
				System.out.println("Vertical - "+verticalList.toString());
				return verticalList;
			}
			
			if (horizontal)
			{
				System.out.println("Horizontal - "+horizontalList.toString());
				return horizontalList;
			}
		}
		
		return null;
			
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Game game = new Game();
//		game.insertIntoBoard(2, 6, "X");
//		game.insertIntoBoard(0, 8, "O");
//		game.insertIntoBoard(1, 7, "X");
//		game.insertIntoBoard(0, 0, "O");
//		game.insertIntoBoard(2, 8, "X");
//		game.insertIntoBoard(2, 1, "O");
//		
//		game.prettyPrintGameBoard();
//		
//		game.resetGame();
		
		
		
		
		//MiniBoard TestCases
//		Game game = new Game();
//		
//		//Create a 3*3 Miniboard
//		String[][] board = new String[3][3];

//T1		
//		board[1][1]= "X";
//		board[2][2]= "X";
//		game.insertIntoMiniBoard(board, 0, 0, "X");

//T2		
//		board[1][0]= "X";
//		board[2][0]= "X";
//		game.insertIntoMiniBoard(board, 0, 0, "X");
	
//T3		
//		board[0][1]= "X";
//		board[0][2]= "X";
//		game.insertIntoMiniBoard(board, 0, 0, "X");
	
//T4		
//		board[0][0]= "X";
//		board[2][0]= "X";
//		game.insertIntoMiniBoard(board, 1, 0, "X");

//T5		
//		board[1][0]= "X";
//		board[1][2]= "X";
//		game.insertIntoMiniBoard(board, 1, 1, "X");

//T6		
//		board[1][1]= "X";
//		board[2][2]= "X";
//		game.insertIntoMiniBoard(board, 2, 0, "X");

//T7		
//		board[0][2]= "X";
//		board[2][2]= "X";
//		game.insertIntoMiniBoard(board, 1, 2, "X");

//		game.printGameBoard(board, 3);

	
		//TestCase 3
//		Game game = new Game();
//		
//		//When you uncomment this, Uncomment the below getter/setter methods to bypass the rules for testing
//		//The below is for winning the board scenario
//		game.getBoardsWon()[1][2]="O";
//		game.getBoardsWon()[2][2]="O";
////		
////		game.insertIntoBoard(2, 6, "X");
//		game.insertIntoBoard(0, 8, "O");
//		game.insertIntoBoard(1, 8, "O");
//		game.insertIntoBoard(2, 8, "O");
////		game.insertIntoBoard(1, 7, "X");
////		game.insertIntoBoard(0, 0, "O");
////		game.insertIntoBoard(5, 8, "X");
////		game.insertIntoBoard(7, 1, "O");
////		game.insertIntoBoard(7, 8, "X");
//		
//		game.prettyPrintGameBoard();
		
		//TestCase 4
		
//		Game game = new Game();
//		
//		String[][] board = new String[3][3];
//		board[0][0] = "X";
//		board[0][1] = "O";
//		//board[0][2] = "O";
//		board[1][0] = "O";
//		board[1][1] = "X";
//		board[1][2] = "X";
//		board[2][0] = "O";
//		board[2][1] = "X";
//		board[2][2] = "O";
//		
//		
//		game.printGameBoard(board, 3);
//		
//		game.machinePlay(board, "O", 3);
//		//game.machinePlay(board, "X", 3);
//		//game.machinePlay(board, "O", 3);
//		//game.machinePlay(board, "X", 3);
//		
//		
//		game.printGameBoard(board, 3);
		

		////////////////////////////////////////////////////
		Game game = new Game();
		
		//When you uncomment this, Uncomment the below getter/setter methods to bypass the rules for testing
		//The below is for winning the board scenario
//		game.getBoardsWon()[1][2]="O";
//		game.getBoardsWon()[2][2]="O";

		game.insertIntoBoard(6, 8, "O");

		game.machinePlay(6, 8, "O");
		
		game.prettyPrintGameBoard(null, 9);
		
	}

	
	//TODO The below getter/setter methods is just for testing. They have to be removed
	public String[][] getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(String[][] gameBoard) {
		this.gameBoard = gameBoard;
	}

	public String[][] getBoardsWon() {
		return boardsWon;
	}

	public void setBoardsWon(String[][] boardsWon) {
		this.boardsWon = boardsWon;
	}

}
