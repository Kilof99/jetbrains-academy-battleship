package battleship;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    static char[] rows = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};

    public static void main(String[] args) {
        char[][] gameBoard1 = prepareBoard();
        char[][] gameBoard2 = prepareBoard();
        Coordinate[][] remainingShips1 = new Coordinate[5][];
        Coordinate[][] remainingShips2 = new Coordinate[5][];
        int currentPlayer = 1;
        printPlaceShipsMessage(currentPlayer);
        placeShips(gameBoard1, remainingShips1);
        currentPlayer = switchPlayers(currentPlayer);
        printPlaceShipsMessage(currentPlayer);
        placeShips(gameBoard2, remainingShips2);
        currentPlayer = switchPlayers(currentPlayer);

        System.out.println("The game starts!");
        char[][] fogBoard1 = prepareBoard();
        char[][] fogBoard2 = prepareBoard();
        boolean gameFinished = false;

        while (!gameFinished) {
            if (currentPlayer == 1) {
                printBoard(fogBoard2, gameBoard1);
            } else {
                printBoard(fogBoard1, gameBoard2);
            }
//        printBoard(fogBoard1);
            printMoveMessage(currentPlayer);

            boolean moveResolved = false;
            while (!moveResolved) {
                Coordinate coordinate = readShot();
                if (coordinate == null) {
                    System.out.println("Error! Incorrect coordinate! Try again:");
                } else {
                    if (currentPlayer == 1) {
                        if (takeShot(gameBoard2, coordinate, fogBoard2)) {
                            printBoard(fogBoard2, gameBoard1);
                            if (sinkShip(remainingShips2, coordinate)) {
                                System.out.println("You sank a ship! Specify a new target:");
                            } else {
                                System.out.println("You hit a ship!");
                            }
                        } else {
                            printBoard(fogBoard2, gameBoard1);
                            System.out.println("You missed!");
                        }
                    } else {
                        if (takeShot(gameBoard1, coordinate, fogBoard1)) {
                            printBoard(fogBoard1, gameBoard2);
                            if (sinkShip(remainingShips1, coordinate)) {
                                System.out.println("You sank a ship! Specify a new target:");
                            } else {
                                System.out.println("You hit a ship!");
                            }
                        } else {
                            printBoard(fogBoard1, gameBoard2);
                            System.out.println("You missed!");
                        }
                    }
                    moveResolved = true;
                    gameFinished = areAllShipsDestroyed(remainingShips1) || areAllShipsDestroyed(remainingShips2);
                    if (!gameFinished) {
                        currentPlayer = switchPlayers(currentPlayer);
                    }
                }
            }
//            printBoard(gameBoard1);
        }
        System.out.println("You sank the last ship. You won. Congratulations!");
    }

    private static void printBoard(char[][] fogBoard, char[][] gameBoard) {
        System.out.println();
        printBoard(fogBoard);
        System.out.println("---------------------");
        printBoard(gameBoard);
        System.out.println();
    }

    private static void printPlaceShipsMessage(int currentPlayer) {
        System.out.printf("Player %d, place your ships on the game field\n",
                currentPlayer);
    }

    private static void printMoveMessage(int currentPlayer) {
        System.out.printf("Player %d, it's your turn:\n",
                currentPlayer);
    }

    private static int switchPlayers(Integer currentPlayer) {
        System.out.println("Press Enter and pass the move to another player");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        if (currentPlayer == 1) {
            return 2;
        } else {
            return 1;
        }

    }

    private static void placeShips(char[][] gameBoard, Coordinate[][] remainingShips) {
        for (Ship s :
                Ship.values()) {
            System.out.println();
            printBoard(gameBoard);
            System.out.println();
            printShipPlacementMessage(s);
            boolean resolved = false;
            while (!resolved) {
                Coordinate[] coordinates = readMove();
                if (coordinates == null || !isLegalLocation(coordinates) || !isCorrectLength(coordinates, s)) {
                    System.out.println("Error! Incorrect coordinates! Try again:");
                    continue;
                }
                Coordinate[] shipFields = generateShip(coordinates);

                if (!isSpaceAvailable(shipFields, gameBoard)) {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                } else {
                    placeShip(shipFields, gameBoard);
                    remainingShips[s.ordinal()] = shipFields;
                    resolved = true;
                }

            }
        }
        System.out.println();
        printBoard(gameBoard);
        System.out.println();
    }

    private static boolean areAllShipsDestroyed(Coordinate[][] remainingShips) {
        for (Coordinate[] ship :
                remainingShips) {
            if (ship != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean sinkShip(Coordinate[][] remainingShips, Coordinate coordinate) {
        for (int i = 0; i < remainingShips.length; i++) {
            if (remainingShips[i] == null) {
                continue;
            }
            for (int k = 0; k < remainingShips[i].length; k++) {
                if (remainingShips[i][k] != null && remainingShips[i][k].equals(coordinate)) {
                    remainingShips[i][k] = null;
                    for (Coordinate value : remainingShips[i]) {
                        if (value != null) {
                            return false;
                        }
                    }
                    remainingShips[i] = null;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean takeShot(char[][] sourceBoard, Coordinate coordinate, char[][] targetBoard) {
        if (sourceBoard[coordinate.row][coordinate.column] == 'O') {
            sourceBoard[coordinate.row][coordinate.column] = 'X';
            targetBoard[coordinate.row][coordinate.column] = 'X';
            return true;
        } else if (sourceBoard[coordinate.row][coordinate.column] == '~') {
            sourceBoard[coordinate.row][coordinate.column] = 'M';
            targetBoard[coordinate.row][coordinate.column] = 'M';
            return false;
        } else {
            return false;
        }
    }

    private static Coordinate readShot() {
        try (Scanner scanner = new Scanner(System.in)) {
            String inputCoordinate = scanner.next();
            if (!isLegalCoordinate(inputCoordinate)) {
                throw new Exception();
            }
            return new Coordinate(inputCoordinate.charAt(0), Integer.parseInt(inputCoordinate.replaceAll("[A-J]", "")));


        } catch (Exception e) {
            return null;
        }
    }

    private static void placeShip(Coordinate[] shipFields, char[][] gameBoard) {
        for (Coordinate c :
                shipFields) {
            gameBoard[c.row][c.column] = 'O';
        }
    }

    private static boolean isSpaceAvailable(Coordinate[] shipFields, char[][] gameBoard) {
        for (Coordinate c :
                shipFields) {
            if (!isCoordinateAvailable(c, gameBoard)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCoordinateAvailable(Coordinate c, char[][] gameBoard) {
        return gameBoard[c.row][c.column] != 'O' &&
                (c.row <= 0 || gameBoard[c.row - 1][c.column] != 'O') &&
                (c.row >= 9 || gameBoard[c.row + 1][c.column] != 'O') &&
                (c.column <= 0 || gameBoard[c.row][c.column - 1] != 'O') &&
                (c.column >= 9 || gameBoard[c.row][c.column + 1] != 'O') &&
                (c.row <= 0 || c.column <= 0 || gameBoard[c.row - 1][c.column - 1] != 'O') &&
                (c.row >= 9 || c.column >= 9 || gameBoard[c.row + 1][c.column + 1] != 'O') &&
                (c.row <= 0 || c.column >= 9 || gameBoard[c.row - 1][c.column + 1] != 'O') &&
                (c.row >= 9 || c.column <= 0 || gameBoard[c.row + 1][c.column - 1] != 'O');
    }

    private static Coordinate[] generateShip(Coordinate[] coordinates) {
        Coordinate[] fields = new Coordinate[getLength(coordinates)];
        boolean horizontal = coordinates[0].row == coordinates[1].row;
        if (horizontal) {
            if (coordinates[1].column > coordinates[0].column) {
                for (int i = 0; i < getLength(coordinates); i++) {
                    fields[i] = new Coordinate(coordinates[0].row, coordinates[0].column + i);
                }
            } else {
                for (int i = 0; i < getLength(coordinates); i++) {
                    fields[i] = new Coordinate(coordinates[1].row, coordinates[1].column + i);
                }
            }
        } else {
            if (coordinates[1].row > coordinates[0].row) {
                for (int i = 0; i < getLength(coordinates); i++) {
                    fields[i] = new Coordinate(coordinates[0].row + i, coordinates[0].column);
                }
            } else {
                for (int i = 0; i < getLength(coordinates); i++) {
                    fields[i] = new Coordinate(coordinates[1].row + i, coordinates[1].column);
                }
            }
        }

        return fields;

    }

    private static boolean isCorrectLength(Coordinate[] coordinates, Ship currentShip) {
        int length = getLength(coordinates);
        return length == currentShip.size;
    }

    private static int getLength(Coordinate[] coordinates) {
        return Math.abs(coordinates[0].column - coordinates[1].column +
                coordinates[0].row - coordinates[1].row) + 1;
    }

    private static Coordinate[] readMove() {
        try (Scanner scanner = new Scanner(System.in)) {
            String[] inputCoordinates = scanner.nextLine().split(" ");
            if (inputCoordinates.length != 2) {
                throw new Exception();
            }
            if (!isLegalCoordinate(inputCoordinates[0]) ||
                    !isLegalCoordinate(inputCoordinates[1])) {
                throw new Exception();
            }
            return new Coordinate[]{
                    new Coordinate(inputCoordinates[0].charAt(0), Integer.parseInt(inputCoordinates[0].replaceAll("[A-J]", ""))),
                    new Coordinate(inputCoordinates[1].charAt(0), Integer.parseInt(inputCoordinates[1].replaceAll("[A-J]", "")))
            };

        } catch (Exception e) {
            return null;
        }


    }

    private static boolean isLegalLocation(Coordinate[] coordinates) {
        return !(coordinates[0].row - coordinates[1].row != 0 &&
                coordinates[0].column - coordinates[1].column != 0);
    }

    private static boolean isLegalCoordinate(String coordinate) {
        if (!contains(rows, coordinate.charAt(0))) {
            return false;
        }

        if (coordinate.length() == 2) {
            int number = Character.getNumericValue(coordinate.charAt(1));
            return number > 0 && number < 10;
        } else if (coordinate.length() == 3) {
            return coordinate.endsWith("10");
        } else {
            return false;
        }
    }

    public static char[][] prepareBoard() {
        char[][] board = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = '~';
            }
        }
        return board;
    }

    public static void printBoard(char[][] board) {

        String columns = "  1 2 3 4 5 6 7 8 9 10";

        System.out.println(columns);
        for (int i = 0; i < board.length; i++) {
            System.out.print(rows[i]);
            for (int j = 0; j < board.length; j++) {
                System.out.print(" " + board[i][j]);
            }
            System.out.println();
        }
    }

    public static void printShipPlacementMessage(Ship ship) {
        System.out.printf("Enter the coordinates of the %s (%d cells): ",
                ship.shipName, ship.size);
    }

    public static boolean contains(char[] arr, char c) {
        for (char e :
                arr) {
            if (e == c) {
                return true;
            }
        }
        return false;
    }
}

enum Ship {
    AIRCRAFT_CARRIER(5),
    BATTLESHIP(4),
    SUBMARINE(3),
    CRUISER(3),
    DESTROYER(2);

    Ship(int size) {
        this.size = size;
    }

    final String shipName = this.name()
            .toUpperCase(Locale.ROOT)
            .replace('_', ' ');
    final int size;
}