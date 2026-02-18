package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class World {
    private static final int DEFAULT_WIDTH = 240;
    private static final int DEFAULT_HEIGHT = 120;

    private Random random;
    public TETile[][] currentState;
    private int width;
    private int height;

    public World(long seed) {
        random = new Random(seed);
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        currentState = new TETile[width][height];
        fillNothing(currentState);
        currentState = generate();
    }

    private void fillNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private int[] sizeGeneratorPreset(int num) {
        return switch (num) {
            case 1 -> new int[]{5, 11};
            case 2 -> new int[]{11, 5};
            case 3 -> new int[]{11, 11};
            case 4 -> new int[]{9, 13};
            case 5 -> new int[]{13, 9};
            case 6 -> new int[]{13, 13};
            default -> new int[]{5, 5};
        };
    }

    public TETile[][] generate() {
        int[] origin = new int[]{5, 5};
        Room hub = new Room(5, 5, origin);

        hub.builder(currentState);
        ArrayList<int[]> entranceList = new ArrayList<>(hub.entranceList);
        Set<int[]> entranceListCopy = new HashSet<>(hub.entranceList);
        ArrayList<Room> roomList = new ArrayList<>();
        int[] start;

        while (!entranceList.isEmpty()) {
            int[] roomDim = sizeGeneratorPreset(random.nextInt(7));
            //int[] entrance = entranceList.get(random.nextInt(entranceList.size()));
            int[] entrance = entranceList.getFirst();
            int length = random.nextInt(5, 16);
            if (entrance[2] == 0) {
                start = new int[]{entrance[0] + length - 1, entrance[1] - Math.floorDiv(roomDim[1], 2)};
            } else {
                start = new int[]{entrance[0] - Math.floorDiv(roomDim[0], 2), entrance[1] + length - 1};
            }

            Room newRoom = new Room(roomDim[0], roomDim[1], start);

            if (newRoom.spaceCheck(currentState)) {
                newRoom.builder(currentState);
                entranceList.addAll(newRoom.entranceList);
                entranceListCopy.addAll(newRoom.entranceList);
                roomList.add(newRoom);
            }
            entranceList.remove(entrance);
        }

        for (int[] entrance : entranceListCopy) {
            Hallway newHall = new Hallway(entrance[2] == 0, entrance, currentState);
            newHall.builder(currentState);
        }

        for (Room room : roomList) {
            if (!room.connected(currentState)) {
                room.remove(currentState);
            }
        }

        return currentState;
    }
}

