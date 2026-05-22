package org.example;

import java.util.Arrays;
import java.util.Random;

/**
 * Потік T3.
 * Виконує введення даних ПВВ 3 та проміжне впорядкування половини вектора S.
 */
class T3 implements Runnable {
    private final int N, H;
    private final Data data;
    private final DataMonitor monitor;

    public T3(int n, Data d, DataMonitor m) {
        this.N = n; this.data = d; this.monitor = m; this.H = n / 4;
    }

    @Override
    public void run() {
        System.out.println("T3 started");

        // 1. Введення даних: скаляр p
        int inputP = new Random().nextInt(5) + 1;
        monitor.setP(inputP);
        monitor.printScalar("p", inputP, "T3");

        // 2. Сигнал іншим потокам про введення та очікування завершення введення (подія 1)
        monitor.signalIn();
        monitor.waitIn();

        // 3. Копіювання спільних ресурсів d та p (КД1)
        int d3 = monitor.copyD();
        int p3 = monitor.copyP();

        // 4. Обчислення 1: локальне впорядкування частини вектора SH
        int[] sh3 = calculateSH(d3, 2 * H, 3 * H);

        // 5. Очікування завершення впорядкування в задачі T4 (подія 3)
        monitor.waitSortS2H(3);

        // 6. Обчислити 2: формування проміжного впорядкованого вектора S2H (злиття SH3 та SH4)
        int[] s2h2 = merge(sh3, Arrays.copyOfRange(data.S, 3 * H, N));
        System.arraycopy(s2h2, 0, data.S, 2 * H, 2 * H);

        // 7. Сигнал задачі T1 про готовність половини вектора (подія 4)
        monitor.signalSortS();

        // 8. Очікування повного формування вектора S задачею T1 (подія 4)
        monitor.waitS();

        // 9. Обчислення 4-5: редукція спільного ресурсу q (КД2)
        monitor.addQ(scalarPart(2 * H, 3 * H));

        // 10. Очікування завершення редукції q (подія 2)
        monitor.waitQ();

        // 11. Копіювання фінального значення q (КД3)
        int q3 = monitor.copyQ();

        // 12. Обчислення 6: розрахунок своєї смуги AH
        calculateAH(p3, q3, 2 * H, 3 * H);

        // 13. Сповіщення задачі T4 про завершення обчислень (подія 5)
        monitor.signalFinal();

        System.out.println("T3 finished");
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

    private int[] merge(int[] a, int[] b) {
        int[] res = new int[a.length + b.length];
        int i=0, j=0, k=0;
        while(i<a.length && j<b.length) res[k++] = (a[i]<b[j]) ? a[i++] : b[j++];
        while(i<a.length) res[k++] = a[i++]; while(j<b.length) res[k++] = b[j++];
        return res;
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
