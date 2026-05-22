package org.example;

/**
 * Задача T3: проміжний вузол.
 * Ретранслює дані до T6, виконує локальні обчислення та передає
 * агрегований результат підгілки до T1.
 */
class T3 extends BaseTask {
    public T3(Messenger m) { super(3, m); }
    public void run() {
        System.out.println("T3 started");

        // Прийом даних від кореня T1
        Message in = messenger.receive(3);

        // Ретрансляція даних до вузла T6
        messenger.send(6, new Message(in.C, in.D, in.MX, getSub(in.MZ, 1), getSub(in.MR, 1)));

        // Локальні обчислення T3 (смуга H3)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(in.D, in.MX);
        // Крок 2: обчислення локального мінімуму a3 = min(C * MZ_H3)
        double a3 = minV(multiplyVM(in.C, getSub(in.MZ, 0)));
        // Крок 3: обчислення локального максимуму e3 = max(V * MR_H3)
        double e3 = maxV(multiplyVM(V, getSub(in.MR, 0)));

        // Прийом результату від вузла
        Message r6 = messenger.receive(3); // a6, e6

        // Редукція та передача результату до T1 (aT3, eT3)
        messenger.send(1, new Message(Math.min(a3, r6.a), Math.max(e3, r6.e)));

        System.out.println("T3 finished");
    }
}