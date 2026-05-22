package org.example;

/**
 * Задача T8: вузол-листок гіперкуба.
 * Отримує дані через ланцюжок T1->T2->T4, обробляє смугу H8
 * та повертає результат у T4.
 */
class T8 extends BaseTask {
    public T8(Messenger m) { super(8, m); }

    @Override
    public void run() {
        System.out.println("T8 started");

        // Прийом даних від T4
        Message in = messenger.receive(8);

        // Локальні обчислення T8 (смуга H8)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(in.D, in.MX);
        // Крок 2: обчислення локального мінімуму a8 = min(C * MZ_H8)
        double a8 = minV(multiplyVM(in.C, in.MZ));
        // Крок 3: обчислення локального максимуму e8 = max(V * MR_H8)
        double e8 = maxV(multiplyVM(V, in.MR));

        // Передача локальних результатів (скалярів) до вузла T4
        messenger.send(4, new Message(a8, e8)); // a8, e8

        System.out.println("T8 finished");
    }
}