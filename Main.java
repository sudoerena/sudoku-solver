import java.util.*;
import java.io.*;
import java.lang.*;

public class Main {
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
}
