package org.example;

import java.util.*;
import java.util.concurrent.*;

/**
 * Базовий клас для всіх задач T1-T8.
 * Містить спільні математичні методи та утиліти.
 */
abstract class BaseTask extends Thread {
    protected final int tid;
    protected final Messenger messenger;

    public BaseTask(int tid, Messenger messenger) {
        this.tid = tid;
        this.messenger = messenger;
    }

    // Векторно-матричне множення
    protected double[] multiplyVM(double[] v, double[][] m) {
        double[] res = new double[m[0].length];
        for (int j = 0; j < m[0].length; j++) {
            for (int i = 0; i < v.length; i++) {
                res[j] += v[i] * m[i][j];
            }
        }
        return res;
    }

    // Пошук мінімального елемента у векторі
    protected double minV(double[] v) {
        double m = v[0];
        for (double d : v) {
            if (d < m) m = d;
        }
        return m;
    }

    // Пошук максимального елемента у векторі
    protected double maxV(double[] v) {
        double m = v[0];
        for (double d : v) {
            if (d > m) m = d;
        }
        return m;
    }

    // Функція копіювання смуг матриці (імітація Scatter)
    protected double[][] getSub(double[][] m, int... indices) {
        double[][] sub = new double[Lab5.N][indices.length * Lab5.H];
        for (int i = 0; i < Lab5.N; i++) {
            for (int k = 0; k < indices.length; k++) {
                System.arraycopy(m[i], indices[k] * Lab5.H, sub[i], k * Lab5.H, Lab5.H);
            }
        }
        return sub;
    }

    // Допоміжний метод для виведення масивів
    protected void printArray(String name, double[] arr) {
        if (Lab5.N == 8) {
            System.out.println(name + ": " + Arrays.toString(arr));
        } else {
            System.out.println(name + ": (значення не виводяться, бо N > 8)");
        }
    }

    // Допоміжний метод для виведення матриць
    protected void printMatrix(String name, double[][] matrix) {
        if (Lab5.N == 8) {
            System.out.println(name + ":");
            for (double[] row : matrix) System.out.println("  " + Arrays.toString(row));
        } else {
            System.out.println(name + ": (значення не виводяться, бо N > 8)");
        }
    }
}