package Board;

import java.util.Objects;

public class Location {
    public int row;
    public int column;

    public Location() {  //also  represents an invalid location
        row = -1;
        column = -1;
    }

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public boolean isValid(int boardLength) {  //checking based on board length
        if(row < 0 || row >= boardLength)
            return false;
        if(column < 0 || column >= boardLength)
            return false;
        return true;
    }

    /*public boolean isInvalid() {  //checking not considering board length
        return row < 0 || column < 0;
    }*/

    public boolean isEven() {
        return (row+column) % 2 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return row == location.row &&
                column == location.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "Location{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
