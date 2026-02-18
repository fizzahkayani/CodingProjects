package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Hallway {
    private int length;
    boolean horizontal;
    int[] startPoint;
    int[] endPoint;
    TETile[][] board;
    boolean cornerIntersectGreater;
    boolean cornerIntersectLesser;


    public Hallway(boolean horizontal, int[] startPoint, TETile[][] board) {
        cornerIntersectGreater = false;
        cornerIntersectLesser = false;
        this.length = 240;
        this.horizontal = horizontal;
        this.startPoint = startPoint;
        this.board = board;
        if (horizontal) {
            endPoint = new int[]{startPoint[0] + length, startPoint[1]};
        } else {
            endPoint = new int[]{startPoint[0], startPoint[1] + length};
        }
        adjust(board);
    }

    private void adjust(TETile[][] board) {
        //check length for walls
        //if it is a wall, check if room edge, room corner, or hall edge
        //merge
        //if room corner, add additional walls
        TETile wall = Tileset.WALL;
        TETile floor = Tileset.FLOOR;

        if (horizontal) {
            if (startPoint[0] == board.length - 1) {
                length = 0;
                return;
            }
            if (board[1 + startPoint[0]][startPoint[1]].character() == floor.character()) {
                length = 1;
                return;
            }
            for (int x = 1; x < length; x++) {
                if (x + startPoint[0] == board.length - 1) {
                    length = 0;
                    return;
                }
                if (board[x + startPoint[0]][startPoint[1]].character() == wall.character()) {
                    length = x + 1;
                    cornerCheck(board, new int[]{x + startPoint[0], startPoint[1]});
                    return;
                }
            }
        } else {
            if (startPoint[1] == board[0].length - 1) {
                length = 0;
                return;
            }
            if (board[startPoint[0]][1 + startPoint[1]].character() == floor.character()) {
                length = 1;
                return;
            }
            for (int y = 1; y < length; y++) {
                if (y + startPoint[1] == board[0].length - 1) {
                    length = 0;
                    return;
                }
                if (board[startPoint[0]][y + startPoint[1]].character() == wall.character()) {
                    length = y + 1;
                    cornerCheck(board, new int[]{startPoint[0], y + startPoint[1]});
                    return;
                }
            }
        }
    }

    private void cornerCheck(TETile[][] board, int[] position) {
        TETile wall = Tileset.WALL;
        TETile floor = Tileset.FLOOR;
        if (horizontal) {
            if (board[position[0] + 1][position[1]].character() == wall.character()) {
                length++;
                if (board[position[0] + 1][position[1] + 1].character() == floor.character()) {
                    cornerIntersectGreater = true;
                } else {
                    cornerIntersectLesser = true;
                }
            }
        } else {
            if (board[position[0]][position[1] + 1].character() == wall.character()) {
                length++;
                if (board[position[0] + 1][position[1] + 1].character() == floor.character()) {
                    cornerIntersectGreater = true;
                } else {
                    cornerIntersectLesser = true;
                }
            }
        }
    }

    public void builder(TETile[][] board) {
        int startX = startPoint[0];
        int startY = startPoint[1];
        TETile floor = Tileset.FLOOR;

        // Build the hallway
        if (horizontal) {
            for (int x = 0; x < length; x++) {
                // Place floor for the hallway
                board[startX + x][startY] = Tileset.FLOOR;

                // Place walls above and below the hallway
                if (startY - 1 >= 0 && board[startX + x][startY - 1].character() != floor.character()) {
                    board[startX + x][startY - 1] = Tileset.WALL;
                }
                if (startY + 1 < board[0].length && board[startX + x][startY + 1].character() != floor.character()) {
                    board[startX + x][startY + 1] = Tileset.WALL;
                }
                if (cornerIntersectGreater && x == length - 1) {
                    board[startX + x][startY + 1] = Tileset.FLOOR;
                }
                if (cornerIntersectLesser && x == length - 1) {
                    board[startX + x][startY - 1] = Tileset.FLOOR;
                }
            }
        } else {
            for (int y = 0; y < length; y++) {
                // Place floor for the hallway
                board[startX][startY + y] = Tileset.FLOOR;

                // Place walls to the left and right of the hallway
                if (startX - 1 >= 0 && board[startX - 1][startY + y].character() != floor.character()) {
                    board[startX - 1][startY + y] = Tileset.WALL;
                }
                if (startX + 1 < board.length && board[startX + 1][startY + y].character() != floor.character()) {
                    board[startX + 1][startY + y] = Tileset.WALL;
                }
                if (cornerIntersectGreater && y == length - 1) {
                    board[startX + 1][startY + y] = Tileset.FLOOR;
                }
                if (cornerIntersectLesser && y == length - 1) {
                    board[startX - 1][startY + y] = Tileset.FLOOR;
                }
            }
        }
    }
}
