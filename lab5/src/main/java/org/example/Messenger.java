package org.example;

import java.util.*;
import java.util.concurrent.*;

/**
 * Клас-посередник Messenger.
 * Виконує роль комунікаційного середовища для імітації передачі повідомлень (Send/Receive)
 * у системі з локальною пам'яттю.
 */
class Messenger {
    /**
     * Мапа "скриньок" для кожного процесора (T1-T8).
     * Використання ConcurrentHashMap та BlockingQueue забезпечує потокобезпеку
     * та автоматичну синхронізацію при передачі даних.
     */
    private final Map<Integer, BlockingQueue<Message>> boxes = new ConcurrentHashMap<>();

    /**
     * Конструктор месенджера.
     * Ініціалізує індивідуальні вхідні буфери для кожного з 8 процесорів.
     */
    public Messenger() {
        for (int i = 1; i <= 8; i++) {
            // Кожна задача отримує власну чергу для прийому повідомлень
            boxes.put(i, new LinkedBlockingQueue<>());
        }
    }

    /**
     * Метод відправки повідомлення (Send).
     * Додає об'єкт повідомлення у вхідну чергу цільового процесора.
     * * @param to ідентифікатор процесора-отримувача (1-8).
     * @param msg об'єкт повідомлення, що містить дані або результати.
     */
    public void send(int to, Message msg) {
        // Додавання повідомлення в чергу (імітація фізичної передачі по лініях зв'язку)
        boxes.get(to).add(msg);
    }

    /**
     * Метод прийому повідомлення (Receive).
     * Реалізує блокуюче очікування: потік зупиняється, доки в його черзі
     * не з'являться дані від іншого процесора.
     * * @param tid ідентифікатор поточного процесора, який чекає на дані.
     * @return об'єкт отриманого повідомлення.
     */
    public Message receive(int tid) {
        try {
            // Метод take() блокує потік до появи даних
            return boxes.get(tid).take();
        } catch (InterruptedException e) {
            // Обробка переривання потоку під час очікування
            Thread.currentThread().interrupt();
            return null;
        }
    }
}