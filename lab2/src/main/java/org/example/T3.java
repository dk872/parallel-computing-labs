package org.example;

import java.util.Arrays;

/**
 * Потік T3.
 * Виконує введення даних, обчислення та збір результатів.
 */
public class T3 extends Thread {
    @Override
    public void run() {
        System.out.println("T3 started");

        // 1. Введення даних: вектор C, матриця MA
        Data.fillVectorRandom(Data.C);
        Data.fillMatrixRandom(Data.MA);

        // Виведення введених даних
        Data.printVector("C  (Input T3)", Data.C);
        Data.printMatrix("MA (Input T3)", Data.MA);

        // 2. Очікування завершення введення даних усіма потоками
        // Засіб синхронізації: Бар'єр B1
        try {
            Data.B1.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 3. Копіювання спільного скаляра d у локальну змінну (КД1)
        // Засіб синхронізації: Семафор S1
        int d3 = 0;
        try {
            Data.S1.acquire();
            d3 = Data.d;
            Data.S1.release();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // 4. Обчислення 1: локальний максимум a3 = max(C_H * MD_H)
        int start = 2 * Data.H;
        int end = 3 * Data.H;
        int a3 = Data.calcMax(start, end);

        // 5. Обчислення 2: редукція глобального максимуму (КД2)
        // Засіб синхронізації: AtomicInteger
        Data.a.accumulateAndGet(a3, Math::max);

        // 6. Очікування завершення редукції всіма потоками
        // Засіб синхронізації: Бар'єр B2
        try {
            Data.B2.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 7. Копіювання фінального значення 'a' у локальну змінну (КД3)
        // Засіб синхронізації: Критична секція (synchronized блок)
        synchronized (Data.CS1_Lock) {
            a3 = Data.a.get();
        }

        // 8. Обчислення 3: Розрахунок смуги WH = a3*CH + E*(MAH*MB)*d3
        Data.calcWH(start, end, a3, d3);

        // 9. Очікування сигналів про завершення обчислень від інших потоків
        // Засіб синхронізації: Семафори S2, S3, S4
        try {
            Data.S2.acquire(); // Чекати T1
            Data.S3.acquire(); // Чекати T2
            Data.S4.acquire(); // Чекати T4
        } catch (InterruptedException e) { e.printStackTrace(); }

        // 10. Виведення фінального результату W
        // Оскільки всі потоки завершили роботу, вектор W повністю сформований
        System.out.println("Result W: " + Arrays.toString(Data.W));

        System.out.println("T3 finished");
    }
}
