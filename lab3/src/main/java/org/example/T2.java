package org.example;

import java.util.Arrays;

/**
 * Потік T2.
 * Виконує обчислення своєї смуги та передачу даних для впорядкування.
 */
class T2 implements Runnable {
    private final int N, H;
    private final Data data;
    private final DataMonitor monitor;

    public T2(int n, Data d, DataMonitor m) {
        this.N = n; this.data = d; this.monitor = m; this.H = n / 4;
    }

    @Override
    public void run() {
        System.out.println("T2 started");

        // 1. Очікування завершення введення даних іншими потоками (подія 1)
        monitor.waitIn();

        // 2. Копіювання спільних ресурсів d та p у локальні змінні (КД1)
        int d2 = monitor.copyD();
        int p2 = monitor.copyP();

        // 3. Обчислення 1: локальне впорядкування частини вектора SH
        int[] sh2 = calculateSH(d2, H, 2 * H);
        System.arraycopy(sh2, 0, data.S, H, H);

        // 4. Сигнал задачі T1 про завершення впорядкування власної частини (подія 3)
        monitor.signalSortS2H(2);

        // 5. Очікування формування глобально впорядкованого вектора S задачею T1 (подія 4)
        monitor.waitS();

        // 6. Обчислення 4-5: редукція спільного ресурсу q (КД2)
        monitor.addQ(scalarPart(H, 2 * H));

        // 7. Очікування завершення редукції q всіма потоками (подія 2)
        monitor.waitQ();

        // 8. Копіювання фінального значення q (КД3)
        int q2 = monitor.copyQ();

        // 9. Обчислення 6: розрахунок своєї смуги AH
        calculateAH(p2, q2, H, 2 * H);

        // 10. Сповіщення задачі T4 про завершення обчислень AH (подія 5)
        monitor.signalFinal();

        System.out.println("T2 finished");
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
