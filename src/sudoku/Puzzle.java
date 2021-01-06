package sudoku;

import java.util.*;
import java.io.*;
import java.lang.*;

public class Puzzle {
    private int grid[][];

    Puzzle () {
        grid = new int[9][9];
    }

    Puzzle (int num) {
        grid = new int[num][num];
    }

    Puzzle (Puzzle puzz) {
        this.grid = new int[9][9];

        this.copy(puzz);
    }

    Puzzle (String name) {
        grid = new int[9][9];
        if (!readFile(name));
        // FIX
    }

    private boolean readFile (String name) {
        try {
            File file = new File(name);
            Scanner fsc = new Scanner(file);

            int i = 0;
            while (fsc.hasNextLine() && i < 9) {
                String line = fsc.nextLine();

                for (int j = 0; j < 9; j++) {
                    grid[i][j] = Integer.parseInt(line.substring(0, line.indexOf('.')));
                    // fix to deal with extra white space better
                    line = line.substring(line.indexOf('.') + 1);
                }
                i++;
            }

            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return false;
        }
    }

    void clear () {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = 0;
            }
        }
    }

    boolean isBlank () {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    void print () {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    System.out.print(". ");
                }
                else {
                    System.out.print(grid[i][j] + " ");
                }

                if (j == 2 || j == 5) {
                    System.out.print("| ");
                }
            }
            System.out.println();

            if (i == 2 || i == 5) {
                System.out.println("- - - + - - - + - - - ");
            }
        }
    }

    void putGrid (int x, int y, int n) {
        grid[x][y] = n;
    }

    int getGrid (int x, int y) {
        return grid[x][y];
    }

    boolean isFull () {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    boolean isSolved () {
        // check horizontals and verticals
        for (int i = 0; i < 9; i++) {
            int xs[] = new int[9], ys[] = new int[9];
            for (int j = 0; j < 9; j++) {
                if (xs[grid[i][j] - 1] == 1) {
                    return false;
                }
                else {
                    xs[grid[i][j] - 1] = 1;
                }
                if (ys[grid[j][i] - 1] == 1) {
                    return false;
                }
                else {
                    ys[grid[j][i] - 1] = 1;
                }
            }
        }

        // check boxes
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int nums[] = new int[9];

                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        if (nums[grid[3*i+x][3*j+y] - 1] == 1) {
                            return false;
                        }
                        else {
                            nums[grid[3*i+x][3*j+y] - 1] = 1;
                        }
                    }
                }
            }
        }

        return true;
    }

    void copy (Puzzle puzz) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.grid[i][j] = puzz.grid[i][j];
            }
        }
    }
}
