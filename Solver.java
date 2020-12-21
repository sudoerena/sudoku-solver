import java.util.*;
import java.io.*;
import java.lang.*;

public class Main {
    public static void main (String args[]) {
        Scanner sc = new Scanner(System.in);

        Solver slv = new Solver();
        slv.solve();
        slv.print();
    }
}

public class Solver {
    Puzzle puzz;
    //int invalid[][][];
    int invalid[][][] = new int[9][9][10];

    Solver () {
        //invalid = new int[9][9][10];

        Scanner sc = new Scanner(System.in);
        int in;

        while (true) {
            System.out.print("Enter 1 for file input, or 2 for manual input: ");
            in = sc.nextInt();
            sc.nextLine();
            if (in == 1 || in == 2) {
                break;
            }
            System.out.println("Please enter a valid choice.");
        }

        if (in == 1) {
            System.out.print("Please enter puzzle file name: ");
            puzz = new Puzzle(sc.nextLine());
        }
        else if (in == 2) {
            System.out.print("Puzzle size: ");
            puzz = new Puzzle(sc.nextInt());

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    System.out.printf("Enter #, or 0 for empty.\nR%d, C%d: ", i + 1, j + 1);
                    puzz.putGrid(i, j, sc.nextInt());
                }
            }
        }
    }

    Solver (Puzzle puzz) {
        this.puzz = puzz;
    }

    void print () {
        puzz.print();
    }

    // return false if failed because invalid
    boolean solve () {
        /*
         * v2:
         * IF BLANK:
         * 1. tryFill()
         *
         * ELSE:
         * 1. add self to every row, col, box
         */

        updateInvalid();

        int x = 0, y = 0, failed = 0;
        while (true) {
            // check full (solved)
            if (puzz.isFull()) {
                if (puzz.isSolved()) {
                    System.out.println("Solved it!");
                }
                break;
            }

            // move to next space
            if (x == 8) {
                x = 0;

                if (y == 8) {
                    y = 0;
                }
                else {
                    y++;
                }
            }
            else {
                x++;
            }

            // if blank cell
            if (puzz.getGrid(x, y) == 0) {
                // try to fill
                int success = tryFill(x, y);
                if (success == -1) {
                    failed++;

                    // unchanged for many steps; trial and error
                    if (failed >= 1000) {
                        failed = 0;
                        updateInvalid();

                        for (int i = 1; i < 10; i++) {
                            if (invalid[x][y][i] == 0) {
                                // new puzzle with random valid #
                                Puzzle tryPuzz = new Puzzle(puzz);
                                tryPuzz.putGrid(x, y, i);
                                Solver trySolve = new Solver(tryPuzz);

                                // if was invalid
                                if (!trySolve.solve()) {
                                    invalid[x][y][i] = 1;
                                }
                                else {
                                    //puzz.putGrid(x, y, i);
                                    puzz = tryPuzz;
                                    return true;
                                }

                                break;
                            }
                        }
                    }
                }
                else if (success == -2) {
                    System.out.println("Invalid result.");
                    return false;
                }
                else {
                    failed = 0;
                    updateInvalid(x, y);
                }
            }
            // if filled cell
            else {
                updateInvalid(x, y);
            }
        }

        return true;
    }

    void updateInvalid () {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                // mark horizontals and verticals
                for (int i = 0; i < 9; i++) {
                    invalid[x][i][puzz.getGrid(x, y)] = 1;
                    invalid[i][y][puzz.getGrid(x, y)] = 1;
                }

                // mark box
                int x0 = x / 3, y0 = y / 3;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        invalid[i+3*x0][j+3*y0][puzz.getGrid(x, y)] = 1;
                    }
                }
            }
        }
    }

    void updateInvalid (int x, int y) {
        // mark horizontals and verticals
        for (int i = 0; i < 9; i++) {
            invalid[x][i][puzz.getGrid(x, y)] = 1;
            invalid[i][y][puzz.getGrid(x, y)] = 1;
        }

        // mark box
        int x0 = x / 3, y0 = y / 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                invalid[i+3*x0][j+3*y0][puzz.getGrid(x, y)] = 1;
            }
        }
    }

    void printInvalid () {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                if (puzz.getGrid(i,j) == 0) {
                    System.out.printf("(%d, %d): ", i, j);
                    for (int k = 1; k < 10; k++) {
                        if (invalid[i][j][k] == 1) {
                            System.out.print(k);
                        }
                        else {
                            System.out.print(0);
                        }
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    int tryFill (int x, int y) {
        int count = 0, val = -1;

        // count valid numbers
        for (int i = 1; i < 10; i++) {
            if (invalid[x][y][i] == 0) {
                val = i;
                count++;
            }
        }

        // works
        if (count == 1) {
            puzz.putGrid(x, y, val);
            return 0;
        }

        // no valid choices
        if (count == 0) {
            return -2;
        }

        // more than one valid choice
        return -1;
    }
}

public class Puzzle{
    int grid[][];

    Puzzle () {
        grid = new int[9][9];
    }

    Puzzle (int num) {
        grid = new int[num][num];
    }

    Puzzle (Puzzle puzz) {
        this.grid = puzz.grid;
    }

    Puzzle (String name) {
        grid = new int[9][9];
        readFile(name);
    }

    private void readFile (String name) {
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
        } catch (FileNotFoundException e) {}
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
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
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
}
