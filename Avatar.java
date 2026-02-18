package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Avatar {
    TETile[][] worldState;
    static int[] currentPosition;
    int[] currentOrigin;
    TETile avatarTile = Tileset.AVATAR;

    public Avatar(World world) {
        worldState = world.currentState;
        currentPosition = new int[]{7, 7};
        avatarUpdate();
        currentOrigin = new int[]{0, 0};
    }

    public Avatar(World world, int[] startPosition) {
        worldState = world.currentState;
        currentPosition = startPosition;
        avatarUpdate();
        currentOrigin = new int[]{0, 0};
        originUpdate();
    }

    public void move(int value, boolean horizontal) {
        int[] newPosition = new int[]{currentPosition[0], currentPosition[1]};
        if (horizontal)  {
            newPosition[0] += value;
        } else {
            newPosition[1] += value;
        }
        if (wallCheck(newPosition)) {
            avatarRemove();
            currentPosition = newPosition;
            avatarUpdate();
            originUpdate();
        }
    }

    public TETile[][] visionBasic(int radius) {
        TETile[][] vision  = new TETile[120][60];
        fillNothing(vision);
        TETile[][] currentScreen = currentScreen();
        int xPos;
        int yPos;

        for (int y = -radius; y < radius; y++) {
            for (int x = -radius; x < radius; x++) {
                xPos = currentPosition[0] + x - currentOrigin[0];
                yPos = currentPosition[1] + y - currentOrigin[1];
                if (xPos > 0 && xPos < 120 && yPos > 0 && yPos < 60) {
                    vision[xPos][yPos] = currentScreen[xPos][yPos];
                }
            }
        }
        return vision;
    }

    public TETile[][] currentScreen() {
        TETile[][] currentScreen = new TETile[120][60];
        for (int x = 0; x < 120; x++) {
            for (int y = 0; y < 60; y++) {
                currentScreen[x][y] = worldState[x + currentOrigin[0]][y + currentOrigin[1]];
            }
        }
        return currentScreen;
    }

    private void avatarUpdate() {
        worldState[currentPosition[0]][currentPosition[1]] = avatarTile;
    }

    private void avatarRemove() {
        worldState[currentPosition[0]][currentPosition[1]] = Tileset.FLOOR;
    }

    private boolean wallCheck(int[] position) {
        TETile wall = Tileset.WALL;
        return worldState[position[0]][position[1]].character() != wall.character();
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

    private void originUpdate() {
        if (currentPosition[0] < 60) {
            currentOrigin[0] = 0;
        } else if (currentPosition[0] >= worldState.length - 60) {
            currentOrigin[0] = worldState.length - 120;
        } else {
            currentOrigin[0] = currentPosition[0] - 60;
        }

        if (currentPosition[1] < 30) {
            currentOrigin[1] = 0;
        } else if (currentPosition[1] >= worldState[0].length - 30) {
            currentOrigin[1] = worldState[0].length - 60;
        } else {
            currentOrigin[1] = currentPosition[1] - 30;
        }
    }
}
