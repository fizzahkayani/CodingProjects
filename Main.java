package core;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Font;
import tileengine.*;

import java.io.*;
import java.util.Arrays;


public class Main {

    private static final String SAVE_FILE = "save.txt";
    private static boolean isColonPressed = false;

    private static TETile[][] view(int type, Avatar avatar) {
        if (type == 0) {
            return avatar.visionBasic(5);
        }
        return avatar.currentScreen();
    }

    public static void main(String[] args) throws IOException {
        int width = 800;
        int height = 600;
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        // Set the font for the menu
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);

        // Draw the main menu
        drawMainMenu();

        // Wait for user input
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                switch (Character.toLowerCase(choice)) {
                    case 'n':
                        enterSeedScreen();
                        return;
                    case 'l':
                        loadGame();
                        return;
                    case 'q':
                        System.exit(0);
                        return;
                    default:
                        break;
                }
            }
        }
    }

    private static void drawMainMenu() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(400, 450, "CS 61B: The Game");
        StdDraw.text(400, 350, "N - New Game");
        StdDraw.text(400, 300, "L - Load Game");
        StdDraw.text(400, 250, "Q - Quit");
        StdDraw.show();
    }

    private static void enterSeedScreen() throws IOException {
        StringBuilder seedInput = new StringBuilder();
        boolean enteringSeed = true;
        StdDraw.clear(StdDraw.BLACK);

        while (enteringSeed) {
            StdDraw.text(400, 450, "Enter Seed followed by S");
            StdDraw.text(400, 400, "Seed so far: " + seedInput);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 's' || key == 'S') {
                    enteringSeed = false;
                } else if (Character.isDigit(key)) {
                    seedInput.append(key);
                    StdDraw.clear(StdDraw.BLACK);
                }
            }
        }

        long seed = Long.parseLong(seedInput.toString());
        startNewGame(seed);
    }

    private static void startNewGame(long seed) throws IOException {
        TERenderer ter = new TERenderer();
        ter.initialize(120, 60);
        World world = new World(seed);
        Avatar avatar = new Avatar(world);
        int viewType = 0;



        ter.renderFrame(view(viewType, avatar));

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                if (isColonPressed) {
                    if (choice == 'q' || choice == 'Q') {
                        saveGame(seed, world, avatar);
                        System.exit(0);
                    }
                } else if (choice == ':') {
                    isColonPressed = true;
                } else {
                    switch (Character.toLowerCase(choice)) {
                        case 'w':
                            avatar.move(1, false);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 'a':
                            avatar.move(-1, true);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 's':
                            avatar.move(-1, false);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 'd':
                            avatar.move(1, true);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 'v':
                            if (viewType == 0) {
                                viewType = 1;

                            } else {
                                viewType = 0;
                            }
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private static void saveGame(long seed, World world, Avatar avatar) {
        StringBuilder gameState = new StringBuilder();
        gameState.append("Seed: ").append(seed).append("\n");
        gameState.append("Avatar: ").append(Arrays.toString(Avatar.currentPosition)).append("\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write(gameState.toString());
            System.out.println("Game saved successfully to " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("Error saving the game: " + e.getMessage());
        }
    }


    public static void loadGame() throws IOException {
        long seed = 0;
        int[] startPosition = null;
        World world = null;
        Avatar avatar = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;

            // Parse the seed
            line = reader.readLine();
            if (line != null && line.startsWith("Seed: ")) {
                seed = Long.parseLong(line.substring(6).trim()); // Extract the seed value
                world = new World(seed); // Initialize the world with the seed
            }

            // Parse the avatar position
            line = reader.readLine();
            if (line != null && line.startsWith("Avatar: ")) {
                if (world == null) {
                    throw new IllegalStateException("World must be initialized before the avatar.");
                }

                // Extract and clean the position data
                String positionData = line.substring(9).trim(); // Remove prefix and trim whitespace
                positionData = positionData.replaceAll("\\[|\\]", ""); // Remove square brackets
                String[] positionParts = positionData.split(","); // Split by commas

                // Convert position data into an int array
                startPosition = new int[positionParts.length];
                for (int i = 0; i < positionParts.length; i++) {
                    startPosition[i] = Integer.parseInt(positionParts[i].trim());
                }

                // Initialize the avatar with the world and start position
                avatar = new Avatar(world, startPosition);
                startLoadedGame(seed, world, avatar);
            }
        }

        // Output or further use of the parsed data
        if (world != null && avatar != null) {
            System.out.println("Game loaded successfully!");
            System.out.println("Seed: " + seed);
            System.out.println("Avatar: " + avatar);
        } else {
            System.exit(0);
            throw new IOException("Failed to load game state: Incomplete or corrupted save file.");
        }
    }

    private static void startLoadedGame(long seed, World world, Avatar avatar) throws IOException {
        TERenderer ter = new TERenderer();
        ter.initialize(120, 60);
        ter.renderFrame(avatar.visionBasic(5));
        int viewType = 0;

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                if (isColonPressed) {
                    if (choice == 'q' || choice == 'Q') {
                        saveGame(seed, world, avatar);
                        System.exit(0);
                    }
                } else if (choice == ':') {
                    isColonPressed = true;
                } else {
                    switch (Character.toLowerCase(choice)) {
                        case 'w':
                            avatar.move(1, false);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 'a':
                            avatar.move(-1, true);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 's':
                            avatar.move(-1, false);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 'd':
                            avatar.move(1, true);
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        case 'v':
                            if (viewType == 0) {
                                viewType = 1;

                            } else {
                                viewType = 0;
                            }
                            ter.renderFrame(view(viewType, avatar));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
