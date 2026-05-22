package org.example;

import java.util.Arrays;

/**
 * Клас DataMonitor
 * Інкапсулює спільні ресурси та засоби синхронізації, доступні всім потокам.
 * Забезпечує взаємне виключення та координацію послідовності етапів обчислень.
 */
class DataMonitor {
    // Засоби синхронізації та спільні скаляри
    private int d, p, q = 0;
    // Подія 1: синхронізація завершення введення даних (T1, T3, T4 сигналізують)
    private int F1 = 3;
    // Подія 2: синхронізація завершення обчислення редукції q усіма 4 потоками
    private int F6 = 4;
    // Подія 5: очікування завершення обчислень фрагментів AH потоками T1, T2, T3
    private int F7 = 3;

    // Засоби синхронізації (прапорці для ієрархічного впорядкування S)
    // Подія 3: готовність фрагментів SH від T2 та T4
    private boolean readyS2H_T2 = false;
    private boolean readyS2H_T4 = false;
    // Подія 4: готовність проміжної половини S2H від T3
    private boolean readyS2H_T3 = false;
    // Подія 4: повна готовність глобально впорядкованого вектора S (від T1)
    private boolean readyS_Full = false;

    // Методи синхронізації
    /**
     * signalIn() – потік сповіщає про завершення введення своєї частини даних.
     * Викликається задачами T1, T3, T4.
     */
    public synchronized void signalIn() {
        F1--;
        if (F1 == 0) notifyAll(); // Розблокування всіх потоків після події 1
    }

    /**
     * waitIn() – реалізує бар'єр для очікування завершення введення вхідних даних.
     * Забезпечує цілісність даних перед початком обчислень.
     */
    public synchronized void waitIn() {
        while (F1 > 0) {
            try { wait(); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    /**
     * КД1: методи для безпечної роботи зі спільними скалярами d та p.
     * set-методи використовуються для запису вхідних даних,
     * copy-методи — для копіювання значень у локальну пам'ять потоків.
     */
    public synchronized void setD(int val) { this.d = val; }
    public synchronized void setP(int val) { this.p = val; }
    public synchronized int copyD() { return d; }
    public synchronized int copyP() { return p; }

    /**
     * signalSortS2H() – сигнал про завершення сортування своєї частини SH.
     * Викликається T2 та T4. Подія 3.
     */
    public synchronized void signalSortS2H(int threadId) {
        if (threadId == 2) readyS2H_T2 = true;
        if (threadId == 4) readyS2H_T4 = true;
        notifyAll();
    }

    /**
     * waitSortS2H() – очікування частин від "дочірніх" потоків.
     * T1 чекає на T2, T3 чекає на T4.
     */
    public synchronized void waitSortS2H(int threadId) {
        if (threadId == 1) while (!readyS2H_T2) try { wait(); } catch (Exception _){}
        if (threadId == 3) while (!readyS2H_T4) try { wait(); } catch (Exception _){}
    }

    /**
     * signalSortS() – T3 сповіщає T1 про готовність впорядкованої половини S2H.
     * Подія 4.
     */
    public synchronized void signalSortS() {
        readyS2H_T3 = true;
        notifyAll();
    }

    /**
     * waitSortS() – T1 очікує на завершення обробки другої половини вектора задачею T3.
     */
    public synchronized void waitSortS() {
        while (!readyS2H_T3) try { wait(); } catch (Exception _){}
    }

    /**
     * signalFullS() – T1 сповіщає всі потоки про повну готовність вектора S.
     * Подія 4.
     */
    public synchronized void signalFullS() {
        readyS_Full = true;
        notifyAll();
    }

    /**
     * waitS() – бар'єр, що блокує T2, T3, T4 до моменту повної готовності вектора S.
     */
    public synchronized void waitS() {
        while (!readyS_Full) try { wait(); } catch (Exception _){}
    }

    /**
     * КД2: доступ до глобального ресурсу q (редукція).
     * Виконує атомарне додавання локальної частини qi до спільного результату.
     */
    public synchronized void addQ(int qi) {
        this.q += qi;
        F6--; // Зменшення лічильника завершених частин редукції
        if (F6 == 0) notifyAll(); // Подія 2: всі частини q зібрані
    }

    /**
     * waitQ() – бар'єр для очікування завершення редукції q усіма потоками.
     */
    public synchronized void waitQ() {
        while (F6 > 0) try { wait(); } catch (Exception _){}
    }

    /**
     * КД3: копіювання фінального значення q у локальну пам'ять.
     */
    public synchronized int copyQ() { return q; }

    /**
     * signalFinal() – сповіщення про завершення обчислення власної смуги AH.
     * Викликається задачами T1, T2, T3.
     */
    public synchronized void signalFinal() {
        F7--;
        if (F7 == 0) notifyAll(); // Сигнал для T4 про можливість виведення
    }

    /**
     * waitFinal() – очікування задачею T4 завершення розрахунків у всіх інших потоках.
     * Подія 5.
     */
    public synchronized void waitFinal() {
        while (F7 > 0) try { wait(); } catch (Exception _){}
    }

    // Методи синхронізованого виведення в консоль
    public synchronized void printScalar(String name, int s, String threadName) {
        System.out.println("[" + threadName + "] input scalar " + name + " = " + s);
    }

    public synchronized void printVector(String name, int[] v, String threadName) {
        System.out.print("[" + threadName + "] input vector " + name + ": ");
        if (v.length > 10) {
            int[] sub = Arrays.copyOfRange(v, 0, 10);
            System.out.println(Arrays.toString(sub) + " ... [size: " + v.length + "]");
        } else {
            System.out.println(Arrays.toString(v));
        }
    }

    public synchronized void printMatrix(String name, int[][] m, String threadName) {
        System.out.println("[" + threadName + "] input matrix " + name + ":");
        int limit = Math.min(m.length, 5);
        for (int i = 0; i < limit; i++) {
            System.out.println("    " + Arrays.toString(m[i].length > 10 ?
                    Arrays.copyOfRange(m[i], 0, 10) : m[i]) + (m[i].length > 10 ? " ..." : ""));
        }
        if (m.length > 5) System.out.println("    ... [hidden rows]");
    }
}