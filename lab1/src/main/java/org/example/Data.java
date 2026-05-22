package org.example;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Data {
    private static final Logger logger = Logger.getLogger(Data.class.getName());

    public static int[][] matrixMultiply(int[][] A, int[][] B) {
        if (A == null || B == null) {
            throw new IllegalArgumentException("Матриці не можуть бути null");
        }
        if (A.length == 0 || A.length != B.length || A[0].length != B[0].length) {
            throw new IllegalArgumentException("Розмірності матриць не співпадають для множення");
        }

        int n = A.length;
        int[][] C = new int[n][n];
        try {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        C[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Помилка під час множення матриць", e);
            throw e;
        }
        return C;
    }

    public static int[][] matrixAdd(int[][] A, int[][] B) {
        if (A == null || B == null) {
            throw new IllegalArgumentException("Матриці не можуть бути null");
        }
        if (A.length != B.length) {
            throw new IllegalArgumentException("Розмірності матриць не співпадають для додавання");
        }

        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    public static int[] vectorMatrixMultiply(int[] V, int[][] M) {
        if (V == null || M == null) {
            throw new IllegalArgumentException("Вектор або матриця дорівнює null");
        }
        if (V.length != M.length) {
            throw new IllegalArgumentException("Розмір вектор не відповідає розміру матриці");
        }

        int n = V.length;
        int[] R = new int[n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                R[j] += V[i] * M[i][j];
            }
        }
        return R;
    }

    public static long vectorDotProduct(int[] A, int[] B) {
        if (A == null || B == null) {
            throw new IllegalArgumentException("Вектори не можуть бути null");
        }
        if (A.length != B.length) {
            throw new IllegalArgumentException("Вектори мають різну довжину");
        }

        long sum = 0;
        for (int i = 0; i < A.length; i++) {
            sum += (long) A[i] * B[i];
        }
        return sum;
    }

    public static int matrixMax(int[][] M) {
        if (M == null || M.length == 0) {
            throw new IllegalArgumentException("Матриця пуста або null");
        }
        int max = Integer.MIN_VALUE;
        for (int[] row : M) {
            for (int val : row) {
                if (val > max) max = val;
            }
        }
        return max;
    }

    public static int[] vectorScalarMultiply(int[] V, int scalar) {
        if (V == null) {
            throw new IllegalArgumentException("Вектор дорівнює null");
        }
        int[] R = new int[V.length];
        for (int i = 0; i < V.length; i++) {
            R[i] = V[i] * scalar;
        }
        return R;
    }
}
