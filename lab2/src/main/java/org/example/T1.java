package org.example;

/**
 * Потік T1.
 * Виконує обчислення своєї смуги даних.
 */
public class T1 extends Thread {
    @Override
    public void run() {
        System.out.println("T1 started");

        // 1. Очікування завершення введення даних іншими потоками
        // Засіб синхронізації: Бар'єр B1
        try {
            Data.B1.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 2. Копіювання спільного скаляра d у локальну змінну (КД1)
        // Засіб синхронізації: Семафор S1
        int d1 = 0;
        try {
            Data.S1.acquire();
            d1 = Data.d;
            Data.S1.release();
        } catch (InterruptedException e) { e.printStackTrace(); }

        // 3. Обчислення 1: локальний максимум a1 = max(C_H * MD_H)
        int start = 0 * Data.H;
        int end = 1 * Data.H;
        int a1 = Data.calcMax(start, end);

        // 4. Обчислення 2: редукція глобального максимуму a = max(a, a1) (КД2)
        // Засіб синхронізації: AtomicInteger (lock-free)
        Data.a.accumulateAndGet(a1, Math::max);

        // 5. Очікування завершення редукції всіма потоками
        // Засіб синхронізації: Бар'єр B2
        try {
            Data.B2.await();
        } catch (Exception e) { e.printStackTrace(); }

        // 6. Копіювання фінального значення 'a' у локальну змінну (КД3)
        // Засіб синхронізації: Критична секція (synchronized блок)
        synchronized (Data.CS1_Lock) {
            a1 = Data.a.get();
        }

        // 7. Обчислення 3: розрахунок смуги WH = a1*CH + E*(MAH*MB)*d1
        Data.calcWH(start, end, a1, d1);

        // 8. Сповіщення потоку T3 про завершення обчислень
        // Засіб синхронізації: Семафор S2
        Data.S2.release();

        System.out.println("T1 finished");
    }
}
