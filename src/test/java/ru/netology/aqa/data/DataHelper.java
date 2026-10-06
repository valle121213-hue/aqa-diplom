package ru.netology.aqa.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {

    private static final Faker FAKER = new Faker(new Locale("en"));

    private DataHelper() {
    }

    // APPROVED-карта
    public static String getApprovedCardNumber() {
        return "1111 2222 3333 4444";
    }

    // DECLINED-карта
    public static String getDeclinedCardNumber() {
        return "5555 6666 7777 8888";
    }

    // Невалидная карта
    public static String getInvalidCardNumber() {
        return "1111 2222 4567 7889";
    }

    // Текущий месяц в формате MM
    public static String getCurrentMonth() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MM"));
    }

    // Текущий год в формате YY
    public static String getCurrentYear() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yy"));
    }

    // Следующий год в формате YY
    public static String getNextYear() {
        return String.valueOf(
                Integer.parseInt(getCurrentYear()) + 1
        );
    }

    // Предыдущий год в формате YY
    public static String getPreviousYear() {
        return String.valueOf(
                Integer.parseInt(getCurrentYear()) - 1
        );
    }

    // Следующий месяц в формате MM
    public static String getNextMonth() {
        return LocalDate.now()
                .plusMonths(1)
                .format(DateTimeFormatter.ofPattern("MM"));
    }

    // Год следующего месяца в формате YY
    public static String getNextMonthYear() {
        return LocalDate.now()
                .plusMonths(1)
                .format(DateTimeFormatter.ofPattern("yy"));
    }

    // Случайный валидный CVC/CVV
    public static String getValidCvc() {
        return FAKER.numerify("###");
    }

    // Случайный валидный владелец
    public static String getValidOwner() {
        return FAKER.regexify("[A-Z]{3,10} [A-Z]{3,10}");
    }

    //длина поля Владелец
    public static String getOwnerMoreThanMaxLength() {
        StringBuilder owner = new StringBuilder();

        for (int i = 0; i < 50; i++) {
            owner.append("A");
        }

        return owner.toString();
    }
    // для Латиницы
    public static String getLatinLetters(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append("A");
        }

        return value.toString();
    }

    // для Кириллицы
    public static String getCyrillicLetters(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append("Б");
        }

        return value.toString();
    }

    // Только спецсимволы
    public static String getSpecialCharacters(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append("@");
        }

        return value.toString();
    }

    // Только пробелы
    public static String getSpaces(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append(" ");
        }

        return value.toString();
    }

    // только цифры
    public static String getDigits(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append("1");
        }

        return value.toString();
    }

    // Метод для пустого поля
    public static String getEmptyValue() {
        return "";
    }

    //Буквы с цифрами для ввода
    public static String getLatinLettersWithDigits(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append("A");
        }

        for (int i = 0; i < length; i++) {
            value.append("1");
        }

        return value.toString();
    }

    // Буквы со специальными символами для ввода
    public static String getLatinLettersWithSpecialCharacters(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append("A");

            if (i < length - 1) {
                value.append("@");
            }
        }

        return value.toString();
    }


    // метод, который принимает количество цифр и вставляет между ними спецсимволы
    public static String getDigitsWithSpecialCharacters(int length) {
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            value.append("1");

            if (i < length - 1) {
                value.append("@");
            }
        }

        return value.toString();
    }

    public static String getLowercaseOwner() {
        return "manual test";
    }

    public static String getOwnerWithMultipleSpaces() {
        return "MANUAL    TEST";
    }

    public static String getOwnerWithSpacesAround() {
        return " MANUAL TEST ";
    }

    public static String getOwnerWithHyphen() {
        return "MANUAL TEST-TEST";
    }

    public static String getCvcWithSpace() {
        return "1 23";
    }
}
