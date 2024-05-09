import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessGame {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;

    public ChessGame() {
        board = new Board();
        whitePlayer = new Player(Color.WHITE);
        blackPlayer = new Player(Color.BLACK);
        currentPlayer = whitePlayer;
    }

    public boolean movePiece(Position start, Position end) {
        Piece piece = board.getPiece(start);
        if (piece == null || piece.getColor() != currentPlayer.getColor()) {
            return false;
        }

        List<Position> validMoves = piece.getValidMoves(board);
        if (!validMoves.contains(end)) {
            return false;
        }

        board.movePiece(start, end);
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
        return true;
    }

    public boolean isCheckmate() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPiece(new Position(i, j));
                if (piece != null && piece.getColor() == currentPlayer.getColor()) {
                    List<Position> validMoves = piece.getValidMoves(board);
                    for (Position move : validMoves) {
                        Board copyBoard = new Board(board);
                        copyBoard.movePiece(new Position(i, j), move);
                        if (!copyBoard.isCheck(currentPlayer.getColor())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        Scanner scanner = new Scanner(System.in);

        while (!game.isCheckmate()) {
            System.out.println(game.board); // Print the board
            System.out.println("Enter move (e.g. e2 e4): ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            Position start = parsePosition(parts[0]);
            Position end = parsePosition(parts[1]);

            if (!game.movePiece(start, end)) {
                System.out.println("Invalid move!");
            }
        }

        System.out.println("Checkmate!");
    }

    private static Position parsePosition(String input) {
        int x = 8 - (input.charAt(1) - '1');
        int y = input.charAt(0) - 'a';
        return new Position(x, y);
    }
}
class Board {
    private Piece[][] board;
    private Player whitePlayer;
    private Player blackPlayer;

    public Board() {
        board = new Piece[8][8];
        setupPieces();

        King whiteKing = new King(Color.WHITE, new Position(7, 4));
        King blackKing = new King(Color.BLACK, new Position(0, 4));

        whitePlayer = new Player(Color.WHITE);
        blackPlayer = new Player(Color.BLACK);

        whitePlayer.setKing(whiteKing);
        blackPlayer.setKing(blackKing);

        board[7][4] = whiteKing;
        board[0][4] = blackKing;
    }

    public Board(Board other) {
        this.board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = other.board[i][j];
                if (piece != null) {
                    this.board[i][j] = createPieceCopy(piece);
                }
            }
        }
    }

    private Piece createPieceCopy(Piece piece) {
        if (piece instanceof Pawn) {
            return new Pawn((Pawn) piece);
        } else if (piece instanceof Rook) {
            return new Rook((Rook) piece);
        } else if (piece instanceof Knight) {
            return new Knight((Knight) piece);
        } else if (piece instanceof Bishop) {
            return new Bishop((Bishop) piece);
        } else if (piece instanceof Queen) {
            return new Queen((Queen) piece);
        } else if (piece instanceof King) {
            return new King((King) piece);
        } else {
            return null;
        }
    }


    public Piece getPiece(Position position) {
        return board[position.getX()][position.getY()];
    }

    public void movePiece(Position start, Position end) {
        Piece piece = getPiece(start);
        board[end.getX()][end.getY()] = piece;
        board[start.getX()][start.getY()] = null;
    }

    public boolean isCheck(Color color) {
        Position kingPosition = findKing(color);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getColor() != color) {
                    List<Position> validMoves = piece.getValidMoves(this);
                    if (validMoves.contains(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Position findKing(Color color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece instanceof King && piece.getColor() == color) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    private void setupPieces() 
    {
    whitePlayer = new Player(Color.WHITE);
    blackPlayer = new Player(Color.BLACK);

    // After initializing kings
    whitePlayer.setKing((King) board[7][4]);
    blackPlayer.setKing((King) board[0][4]);

    // Set up pawns
    for (int i = 0; i < 8; i++) {
        board[1][i] = new Pawn(Color.BLACK, new Position(1, i));
        board[6][i] = new Pawn(Color.WHITE, new Position(6, i));
    }

    // Set up rooks
    board[0][0] = new Rook(Color.BLACK, new Position(0, 0));
    board[0][7] = new Rook(Color.BLACK, new Position(0, 7));
    board[7][0] = new Rook(Color.WHITE, new Position(7, 0));
    board[7][7] = new Rook(Color.WHITE, new Position(7, 7));

    // Set up knights
    board[0][1] = new Knight(Color.BLACK, new Position(0, 1));
    board[0][6] = new Knight(Color.BLACK, new Position(0, 6));
    board[7][1] = new Knight(Color.WHITE, new Position(7, 1));
    board[7][6] = new Knight(Color.WHITE, new Position(7, 6));

    // Set up bishops
    board[0][2] = new Bishop(Color.BLACK, new Position(0, 2));
    board[0][5] = new Bishop(Color.BLACK, new Position(0, 5));
    board[7][2] = new Bishop(Color.WHITE, new Position(7, 2));
    board[7][5] = new Bishop(Color.WHITE, new Position(7, 5));

    // Set up queens
    board[0][3] = new Queen(Color.BLACK, new Position(0, 3));
    board[7][3] = new Queen(Color.WHITE, new Position(7, 3));

    // Set up kings
    board[0][4] = new King(Color.BLACK, new Position(0, 4));
    board[7][4] = new King(Color.WHITE, new Position(7, 4));
   }
}

class Player {
    private Color color;
    private King king;

    public Player(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public King getKing() {
        return king;
    }

    public void setKing(King king) {
        this.king = king;
    }




    
}

enum Color {
    WHITE, BLACK
}

class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

abstract class Piece {
    private final Color color;
    protected Position position;

    public Piece(Color color) {
        this.color = color;
    }

    public final Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public abstract List<Position> getValidMoves(Board board);
}

class Pawn extends Piece
 {
    public Pawn(Color color, Position position) {
    super(color);
    this.position = position;
    }

    public Pawn(Pawn pawn) {
    super(pawn.getColor());
    this.position = new Position(pawn.getPosition().getX(), pawn.getPosition().getY());
}




    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        int direction = (getColor() == Color.WHITE) ? -1 : 1;
        int startX = getPosition().getX();
        int startY = getPosition().getY();

        // Move forward
        int endXForward = startX + direction; // Rename endX to endXForward
        if (endXForward >= 0 && endXForward < 8 && board.getPiece(new Position(endXForward, startY)) == null) {
            validMoves.add(new Position(endXForward, startY));
        }

        // Capture diagonally
        for (int i = -1; i <= 1; i += 2) {
            int endXDiagonal = startX + direction;
            int endYDiagonal = startY + i;
            if (endXDiagonal >= 0 && endXDiagonal < 8 && endYDiagonal >= 0 && endYDiagonal < 8) {
                Piece piece = board.getPiece(new Position(endXDiagonal, endYDiagonal));
                if (piece != null && piece.getColor() != getColor()) {
                    validMoves.add(new Position(endXDiagonal, endYDiagonal));
                }
            }
        }

        return validMoves;
    }
}

class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

   @Override
   public List<Position> getValidMoves(Board board) {
       List<Position> validMoves = new ArrayList<>();
       int startX = getPosition().getX();
       int startY = getPosition().getY();

       // Move horizontally
       for (int i = -1; i <= 1; i += 2) {
           for (int j = 1; j < 8; j++) {
               int endX = startX + i * j;
               if (endX < 0 || endX >= 8) break;

               Piece piece = board.getPiece(new Position(endX, startY));
               if (piece == null) {
                   validMoves.add(new Position(endX, startY));
               } else if (piece.getColor() != getColor()) {
                   validMoves.add(new Position(endX, startY));
                   break;
               } else break;
           }
       }

       // Move vertically
       for (int i = -1; i <= 1; i += 2) {
           for (int j = 1; j < 8; j++) {
               int endY = startY + i * j;
               if (endY < 0 || endY >= 8) break;

               Piece piece = board.getPiece(new Position(startX, endY));
               if (piece == null) {
                   validMoves.add(new Position(startX, endY));
               } else if (piece.getColor() != getColor()) {
                   validMoves.add(new Position(startX, endY));
                   break;
               } else break;
           }
       }

       return validMoves;
   }
}
class Knight extends Piece {
    public Knight(Color color) {
        super(color);}

   @Override
   public List<Position> getValidMoves(Board board) {
       List<Position> validMoves = new ArrayList<>();
       int startX = getPosition().getX();
       int startY = getPosition().getY();

       // Move in L shape
       for (int i : new int[]{-2, -1, 1, 2}) {
           for (int j : new int[]{-2, -1, 1, 2}) {
               if (Math.abs(i) == Math.abs(j)) continue;
               int endX = startX + i;
               int endY = startY + j;
               if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8) continue;

               Piece piece = board.getPiece(new Position(endX, endY));
               if (piece == null || piece.getColor() != getColor()) {
                   validMoves.add(new Position(endX, endY));
               }
           }
       }

       return validMoves;
   }
}

class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

   @Override
   public List<Position> getValidMoves(Board board) {
       List<Position> validMoves = new ArrayList<>();
       int startX = getPosition().getX();
       int startY = getPosition().getY();

       // Move diagonally
       for (int i = -1; i <= 1; i += 2) {
           for (int j = -1; j <= 1; j += 2) {
               for (int k = 1; k < 8; k++) {
                   int endX = startX + i * k;
                   int endY = startY + j * k;
                   if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8) break;

                   Piece piece = board.getPiece(new Position(endX, endY));
                   if (piece == null) {
                       validMoves.add(new Position(endX, endY));
                   } else if (piece.getColor() != getColor()) {
                       validMoves.add(new Position(endX, endY));
                       break;
                   } else break;
               }
           }
       }

       return validMoves;
   }
}

class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

   @Override
   public List<Position> getValidMoves(Board board) {
       List<Position> validMoves = new ArrayList<>();
       Rook rook = new Rook(getColor(), getPosition());
       Bishop bishop = new Bishop(getColor(), getPosition());
       validMoves.addAll(rook.getValidMoves(board));
       validMoves.addAll(bishop.getValidMoves(board));
       return validMoves;
   }
}

class King extends Piece {
    public King(Color color) {
        super(color);
    }

   @Override
   public List<Position> getValidMoves(Board board) {
       List<Position> validMoves = new ArrayList<>();
       int startX = getPosition().getX();
       int startY = getPosition().getY();

       // Move one square in any direction
       for (int i = -1; i <= 1; i++) {
           for (int j = -1; j <= 1; j++) {
               if (i == 0 && j == 0) continue;
               int endX = startX + i;
               int endY = startY + j;
               if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8) continue;

               Piece piece = board.getPiece(new Position(endX, endY));
               if (piece == null || piece.getColor() != getColor()) {
                   validMoves.add(new Position(endX, endY));
               }
           }
       }

       return validMoves;
   }
}
