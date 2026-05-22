package org.example;

/**
 * ПЗВПКС
 * Лабораторна робота №5. Повідомлення (MPI)
 * Варіант 16
 * Формула: a = min(C*MZ) + max(D*(MX*MR))
 * Виконав: Кулик Д. А. ІМ-32
 * Дата: 06.04.2026
 */

public class Lab5 {
    // Параметри задачі
    public static final int N = 5000; // Розмірність векторів та матриць
    public static final int P = 8; // Кількість процесорів (потоків)
    public static final int H = N / P; // Смуга обробки одного потоку

    public static void main(String[] args) {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Lab5 started");

        // Фіксація часу початку виконання всієї програми
        long startTime = System.currentTimeMillis();

        // Створення месенджера для обміну повідомленнями між задачами
        Messenger messenger = new Messenger();

        // Масив для зберігання посилань на потоки задач
        BaseTask[] tasks = new BaseTask[P];

        // Ініціалізація та запуск задач T1-T8
        tasks[0] = new T1(messenger);
        tasks[1] = new T2(messenger);
        tasks[2] = new T3(messenger);
        tasks[3] = new T4(messenger);
        tasks[4] = new T5(messenger);
        tasks[5] = new T6(messenger);
        tasks[6] = new T7(messenger);
        tasks[7] = new T8(messenger);

        for (BaseTask task : tasks) {
            task.start();
        }

        // Очікування завершення всіх задач
        for (BaseTask task : tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Розрахунок та виведення загального часу виконання
        long endTime = System.currentTimeMillis();
        System.out.println("Lab5 finished");
        System.out.println("Total execution time: " + (endTime - startTime) + " ms");
    }
}