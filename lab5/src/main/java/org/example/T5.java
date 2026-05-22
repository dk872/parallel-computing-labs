package org.example;

/**
 * Задача T5: вузол-листок гіперкуба.
 * Отримує дані напряму від кореня T1, виконує обчислення на своїй смузі H5
 * та повертає результат у T1 для фінальної редукції.
 */
class T5 extends BaseTask {
    public T5(Messenger m) { super(5, m); }

    @Override
    public void run() {
        System.out.println("T5 started");

        // Прийом даних від T1
        Message in = messenger.receive(5);

        // Локальні обчислення T5 (смуга H5)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(in.D, in.MX);
        // Крок 2: обчислення локального мінімуму a5 = min(C * MZ_H5)
        double a5 = minV(multiplyVM(in.C, in.MZ));
        // Крок 3: обчислення локального максимуму e5 = max(V * MR_H5)
        double e5 = maxV(multiplyVM(V, in.MR));

        // Передача локальних результатів (скалярів) до вузла T1
        messenger.send(1, new Message(a5, e5)); // a5, e5

        System.out.println("T5 finished");
    }
}