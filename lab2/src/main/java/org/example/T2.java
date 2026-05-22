package org.example;

/**
 * Потік T2.
 * Виконує введення частини даних та обчислення.
 */
public class T2 extends Thread {
    @Override
    public void run() {
        System.out.println("T2 started");

        // 1. Введення даних: матриця MB, вектор E
        Data.fillMatrixRandom(Data.MB);
        Data.fillVectorRandom(Data.E);

        // Виведення введених даних
        Data.printMatrix("MB (Input T2)", Data.MB);
        Data.printVector("E  (Input T2)", Data.E);

        // 2. Очікування завершення введення даних усіма потоками
        // Засіб синхронізації: Бар'єр B1
        try {
            Data.B1.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 3. Копіювання спільного скаляра d у локальну змінну (КД1)
        // Засіб синхронізації: Семафор S1
        int d2 = 0;
        try {
            Data.S1.acquire();
            d2 = Data.d;
            Data.S1.release();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // 4. Обчислення 1: локальний максимум a2 = max(C_H * MD_H)
        int start = 1 * Data.H;
        int end = 2 * Data.H;
        int a2 = Data.calcMax(start, end);

        // 5. Обчислення 2: редукція глобального максимуму (КД2)
        // Засіб синхронізації: AtomicInteger
        Data.a.accumulateAndGet(a2, Math::max);

        // 6. Очікування завершення редукції всіма потоками
        // Засіб синхронізації: Бар'єр B2
        try {
            Data.B2.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 7. Копіювання фінального значення 'a' у локальну змінну (КД3)
        // Засіб синхронізації: Критична секція (synchronized блок)
        synchronized (Data.CS1_Lock) {
            a2 = Data.a.get();
        }

        // 8. Обчислення 3: розрахунок смуги WH = a2*CH + E*(MAH*MB)*d2
        Data.calcWH(start, end, a2, d2);

        // 9. Сповіщення потоку T3 про завершення обчислень
        // Засіб синхронізації: Семафор S3
        Data.S3.release();

        System.out.println("T2 finished");
    }
}
