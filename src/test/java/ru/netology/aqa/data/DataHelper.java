package ru.netology.aqa.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataHelper {

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

    // Текущий год в формате YY +1 год
    public static String getNextYear() {
        return String.valueOf(
                Integer.parseInt(getCurrentYear()) + 1
        );
    }

    public static String getPreviousYear() {
        return String.valueOf(
                Integer.parseInt(getCurrentYear()) - 5
        );
    }
    // Валидный владелец
    public static String getValidOwner() {
        return "MANUAL TESTING";
    }

    // Валидный CVC/CVV
    public static String getValidCvc() {
        return "123";
    }
}
