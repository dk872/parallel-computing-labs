with Ada.Text_IO;
with Ada.Calendar;
with Ada.Float_Text_IO;
use type Ada.Calendar.Time;

-- ПЗВПКС
-- Лабораторна робота №5. Повідомлення
-- Варіант 16
-- Формула: a = min(C*MZ) + max(D*(MX*MR))
-- Виконав: Кулик Д.А. ІМ-32
-- ПВВ1: C, a, MX, MR, D, MZ
-- Дата: 09.04.2026
procedure Lab5 is

   Start_Time, End_Time : Ada.Calendar.Time;
   Execution_Time : Duration;

   -- Пакет Data: винесені типи даних, константи та допоміжні функції
   package Data is
      -- Позначення розмірностей
      N : constant Integer := 4000;
      P : constant Integer := 8;
      H : constant Integer := N / P;

      -- Типи даних
      type Vector is array (1 .. N) of Float;
      type Matrix is array (1 .. N, 1 .. N) of Float;
      
      type Matrix_H is array (1 .. N, 1 .. H) of Float;
      type Matrix_2H is array (1 .. N, 1 .. 2 * H) of Float;
      type Matrix_4H is array (1 .. N, 1 .. 4 * H) of Float;

      type Vector_Ptr is access Vector;
      type Matrix_Ptr is access Matrix;
      type Matrix_H_Ptr is access Matrix_H;
      type Matrix_2H_Ptr is access Matrix_2H;
      type Matrix_4H_Ptr is access Matrix_4H;

      -- Математичні операції
      function Compute_A_Local (C : Vector_Ptr; MZ_Part : Matrix_H_Ptr) return Float;
      function Compute_E_Local (D : Vector_Ptr; MX : Matrix_Ptr; MR_Part : Matrix_H_Ptr) return Float;
      
      function Min_F (A, B : Float) return Float;
      function Max_F (A, B : Float) return Float;

      -- Виведення на екран
      procedure Print_Vector(Name : String; V : Vector_Ptr);
      procedure Print_Matrix(Name : String; M : Matrix_Ptr);
   end Data;

   package body Data is
      package Float_IO is new Ada.Text_IO.Float_IO(Float);

      -- Обчислення локального мінімуму ai = min(C * MZH)
      function Compute_A_Local (C : Vector_Ptr; MZ_Part : Matrix_H_Ptr) return Float is
         Min_Val : Float := Float'Last;
         Sum     : Float;
      begin
         for J in 1 .. H loop
            Sum := 0.0;
            for I in 1 .. N loop
               Sum := Sum + C.all(I) * MZ_Part.all(I, J);
            end loop;
            if Sum < Min_Val then
               Min_Val := Sum;
            end if;
         end loop;
         return Min_Val;
      end Compute_A_Local;

      -- Обчислення локального максимуму ei = max(D * (MX * MRH))
      function Compute_E_Local (D : Vector_Ptr; MX : Matrix_Ptr; MR_Part : Matrix_H_Ptr) return Float is
         W : Vector_Ptr := new Vector;
         Max_Val : Float := Float'First;
         Sum     : Float;
      begin
         for J in 1 .. N loop
            W.all(J) := 0.0;
            for I in 1 .. N loop
               W.all(J) := W.all(J) + D.all(I) * MX.all(I, J);
            end loop;
         end loop;
      
         for J in 1 .. H loop
            Sum := 0.0;
            for I in 1 .. N loop
               Sum := Sum + W.all(I) * MR_Part.all(I, J);
            end loop;
            if Sum > Max_Val then
               Max_Val := Sum;
            end if;
         end loop;
         return Max_Val;
      end Compute_E_Local;

      -- Редукція мінімуму
      function Min_F (A, B : Float) return Float is
      begin
         if A < B then return A; else return B; end if;
      end Min_F;

      -- Редукція максимуму
      function Max_F (A, B : Float) return Float is
      begin
         if A > B then return A; else return B; end if;
      end Max_F;

      -- Допоміжна процедура для виведення вектора на екран (тільки для N <= 8)
      procedure Print_Vector(Name : String; V : Vector_Ptr) is
      begin
         if N <= 8 then
            Ada.Text_IO.Put(Name & ": [");
            for I in 1 .. N loop
               Float_IO.Put(Item => V.all(I), Fore => 1, Aft => 1, Exp => 0);
               if I < N then Ada.Text_IO.Put(", "); end if;
            end loop;
            Ada.Text_IO.Put_Line("]");
         end if;
      end Print_Vector;

      -- Допоміжна процедура для виведення матриці на екран (тільки для N <= 8)
      procedure Print_Matrix(Name : String; M : Matrix_Ptr) is
      begin
         if N <= 8 then
            Ada.Text_IO.Put_Line(Name & ":");
            for I in 1 .. N loop
               Ada.Text_IO.Put("  [");
               for J in 1 .. N loop
                  Float_IO.Put(Item => M.all(I, J), Fore => 1, Aft => 1, Exp => 0);
                  if J < N then Ada.Text_IO.Put(", "); end if;
               end loop;
               Ada.Text_IO.Put_Line("]");
            end loop;
         end if;
      end Print_Matrix;
   end Data;

   use Data; -- Підключення пакета Data

   -- Оголошення задач та їх точок входу (рандеву) для передачі повідомлень
   task T1 is
      pragma Storage_Size (200_000_000);
      entry Res_T2(A_Part, E_Part : in Float);
      entry Res_T3(A_Part, E_Part : in Float);
      entry Res_T5(A_Part, E_Part : in Float);
   end T1;

   task T2 is
      pragma Storage_Size (200_000_000);
      entry Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr);
   end T2;

   task T3 is
      pragma Storage_Size (200_000_000);
      entry Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_2H_Ptr);
      entry Res_T4(A_Part, E_Part : in Float);
   end T3;

   task T4 is
      pragma Storage_Size (200_000_000);
      entry Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr);
   end T4;

   task T5 is
      pragma Storage_Size (200_000_000);
      entry Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_4H_Ptr);
      entry Res_T6(A_Part, E_Part : in Float);
      entry Res_T7(A_Part, E_Part : in Float);
   end T5;

   task T6 is
      pragma Storage_Size (200_000_000);
      entry Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_2H_Ptr);
      entry Res_T8(A_Part, E_Part : in Float);
   end T6;

   task T7 is
      pragma Storage_Size (200_000_000);
      entry Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr);
   end T7;

   task T8 is
      pragma Storage_Size (200_000_000);
      entry Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr);
   end T8;

   -- Реалізація алгоритму задачі T1
   task body T1 is
      C, D : Vector_Ptr := new Vector;
      MX, MZ, MR : Matrix_Ptr := new Matrix;
      
      MZ_1, MR_1 : Matrix_H_Ptr := new Matrix_H;
      MZ_2, MR_2 : Matrix_H_Ptr := new Matrix_H;
      MZ_34, MR_34 : Matrix_2H_Ptr := new Matrix_2H;
      MZ_58, MR_58 : Matrix_4H_Ptr := new Matrix_4H;
      
      A1, E1, A2, E2, AT3, ET3, AT5, ET5, A_Final, E_Final, Result : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("Lab5 started");
      Ada.Text_IO.Put_Line("T1 started");
      
      -- 1. Введення C, MX, MR, D, MZ
      for I in 1 .. N loop
         C.all(I) := 1.0; D.all(I) := 1.0;
         for J in 1 .. N loop
            MX.all(I, J) := 1.0; MZ.all(I, J) := 1.0; MR.all(I, J) := 1.0;
         end loop;
      end loop;
      MZ.all(1, N) := -9.0; MR.all(1, N) := 16.0; -- Тестове значення для перевірки
      
      -- Виведення векторів та матриць
      Print_Vector("Vector C", C);
      Print_Vector("Vector D", D);
      Print_Matrix("Matrix MX", MX);
      Print_Matrix("Matrix MZ", MZ);
      Print_Matrix("Matrix MR", MR);
      
      -- Підготовка та нарізка смуг для відправки
      for I in 1 .. N loop
         for J in 1 .. H loop
            MZ_1.all(I, J) := MZ.all(I, J);           MR_1.all(I, J) := MR.all(I, J);
            MZ_2.all(I, J) := MZ.all(I, H + J);       MR_2.all(I, J) := MR.all(I, H + J);
         end loop;
         for J in 1 .. 2*H loop
            MZ_34.all(I, J) := MZ.all(I, 2*H + J);    MR_34.all(I, J) := MR.all(I, 2*H + J);
         end loop;
         for J in 1 .. 4*H loop
            MZ_58.all(I, J) := MZ.all(I, 4*H + J);    MR_58.all(I, J) := MR.all(I, 4*H + J);
         end loop;
      end loop;

      Start_Time := Ada.Calendar.Clock;

      -- 2. Передати задачі T2 C, D, MX, MZH, MRH
      T2.Data(C, D, MX, MZ_2, MR_2);
      
      -- 3. Передати задачі T3 C, D, MX, MZ2H, MR2H
      T3.Data(C, D, MX, MZ_34, MR_34);
      
      -- 4. Передати задачі T5 C, D, MX, MZ4H, MR4H
      T5.Data(C, D, MX, MZ_58, MR_58);

      -- 5. Обчислити 1: a1 = min(C * MZH)
      A1 := Compute_A_Local(C, MZ_1);
      
      -- 6. Обчислити 3: e1 = max(D * (MX * MRH))
      E1 := Compute_E_Local(D, MX, MR_1);

      -- 7. Прийняти від задачі T2 a2 та e2
      accept Res_T2(A_Part, E_Part : in Float) do A2 := A_Part; E2 := E_Part; end Res_T2;
      
      -- 8. Прийняти від задачі T3 aT3 та eT3
      accept Res_T3(A_Part, E_Part : in Float) do AT3 := A_Part; ET3 := E_Part; end Res_T3;
      
      -- 9. Прийняти від задачі T5 aT5 та eT5
      accept Res_T5(A_Part, E_Part : in Float) do AT5 := A_Part; ET5 := E_Part; end Res_T5;

      -- 10. Обчислити 2: a = min(a1, a2, aT3, aT5)
      A_Final := Min_F(A1, Min_F(A2, Min_F(AT3, AT5)));
      
      -- 11. Обчислити 4: e = max(e1, e2, eT3, eT5)
      E_Final := Max_F(E1, Max_F(E2, Max_F(ET3, ET5)));
      
      -- 12. Обчислити 5: a = a + e
      Result := A_Final + E_Final;

      End_Time := Ada.Calendar.Clock;
      Execution_Time := End_Time - Start_Time;

      -- 13. Виведення a
      Ada.Text_IO.Put("FINAL RESULT: a = ");
      Ada.Float_Text_IO.Put(Item => Result, Fore => 1, Aft => 1, Exp => 0);
      Ada.Text_IO.New_Line;
      Ada.Text_IO.Put_Line("Total execution time: " & Duration'Image(Execution_Time) & " seconds");
      Ada.Text_IO.Put_Line("T1 finished");
      Ada.Text_IO.Put_Line("Lab5 finished");
   end T1;

   -- Реалізація алгоритму задачі T2
   task body T2 is
      C, D : Vector_Ptr := new Vector;
      MX : Matrix_Ptr := new Matrix;
      MZ_Loc, MR_Loc : Matrix_H_Ptr := new Matrix_H;
      A2, E2 : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("T2 started");
      -- 1. Прийняти від задачі T1 C, D, MX, MZH, MRH
      accept Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr) do
         C.all := C_In.all; D.all := D_In.all; MX.all := MX_In.all;
         MZ_Loc.all := MZ_In.all; MR_Loc.all := MR_In.all;
      end Data;
      
      -- 2. Обчислити 1: a2 = min(C * MZH)
      A2 := Compute_A_Local(C, MZ_Loc);
      
      -- 3. Обчислити 3: e2 = max(D * (MX * MRH))
      E2 := Compute_E_Local(D, MX, MR_Loc);
      
      -- 4. Передати задачі T1 a2 та e2
      T1.Res_T2(A2, E2);
      Ada.Text_IO.Put_Line("T2 finished");
   end T2;

   -- Реалізація алгоритму задачі T3
   task body T3 is
      C, D : Vector_Ptr := new Vector;
      MX : Matrix_Ptr := new Matrix;
      MZ_Loc, MR_Loc, MZ_4, MR_4 : Matrix_H_Ptr := new Matrix_H;
      A3, E3, A4, E4, AT3, ET3 : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("T3 started");
      -- 1. Прийняти від задачі T1 C, D, MX, MZ2H, MR2H
      accept Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_2H_Ptr) do
         C.all := C_In.all; D.all := D_In.all; MX.all := MX_In.all;
         for I in 1 .. N loop
            for J in 1 .. H loop
               MZ_Loc.all(I, J) := MZ_In.all(I, J);   MR_Loc.all(I, J) := MR_In.all(I, J);
               MZ_4.all(I, J)   := MZ_In.all(I, H+J); MR_4.all(I, J)   := MR_In.all(I, H+J);
            end loop;
         end loop;
      end Data;
      
      -- 2. Передати задачі T4 C, D, MX, MZH, MRH
      T4.Data(C, D, MX, MZ_4, MR_4);
      
      -- 3. Обчислити 1: a3 = min(C * MZH)
      A3 := Compute_A_Local(C, MZ_Loc);
      
      -- 4. Обчислити 3: e3 = max(D * (MX * MRH))
      E3 := Compute_E_Local(D, MX, MR_Loc);
      
      -- 5. Прийняти від задачі T4 a4 та e4
      accept Res_T4(A_Part, E_Part : in Float) do A4 := A_Part; E4 := E_Part; end Res_T4;
      
      -- 6. Обчислити 2: aT3 = min(a3, a4)
      AT3 := Min_F(A3, A4);
      
      -- 7. Обчислити 4: eT3 = max(e3, e4)
      ET3 := Max_F(E3, E4);
      
      -- 8. Передати задачі T1 aT3 та eT3
      T1.Res_T3(AT3, ET3);
      Ada.Text_IO.Put_Line("T3 finished");
   end T3;

   -- Реалізація алгоритму задачі T4
   task body T4 is
      C, D : Vector_Ptr := new Vector;
      MX : Matrix_Ptr := new Matrix;
      MZ_Loc, MR_Loc : Matrix_H_Ptr := new Matrix_H;
      A4, E4 : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("T4 started");
      -- 1. Прийняти від задачі T3 C, D, MX, MZH, MRH
      accept Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr) do
         C.all := C_In.all; D.all := D_In.all; MX.all := MX_In.all;
         MZ_Loc.all := MZ_In.all; MR_Loc.all := MR_In.all;
      end Data;
      
      -- 2. Обчислити 1: a4 = min(C * MZH)
      A4 := Compute_A_Local(C, MZ_Loc);
      
      -- 3. Обчислити 3: e4 = max(D * (MX * MRH))
      E4 := Compute_E_Local(D, MX, MR_Loc);
      
      -- 4. Передати задачі T3 a4 та e4
      T3.Res_T4(A4, E4);
      Ada.Text_IO.Put_Line("T4 finished");
   end T4;

   -- Реалізація алгоритму задачі T5
   task body T5 is
      C, D : Vector_Ptr := new Vector;
      MX : Matrix_Ptr := new Matrix;
      MZ_Loc, MR_Loc, MZ_7, MR_7 : Matrix_H_Ptr := new Matrix_H;
      MZ_68, MR_68 : Matrix_2H_Ptr := new Matrix_2H;
      A5, E5, AT6, ET6, A7, E7, AT5, ET5 : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("T5 started");
      -- 1. Прийняти від задачі T1 C, D, MX, MZ4H, MR4H
      accept Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_4H_Ptr) do
         C.all := C_In.all; D.all := D_In.all; MX.all := MX_In.all;
         for I in 1 .. N loop
            for J in 1 .. H loop
               MZ_Loc.all(I, J) := MZ_In.all(I, J);        MR_Loc.all(I, J) := MR_In.all(I, J);
               MZ_68.all(I, J)  := MZ_In.all(I, H+J);      MR_68.all(I, J)  := MR_In.all(I, H+J);
               MZ_7.all(I, J)   := MZ_In.all(I, 2*H+J);    MR_7.all(I, J)   := MR_In.all(I, 2*H+J);
               MZ_68.all(I, H+J):= MZ_In.all(I, 3*H+J);    MR_68.all(I, H+J):= MR_In.all(I, 3*H+J);
            end loop;
         end loop;
      end Data;
      
      -- 2. Передати задачі T6 C, D, MX, MZ2H, MR2H
      T6.Data(C, D, MX, MZ_68, MR_68);
      
      -- 3. Передати задачі T7 C, D, MX, MZH, MRH
      T7.Data(C, D, MX, MZ_7, MR_7);
      
      -- 4. Обчислити 1: a5 = min(C * MZH)
      A5 := Compute_A_Local(C, MZ_Loc);
      
      -- 5. Обчислити 3: e5 = max(D * (MX * MRH))
      E5 := Compute_E_Local(D, MX, MR_Loc);
      
      -- 6. Прийняти від задачі T6 aT6 та eT6
      accept Res_T6(A_Part, E_Part : in Float) do AT6 := A_Part; ET6 := E_Part; end Res_T6;
      
      -- 7. Прийняти від задачі T7 a7 та e7
      accept Res_T7(A_Part, E_Part : in Float) do A7 := A_Part; E7 := E_Part; end Res_T7;
      
      -- 8. Обчислити 2: aT5 = min(a5, aT6, a7)
      AT5 := Min_F(A5, Min_F(AT6, A7));
      
      -- 9. Обчислити 4: eT5 = max(e5, eT6, e7)
      ET5 := Max_F(E5, Max_F(ET6, E7));
      
      -- 10. Передати задачі T1 aT5 та eT5
      T1.Res_T5(AT5, ET5);
      Ada.Text_IO.Put_Line("T5 finished");
   end T5;

   -- Реалізація алгоритму задачі T6
   task body T6 is
      C, D : Vector_Ptr := new Vector;
      MX : Matrix_Ptr := new Matrix;
      MZ_Loc, MR_Loc, MZ_8, MR_8 : Matrix_H_Ptr := new Matrix_H;
      A6, E6, A8, E8, AT6, ET6 : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("T6 started");
      -- 1. Прийняти від задачі T5 C, D, MX, MZ2H, MR2H
      accept Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_2H_Ptr) do
         C.all := C_In.all; D.all := D_In.all; MX.all := MX_In.all;
         for I in 1 .. N loop
            for J in 1 .. H loop
               MZ_Loc.all(I, J) := MZ_In.all(I, J);   MR_Loc.all(I, J) := MR_In.all(I, J);
               MZ_8.all(I, J)   := MZ_In.all(I, H+J); MR_8.all(I, J)   := MR_In.all(I, H+J);
            end loop;
         end loop;
      end Data;
      
      -- 2. Передати задачі T8 C, D, MX, MZH, MRH
      T8.Data(C, D, MX, MZ_8, MR_8);
      
      -- 3. Обчислити 1: a6 = min(C * MZH)
      A6 := Compute_A_Local(C, MZ_Loc);
      
      -- 4. Обчислити 3: e6 = max(D * (MX * MRH))
      E6 := Compute_E_Local(D, MX, MR_Loc);
      
      -- 5. Прийняти від задачі T8 a8 та e8
      accept Res_T8(A_Part, E_Part : in Float) do A8 := A_Part; E8 := E_Part; end Res_T8;
      
      -- 6. Обчислити 2: aT6 = min(a6, a8)
      AT6 := Min_F(A6, A8);
      
      -- 7. Обчислити 4: eT6 = max(e6, e8)
      ET6 := Max_F(E6, E8);
      
      -- 8. Передати задачі T5 aT6 та eT6
      T5.Res_T6(AT6, ET6);
      Ada.Text_IO.Put_Line("T6 finished");
   end T6;

   -- Реалізація алгоритму задачі T7
   task body T7 is
      C, D : Vector_Ptr := new Vector;
      MX : Matrix_Ptr := new Matrix;
      MZ_Loc, MR_Loc : Matrix_H_Ptr := new Matrix_H;
      A7, E7 : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("T7 started");
      -- 1. Прийняти від задачі T5 C, D, MX, MZH, MRH
      accept Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr) do
         C.all := C_In.all; D.all := D_In.all; MX.all := MX_In.all;
         MZ_Loc.all := MZ_In.all; MR_Loc.all := MR_In.all;
      end Data;
      
      -- 2. Обчислити 1: a7 = min(C * MZH)
      A7 := Compute_A_Local(C, MZ_Loc);
      
      -- 3. Обчислити 3: e7 = max(D * (MX * MRH))
      E7 := Compute_E_Local(D, MX, MR_Loc);
      
      -- 4. Передати задачі T5 a7 та e7
      T5.Res_T7(A7, E7);
      Ada.Text_IO.Put_Line("T7 finished");
   end T7;

   -- Реалізація алгоритму задачі T8
   task body T8 is
      C, D : Vector_Ptr := new Vector;
      MX : Matrix_Ptr := new Matrix;
      MZ_Loc, MR_Loc : Matrix_H_Ptr := new Matrix_H;
      A8, E8 : Float;
   begin
      delay 15.0;
      Ada.Text_IO.Put_Line("T8 started");
      -- 1. Прийняти від задачі T6 C, D, MX, MZH, MRH
      accept Data(C_In, D_In : Vector_Ptr; MX_In : Matrix_Ptr; MZ_In, MR_In : Matrix_H_Ptr) do
         C.all := C_In.all; D.all := D_In.all; MX.all := MX_In.all;
         MZ_Loc.all := MZ_In.all; MR_Loc.all := MR_In.all;
      end Data;
      
      -- 2. Обчислити 1: a8 = min(C * MZH)
      A8 := Compute_A_Local(C, MZ_Loc);
      
      -- 3. Обчислити 3: e8 = max(D * (MX * MRH))
      E8 := Compute_E_Local(D, MX, MR_Loc);
      
      -- 4. Передати задачі T6 a8 та e8
      T6.Res_T8(A8, E8);
      Ada.Text_IO.Put_Line("T8 finished");
   end T8;

begin
   null;
end Lab5;