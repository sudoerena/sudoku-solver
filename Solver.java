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
    int invalid[][][] = new int[9][9][10];
    Solver () {
        Scanner sc = new Scanner(System.in);
        int in;

        while (true) {
            System.out.print("Enter 0 for file input, or 1 for manual input: ");
            in = sc.nextInt();
            if (in == 0 || in == 1) {
                break;
            }
            System.out.println("Please enter a valid choice.");
        }

        if (in == 0) {
            System.out.print("Please enter puzzle file name: ");
            puzz = new Puzzle(sc.nextLine());
        }
        else if (in == 1) {
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

    Solver (String name) {
        puzz = new Puzzle(name);
    }

    Solver (Puzzle puzz) {
        this.puzz = puzz;
    }

    boolean solve () {
        // SOLVE puzzle and return value
        /*
         * 1. search for blank
         * 2. check lines
         * 3. check square
         * 4. if only one valid, EDIT
         *      else, return to 1.
         *  If repeats ~80 times with no change,
         *      NEW puzzle
         *      put down random valid number and solve for that
         *      need invalid checker
         *      --> return all blank if invalid?
         *  If invalid, set that num to invalid
         */
        int x = 0, y = 0, inv[], failed = 0;

        while (true) {
            // check full (solved)
            if (puzz.isFull()) {
                break;
            }

            // get next blank space
            int temp = x;
            x = nextX(x, y);
            y = nextY(temp, y);
            inv = invalid[x][y];

            // check horizontal
            for (int j = 0; j < 9; j++) {
                inv[puzz.getGrid(x, j)] = 1;
            }

            // check vertical
            for (int i = 0; i < 9; i++) {
                inv[puzz.getGrid(i, y)] = 1;
            }

            // check square
            int x0 = 3*(x/3), y0 = 3*(y/3);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    inv[puzz.getGrid(i + x0, j + y0)] = 1;
                }
            }

            // try to fill
            int success = tryFill(x, y, inv);
            if (success == -1) {
                failed++;
            }
            else if (success == -2) {
                puzz.clear();
                break;
            }

            // unchanged for many steps; trial and error
            if (failed == 80) {
                failed = 0;

                int tryNum = 0;

                for (int i = 1; i < 10; i++) {
                    if (inv[i] == 0) {
                        tryNum = i;
                        break;
                    }
                }

                // new puzzle with random valid #
                Puzzle tryPuzz = puzz;
                tryPuzz.putGrid(x, y, tryNum);
                Solver trySolve = new Solver(tryPuzz);

                // if was invalid
                if (trySolve.solve()) {
                    inv[tryNum] = 1;
                }
                else {
                    puzz.putGrid(x, y, tryNum);
                }
            }

            // update invalid
            invalid[x][y] = inv;
        }

        return (puzz.getGrid(0, 0) == 0);
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

    int tryFill (int x, int y, int in[]) {
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
            return -1;
        }

        // no valid choices
        if (count == 0) {
            return -2;
        }

        puzz.putGrid(x, y, val);
        return 0;
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

    Puzzle (int num) {
        grid = new int[num][num];
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
