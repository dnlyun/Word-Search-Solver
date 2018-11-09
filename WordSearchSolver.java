/**
 * [WordSearchSolver.java]
 * Ask user for word search file and output a solved version in txt and html files
 *
 * @author Daniel Yun
 * @version %I%, %G%
 */
package wordsearchsolver;

//Imports
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class WordSearchSolver {

    //Class variable declaration
    private static int gridDim;
    private static String[][] grid;
    private static char[][] solvedGrid;
    private static ArrayList<String> words;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        while (true) {
            //Ask user for file name
            System.out.print("Please enter the file name: ");
            //If setPuzzle is true, the file entered is compatible and should try to solve it
            if (setPuzzle(new File(input.nextLine() + ".txt"))) { //No need to solve if file entered cannot be used
                solve();
                getSolvedGrid();
            }
        }
    }

    /**
     * setPuzzle
     * Reads a file entered by the user and store the list of words and puzzle grid separately
     * @param file the file containing the list of words and puzzle
     * @return true if file contains the list of words and the puzzle in correct format
     */
    private static boolean setPuzzle(File file) {
        try {
            Scanner reader = new Scanner(file);
            ArrayList<String> temp = new ArrayList<>();
            words = new ArrayList<>();

            while (reader.hasNext()) {
                String line = reader.nextLine();
                //If line does not contain spaces, it is a word, else it is a line of the puzzle
                if (!line.contains(" ")) {
                    words.add(line);
                } else {
                    temp.add(line);
                }
            }
            reader.close();

            gridDim = temp.size();
            grid = new String[gridDim][gridDim];

            for (int i = 0; i < gridDim; i++) {
                grid[i] = temp.get(i).split(" "); //Put each letter into a 2d array
            }
            return true; //File can be read and solved
        } catch (Exception e) {
            //File not found or in desired format
            System.out.println("Error: File cannot be used\n");
            return false; //File cannot be read or solved
        }
    }

    /**
     * solve
     * Traverses a puzzle grid and finds the coordinate and direction of the words from the list of words. After finding the correct word, it adds the word to a new grid
     * @see #checkDirection(String word, int y, int x)
     * @see #addSolvedWord(String word, int dir, int y, int x)
     */
    private static void solve() {
        solvedGrid = new char[gridDim][gridDim]; //Puzzle grid only with words from the list

        for (String word : words) {
            int x = 0, y = 0;
            boolean found = false;

            //Search for the beginning letter of the word and check all direction
            while (!found) {
                if (word.indexOf(grid[y][x]) == 0) {
                    int dir = checkDirection(word, y, x);
                    //If a direction at this location contains the word, add it to a new grid
                    if (dir != -1) {
                        addSolvedWord(word.toUpperCase(), y, x, dir);
                        found = true; //If found, break out of loop so it doesn't search for it again
                    }
                }
                if (x + 1 == gridDim) { //If x if about to go out of the grid
                    if (y + 1 == gridDim) {
                        words.remove(word); //Remove word from list of words since it is not found in the puzzle
                        found = true; //Set true so it doesn't keep trying to find the word
                    } else {
                        x = 0; //Reset x
                        y++; //Start on a new row
                    }
                } else {
                    x++; //Check the letter on the right
                }
            }
        }
    }

    /**
     * checkDirection
     * Checks if any of the 8 directions contains the matching word. Must assume a single word appears only once in a puzzle
     * @param word the word to look for
     * @param y the y coordinate of the letter in the 2d array
     * @param x the x coordinate of the letter in the 2d array
     * @return a number from 0 to 7 that represents the direction of the word, only if there is a matching word in the first place, else -1
     * <br>0 - Up
     * <br>1 - Down
     * <br>2 - Left
     * <br>3 - Right
     * <br>4 - Up left
     * <br>5 - Up right
     * <br>6 - Down left
     * <br>7 - Down right
     * @see #searchWord(String word, int wordLength, int y, int x, int ydir, int xdir)
     */
    private static int checkDirection(String word, int y, int x) {
        boolean[] correctWord = new boolean[8];

        correctWord[0] = searchWord(word, 0, y, x, -1, 0);
        correctWord[1] = searchWord(word, 0, y, x, 1, 0);
        correctWord[2] = searchWord(word, 0, y, x, 0, -1);
        correctWord[3] = searchWord(word, 0, y, x, 0, 1);
        correctWord[4] = searchWord(word, 0, y, x, -1, -1);
        correctWord[5] = searchWord(word, 0, y, x, -1, 1);
        correctWord[6] = searchWord(word, 0, y, x, 1, -1);
        correctWord[7] = searchWord(word, 0, y, x, 1, 1);

        //If any direction contains the word, return the direction
        for (int i = 0; i < 8; i++) {
            if (correctWord[i]) {
                return i;
            }
        }
        return -1; //No direction has the word
    }

    /**
     * searchWord
     * Checks the letters around a coordinate and checks to see if it matches the given word
     * @param word the word to look for
     * @param wordLength the max distance of a letter to check from the given location
     * @param y the y coordinate of the letter in the 2d array
     * @param x the x coordinate of the letter in the 2d array
     * @param ydir states whether to go in the upward or downward direction
     * @param xdir states whether to go in the leftward or rightward direction
     * @return true if a word matches the given word about the given coordinate
     */
    private static boolean searchWord(String word, int wordLength, int y, int x, int ydir, int xdir) {
        //Check conditions only if number of letters checked is less than length of word
        if (wordLength < word.length()) {
            //If word goes out of bounds
            if ((y < 0) || (y > gridDim - 1) || (x < 0) || (x > gridDim - 1)) {
                return false;
            } else if (word.charAt(wordLength) != grid[y][x].charAt(0)) { //If the word's position does not contain this letter
                return false;
            }
            return searchWord(word, wordLength + 1, y + ydir, x + xdir, ydir, xdir); //Search the next letter
        }
        return true; //Return true if word is found
    }

    /**
     * addSolvedWord
     * Adds a word about the coordinate in the given direction
     * @param word the word to add
     * @param y the y coordinate of the letter in the 2d array
     * @param x the x coordinate of the letter in the 2d array
     * @param dir states which direction to add the word
     */
    private static void addSolvedWord(String word, int y, int x, int dir) {
        switch(dir) {
            case 0:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y - i][x] = word.charAt(i);
                }
                break;
            case 1:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y + i][x] = word.charAt(i);
                }
                break;
            case 2:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y][x - i] = word.charAt(i);
                }
                break;
            case 3:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y][x + i] = word.charAt(i);
                }
                break;
            case 4:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y - i][x - i] = word.charAt(i);
                }
                break;
            case 5:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y - i][x + i] = word.charAt(i);
                }
                break;
            case 6:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y + i][x - i] = word.charAt(i);
                }
                break;
            case 7:
                for (int i = 0; i < word.length(); i++) {
                    solvedGrid[y + i][x + i] = word.charAt(i);
                }
                break;
            default:
                break;
        }
    }

    /**
     * getSolvedGrid
     * Prints the solved puzzle to an html file
     */
    private static void getSolvedGrid() {
        try {
            //Create files and printwriter
            File txtFile = new File("Solved Word Search.txt");
            File htmlFile = new File ("Answer Print Out.html");
            PrintWriter writer = new PrintWriter(txtFile);

            //Output to text file the list of words and solved puzzle
            for (String word : words) {
                writer.println(word);
            }
            for (int i = 0; i < gridDim; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < gridDim; j++) {
                    if (solvedGrid[i][j] == 0) {
                        line.append(grid[i][j].charAt(0));
                    } else {
                        line.append(solvedGrid[i][j]);
                    }
                }
                //Between each character add a space
                writer.println(line.toString().replace("", " ").trim());
            }
            writer.close();

            writer = new PrintWriter(htmlFile);

            //Format html file for user to print out
            writer.println("<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<title>Word Search Answer</title>" +
                    "<h1><font size = +5>Word Search Answer</font></h1>" +
                    "<style>" +
                    "table { border-collapse: collapse; }" +
                    "td, th, tr { border: 1px solid black; text-align: center; width: 40px; table-layout: fixed; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<font size = +2>" +
                    "<table>");

            //Put the puzzle grid into a table
            for (int i = 0; i < gridDim; i++) {
                writer.println("<tr>");
                for (int j = 0; j < gridDim; j++) {
                    if (solvedGrid[i][j] == 0) {
                        writer.println("<td>" + grid[i][j].charAt(0) + "</td>");
                    } else {
                        writer.println("<th>" + solvedGrid[i][j] + "</th>");
                    }
                }
                writer.println("</tr>");
            }
            writer.println("</table>" +
                    "<p>" +
                    "Words to find:<br>");

            //Put the list of words beside the puzzle grid
            for (String word : words) {
                writer.println("<br>" + word);
            }
            writer.println("</p>" +
                    "</font>" +
                    "</body>" +
                    "</html>");
            writer.close();

            System.out.println("Please check project folder to see the solved puzzle\n");
        } catch (Exception e) {
            //Files with the same name and extension might already exist and can't write over it
            System.out.println("Error: Unable to create new files\n");
        }
    }
}