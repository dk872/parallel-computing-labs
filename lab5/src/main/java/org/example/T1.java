package org.example;

import java.util.*;

/**
 * Задача T1: вузол введення/виведення та корінь дерева редукції.
 * Відповідає за ініціалізацію даних, розсилку сусідам по Гіперкубу (T2, T3, T5),
 * власні обчислення та формування фінального результату.
 */
class T1 extends BaseTask {
    public T1(Messenger m) { super(1, m); }

    public void run() {
        System.out.println("T1 started");

        // Введення даних через ПВВ1
        double[] C = new double[Lab5.N]; Arrays.fill(C, 1.0);
        double[] D = new double[Lab5.N]; Arrays.fill(D, 1.0);
        double[][] MX = new double[Lab5.N][Lab5.N];
        double[][] MZ = new double[Lab5.N][Lab5.N];
        double[][] MR = new double[Lab5.N][Lab5.N];

        for (int i = 0; i < Lab5.N; i++) {
            Arrays.fill(MX[i], 1.0);
            Arrays.fill(MZ[i], 1.0);
            Arrays.fill(MR[i], 1.0);
        }
        // Тестові значення для перевірки коректності обчислень
        MZ[0][Lab5.N - 1] = -9.0;
        MR[0][Lab5.N - 1] = 16.0;

        // Виведення вхідних даних (якщо N=8)
        printArray("Vector C", C);
        printArray("Vector D", D);
        printMatrix("Matrix MX", MX);
        printMatrix("Matrix MZ", MZ);
        printMatrix("Matrix MR", MR);

        // Розсилка даних сусідам
        // Передача даних для гілки T2 (включає смуги для T4, T7, T8)
        messenger.send(2, new Message(C, D, MX, getSub(MZ, 1, 3, 6, 7), getSub(MR, 1, 3, 6, 7)));
        // Передача даних для гілки T3 (включає смугу для T6)
        messenger.send(3, new Message(C, D, MX, getSub(MZ, 2, 5), getSub(MR, 2, 5)));
        // Передача даних для вузла T5
        messenger.send(5, new Message(C, D, MX, getSub(MZ, 4), getSub(MR, 4)));

        // Локальні обчислення задачі T1 (смуга H1)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(D, MX);
        // Крок 2: обчислення локального мінімуму a1 = min(C * MZ_H1)
        double a1 = minV(multiplyVM(C, getSub(MZ, 0)));
        // Крок 3: обчислення локального максимуму e1 = max(V * MR_H1)
        double e1 = maxV(multiplyVM(V, getSub(MR, 0)));

        // Прийом результатів редукції від дочірніх гілок
        Message r2 = messenger.receive(1); // aT2, eT2
        Message r3 = messenger.receive(1); // aT3, eT3
        Message r5 = messenger.receive(1); // a5, e5

        // Фінальна редукція та розрахунок результату
        double a_min = Math.min(a1, Math.min(r2.a, Math.min(r3.a, r5.a))); // a
        double e_max = Math.max(e1, Math.max(r2.e, Math.max(r3.e, r5.e))); // e
        double finalA = a_min + e_max;

        // Виведення результату через ПВВ1
        System.out.println("FINAL RESULT: a = " + finalA);
        System.out.println("T1 finished");
    }
}