package org.example;

import java.util.Arrays;

/**
 * Клас Data
 * Містить лише структури даних (матриці та вектори).
 * Потоки звертаються сюди для читання/запису своїх частин.
 */
class Data {
    private final int N;

    // Спільні ресурси (вхідні дані та результати)
    public int[] B, Z, S, A;         // Вектори
    public int[][] MM, MX, MT;       // Матриці

    public Data(int n) {
        this.N = n;
        this.S = new int[n];
        this.A = new int[n];
    }

    // Генерація вхідних даних
    public int[] generateVector(int val) {
        int[] v = new int[N];
        Arrays.fill(v, val);
        return v;
    }

    public int[][] generateMatrix(int val) {
        int[][] m = new int[N][N];
        for (int i = 0; i < N; i++) Arrays.fill(m[i], val);
        return m;
    }
}