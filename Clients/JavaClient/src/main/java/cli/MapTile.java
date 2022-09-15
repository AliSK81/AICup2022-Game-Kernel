package cli;

public class MapTile {
    public MapType type;
    public int data;
    public Point coordinates;

    @Override
    public String toString() {
        return "\n\t\t" +
                coordinates +
                ", " + data +
                ", " + type;
    }
}
