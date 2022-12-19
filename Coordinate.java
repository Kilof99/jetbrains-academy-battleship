package battleship;

import java.util.Objects;

public class Coordinate {
    public Coordinate(char row, int column) {
        this.column = column - 1;
        this.row = row - 65;
    }

    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    int row;
    int column;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
