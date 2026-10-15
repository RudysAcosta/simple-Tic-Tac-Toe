package tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

record Position(int row, int col) {}

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TicTacToe ticTacToe = new TicTacToe(scanner);

        ticTacToe.printBoard();

        int cont = 0;
        char player = 'X';

        while (true) {
            if (ticTacToe.playTurn(player)) {
                ticTacToe.printBoard();

                if (cont > 5) {
                    char win = ticTacToe.getWinner();

                    if (win == 'X' || win == 'O') {
                        System.out.printf("%c wins", win);
                        break;
                    }

                    if (ticTacToe.gameFinished()) {
                        System.out.println("Draw");
                        break;
                    }
                }

                cont++;
                player = (player == 'X') ? 'O' : 'X';
            }
        }
    }
}


class WinPattern {

    private final List<Position> positions;

    public WinPattern(Position... positions) {
        this.positions = new ArrayList<>(Arrays.asList(positions));
    }

    public List<Position> getPositions() {
        return List.copyOf(this.positions);
    }
}


class TicTacToe {

    char[][] board = new char[3][3];
    private final List<WinPattern> winPatterns = new ArrayList<>();
    private final Scanner scanner;


    public TicTacToe(Scanner scanner) {
        initializeWinPatterns();
        this.scanner = scanner;

        Arrays.stream(board)
                .forEach(row -> Arrays.fill(row, ' '));
    }

    public boolean playTurn(char player) {
        while (true) {
            try {
                Position position = getPosition();
                fill(position, player);
                return true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
    }

    public Position getPosition() {
        String turn = scanner.nextLine();

        try {
            int[] positionInt = Arrays.stream(turn.split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            if (positionInt.length != 2) {
                throw new IllegalArgumentException(
                        "You must enter two coordinates!"
                );
            }

            int row = positionInt[0] - 1;
            int col = positionInt[1] - 1;

            if (col > 2 || col < 0 || row > 2 || row < 0) {
                throw new IllegalArgumentException("Coordinates should be from 1 to 3!");
            }

            if (board[row][col] == 'X' || board[row][col] == 'O') {
                throw new IllegalArgumentException("This cell is occupied! Choose another one!");
            }

            return new Position(row, col);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("You should enter numbers!");
        }
    }

    public void fill(Position cell, char player) {
        board[cell.row()][cell.col()] = player;
    }

    public char getWinner() {

        for (WinPattern pattern : winPatterns) {

            boolean win = true;
            char cellWin = '\0';

            for (int i = 1; i < pattern.getPositions().size(); i++) {

                Position prePosition = pattern.getPositions().get(i - 1);
                Position position = pattern.getPositions().get(i);

                char preCell = board[prePosition.row()][prePosition.col()];
                char cell = board[position.row()][position.col()];

                if (cell == ' ' || cell == '_' || preCell != cell) {
                    win = false;
                    break;
                }

                cellWin = cell;
            }

            if (win) {
                return cellWin;
            }
        }

        return '\0';
    }

    public boolean gameFinished() {
        for (char row[] : board) {
            for (char cell : row) {
                if (cell != 'X' && cell != 'O') return false;
            }
        }

        return true;
    }

    public boolean isImpossible() {
        int countX = 0;
        int countO = 0;

        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'X') countX++;
                if (cell == 'O') countO++;
            }
        }

        if (Math.abs(countX - countO) >= 2) {
            return true;
        }

        boolean xWins = false;
        boolean oWins = false;

        for (WinPattern pattern : winPatterns) {

            List<Position> positions = pattern.getPositions();

            Position first = positions.get(0);
            char symbol = board[first.row()][first.col()];

            if (symbol != 'X' && symbol != 'O') {
                continue;
            }

            boolean win = true;

            for (Position position : positions) {
                if (board[position.row()][position.col()] != symbol) {
                    win = false;
                    break;
                }
            }

            if (win) {
                if (symbol == 'X') xWins = true;
                if (symbol == 'O') oWins = true;
            }
        }

        return xWins && oWins;
    }

    public void printBoard() {

        System.out.println("-".repeat(10));
        for (int i = 0; i < this.board.length; i++) {
            System.out.print("| ");

            for (int j = 0; j < this.board[i].length; j++) {
                if (j == 1) {
                    System.out.printf(" %c ", this.board[i][j]);
                } else {
                    System.out.printf("%c", this.board[i][j]);
                }
            }

            System.out.print(" |");
            System.out.println();
        }

        System.out.println("-".repeat(10));
    }

    private void initializeWinPatterns() {

        for (int row = 0; row < 3; row++) {
            winPatterns.add(new WinPattern(
                    new Position(row, 0),
                    new Position(row, 1),
                    new Position(row, 2)
            ));
        }

        for (int col = 0; col < 3; col++) {
            winPatterns.add(new WinPattern(
                    new Position(0, col),
                    new Position(1, col),
                    new Position(2, col)
            ));
        }

        winPatterns.add(new WinPattern(
                new Position(0, 0),
                new Position(1, 1),
                new Position(2, 2)
        ));

        winPatterns.add(new WinPattern(
                new Position(0, 2),
                new Position(1, 1),
                new Position(2, 0)
        ));
    }
}
