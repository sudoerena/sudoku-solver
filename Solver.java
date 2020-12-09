import java.util.*;
import java.io.*;
import java.lang.*;

public class Main {
    public static void main (String args[]) {
        Scanner sc = new Scanner(System.in);

        Solver slv = new Solver();
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

        return puzz;
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
}
