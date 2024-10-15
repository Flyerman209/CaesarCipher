package org.example;

import java.io.*;
import java.util.*;

public class CaesarCipher {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите режим работы программы:");
        System.out.println("1. Шифрование");
        System.out.println("2. Расшифровка с ключом");
        System.out.println("3. Brute Force (перебор ключей)");
        System.out.println("4. Статистический анализ");

        int mode = scanner.nextInt();
        scanner.nextLine();  // Пропуск новой строки после ввода числа

        System.out.println("Введите полный путь к файлу для обработки:");
        String filePath = scanner.nextLine();

        String content = readFile(filePath);
        if (content == null) {
            System.out.println("Ошибка при чтении файла.");
            return;
        }

        switch (mode) {
            case 1:
                System.out.println("Введите ключ для шифра (целое число): ");
                int encryptKey = scanner.nextInt();
                String encryptedText = caesarCipher(content, encryptKey);
                writeFile(filePath.replace(".txt", "_encrypted.txt"), encryptedText);
                System.out.println("Шифрование завершено. Зашифрованный текст записан в файл.");
                break;
            case 2:
                System.out.println("Введите ключ для расшифровки (целое число): ");
                int decryptKey = scanner.nextInt();
                String decryptedText = caesarCipher(content, -decryptKey);
                writeFile(filePath.replace(".txt", "_decrypted.txt"), decryptedText);
                System.out.println("Расшифровка завершена. Расшифрованный текст записан в файл.");
                break;
            case 3:
                System.out.println("Введите полный путь к файлу для вывода результата Brute Force:");
                String bruteForceOutputPath = scanner.nextLine();
                bruteForce(content, bruteForceOutputPath);
                break;
            case 4:
                int key = frequencyAnalysis(content);
                System.out.println("Предполагаемый ключ: " + key);
                String autoDecryptedText = caesarCipher(content, -key);
                writeFile(filePath.replace(".txt", "_auto_decrypted.txt"), autoDecryptedText);
                System.out.println("Статистический анализ завершен. Расшифрованный текст записан в файл.");
                break;
            default:
                System.out.println("Неверный режим работы.");
        }
    }

    // Чтение содержимого файла
    private static String readFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.toUpperCase()).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Запись данных в файл
    private static void writeFile(String fileName, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод шифрования/расшифровки с помощью шифра Цезаря
    private static String caesarCipher(String text, int key) {
        StringBuilder result = new StringBuilder();
        key = key % 26;  // Ограничение ключа для алфавита

        for (char c : text.toCharArray()) {
            if (ALPHABET.indexOf(c) != -1) {
                int originalPosition = ALPHABET.indexOf(c);
                int newPosition = (originalPosition + key + 26) % 26;
                result.append(ALPHABET.charAt(newPosition));
            } else {
                result.append(c);  // Оставляем символы, которые не являются буквами
            }
        }
        return result.toString();
    }

    // Brute Force: Перебор всех ключей с выводом в файл
    private static void bruteForce(String encryptedText, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (int key = 1; key <= 25; key++) {
                String decryptedText = caesarCipher(encryptedText, -key);
                writer.write("Ключ: " + key + "\n");
                writer.write(decryptedText + "\n");
                writer.write("----------------------------\n");
            }
            System.out.println("Результаты Brute Force записаны в файл: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи файла: " + e.getMessage());
        }
    }

    // Статистический анализ для нахождения ключа
    private static int frequencyAnalysis(String encryptedText) {
        int[] letterFrequencies = new int[26];
        for (char c : encryptedText.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index != -1) {
                letterFrequencies[index]++;
            }
        }

        // Находим букву с максимальной частотой в зашифрованном тексте
        int maxIndex = 0;
        for (int i = 1; i < letterFrequencies.length; i++) {
            if (letterFrequencies[i] > letterFrequencies[maxIndex]) {
                maxIndex = i;
            }
        }

        // Предполагаем, что самая частая буква - это 'E'
        int assumedEPosition = ALPHABET.indexOf('E');
        int key = (maxIndex - assumedEPosition + 26) % 26;

        return key;
    }
}
