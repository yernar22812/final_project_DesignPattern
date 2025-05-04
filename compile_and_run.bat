@echo off
echo Компиляция Tower Defense Game...

:: Компилируем все Java файлы
javac -d . Tamerlan_towers\*.java Yernar_map\*.java *.java

:: Проверяем успешность компиляции
if %errorlevel% neq 0 (
    echo Ошибка компиляции! Проверьте код и попробуйте снова.
    pause
    exit /b
)

echo Компиляция завершена успешно!
echo Запуск игры...

:: Запускаем игру
java Main

pause
