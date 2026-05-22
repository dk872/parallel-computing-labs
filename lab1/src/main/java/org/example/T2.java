package org.example;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class T2 extends Thread {
    public T2(String name, int priority) {
        super(name);
        setPriority(priority);
    }

    @Override
    public void run() {
        System.out.println("Task T2 started.");
        int n = Lab1.N;

        // Локальні змінні
        int[][] MG = new int[n][n];
        int[][] MH = new int[n][n];
        int[][] MK = new int[n][n];
        int[][] ML = new int[n][n];

        // Введення даних
        if (n <= 4) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("T2: Введіть матрицю MG:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    MG[i][j] = getSafeInt(scanner);
                }
            }

            System.out.println("T2: Введіть матрицю MH:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    MH[i][j] = getSafeInt(scanner);
                }
            }

            System.out.println("T2: Введіть матрицю MK:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    MK[i][j] = getSafeInt(scanner);
                }
            }

            System.out.println("T2: Введіть матрицю ML:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    ML[i][j] = getSafeInt(scanner);
                }
            }
        } else {
            fillRandomMatrix(MG);
            fillRandomMatrix(MH);
            fillRandomMatrix(MK);
            fillRandomMatrix(ML);
        }

        // Обчислення F2
        // MG * MH -> TempMatrix1
        int[][] tempMatrix1 = Data.matrixMultiply(MG, MH);

        // MK + ML -> TempMatrix2
        int[][] tempMatrix2 = Data.matrixAdd(MK, ML);

        // TempMatrix1 * TempMatrix2 -> MF
        int[][] MF = Data.matrixMultiply(tempMatrix1, tempMatrix2);

        // Виведення результату
        if (n <= 4) {
            System.out.println("Результат MF (F2):");
            for (int[] row : MF) {
                System.out.println(Arrays.toString(row));
            }
        } else {
            System.out.println("Результат MF (F2) обчислено.");
        }

        System.out.println("Task T2 finished.");
    }

    private int getSafeInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("T2: Помилка! Введено не число. Спробуйте ще раз:");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private void fillRandomMatrix(int[][] m) {
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++) {
                m[i][j] = ThreadLocalRandom.current().nextInt(-10, 10);
            }
    }
}