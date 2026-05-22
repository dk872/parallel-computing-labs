package org.example;

/**
 * Задача T7: вузол-листок гіперкуба.
 * Отримує дані від транзитного вузла T2, обробляє смугу H7
 * та повертає результат у T2.
 */
class T7 extends BaseTask {
    public T7(Messenger m) { super(7, m); }

    @Override
    public void run() {
        System.out.println("T7 started");

        // Прийом даних від T2
        Message in = messenger.receive(7);

        // Локальні обчислення T7 (смуга H7)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(in.D, in.MX);
        // Крок 2: обчислення локального мінімуму a7 = min(C * MZ_H7)
        double a7 = minV(multiplyVM(in.C, in.MZ));
        // Крок 3: обчислення локального максимуму e7 = max(V * MR_H7)
        double e7 = maxV(multiplyVM(V, in.MR));

        // Передача локальних результатів (скалярів) до вузла T2
        messenger.send(2, new Message(a7, e7)); // a7, e7

        System.out.println("T7 finished");
    }
}