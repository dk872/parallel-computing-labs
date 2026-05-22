/*
 * ПЗВПКС
 * Лабораторна робота №4. Бібліотека OpenMP. Бар’єри, критичні секції
 * Варіант 8
 * Формула: X = p*max(C*(MA*MD))*R + e*B
 * Виконав: Кулик Д.А. ІМ-32
 * Дата: 29.03.2026
 */

#include <iostream>
#include <vector>
#include <omp.h>
#include <algorithm>
#include <limits>
#include <string>
#include <thread>
#include <chrono>

using namespace std;

// Параметри
const int N = 5000;
const int P = 4;
const int H = N / P;

// Спільні ресурси
vector<double> C(N), R(N), B(N), X(N);
vector<vector<double>> MA(N, vector<double>(N));
vector<vector<double>> MD(N, vector<double>(N));
double p, e, a = -numeric_limits<double>::max();

// Допоміжна функція для виведення вектора
void printVector(const string& name, const vector<double>& vec) {
    cout << name << ": [";
    size_t size = vec.size();
    if (size <= 10) {
        for (size_t i = 0; i < size; i++) {
            cout << vec[i] << (i == size - 1 ? "" : ", ");
        }
    } else {
        for (size_t i = 0; i < 5; i++) cout << vec[i] << ", ";
        cout << "... , ";
        for (size_t i = size - 5; i < size; i++) {
            cout << vec[i] << (i == size - 1 ? "" : ", ");
        }
    }
    cout << "]" << endl;
}

// Допоміжна функція для виведення матриці
void printMatrix(const string& name, const vector<vector<double>>& mat) {
    cout << name << ":" << endl;
    for (size_t i = 0; i < mat.size(); i++) {
        cout << "  [";
        for (size_t j = 0; j < mat[i].size(); j++) {
            cout << mat[i][j] << (j == mat[i].size() - 1 ? "" : ", ");
        }
        cout << "]" << endl;
    }
}

int main() {
    // Затримка перед виконанням
    std::this_thread::sleep_for(std::chrono::seconds(10));

    cout << "Lab4 started" << endl;

    // Встановлення кількості потоків
    omp_set_num_threads(P);

    // Початок замірювання часу
    double start_time = omp_get_wtime();

    // Початок паралельної області
    #pragma omp parallel
    {
        // Локальні змінні потоку
        int tid = omp_get_thread_num(); // Номер потоку
        int tname = tid + 1;            // Ім'я для виводу (T1-T4)

        cout << "T" << tname << " started" << endl;

        // Локальні копії для обчислень (копіювання зі спільних ресурсів)
        double p_i, e_i, a_loc_max, a_i;

        // Введення даних
        // Розподіл завдань між потоками
        if (tid == 0) { // T1
            e = 1.0;
        }
        else if (tid == 1) { // T2
            fill(C.begin(), C.end(), 1.0);
            for(int i = 0; i < N; i++) fill(MA[i].begin(), MA[i].end(), 1.0);
        }
        else if (tid == 2) { // T3
            fill(R.begin(), R.end(), 1.0);
            for(int i = 0; i < N; i++) fill(MD[i].begin(), MD[i].end(), 1.0);
            MD[0][N-1] = 5.0; // Контрольне значення для перевірки максимуму
        }
        else if (tid == 3) { // T4
            fill(B.begin(), B.end(), 1.0);
            p = 1.0;
        }

        // Бар'єр B1
        // Синхронізація: чекаємо, поки всі потоки завершать введення даних
        #pragma omp barrier

        // Виконання потоком T1 виводу введених даних
        #pragma omp master
        {
            if (N <= 8) {
                printVector("Vector C", C);
                printVector("Vector R", R);
                printVector("Vector B", B);
                cout << "p = " << p << ", e = " << e << endl;
                printMatrix("Matrix MA", MA);
                printMatrix("Matrix MD", MD);
            } else {
                cout << "p = " << p << ", e = " << e << endl;
                printVector("Vector C", C);
                printVector("Vector R", R);
                printVector("Vector B", B);
                cout << "N is large, matrices MA/MD are not printed." << endl;
            }
        }

        // Критичні секції CS1 та CS2 (копіювання констант)
        // Захист доступу до спільних ресурсів 'p' та 'e'
        #pragma omp critical(CS1)
        { p_i = p; }

        #pragma omp critical(CS2)
        { e_i = e; }

        // Обчислення проміжного вектора W = C * MA (кожен потік рахує свою частину)
        vector<double> W(N, 0);
        for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
                W[j] += C[k] * MA[k][j];
            }
        }

        // Пошук локального максимуму в межах своєї смуги H (MD_h)
        a_loc_max = -numeric_limits<double>::max();
        for (int j = tid * H; j < (tid + 1) * H; j++) {
            double current_col_sum = 0;
            for (int k = 0; k < N; k++) {
                current_col_sum += W[k] * MD[k][j];
            }
            if (current_col_sum > a_loc_max) a_loc_max = current_col_sum;
        }

        // Критична секція CS3 (редукція максимуму)
        // Безпечне оновлення спільного ресурсу 'a' локальними максимумами
        #pragma omp critical(CS3)
        {
            if (a_loc_max > a) a = a_loc_max;
        }

        // Бар'єр B2
        // Синхронізація: чекаємо, поки всі потоки завершать пошук глобального максимуму
        #pragma omp barrier

        // Критична секція CS4 (копіювання результату редукції)
        // Кожен потік бере фінальне значення 'a' у свою приватну пам'ять
        #pragma omp critical(CS4)
        { a_i = a; }

        // Розрахунок частини вектора X для поточної смуги
        for (int i = tid * H; i < (tid + 1) * H; i++) {
            X[i] = p_i * a_i * R[i] + e_i * B[i];
        }

        // Бар'єр B3
        // Фінальна синхронізація: чекаємо завершення обчислень всього вектора X
        #pragma omp barrier

        cout << "T" << tname << " finished" << endl;

        // Виведення результату
        // Потік T1 (tid == 0) виводить фінальний вектор X зі спільної пам'яті
        if (tid == 0) {
            printVector("\nResult Vector X", X);
        }
    }
    // Кінець паралельної області

    // Кінець замірювання часу
    double end_time = omp_get_wtime();
    cout << "\nExecution time: " << end_time - start_time << " seconds." << endl;

    return 0;
}
