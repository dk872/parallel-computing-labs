package org.example;

import java.util.Arrays;
import java.util.Random;

/**
 * Потік T4.
 * Виконує введення даних ПВВ 4 та фінальне виведення результату.
 */
class T4 implements Runnable {
    private final int N, H;
    private final Data data;
    private final DataMonitor monitor;

    public T4(int n, Data d, DataMonitor m) {
        this.N = n; this.data = d; this.monitor = m; this.H = n / 4;
    }

    @Override
    public void run() {
        System.out.println("T4 started");

        // 1. Введення даних: вектор Z, матриця MT, скаляр d
        data.Z = data.generateVector(1);
        data.MT = data.generateMatrix(1);
        int inputD = new Random().nextInt(5) + 1;
        monitor.setD(inputD);

        // Виведення введених даних через синхронізовані методи
        monitor.printVector("Z", data.Z, "T4");
        monitor.printMatrix("MT", data.MT, "T4");
        monitor.printScalar("d", inputD, "T4");

        // 2. Сигнал іншим потокам про введення та очікування завершення введення (подія 1)
        monitor.signalIn();
        monitor.waitIn();

        // 3. Копіювання спільних ресурсів d та p (КД1)
        int d4 = monitor.copyD();
        int p4 = monitor.copyP();

        // 4. Обчислення 1: локальне впорядкування частини вектора SH
        int[] sh4 = calculateSH(d4, 3 * H, N);
        System.arraycopy(sh4, 0, data.S, 3 * H, H);

        // 5. Сигнал задачі T3 про завершення впорядкування власної частини (подія 3)
        monitor.signalSortS2H(4);

        // 6. Очікування повного формування вектора S задачею T1 (подія 4)
        monitor.waitS();

        // 7. Обчислення 4-5: редукція спільного ресурсу q (КД2)
        monitor.addQ(scalarPart(3 * H, N));

        // 8. Очікування завершення редукції q (подія 2)
        monitor.waitQ();

        // 9. Копіювання фінального значення q (КД3)
        int q4 = monitor.copyQ();

        // 10. Обчислення 6: розрахунок своєї смуги AH
        calculateAH(p4, q4, 3 * H, N);

        // 11. Очікування сигналів про завершення обчислень AH від T1, T2, T3 (подія 5)
        // Засіб синхронізації: Монітор (метод waitFinal)
        monitor.waitFinal();

        // 12. Виведення фінального результату A
        monitor.printVector("Результат A", data.A, "T4");

        System.out.println("T4 finished");
    }

    // Математичні методи потоку
    private int[] calculateSH(int d, int s, int e) {
        int[] res = new int[e - s];
        for (int i = 0; i < res.length; i++) {
            int col = s + i; int zmm = 0;
            for (int j = 0; j < N; j++) zmm += data.Z[j] * data.MM[j][col];
            res[i] = d * data.B[col] + zmm;
        }
        Arrays.sort(res); return res;
    }

    private int scalarPart(int s, int e) {
        int res = 0; for (int i = s; i < e; i++) res += data.B[i] * data.Z[i]; return res;
    }

    private void calculateAH(int p, int q, int s, int e) {
        for (int j = s; j < e; j++) {
            int smxmt = 0;
            for (int i = 0; i < N; i++) {
                int val = 0; for (int k = 0; k < N; k++) val += data.MX[i][k] * data.MT[k][j];
                smxmt += data.S[i] * val;
            }
            data.A[j] = p * smxmt + q * data.Z[j];
        }
    }
}
