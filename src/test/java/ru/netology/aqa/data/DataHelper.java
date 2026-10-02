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

    // Случайный валидный владелец
    public static String getValidOwner() {
        return FAKER.regexify("[A-Z]{3,10} [A-Z]{3,10}");
    }

    // Случайный валидный CVC/CVV
    public static String getValidCvc() {
        return FAKER.numerify("###");
    }
}
