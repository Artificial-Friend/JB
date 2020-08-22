import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();
        int square = size * size;
        boolean answer = true;

        int[][] sudoku = new int[square][square];

        for (int i = 0; i < square; i++) {
            for (int b = 0; b < square; b++) {
                sudoku[i][b] = scanner.nextInt();
            }
        }

        for (int a = 0; a < square; a += size) {
            for (int b = 0; b < square; b += size) {

                for (int numberColumnCount = a; numberColumnCount < a + size; numberColumnCount++) {
                    for (int numberRowCount = b; numberRowCount < b + size; numberRowCount++) {

                        int number = sudoku[numberColumnCount][numberRowCount];
                        if (number > square) answer = false;

                        for (int iteratorColumn = a; iteratorColumn < a + size; iteratorColumn++) {
                            for (int iteratorRow = b; iteratorRow < b + size; iteratorRow++) {

                                if (numberColumnCount == iteratorColumn && numberRowCount == iteratorRow) {
                                    continue;
                                }

                                if (number == sudoku[iteratorColumn][iteratorRow]) {
                                    answer = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int a = 0; a < square; a++) {
            for (int b = 0; b < square; b++) {

                int num = sudoku[a][b];
                /*
                row
                 */
                for (int i = 0; i < square; i++) {
                    if (b == i) continue;

                    int temp = sudoku[a][i];
                    if (num == temp) {
                        answer = false;
                        break;
                    }
                }
                /*
                column
                 */
                for (int i = 0; i < square; i++) {
                    if (a == i) continue;
                    int temp = sudoku[i][b];
                    if (num == temp) {
                        answer = false;
                        break;
                    }
                }
            }
        }
        String result = answer ? "YES" : "NO";
        System.out.println(result);

    }
}