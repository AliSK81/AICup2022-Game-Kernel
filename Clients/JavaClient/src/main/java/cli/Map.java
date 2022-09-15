package cli;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class Map {
    public int width, height;
    public int goldCount;
    public int sightRange;
    public Vector<MapTile> grid;

    @Override
    public String toString() {

        List<MapTile> grid = this.grid.stream()
                .filter(tile -> tile.type != MapType.OUT_OF_SIGHT && tile.type != MapType.OUT_OF_MAP)
                .sorted(Comparator.comparingInt((MapTile t) -> t.coordinates.x).thenComparing((t -> t.coordinates.y)))
                .toList();

        return "\n\tMap{" +
                "width=" + width +
                ", height=" + height +
                ", gold=" + goldCount +
                ", vision=" + sightRange +
                grid +
                "\n\t}\n";
    }
}
