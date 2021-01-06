package sudoku;

import java.util.*;
import java.io.*;
import java.lang.*;

public class Solver {
    Puzzle puzz;
    //int invalid[][][];
    int invalid[][][] = new int[9][9][10];

    public static void main (String args[]) {
        if (args.length == 1) {
            Puzzle puzz = new Puzzle(args[0]);
            Solver slv = new Solver(puzz);

            slv.solve();
            slv.print();
        }
        else {
            Scanner sc = new Scanner(System.in);
            Solver slv = new Solver();
            System.out.println("here");
            slv.solve();
            slv.print();
        }
    }

    Solver () {
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

            System.out.println("Enter number, or 0 for empty.");
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    System.out.printf("R%d, C%d: ", i + 1, j + 1);
                    puzz.putGrid(i, j, sc.nextInt());
                }
            }
            System.out.println();
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
         * 1. tryCell()
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
                    return true;
                }
                else {
                    System.out.println("No solution found.");
                    return false;
                }
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

            // if reached blank cell
            if (puzz.getGrid(x, y) == 0) {
                // try to fill
                int success = tryCell(x, y);

                // cannot determine value
                if (success == -1) {
                    failed++;

                    // unchanged for many steps; trial and error
                    if (failed >= 1000) {
                        failed = 0;

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
                                    puzz.copy(tryPuzz);
                                    return true;
                                }

                                break;
                            }
                        }
                    }
                }
                // invalid; abort
                else if (success == -2) {
                    System.out.println("Invalid puzzle.");
                    return false;
                }
                // sucessfully filled
                else {
                    failed = 0;
                    updateInvalid(x, y);

                    for (int n = 1; n < 10; n++) {
                        invalid[x][y][n] = 1;
                    }
                }
            }
            // if reached nonempty cell
            else {
                failed = 0;
                updateInvalid(x, y);
            }

            // just entered new square
            if (x%3 == 0 && y%3 == 0) {
                trySquare(x, y);
            }

            // just entered new row (always)
            tryRow(y);

            // just entered new col
            if (y == 0) {
                tryCol(x);
            }
        }

        //return true;
    }

    // mark for each box:
    //      column, row, square
    void updateInvalid () {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (puzz.getGrid(x, y) != 0) {
                    updateInvalid(x, y);
                }
            }
        }

        return;
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

        // if (x,y) is nonempty, all is invalid
        if (puzz.getGrid(x, y) != 0) {
            for (int n = 0; n < 10; n++) {
                invalid[x][y][n] = 1;
            }
        }

        return;
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

    void checkInvalid () {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                checkInvalid(x,y);
            }
        }

        return;
    }

    void checkInvalid (int x, int y) {
        boolean oop = true;

        for (int n = 1; n < 10; n++) {
            System.out.print(n + ": ");
            if (invalid[x][y][n] == 1) {
                System.out.print("(inv) ");
                oop = false;
                for (int i = 0; i < 9; i++) {
                    if (puzz.getGrid(x,i) == n) {
                        oop = true;
                    }
                    if (puzz.getGrid(i,y) == n) {
                        oop = true;
                    }
                }

                int x0 = x/3, y0 = y/3;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (puzz.getGrid(3*x0+i, 3*y0+j) == n) {
                            oop = true;
                        }
                    }
                }

                if (puzz.getGrid(x,y) != 0) {
                    oop = true;
                }
            }
            if (!oop) {
                System.out.println(x + ", " + y + ": " + puzz.getGrid(x,y) + " with invalid " + n + "; wrong");
                for (int i = 0; i < 9; i++) {
                    System.out.print(puzz.getGrid(x,i));
                }
                System.out.println();
                for (int i = 0; i < 9; i++) {
                    System.out.print(puzz.getGrid(i,y));
                }
                System.out.println();
                int x0 = x/3, y0 = y/3;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        System.out.print(puzz.getGrid(3*x0+i, 3*y0+j));
                    }
                }
                System.out.println();
            }
            else {
                System.out.println("invalid is fine");
            }
        }

        return;
    }

    // fills puzz.grid[x][y] IF only one valid option exists there
    //      otherwise,
    //      if more exist, return -1
    //      if none exist, return -2
    int tryCell (int x, int y) {
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

    // fills square around puzz.grid[x][y] 
    // based on if only one cell can have n \in {1, 2, ... 9}
    //      return 0 if succeeded
    //      return -1 if none found
    //  OR return how many were filled
    int trySquare (int x, int y) {
        int x0 = x / 3, y0 = y / 3, valid[][] = new int[9][3], status = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int n = 0; n < 9; n++) {
                    if (invalid[i+3*x0][j+3*y0][n + 1] == 0) {
                        valid[n][0]++;
                        valid[n][1] = i + 3*x0;
                        valid[n][2] = j + 3*y0;
                    }
                }
            }
        }

        // check for numbers where only one space is valid
        for (int n = 0; n < 9; n++) {
            if (valid[n][0] == 1 && puzz.getGrid(valid[n][1], valid[n][2]) == 0) {
                puzz.putGrid(valid[n][1], valid[n][2], n + 1);
                status = 0;
            }
        }

        return status;
    }

    // fills puzz.grid[x][n]  for all 0 < n < 10
    // based on if only one cell can have n \in {1, 2, ... 9}
    //      return 0 if succeeded
    //      return -1 if none found
    //  OR return how many were filled
    int tryCol (int x) {
        int valid[][] = new int[9][2], status = -1;

        for (int j = 0; j < 9; j++) {
            for (int n = 0; n < 9; n++) {
                if (invalid[x][j][n + 1] == 0) {
                    valid[n][0]++;
                    valid[n][1] = j;
                }
            }
        }

        // check for numbers where only one space is valid
        for (int n = 0; n < 9; n++) {
            if (valid[n][0] == 1) {
                puzz.putGrid(x, valid[n][1], n + 1);
                status = 0;
            }
        }

        return status;
    }

    // fills puzz.grid[n][y]  for all 0 < n < 10
    // based on if only one cell can have n \in {1, 2, ... 9}
    //      return 0 if succeeded
    //      return -1 if none found
    //  OR return how many were filled
    int tryRow (int y) {
        int valid[][] = new int[9][2], status = -1;

        for (int i = 0; i < 9; i++) {
            for (int n = 0; n < 9; n++) {
                if (invalid[i][y][n + 1] == 0) {
                    valid[n][0]++;
                    valid[n][1] = i;
                }
            }
        }

        // check for numbers where only one space is valid
        for (int n = 0; n < 9; n++) {
            if (valid[n][0] == 1) {
                puzz.putGrid(valid[n][1], y, n + 1);
                status = 0;
            }
        }

        return status;
    }
}
