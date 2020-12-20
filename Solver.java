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
    Solver () {
        Scanner sc = new Scanner(System.in);

        System.out.print("Please enter puzzle file name: ");
        puzz = new Puzzle(sc.nextLine());
    }

    Solver (String name) {
        puzz = new Puzzle(name);
    }

    Solver (Puzzle puzz) {
        this.puzz = puzz;
    }

    Puzzle solve () {
        // SOLVE puzzle and return value
        /*
         * 1. search for blank
         * 2. check lines
         * 3. check square
         * 4. if only one valid, EDIT
         *      else, return to 1.
         */
        int x = 0, y = 0;

        while (true) {
            int invalid[] = new int[10];

            // check full (solved)
            if (puzz.isFull()) {
                break;
            }

            // get next blank space
            int temp = x;
            x = nextX(x, y);
            y = nextY(temp, y);

            // check horizontal
            for (int j = 0; j < 9; j++) {
                invalid[puzz.getGrid(x, j)] = 1;
            }

            // check vertical
            for (int i = 0; i < 9; i++) {
                invalid[puzz.getGrid(i, y)] = 1;
            }

            // check square
            int x0 = 3*(x/3), y0 = 3*(y/3);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    invalid[puzz.getGrid(i + x0, j + y0)] = 1;
                }
            }

            // try to fill
            tryFill(x, y, invalid);
        }

        return puzz;
    }

    int nextX (int x, int y) {
        do {
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
        } while (puzz.getGrid(x, y) != 0);

        return x;
    }

    int nextY (int x, int y) {
        do {
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
        } while (puzz.getGrid(x, y) != 0);

        return y;
    }

    void tryFill (int x, int y, int in[]) {
        int count = 0, val = 0;

        // count valid numbers
        for (int i = 1; i < 10; i++) {
            if (in[i] == 0) {
                val = i;
                count++;
            }
        }

        // more than one valid choice
        if (count > 1) {
            return;
        }

        puzz.putGrid(x, y, val);
        return;
    }

    void print () {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(puzz.grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}

public class Puzzle{
    int grid[][];

    Puzzle () {
        grid = new int[9][9];
    }

    /* for manual input, double nested loop
       Puzzle (int num) {
       grid = neq int[9][9];
       }
       */

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

    void putGrid (int x, int y, int n) {
        grid[x][y] = n;
        return;
    }

    int getGrid (int x, int y) {
        return this.grid[x][y];
    }

    boolean isFull () {
        boolean full = true;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    full = false;
                    break;
                }
            }
        }

        return full;
    }
}
