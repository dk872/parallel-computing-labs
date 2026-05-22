package org.example;

/**
 * ПЗВПКС
 * Лабораторна робота №3. Мова Java. Монітори.
 * Варіант 16
 * Формула: A = p*(sort(d*B + Z*MM) * (MX*MT)) + (B*Z)*Z
 * Виконав: Кулик Д. А. ІМ-32
 * Дата: 21.03.2026
 */
public class Lab3 {
    public static void main(String[] args) {
        System.out.println("Lab3 started");
        int N = 1200; // Розмірність векторів і матриць

        // Створення окремих об'єктів для даних та управління синхронізацією
        Data data = new Data(N);
        DataMonitor monitor = new DataMonitor();

        try {
            Thread.sleep(18000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ініціалізація об'єктів потоків (задач)
        Thread t1 = new Thread(new T1(N, data, monitor));
        Thread t2 = new Thread(new T2(N, data, monitor));
        Thread t3 = new Thread(new T3(N, data, monitor));
        Thread t4 = new Thread(new T4(N, data, monitor));

        // Початок вимірювання часу
        long startTime = System.currentTimeMillis();

        // Запуск потоків на виконання
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // Очікування завершення всіх потоків головним потоком
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Кінець вимірювання часу
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time execution: " + duration + " ms");
        System.out.println("Lab3 finished");
    }
}