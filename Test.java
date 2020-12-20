import java.lang.*;
import java.util.*;
import java.io.*;

public class Test {
    public static void main (String args[]) {
        for (int i=0; i<9; i++) {
            System.out.print(i);
        }

        /*
           String line = "1,2,3,4,5,6,7,8,9,";
           System.out.println(line.indexOf(','));

           int num = Integer.parseInt(line.substring(0, line.indexOf(',')));

           System.out.println("The integer is: " + num);
           */

        /*
        try {
            File file = new File("test-file.txt");
            Scanner fsc = new Scanner(file);
            int grid[][] = new int[9][9];

            int i = 0;
            while (fsc.hasNextLine() && i < 9) {
                String line = fsc.nextLine();

                for (int j = 0; j < 9; j++) {
                    System.out.print(line.substring(0, line.indexOf('.')) + "-");
                    // grid[i][j] = Integer.parseInt(line.substring(0, line.indexOf('.')));
                    // fix to deal with extra white space better
                    line = line.substring(line.indexOf('.') + 1);
                }
                System.out.println();
            }
        } catch (FileNotFoundException e) {}
        */
    }
}
