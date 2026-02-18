package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;

public class Room {
    private int height;
    private int width;

    public ArrayList<int[]> entranceList;
    private int[] startPoint;

    public Room(int width, int height, int[] startPoint) {
        this.height = height;
        this.width = width;
        this.startPoint = startPoint;
        entranceList = new ArrayList<>();
        int midX = Math.floorDiv(width, 2);
        int midY = Math.floorDiv(height, 2);
        entranceList.add(new int[]{width - 1 + startPoint[0], midY + startPoint[1], 0});
        entranceList.add(new int[]{midX + startPoint[0], height - 1 + startPoint[1], 1});
    }

    public boolean spaceCheck(TETile[][] board) {
        int startX = startPoint[0];
        int startY = startPoint[1];
        TETile nothing = Tileset.NOTHING;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (startX + x >= board.length || startY + y >= board[0].length
                    || startX < 0 || startY < 0) {
                    return false;
                }
                if (board[x + startX][y + startY].character() != nothing.character()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void builder(TETile[][] board) {

        int startX = startPoint[0];
        int startY = startPoint[1];

        // Build the walls and floor of the room
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Determine if the current tile is a wall or floor
                if (x == 0 || x == width - 1) {
                    board[startX + x][startY + y] = Tileset.WALL;
                } else if (y == 0 || y == height - 1) {
                    board[startX + x][startY + y] = Tileset.WALL;
                } else {
                    // Place floor inside the room
                    board[startX + x][startY + y] = Tileset.FLOOR;
                }
            }
        }
    }

    public boolean connected(TETile[][] board) {
        TETile floor = Tileset.FLOOR;
        for (int x = 0; x < width; x++) {
            if (board[startPoint[0] + x][startPoint[1]].character() == floor.character()) {
                return true;
            } else if (board[startPoint[0] + x][startPoint[1] + height - 1].character() == floor.character()) {
                return true;
            }
        }

        for (int y = 0; y < height; y++) {
            if (board[startPoint[0]][startPoint[1] + y].character() == floor.character()) {
                return true;
            } else if (board[startPoint[0] + width - 1][startPoint[1] + y].character() == floor.character()) {
                return true;
            }
        }
        return false;
    }

    public void remove(TETile[][] board) {
        int startX = startPoint[0];
        int startY = startPoint[1];
        TETile floor = Tileset.FLOOR;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                board[startX + x][startY + y] = Tileset.NOTHING;
                if (x == 0 && startX - 1 >= 0 && board[startX + x - 1][startY + y].character() == floor.character()) {
                    board[startX + x][startY + y] = Tileset.WALL;
                }
                if (x == width - 1 && startX + x + 1 < board.length && board[startX + x + 1][startY + y].character()
                        == floor.character()) {
                    board[startX + x][startY + y] = Tileset.WALL;
                }
                if (y == 0 && startY - 1 >= 0 && board[startX + x][startY + y - 1].character() == floor.character()) {
                    board[startX + x][startY + y] = Tileset.WALL;
                }
                if (y == height - 1 && startY + y + 1 < board[0].length && board[startX + x][startY + y + 1].character()
                        == floor.character()) {
                    board[startX + x][startY + y] = Tileset.WALL;
                }
            }
        }
    }
}
