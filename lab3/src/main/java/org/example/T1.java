package org.example;

import java.util.Arrays;

/**
 * Потік T1.
 * Виконує введення даних ПВВ 1 та впорядкування фінального вектора S.
 */
class T1 implements Runnable {
    private final int N, H;
    private final Data data;
    private final DataMonitor monitor;

    public T1(int n, Data d, DataMonitor m) {
        this.N = n; this.data = d; this.monitor = m; this.H = n / 4;
    }

    @Override
    public void run() {
        System.out.println("T1 started");

        // 1. Введення даних: матриця MM, вектор B, матриця MX
        data.MM = data.generateMatrix(1);
        data.B = data.generateVector(1);
        data.MX = data.generateMatrix(1);

        // Для демонстрації сортування (якщо N = 4) встановимо відмінне число
        if (N == 4) data.B[0] = 3;

        // Виведення введених даних через синхронізовані методи
        monitor.printMatrix("MM", data.MM, "T1");
        monitor.printVector("B", data.B, "T1");
        monitor.printMatrix("MX", data.MX, "T1");

        // 2. Сигнал іншим потокам про введення даних та очікування завершення введення іншими (подія 1)
        monitor.signalIn();
        monitor.waitIn();

        // 3. Копіювання спільних ресурсів d та p у локальні змінні (КД1)
        int d1 = monitor.copyD();
        int p1 = monitor.copyP();

        // 4. Обчислення 1: локальне впорядкування частини вектора SH
        int[] sh1 = calculateSH(d1, 0, H);

        // 5. Очікування завершення впорядкування вектора S в задачі T2 (подія 3)
        // Засіб синхронізації: Монітор (метод waitSortS2H)
        monitor.waitSortS2H(1);

        // 6. Обчислити 2: проміжне впорядкування S2H (злиття SH1 та SH2)
        int[] s2h1 = merge(sh1, Arrays.copyOfRange(data.S, H, 2 * H));
        System.arraycopy(s2h1, 0, data.S, 0, 2 * H);

        // 7. Очікування завершення формування половини вектора S задачею T3 (подія 4)
        monitor.waitSortS();

        // 8. Обчислити 3: формування глобально впорядкованого вектора S (подія 4)
        int[] sFull = merge(s2h1, Arrays.copyOfRange(data.S, 2 * H, N));
        System.arraycopy(sFull, 0, data.S, 0, N);

        // 9. Сигнал іншим задачам про повну готовність вектора S
        monitor.signalFullS();

        // 10. Обчислення 4-5: редукція спільного ресурсу q (КД2)
        // Засіб синхронізації: Монітор (метод addQ)
        monitor.addQ(scalarPart(0, H));

        // 11. Очікування завершення редукції q всіма потоками (подія 2)
        monitor.waitQ();

        // 12. Копіювання фінального значення q у локальну змінну (КД3)
        int q1 = monitor.copyQ();

        // 13. Обчислення 6: розрахунок своєї смуги AH
        calculateAH(p1, q1, 0, H);

        // 14. Сповіщення задачі T4 про завершення обчислень (подія 5)
        monitor.signalFinal();

        System.out.println("T1 finished");
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
