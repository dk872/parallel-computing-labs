package org.example;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ПЗВПКС
 * Лабораторна робота ЛР1. Потоки в мові Java
 * Варіант 1.18 2.22 3.6
 * F1: d = (A*B) + (C*(B*(MA*MD)))
 * F2: MF = (MG * MH) * (MK + ML)
 * F3: O = MAX(MP*MR) * V
 * Кулик Д. ІМ-32
 * Дата 10.02.2026
 */
public class Lab1 {

    private static final Logger logger = Logger.getLogger(Lab1.class.getName());
    public static final int N = 1001;

    public static void main(String[] args) {
        System.out.println("Lab1 started. N = " + N);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Затримка була перервана", e);
            Thread.currentThread().interrupt();
        }

        long startTime = System.currentTimeMillis();

        // Створення потоків з різними пріоритетами
        T1 t1 = new T1("T1", Thread.MAX_PRIORITY); // високий пріоритет
        T2 t2 = new T2("T2", Thread.NORM_PRIORITY); // нормальний пріоритет
        T3 t3 = new T3("T3", Thread.MIN_PRIORITY); // низький пріоритет

        // Запуск потоків
        t1.start();
        t2.start();
        t3.start();

        // Очікування завершення потоків
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Головний потік був перерваний під час очікування завершення задач.", e);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nLab1 finished.");
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }
}
