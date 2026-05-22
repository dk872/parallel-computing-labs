package org.example;

/**
 * Задача T6: вузол-листок гіперкуба.
 * Отримує дані від транзитного вузла T3, обробляє смугу H6
 * та повертає результат у T3 для проміжної редукції.
 */
class T6 extends BaseTask {
    public T6(Messenger m) { super(6, m); }

    @Override
    public void run() {
        System.out.println("T6 started");

        // Прийом даних від T3
        Message in = messenger.receive(6);

        // Локальні обчислення T6 (смуга H6)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(in.D, in.MX);
        // Крок 2: обчислення локального мінімуму a6 = min(C * MZ_H6)
        double a6 = minV(multiplyVM(in.C, in.MZ));
        // Крок 3: обчислення локального максимуму e6 = max(V * MR_H6)
        double e6 = maxV(multiplyVM(V, in.MR));

        // Передача локальних результатів (скалярів) до вузла T3
        messenger.send(3, new Message(a6, e6)); // a6, e6

        System.out.println("T6 finished");
    }
}