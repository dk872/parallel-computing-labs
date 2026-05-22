package org.example;

/**
 * Потік T4.
 * Виконує введення частини даних та обчислення.
 */
public class T4 extends Thread {
    @Override
    public void run() {
        System.out.println("T4 started");

        // 1. Введення даних: матриця MD, скаляр d
        Data.fillMatrixRandom(Data.MD);
        Data.d = Data.getRandomScalar();

        // Виведення введених даних
        Data.printMatrix("MD (Input T4)", Data.MD);
        Data.printScalar("d  (Input T4)", Data.d);

        // 2. Очікування завершення введення даних усіма потоками
        // Засіб синхронізації: Бар'єр B1
        try {
            Data.B1.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 3. Копіювання спільного скаляра d у локальну змінну (КД1)
        // Засіб синхронізації: Семафор S1
        int d4 = 0;
        try {
            Data.S1.acquire();
            d4 = Data.d;
            Data.S1.release();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // 4. Обчислення 1: локальний максимум a4 = max(C_H * MD_H)
        int start = 3 * Data.H;
        int end = 4 * Data.H;
        int a4 = Data.calcMax(start, end);

        // 5. Обчислення 2: редукція глобального максимуму (КД2)
        // Засіб синхронізації: AtomicInteger
        Data.a.accumulateAndGet(a4, Math::max);

        // 6. Очікування завершення редукції всіма потоками
        // Засіб синхронізації: Бар'єр B2
        try {
            Data.B2.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 7. Копіювання фінального значення 'a' у локальну змінну (КД3)
        // Засіб синхронізації: Критична секція (synchronized блок)
        synchronized (Data.CS1_Lock) {
            a4 = Data.a.get();
        }

        // 8. Обчислення 3: розрахунок смуги WH = a4*CH + E*(MAH*MB)*d4
        Data.calcWH(start, end, a4, d4);

        // 9. Сповіщення потоку T3 про завершення обчислень
        // Засіб синхронізації: Семафор S4
        Data.S4.release();

        System.out.println("T4 finished");
    }
}
