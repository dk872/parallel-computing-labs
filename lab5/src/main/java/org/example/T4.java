package org.example;

/**
 * Задача T4: проміжний вузол.
 * Ретранслює дані до T8, виконує локальні обчислення та передає
 * агрегований результат підгілки до T2.
 */
class T4 extends BaseTask {
    public T4(Messenger m) { super(4, m); }
    public void run() {
        System.out.println("T4 started");

        // Прийом даних від T2
        Message in = messenger.receive(4);

        // Ретрансляція даних до вузла T8
        messenger.send(8, new Message(in.C, in.D, in.MX, getSub(in.MZ, 1), getSub(in.MR, 1)));

        // Локальні обчислення T4 (смуга H4)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(in.D, in.MX);
        // Крок 2: обчислення локального мінімуму a4 = min(C * MZ_H4)
        double a4 = minV(multiplyVM(in.C, getSub(in.MZ, 0)));
        // Крок 3: обчислення локального максимуму e4 = max(V * MR_H4)
        double e4 = maxV(multiplyVM(V, getSub(in.MR, 0)));

        // Прийом результату від вузла T8
        Message r8 = messenger.receive(4); // a8, e8

        // Редукція та передача результату до T2 (aT4, eT4)
        messenger.send(2, new Message(Math.min(a4, r8.a), Math.max(e4, r8.e)));

        System.out.println("T4 finished");
    }
}