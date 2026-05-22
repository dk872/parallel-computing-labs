package org.example;

/**
 * Задача T2: проміжний вузол.
 * Відповідає за ретрансляцію даних до T4 і T7, власні обчислення
 * та агрегацію результатів своєї підгілки для передачі в T1.
 */
class T2 extends BaseTask {
    public T2(Messenger m) { super(2, m); }
    public void run() {
        System.out.println("T2 started");

        // Прийом даних від кореня T1
        Message in = messenger.receive(2);

        // Ретрансляція даних сусідам нижче по дереву
        messenger.send(4, new Message(in.C, in.D, in.MX, getSub(in.MZ, 1, 3), getSub(in.MR, 1, 3))); // До T4, T8
        messenger.send(7, new Message(in.C, in.D, in.MX, getSub(in.MZ, 2), getSub(in.MR, 2)));      // До T7

        // Локальні обчислення T2 (смуга H2)
        // Крок 1: обчислення проміжного вектора V = D * MX
        double[] V = multiplyVM(in.D, in.MX);
        // Крок 2: обчислення локального мінімуму a2 = min(C * MZ_H2)
        double a2 = minV(multiplyVM(in.C, getSub(in.MZ, 0)));
        // Крок 3: обчислення локального максимуму e2 = max(V * MR_H2)
        double e2 = maxV(multiplyVM(V, getSub(in.MR, 0)));

        // Прийом результатів від T4 та T7
        Message r4 = messenger.receive(2); // aT4, eT4
        Message r7 = messenger.receive(2); // a7, e7

        // Локальна редукція та передача результату до T1
        double resA = Math.min(a2, Math.min(r4.a, r7.a)); // aT2
        double resE = Math.max(e2, Math.max(r4.e, r7.e)); // eT2
        messenger.send(1, new Message(resA, resE));

        System.out.println("T2 finished");
    }
}