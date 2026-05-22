package org.example;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Клас Data
 * Містить спільні ресурси (пам'ять) та засоби синхронізації, доступні всім потокам.
 */
public class Data {
    // Параметри обчислювальної системи
    public static final int N = 4;    // Розмірність векторів і матриць
    public static final int P = 4;       // Кількість процесорів (потоків)
    public static final int H = N / P;   // Розмір смуги (кількість рядків на потік)

    // Спільні ресурси (вхідні дані)
    public static int[][] MA = new int[N][N];       // Матриця MA
    public static int[][] MB = new int[N][N];       // Матриця MB
    public static int[][] MD = new int[N][N];       // Матриця MD
    public static int[] C = new int[N];             // Вектор C
    public static int[] E = new int[N];             // Вектор E
    public static int d = 0;                        // Скаляр d

    // Спільний ресурс (результат)
    public static int[] W = new int[N];       // Результуючий вектор W

    // Глобальний ресурс для редукції
    // AtomicInteger дозволяє виконувати операції запису без явного блокування
    public static AtomicInteger a = new AtomicInteger(Integer.MIN_VALUE);

    // Засоби синхронізації
    // Бар'єр B1: синхронізація завершення введення даних усіма потоками
    public static CyclicBarrier B1 = new CyclicBarrier(4);

    // Семафор S1: захист критичної ділянки КД1 (копіювання d)
    public static Semaphore S1 = new Semaphore(1);

    // Бар'єр B2: синхронізація завершення обчислення редукції a всіма потоками
    public static CyclicBarrier B2 = new CyclicBarrier(4);

    // Критична секція CS1_Lock: об'єкт для блокування synchronized-блоку (КД3 - копіювання a)
    public static final Object CS1_Lock = new Object();

    // Сигнальні семафори для сповіщення потоку T3 про завершення обчислень
    public static Semaphore S2 = new Semaphore(0); // Сигнал від T1 до T3
    public static Semaphore S3 = new Semaphore(0); // Сигнал від T2 до T3
    public static Semaphore S4 = new Semaphore(0); // Сигнал від T4 до T3

    // Допоміжні методи
    private static final Random random = new Random();

    /**
     * Заповнення матриці випадковими числами в діапазоні [-10; 10].
     */
    public static void fillMatrixRandom(int[][] M) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                M[i][j] = random.nextInt(21) - 10;
            }
        }
    }

    /**
     * Заповнення вектора випадковими числами в діапазоні [-10; 10].
     */
    public static void fillVectorRandom(int[] V) {
        for (int i = 0; i < V.length; i++) {
            V[i] = random.nextInt(21) - 10;
        }
    }

    /**
     * Генерація випадкового скаляра в діапазоні [-10; 10].
     */
    public static int getRandomScalar() {
        return random.nextInt(21) - 10;
    }

    // Методи синхронізованого виведення в консоль
    public static synchronized void printVector(String name, int[] V) {
        if (N <= 20) System.out.println(name + ": " + Arrays.toString(V));
        else System.out.println(name + ": [Output hidden for large N]");
    }

    public static synchronized void printMatrix(String name, int[][] M) {
        if (N <= 20) {
            System.out.println(name + ":");
            for (int[] row : M) {
                System.out.println("  " + Arrays.toString(row));
            }
        } else {
            System.out.println(name + ": [Output hidden for large N]");
        }
    }

    public static synchronized void printScalar(String name, int val) {
        System.out.println(name + ": " + val);
    }

    // Математичні функції
    /**
     * Обчислення 1: пошук локального максимуму.
     * Формула: ai = max(C_H * MD_H)
     */
    public static int calcMax(int start, int end) {
        int maxVal = Integer.MIN_VALUE;
        // Проходимо по стовпчиках результуючого вектора
        for (int j = start; j < end; j++) {
            int colSum = 0;
            // Скалярний добуток вектора C та j-го стовпця матриці MD
            for (int k = 0; k < N; k++) {
                colSum += C[k] * MD[k][j];
            }
            if (colSum > maxVal) maxVal = colSum;
        }
        return maxVal;
    }

    /**
     * Обчислення 3: фінальний розрахунок смуги вектора W.
     * Формула: W_H = a * C_H + E * (MA_H * MB) * d
     * Модифіковано: використовується спільний вектор Data.E безпосередньо.
     */
    public static void calcWH(int start, int end, int a_local, int d_local) {
        for (int i = start; i < end; i++) {
            // Обчислення i-го елемента виразу E * (MA * MB)
            int sumEMAMB = 0;
            for (int j = 0; j < N; j++) {
                // Обчислення елемента (MA * MB)_{j,i}
                int ma_mb_ji = 0;
                for (int k = 0; k < N; k++) {
                    ma_mb_ji += MA[j][k] * MB[k][i];
                }
                // Використовуємо глобальний Data.E
                sumEMAMB += Data.E[j] * ma_mb_ji;
            }
            // Фінальна формула для елемента W[i]
            W[i] = (a_local * C[i]) + (sumEMAMB * d_local);
        }
    }
}