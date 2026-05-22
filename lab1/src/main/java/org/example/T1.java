package org.example;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class T1 extends Thread {
    public T1(String name, int priority) {
        super(name);
        setPriority(priority);
    }

    @Override
    public void run() {
        System.out.println("Task T1 started.");
        int n = Lab1.N;

        // Локальні змінні
        int[] A = new int[n];
        int[] B = new int[n];
        int[] C = new int[n];
        int[][] MA = new int[n][n];
        int[][] MD = new int[n][n];

        if (n <= 4) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("T1: Введіть вектор A:");
            for (int i = 0; i < n; i++) {
                A[i] = getSafeInt(scanner);
            }

            System.out.println("T1: Введіть вектор B:");
            for (int i = 0; i < n; i++) {
                B[i] = getSafeInt(scanner);
            }

            System.out.println("T1: Введіть вектор C:");
            for (int i = 0; i < n; i++) {
                C[i] = getSafeInt(scanner);
            }

            System.out.println("T1: Введіть матрицю MA:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    MA[i][j] = getSafeInt(scanner);
                }
            }

            System.out.println("T1: Введіть матрицю MD:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    MD[i][j] = getSafeInt(scanner);
                }
            }
        } else {
            // Генерація випадкових даних
            fillRandomVector(A);
            fillRandomVector(B);
            fillRandomVector(C);
            fillRandomMatrix(MA);
            fillRandomMatrix(MD);
        }

        // Обчислення F1
        // MA * MD -> TempMatrix
        int[][] tempMatrix = Data.matrixMultiply(MA, MD);

        // B * TempMatrix -> TempVector
        int[] tempVector = Data.vectorMatrixMultiply(B, tempMatrix);

        // C * TempVector -> Scalar1 (скалярний добуток)
        long scalar1 = Data.vectorDotProduct(C, tempVector);

        // A * B -> Scalar2 (скалярний добуток)
        long scalar2 = Data.vectorDotProduct(A, B);

        // d = Scalar2 + Scalar1
        long d = scalar2 + scalar1;

        // Виведення результату
        System.out.println("Результат d (F1) = " + d);

        System.out.println("Task T1 finished.");
    }


    private int getSafeInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("T1: Помилка! Введено не число. Спробуйте ще раз:");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private void fillRandomVector(int[] v) {
        for (int i = 0; i < v.length; i++) {
            v[i] = ThreadLocalRandom.current().nextInt(-10, 10);
        }
    }

    private void fillRandomMatrix(int[][] m) {
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++) {
                m[i][j] = ThreadLocalRandom.current().nextInt(-10, 10);
            }
    }
}
