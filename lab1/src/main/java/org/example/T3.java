package org.example;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class T3 extends Thread {
    public T3(String name, int priority) {
        super(name);
        setPriority(priority);
    }

    @Override
    public void run() {
        System.out.println("Task T3 started.");
        int n = Lab1.N;

        // Локальні змінні
        int[][] MP = new int[n][n];
        int[][] MR = new int[n][n];
        int[] V = new int[n];

        // Введення даних
        if (n <= 4) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("T3: Введіть матрицю MP:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    MP[i][j] = getSafeInt(scanner);
                }
            }

            System.out.println("T3: Введіть матрицю MR:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    MR[i][j] = getSafeInt(scanner);
                }
            }

            System.out.println("T3: Введіть вектор V:");
            for (int i = 0; i < n; i++) {
                V[i] = getSafeInt(scanner);
            }
        } else {
            fillRandomMatrix(MP);
            fillRandomMatrix(MR);
            fillRandomVector(V);
        }

        // Обчислення F3
        // MP * MR -> TempMatrix
        int[][] tempMatrix = Data.matrixMultiply(MP, MR);

        // MAX(TempMatrix) -> ScalarMax
        int scalarMax = Data.matrixMax(tempMatrix);

        // ScalarMax * V -> O (вектор)
        int[] O = Data.vectorScalarMultiply(V, scalarMax);

        // Виведення результату
        if (n <= 4) {
            System.out.println("Результат O (F3): " + Arrays.toString(O));
        } else {
            System.out.println("Результат O (F3) обчислено.");
        }

        System.out.println("Task T3 finished.");
    }

    private int getSafeInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("T3: Помилка! Введено не число. Спробуйте ще раз:");
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
