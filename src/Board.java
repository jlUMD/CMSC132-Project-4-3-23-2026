import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class creates the board. The board takes care of the array of pieces,
 * the square tiles, and a few setup and background methods
 * @author justi
 *
 */
public class Board
{

	/**
	 * An inner class specifically for the background tiles. Each tile has a piece
	 * either null or an actual piece, and has the potential to be 'lighted'
	 * to show that its being selected
	 * @author justi
	 *
	 */
    private class Square
    {
        Piece pieceHere;
        boolean lighted;
        boolean darkSquare;

        /** 
         * Creates square object
         * 
         * @param darkOrNot -- light or dark square, for visuals
         */
        Square(boolean darkOrNot)
        {
            pieceHere = null;
            lighted = false;
            darkSquare = darkOrNot;
        }
        
        /**
         * Method changes the square's state to lighted
         */
        public void lightSwitchOn() {
        	lighted = true;
        }
        
        /**
         * Method changes the square's state to not lighted
         */
        public void lightSwitchOff() {
        	lighted = false;
        }
        
    }
    
    // Array of the board is created
    private Square[][] squares;
    
    /**
     * Creates the board object with the 2d array of squares for the board
     */
    public Board()
    {
        squares = new Square[8][8];
        
        /**
         *  Creates square objects to fill the squares array, alternates between
         *  light and dark squares
         */
        for (int r = 0; r < 8; r += 2)
        {
            for (int c = 0; c < 8; c += 2)
            {
                squares[r][c] = new Square(false);
                squares[r][c + 1] = new Square(true);
            }
            for (int c = 0; c < 8; c += 2)
            {
                squares[r + 1][c] = new Square(true);
                squares[r + 1][c + 1] = new Square(false);
            }
        }
    }

    /**
     * Gets piece at row, col. If piece out of bounds or empty, returns null
     * @param row -- row of target
     * @param col -- column of target
     * @return Piece -- the piece at the target location
     */
    public Piece getPiece(int row, int col)
    {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
        	return null;
        }
        return squares[row][col].pieceHere;
    }

    /**
     * Assigns a piece to the location it has stored
     * @param piece
     */
    public void assignPiece(Piece piece)
    {
    	if (piece != null) {
    		squares[piece.getBoardRow()][piece.getBoardCol()].pieceHere = piece;
    	}
    }

    /**
     * Removes a piece from target square in 2d array
     * @param row
     * @param col
     * @return
     */
    public Piece removePiece(int row, int col)
    {
        Piece p = squares[row][col].pieceHere;
        squares[row][col].pieceHere = null;
        return p;
    }

    /**
     * Highlights a square by calling setter method
     * @param row
     * @param col
     */
    public void highlightSquare(int row, int col)
    {
        if (row >= 0 && row < 8 && col >= 0 && col < 8)
        {
            squares[row][col].lightSwitchOn();
        }
    }
    
    /**
     * Unhighlights a square by calling setter method
     * @param row
     * @param col
     */
    public void unhighlightSquare(int row, int col)
    {
        if (row >= 0 && row < 8 && col >= 0 && col < 8)
        {
            squares[row][col].lightSwitchOff();
        }
    }

    /**
     * Unhighlights from every square from the board
     */
    public void clearHighlights()
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                unhighlightSquare(r, c);
            }
        }
    }

    /**
     * Sets up the board to start the game. First empties the board, then assigns
     * all pieces to starting positions
     */
    public void setupStandardBoard()
    {
    	
    	// Empties current board (in case we add replay ability)
        clearBoard();

        // Sets up black pieces
        assignPiece(new Rook(false, 0, 0));
        assignPiece(new Knight(false, 0, 1));
        assignPiece(new Bishop(false, 0, 2));
        assignPiece(new Queen(false, 0, 3));
        assignPiece(new King(false, 0, 4));
        assignPiece(new Bishop(false, 0, 5));
        assignPiece(new Knight(false, 0, 6));
        assignPiece(new Rook(false, 0, 7));
        assignPiece(new Pawn(false, 1, 0));
        assignPiece(new Pawn(false, 1, 1));
        assignPiece(new Pawn(false, 1, 2));
        assignPiece(new Pawn(false, 1, 3));
        assignPiece(new Pawn(false, 1, 4));
        assignPiece(new Pawn(false, 1, 5));
        assignPiece(new Pawn(false, 1, 6));
        assignPiece(new Pawn(false, 1, 7));

        // Sets up white pieces
        assignPiece(new Rook(true, 7, 0));
        assignPiece(new Knight(true, 7, 1));
        assignPiece(new Bishop(true, 7, 2));
        assignPiece(new Queen(true, 7, 3));
        assignPiece(new King(true, 7, 4));
        assignPiece(new Bishop(true, 7, 5));
        assignPiece(new Knight(true, 7, 6));
        assignPiece(new Rook(true, 7, 7));
        assignPiece(new Pawn(true, 6, 0));
        assignPiece(new Pawn(true, 6, 1));
        assignPiece(new Pawn(true, 6, 2));
        assignPiece(new Pawn(true, 6, 3));
        assignPiece(new Pawn(true, 6, 4));
        assignPiece(new Pawn(true, 6, 5));
        assignPiece(new Pawn(true, 6, 6));
        assignPiece(new Pawn(true, 6, 7));
    }

    /**
     * Clears the board lol
     */
    // Is this even needed?
    public void clearBoard()
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                squares[r][c].pieceHere = null;
            }
        }
    }

    /**
     * Makes the move by checking certain conditions before removing a piece's
     * old location and assigning it to a new position
     * Note this method returns MoveResult which stores the captured piece and 
     * isCapture boolean
     * Also Note that this method only makes the move ON THE 2D ARRAYED BACKEND
     * and does NOT apply the animations yet
     * @param fromRow -- Row of original place
     * @param fromCol -- Column of original place
     * @param toRow -- target row
     * @param toCol -- target column
     * @return
     */
    public Piece.MoveResult makeMove(int fromRow, int fromCol, int toRow, int toCol)
    {
        Piece pieceMoved = getPiece(fromRow, fromCol);
        Piece captured = null;
        boolean isCapture = false;
        
        // There is no piece
        if (pieceMoved == null)
        {
        	return new Piece.MoveResult(false, false, null);
        }
        
        // Invalid move
        if (!pieceMoved.canMoveTo(toRow, toCol, this))
        {
            return new Piece.MoveResult(false, false, null);
        }

        // Checks for en passant rules (this might not work correctly)
        if (pieceMoved instanceof Pawn && Math.abs(toCol - fromCol) == 1 
        		&& getPiece(toRow, toCol) == null
        		&& getPiece(fromRow, toCol) instanceof Pawn 
        		&& ((Pawn) getPiece(fromRow, toCol)).justDoubleMoved())
        {
            captured = removePiece(fromRow, toCol);
            isCapture = true;
        }

        // If there's a piece at target location, takes that piece
        if (getPiece(toRow, toCol) != null)
        {
            captured = removePiece(toRow, toCol);
            isCapture = true;
        }
        

        // Takes the pawn away from the original spot in 2d array
        removePiece(fromRow, fromCol);
        
        // Changes the location values of the pawn object
        pieceMoved.moveTo(toRow, toCol);
        
        // Adds the pawn to the new location in the 2d array
        assignPiece(pieceMoved);
        
        // Performs castle (already checked that it's a valid move earlier)
        // Note king is already moved
        if (pieceMoved instanceof King && Math.abs(toCol - fromCol) == 2)
        {
            if (toCol > fromCol)
            {
                Piece rook = removePiece(fromRow, 7);
                if (rook != null)
                {
                    rook.moveTo(fromRow, toCol - 1);
                    assignPiece(rook);
                }
            } else
            {
                Piece rook = removePiece(fromRow, 0);
                if (rook != null)
                {
                    rook.moveTo(fromRow, toCol + 1);
                    assignPiece(rook);
                }
            }
        }

        // Checks for a promoted pawn (pawn on opposite side last row) and promotes it
        // Note that it only makes it a queen, can't change it to any other piece
        if (pieceMoved instanceof Pawn)
        {
            int promoRow = pieceMoved.isWhite() ? 0 : 7;
            if (toRow == promoRow)
            {
                removePiece(toRow, toCol);
                Queen queen = new Queen(pieceMoved.isWhite(), toRow, toCol);
                
                // Does this even make it rotate?
                queen.setRotation(360);
                assignPiece(queen);
            }
        }

        return new Piece.MoveResult(true, isCapture, captured);
    }


    /**
     * Makes a move and checks to see if that move puts you in check. If it does
     * return false, if it doesn't, return true. Then reverts the board back to
     * how it was.
     * @param fromRow -- Original row
     * @param fromCol -- Original column
     * @param toRow -- Target row
     * @param toCol -- Target column
     * @return -- True if move doesn't put themself in check, false otherwise.
     */
    public boolean isMoveSafe(int fromRow, int fromCol, int toRow, int toCol)
    {
    	// Checks for piece at original spot, returns false if none
        Piece piece = getPiece(fromRow, fromCol);
        if (piece == null) 
        {
        	return false;
        }

        // Moves the piece while storing important information
        boolean hasItMoved = piece.hasMoved();
        Piece.MoveResult temp = makeMove(fromRow, fromCol, toRow, toCol);
        piece.setHasMoved(hasItMoved);
        
        // Checks if its a safe move
        boolean safeMove = !isInCheck(piece.isWhite());

        // Reverts back to before the simulated move
        squares[toRow][toCol].pieceHere = null;
        piece.setBoardRow(fromRow);
        piece.setBoardCol(fromCol);
        squares[fromRow][fromCol].pieceHere = piece;
        if (temp.capturedPiece != null) {
            squares[temp.capturedPiece.getBoardRow()][temp.capturedPiece.getBoardCol()].pieceHere = temp.capturedPiece;
        }

        return safeMove;
    }

    /**
     * Checks if isWhite King is in check
     * @param isWhite -- The side that's being checked for checks
     * @return
     */
    public boolean isInCheck(boolean isWhite)
    {
        int kingRow = -1;
        int kingCol = -1;
        
        // Loop finds the correct King and stores its location
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = getPiece(r, c);
                if (p instanceof King && p.isWhite() == isWhite)
                {
                    kingRow = p.getBoardRow();
                    kingCol = p.getBoardCol();
                    break;
                }
            }
            // Once found, breaks second loop too
            if (kingRow != -1 && kingCol != -1) 
            	{
            		break;
            	}
        }

        // Loops through every piece of the opposite color to see if they CAN
        // move to the location of the King. If yes, then that's check. If not,
        // nothing happens and loop ends.
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = getPiece(r, c);
                if (p != null && p.isWhite() != isWhite)
                {
                    if (p.canMoveTo(kingRow, kingCol, this))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Returns true if checkmate, returns false if not
     * @param isWhite -- Side to be checked for checkmate
     * @return
     */
    public boolean isCheckmate(boolean isWhite)
    {
    	// Checks for check, if no check then no checkmate
        if (!isInCheck(isWhite)) 
        {
        	return false;
        }
        
        // Checks if side has any possible moves. if they do, return false
        // because no checkmate, if they don't, return true.
        return !hasLegalMoves(isWhite);
    }

    /**
     * Checks to see if side's King is in check, if not in check and has no
     * legal moves then return true. If in check return false. If not in check
     * but has legal moves return false.
     * @param isWhite -- Side to be checked
     * @return
     */
    public boolean isStalemate(boolean isWhite)
    {
        if (isInCheck(isWhite))
        {
        	return false;
        }
        return !hasLegalMoves(isWhite);
    }

    /**
     * Iterates through every piece of a given color and checks if they have any
     * possible moves. If ANY piece has at least one possible move, returns true
     * If not, return false
     * @param isWhite -- Side to be checked
     * @return
     */
    private boolean hasLegalMoves(boolean isWhite)
    {
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = getPiece(r, c);
                // Checks for piece at r, c. Checks if correct color
                if (p != null && p.isWhite() == isWhite)
                {
                	// Puts all possible moves into array
                    Point[] moves = p.getPossibleMoves(this);
                    // Iterates through array to see if each move is safe
                    for (Point m : moves)
                    {
                        if (isMoveSafe(r, c, m.getX(), m.getY()))
                        {
                        	// If it reaches here, that means a valid move exists
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a list of all legal moves of a piece in the target location
     * @param row - Row of target Piece
     * @param col - Col of target Piece
     * @return
     */
    public List<Point> getLegalMoves(int row, int col)
    {
        Piece piece = getPiece(row, col);
        // If no piece, return empty arraylist
        if (piece == null) 
        {
        	return new ArrayList<>();
        }
        Point[] possible = piece.getPossibleMoves(this);
        
        // Lambda Expression (wtf is stream and collect)
        List<Point> legalMoves = Arrays.stream(possible)
                .filter(move -> isMoveSafe(row, col, move.getX(), move.getY()))
                .collect(Collectors.toList());
        return legalMoves;
    }

    /** 
     * Makes an arraylist of a certain color's pieces on the board
     * @param isWhite -- Color to search
     * @return
     */
    public List<Piece> getPieces(boolean isWhite)
    {
        List<Piece> pieces = new ArrayList<>();
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Piece p = getPiece(r, c);
                // checks if there's an actual piece, and checks correct color
                if (p != null && p.isWhite() == isWhite)
                {
                    pieces.add(p);
                }
            }
        }
        return pieces;
    }

    
    /**
     * This draws the board including the different color squares and calls the
     * piece's own drawPiece method. This method doesn't return anything and
     * works on miracles.
     * @param brush -- The brush is created in ChessGame or Game
     * @param offsetX -- X Position of the left side of the board (moves right)
     * @param offsetY -- Y Position of the top of the board (moves down)
     * @param squareSize -- duh
     * @param flipped -- It's here even though its undecided if we're going to flip
     * or not because flipping causes a few problems in other places
     * @param whiteToMove
     */
    public void drawBoard(Graphics brush, int offsetX, int offsetY, int squareSize,
                          boolean flipped, boolean whiteToMove)
                          {
        Graphics g2 = (Graphics2D) brush;

     
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                int displayRow = flipped ? 7 - r : r;
                int displayCol = flipped ? 7 - c : c;
                int sx = offsetX + displayCol * squareSize;
                int sy = offsetY + displayRow * squareSize;

                boolean isLight = (r + c) % 2 == 0;
                Color lightColor = new Color(240, 217, 181);
                Color darkColor = new Color(181, 136, 99);
                g2.setColor(isLight ? lightColor : darkColor);
                g2.fillRect(sx, sy, squareSize, squareSize);

                if (squares[displayRow][displayCol].lighted)
                {
                    g2.setColor(new Color(100, 200, 100, 120));
                    g2.fillRect(sx, sy, squareSize, squareSize);
                }

                Piece piece = squares[displayRow][displayCol].pieceHere;
                if (piece != null)
                {
                        piece.drawPiece(g2, sx, sy, squareSize);
                }
            }
        }

        g2.setColor(new Color(60, 40, 20));
        g2.drawRect(offsetX, offsetY, squareSize * 8, squareSize * 8);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(new Color(200, 200, 200));
        for (int c = 0; c < 8; c++)
        {
            int dc = flipped ? 7 - c : c;
            String label = String.valueOf((char)('a' + c));
            g2.drawString(label, offsetX + dc * squareSize + squareSize / 2 - 4,
                         offsetY + 8 * squareSize + 15);
        }
        for (int r = 0; r < 8; r++)
        {
            int dr = flipped ? 7 - r : r;
            String label = String.valueOf(8 - dr);
            g2.drawString(label, offsetX - 15, offsetY + dr * squareSize + squareSize / 2 + 4);
        }
    }
}
