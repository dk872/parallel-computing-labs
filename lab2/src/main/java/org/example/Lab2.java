package org.example;

/**
 * ПЗВПКС
 * Лабораторна робота ЛР2. Мова Java. Семафори, критичні секції, атомік-змінні, бар’єри.
 * Варіант 22
 * Формула: W = max(C*MD)*С + E*(MA*MB)*d
 * Виконав: Кулик Д. ІМ-32
 * Дата: 05.03.2026
 */
public class Lab2 {
    public static void main(String[] args) {
        System.out.println("Lab2 started");

        // Ініціалізація об'єктів потоків (задач)
        T1 t1 = new T1();
        T2 t2 = new T2();
        T3 t3 = new T3();
        T4 t4 = new T4();

        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            e.printStackTrace();
        }

        // Кінець вимірювання часу
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Time execution: " + duration + " ms");

        System.out.println("Lab2 finished");
    }
}