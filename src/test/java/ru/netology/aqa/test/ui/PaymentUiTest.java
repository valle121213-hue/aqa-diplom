package ru.netology.aqa.test.ui;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.data.SQLHelper;
import ru.netology.aqa.page.DashboardPage;
import ru.netology.aqa.page.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentUiTest {

    private DashboardPage dashboardPage;

    @BeforeEach
    void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide());

        SQLHelper.cleanDatabase();
        dashboardPage = open(
                "http://localhost:8080",
                DashboardPage.class
        );
    }

    //AUT-01. Успешная покупка с APPROVED-картой
    @Test
    void shouldMakeSuccessfulPaymentWithApprovedCard() {

        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowSuccessTitle("Успешно");
        paymentPage.shouldShowSuccessContent("Операция одобрена Банком.");

        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    //AUT-02. Отказ в покупке с DECLINED-картой
    @Test
    void shouldShowErrorForDeclinedCard() {

        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getDeclinedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("DECLINED", SQLHelper.getPaymentStatus());

        paymentPage.shouldShowInvalidCardErrorTitle("Ошибка");
        paymentPage.shouldShowInvalidCardErrorContent(
                "Ошибка! Банк отказал в проведении операции."
        );
    }

    //AUT-03. Некорректный номер карты
    @Test
    void shouldRejectPaymentWithInvalidCardNumber() {

        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getInvalidCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowInvalidCardErrorTitle("Ошибка");
        paymentPage.shouldShowInvalidCardErrorContent(
                "Ошибка! Банк отказал в проведении операции."
        );

    }

    //AUT-04. Пустой номер карты
    @Test
    void shouldShowErrorForEmptyCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getEmptyValue());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberFormatError("Неверный формат");

    }

    //AUT-05. Номер карты: менее 16 цифр
    @Test
    void shouldShowErrorForShortCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getDigits(12));
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberFormatError("Неверный формат");

    }

    // AUT-06. Номер карты: более 16 цифр
    @Test
    void shouldNotAllowMoreThan16DigitsInCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getDigits(20));

        paymentPage.shouldHaveCardNumberLength(16);
    }

    //AUT-07a. Номер карты: введены буквы латиницы
    @Test
    void shouldShowErrorForLettersInCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getLatinLetters(16));
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberFormatError("Неверный формат");

    }

    //AUT-07b. Номер карты: введены буквы кириллицы
    @Test
    void shouldRejectLettersInCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getCyrillicLetters(16));
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberFormatError("Неверный формат");

    }

    // AUT-08a. Поле «Номер карты»: специальные символы с цифрами
    @Test
    void shouldIgnoreSpecialCharactersInCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(
                DataHelper.getDigitsWithSpecialCharacters(16)
        );

        paymentPage.shouldHaveCardNumberLength(16);
    }

    // AUT-08b. Номер карты: вводится с пробелами вручную
    @Test
    void shouldIgnoreSpacesInCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String cardNumberWithSpaces =
                DataHelper.getApprovedCardNumber();

        paymentPage.fillCardNumber(cardNumberWithSpaces);

        paymentPage.shouldHaveCardNumberLength(16);
    }

    //AUT-08c. Номер карты: только спецсимволы (без цифр)
    @Test
    void shouldRejectSpecialCharactersInCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getSpecialCharacters(16));
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberFormatError("Неверный формат");
    }

    // AUT-08d. Номер карты: только пробелы (без цифр)
    @Test
    void shouldShowErrorForSpacesOnlyInCardNumber() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getSpaces(16));
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberFormatError("Неверный формат");

    }
    //Проверка поля «Месяц»

    //AUT-09. Поле «Месяц»: пустое значение
    @Test
    void shouldShowErrorForEmptyMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getEmptyValue());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthFormatError("Неверный формат");
    }

    // AUT-09b. Поле «Месяц»: одна цифра
    @Test
    void shouldShowErrorForOneDigitMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getDigits(1));
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthFormatError("Неверный формат");
    }

    // AUT-09c. Поле «Месяц»: три цифры
    @Test
    void shouldLimitMonthToTwoDigits() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillMonth(DataHelper.getDigits(3));

        paymentPage.shouldHaveMonthLength(2);
    }

    //AUT-10. Поле «Месяц»: значение 00
    @Test
    void shouldShowPeriodErrorForZeroMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("00");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthPeriodError(
                "Неверно указан срок действия карты"
        );
    }

    // AUT-11. Поле «Месяц»: значение 01 (минимально допустимая граница)
    @Test
    void shouldAcceptMinimumValidMonthForNextYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("01");
        paymentPage.fillYear(DataHelper.getNextYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldNotShowValidationErrors();
    }

    //AUT-12. Поле «Месяц»: значение 13 (недопустимое)
    @Test
    void shouldShowPeriodErrorForInvalidMonth13() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("13");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthPeriodError(
                "Неверно указан срок действия карты"
        );
    }

    // AUT-13a. Поле «Месяц»: введены буквы латиницы
    @Test
    void shouldShowErrorForLatinLettersInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getLatinLetters(2));
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthFormatError("Неверный формат");
    }

    // AUT-13b. Поле «Месяц»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicLettersInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCyrillicLetters(2));
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthFormatError("Неверный формат");
    }

    // AUT-14a. Поле «Месяц»: специальные символы с цифрами
    @Test
    void shouldIgnoreSpecialCharactersInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillMonth(
                DataHelper.getDigitsWithSpecialCharacters(2)
        );

        paymentPage.shouldHaveMonthValue(
                DataHelper.getDigits(2)
        );
    }

    // AUT-14b. Поле «Месяц»: только спецсимволы (без цифр)
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getSpecialCharacters(2));
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthFormatError("Неверный формат");
    }

    // AUT-14c. Поле «Месяц»: только пробелы (без цифр)
    @Test
    void shouldShowErrorForSpacesOnlyInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getSpaces(2));
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowMonthFormatError("Неверный формат");
    }

    // AUT-14d. Поле «Месяц»: цифры с пробелом
    @Test
    void shouldIgnoreSpaceInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String currentMonth = DataHelper.getCurrentMonth();
        String monthWithSpace = currentMonth.charAt(0) + " " + currentMonth.charAt(1);

        paymentPage.fillMonth(monthWithSpace);

        paymentPage.shouldHaveMonthValue(currentMonth);
    }


    // Проверка поля «Год»

    // AUT-15a. Поле «Год»: пустое значение
    @Test
    void shouldShowErrorForEmptyYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getEmptyValue());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearFormatError("Неверный формат");
    }


    // AUT-15b. Поле «Год»: одна цифра
    @Test
    void shouldShowErrorForOneDigitYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getDigits(1));
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearFormatError("Неверный формат");
    }

    // AUT-15c. Поле «Год»: три цифры
    @Test
    void shouldLimitYearToTwoDigits() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillYear(DataHelper.getDigits(3));

        paymentPage.shouldHaveYearLength(2);
    }

    // AUT-16. Поле «Год»: значение 00
    @Test
    void shouldShowExpiredCardErrorForZeroYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("00");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearExpiredError(
                "Истёк срок действия карты"
        );
    }

    // AUT-17. Поле «Год»: истёкший год
    @Test
    void shouldShowExpiredCardErrorForPreviousYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getPreviousYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearExpiredError(
                "Истёк срок действия карты"
        );
    }

    // AUT-18. Поле «Год»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicLettersInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCyrillicLetters(2));
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearFormatError("Неверный формат");
    }

    // AUT-19. Поле «Год»: введены буквы латиницы
    @Test
    void shouldShowErrorForLatinLettersInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getLatinLetters(2));
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearFormatError("Неверный формат");
    }

    // AUT-20b. Поле «Год»: введены только специальные символы
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getSpecialCharacters(2));
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearFormatError("Неверный формат");
    }

    // AUT-20c. Поле «Год»: только пробелы
    @Test
    void shouldShowErrorForSpacesOnlyInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getSpaces(2));
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearFormatError("Неверный формат");
    }

    // AUT-20d. Поле «Год»: числа с пробелом
    @Test
    void shouldIgnoreSpaceInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String currentYear = DataHelper.getCurrentYear();
        String yearWithSpace = currentYear.charAt(0) + " " + currentYear.charAt(1);

        paymentPage.fillYear(yearWithSpace);

        paymentPage.shouldHaveYearValue(currentYear);
    }

    // Проверка поля «Владелец»

    // AUT-21a. Поле «Владелец»: пустое значение
    @Test
    void shouldShowErrorForEmptyOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getEmptyValue());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowOwnerFormatError("Поле обязательно для заполнения");
    }

    // AUT-21b. Поле «Владелец»: значение короче минимальной длины
    @Test
    void shouldShowErrorForShortOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("A");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowOwnerInvalidFormatError("Неверный формат");
    }

    // AUT-21c. Поле «Владелец»: ввод длинного значения
    @Test
    void shouldAccept50CharactersInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillOwner(DataHelper.getOwnerMoreThanMaxLength());

        paymentPage.shouldHaveOwnerLength(50);
    }

    // AUT-22a. Поле «Владелец»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getCyrillicLetters(20));
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowOwnerInvalidFormatError("Неверный формат");
    }

    // AUT-22b. Поле «Владелец»: нижний регистр латинскими буквами
    @Test
    void shouldConvertOwnerToUppercase() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillOwner(DataHelper.getLowercaseOwner());

        paymentPage.shouldHaveOwnerValue("MANUAL TEST");
    }

    // AUT-23a. Поле «Владелец»: значение с цифрами
    @Test
    void shouldIgnoreDigitsInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String ownerWithDigits = DataHelper.getLatinLettersWithDigits(5);

        paymentPage.fillOwner(ownerWithDigits);

        paymentPage.shouldHaveOwnerValue(DataHelper.getLatinLetters(5));
    }

    // AUT-23b. Поле «Владелец»: только цифры
    @Test
    void shouldShowErrorForDigitsOnlyInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getDigits(10));
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowOwnerFormatError("Поле обязательно для заполнения");
    }

    // AUT-23c. Поле «Владелец»: пробелы подряд
    @Test
    void shouldShowErrorForSpacesOnlyInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getSpaces(10));
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowOwnerFormatError("Поле обязательно для заполнения");
    }

    // AUT-23d. Поле «Владелец»: множественные пробелы между словами
    @Test
    void shouldReduceMultipleSpacesBetweenOwnerWords() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getOwnerWithMultipleSpaces());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldHaveOwnerValue("MANUAL TEST");
    }

    // AUT-23e. Поле «Владелец»: пробелы в начале и в конце
    @Test
    void shouldTrimSpacesAroundOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getOwnerWithSpacesAround());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldHaveOwnerValue("MANUAL TEST");
    }

    // AUT-24a. Поле «Владелец»: буквы со специальными символами
    @Test
    void shouldIgnoreSpecialCharactersInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillOwner(
                DataHelper.getLatinLettersWithSpecialCharacters(5)
        );

        paymentPage.shouldHaveOwnerValue(
                DataHelper.getLatinLetters(5)
        );
    }

    // AUT-24b. Поле «Владелец»: только специальные символы
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getSpecialCharacters(20));
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowOwnerFormatError("Поле обязательно для заполнения");
    }


    // AUT-24c. Поле «Владелец»: имя с дефисом
    @Test
    void shouldAcceptOwnerWithHyphen() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getOwnerWithHyphen());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldHaveOwnerValue("MANUAL TEST-TEST");
        paymentPage.shouldNotShowOwnerRequiredError();
        paymentPage.shouldNotShowOwnerInvalidFormatError();
    }


    //Проверка поля «CVC/CVV»

    // AUT-25. Поле «CVC/CVV»: пустое значение
    @Test
    void shouldShowErrorForEmptyCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getEmptyValue());

        paymentPage.clickContinue();

        paymentPage.shouldShowCvcFormatError("Неверный формат");
    }

    //  AUT-26. Поле «CVC/CVV»: две цифры
    @Test
    void shouldShowErrorForTwoDigitsInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getDigits(2));

        paymentPage.clickContinue();

        paymentPage.shouldShowCvcFormatError("Неверный формат");
    }

    // AUT-27. Поле «CVC/CVV»: четыре цифры
    @Test
    void shouldLimitCvcToThreeDigits() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCvc(DataHelper.getDigits(4));

        paymentPage.shouldHaveCvcValue(DataHelper.getDigits(3));
    }

    // AUT-28a. Поле «CVC/CVV»: введены буквы латиницы
    @Test
    void shouldShowErrorForLatinLettersInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getLatinLetters(3));

        paymentPage.clickContinue();

        paymentPage.shouldShowCvcFormatError("Неверный формат");
    }

    // AUT-28b. Поле «CVC/CVV»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicLettersInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getCyrillicLetters(3));

        paymentPage.clickContinue();

        paymentPage.shouldShowCvcFormatError("Неверный формат");
    }

    // AUT-29a. Поле «CVC/CVV»: специальные символы с цифрами
    @Test
    void shouldIgnoreSpecialCharactersInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCvc(
                DataHelper.getDigitsWithSpecialCharacters(3)
        );

        paymentPage.shouldHaveCvcValue(
                DataHelper.getDigits(3)
        );
    }


    // AUT-29b. Поле «CVC/CVV»: введены только специальные символы
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getSpecialCharacters(3));

        paymentPage.clickContinue();

        paymentPage.shouldShowCvcFormatError("Неверный формат");

        paymentPage.shouldNotShowOwnerRequiredError();
    }

    // AUT-29c. Поле «CVC/CVV»: введены цифры с пробелами
    @Test
    void shouldIgnoreSpaceInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCvc(DataHelper.getCvcWithSpace());

        paymentPage.shouldHaveCvcValue("123");
    }

    // AUT-29d. Поле «CVC/CVV»: введены только пробелы
    @Test
    void shouldShowErrorForSpacesOnlyInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getSpaces(3));

        paymentPage.clickContinue();

        paymentPage.shouldShowCvcFormatError("Неверный формат");
    }

    // AUT-30. Отправка формы с пустыми обязательными полями
    @Test
    void shouldShowErrorsForAllEmptyRequiredFields() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getEmptyValue());
        paymentPage.fillMonth(DataHelper.getEmptyValue());
        paymentPage.fillYear(DataHelper.getEmptyValue());
        paymentPage.fillOwner(DataHelper.getEmptyValue());
        paymentPage.fillCvc(DataHelper.getEmptyValue());

        paymentPage.clickContinue();

        paymentPage.shouldShowCardNumberFormatError("Неверный формат");

        paymentPage.shouldShowMonthFormatError("Неверный формат");

        paymentPage.shouldShowYearFormatError("Неверный формат");

        paymentPage.shouldShowOwnerFormatError(
                "Поле обязательно для заполнения"
        );

        paymentPage.shouldShowCvcFormatError("Неверный формат");
    }

    // AUT-31. Снятие подсветки полей после корректного заполнения
    @Test
    void shouldRemoveErrorsAfterCorrectFieldFilling() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        // Этап 1. Отправляем пустую форму
        paymentPage.clickContinue();

        // Этап 2. Заполняем все поля валидными данными
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        // Повторно отправляем форму
        paymentPage.clickContinue();

        // Ошибки валидации должны исчезнуть
        paymentPage.shouldNotShowValidationErrors();
    }


    // AUT-32. Карта истекает в текущем месяце
    @Test
    void shouldAcceptCardExpiringThisMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldNotShowMonthErrors();
        paymentPage.shouldNotShowYearErrors();
    }


    // AUT-33. Карта действительна ровно 1 месяц
    @Test
    void shouldAcceptCardValidForOneMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String nextMonth = DataHelper.getNextMonth();
        String nextYear = DataHelper.getNextMonthYear();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(nextMonth);
        paymentPage.fillYear(nextYear);
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldNotShowMonthErrors();
        paymentPage.shouldNotShowYearErrors();
    }


    // ### AUT-34. Поле «Год»: отдалённое будущее (99)
    @Test
    void shouldShowPeriodErrorForDistantFutureYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("99");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearPeriodError(
                "Неверно указан срок действия карты"
        );
    }

    // ### AUT-35. Карта истекла в прошлом году (текущий месяц + прошлый год)
    @Test
    void shouldShowExpiredErrorForPreviousYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getPreviousYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        paymentPage.shouldShowYearExpiredError(
                "Истёк срок действия карты"
        );
    }

}

