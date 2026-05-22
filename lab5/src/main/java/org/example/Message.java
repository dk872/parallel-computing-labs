package org.example;


/**
 * Клас для зберігання даних повідомлення.
 * Використовується для імітації передачі даних у локальній пам'яті.
 */
class Message {
    double[] C, D;
    double[][] MX, MZ, MR;
    double a, e;

    // Конструктор для передачі масивів (Scatter / Routing)
    Message(double[] c, double[] d, double[][] mx,
            double[][] mz, double[][] mr) {
        this.C = c; this.D = d; this.MX = mx;
        this.MZ = mz; this.MR = mr;
    }

    // Конструктор для передачі скалярних результатів (Reduction)
    Message(double a, double e) {
        this.a = a; this.e = e;
    }
}