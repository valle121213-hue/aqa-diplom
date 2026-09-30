package ru.netology.aqa.test.ui;

import org.junit.jupiter.api.BeforeEach;
import ru.netology.aqa.data.DataHelper;
import ru.netology.aqa.page.DashboardPage;
import ru.netology.aqa.page.PaymentPage;
import org.junit.jupiter.api.Test;
import ru.netology.aqa.data.SQLHelper;

import java.sql.SQLException;

import static com.codeborne.selenide.Configuration.timeout;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentUiTest {

    private DashboardPage dashboardPage;

    @BeforeEach
    void setUp() throws SQLException {

        SQLHelper.cleanDatabase();

        timeout = 10_000;

        dashboardPage = open(
                "http://localhost:8080",
                DashboardPage.class
        );
    }

    //AUT-01. Успешная покупка с APPROVED-картой
    @Test
    void shouldMakeSuccessfulPaymentWithApprovedCard() throws SQLException {

        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    //AUT-02. Отказ в покупке с DECLINED-картой
    @Test
    void shouldShowErrorForDeclinedCard() throws SQLException {

        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getDeclinedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("DECLINED", SQLHelper.getPaymentStatus());

        assertEquals("Ошибка", paymentPage.getInvalidCardErrorTitle());
        assertEquals(
                "Ошибка! Банк отказал в проведении операции.",
                paymentPage.getInvalidCardErrorContent()
        );
    }

    //AUT-03. Некорректный номер карты
    @Test
    void shouldRejectPaymentWithInvalidCardNumber() throws SQLException {

        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("1111 2222 4567 7889");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Ошибка", paymentPage.getInvalidCardErrorTitle());
        assertEquals(
                "Ошибка! Банк отказал в проведении операции.",
                paymentPage.getInvalidCardErrorContent()
        );

    }

    //AUT-04. Пустой номер карты
    @Test
    void shouldShowErrorForEmptyCardNumber() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCardNumberFormatError()
        );

    }

    //AUT-05. Номер карты: менее 16 цифр
    @Test
    void shouldShowErrorForShortCardNumber() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("1111 2222 3333");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCardNumberFormatError()
        );

    }

    //AUT-06. Номер карты: более 16 цифр
    @Test
    void shouldIgnoreExtraCardDigitAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("1111 2222 3333 44445");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    //AUT-07a. Номер карты: введены буквы латиницы
    @Test
    void shouldShowErrorForLettersInCardNumber() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("AAAA BBBB CCCC DDDD");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCardNumberFormatError()
        );

    }

    //AUT-07b. Номер карты: введены буквы кириллицы
    @Test
    void shouldRejectLettersInCardNumber() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("АААА ББББ ВВВВ ГГГГ");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCardNumberFormatError()
        );

    }

    //AUT-08a. Номер карты: спецсимволы вперемешку с цифрами
    @Test
    void shouldIgnoreSpecialCharactersInCardNumber() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("1111@2222#3333$4444");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-08b. Номер карты: вводится с пробелами вручную
    @Test
    void shouldMakeSuccessfulPaymentWithCardNumberEnteredWithSpaces() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("1111 2222 3333 4444");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    //AUT-08c. Номер карты: только спецсимволы (без цифр)
    @Test
    void shouldRejectSpecialCharactersInCardNumber() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("@@@@");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCardNumberFormatError()
        );

    }

    // AUT-08d. Номер карты: только пробелы (без цифр)
    @Test
    void shouldShowErrorForSpacesOnlyInCardNumber() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("                ");
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCardNumberFormatError()
        );

    }
    //Проверка поля «Месяц»

    //AUT-09. Поле «Месяц»: пустое значение
    @Test
    void shouldShowErrorForEmptyMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getMonthFormatError()
        );
    }

    // AUT-09b. Поле «Месяц»: одна цифра
    @Test
    void shouldShowErrorForOneDigitMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("1");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getMonthFormatError()
        );
    }

    // AUT-09c. Поле «Месяц»: три цифры
    @Test
    void shouldLimitMonthToTwoDigitsAndShowPeriodError() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("234");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверно указан срок действия карты",
                paymentPage.getMonthPeriodError()
        );
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

        assertEquals(
                "Неверно указан срок действия карты",
                paymentPage.getMonthPeriodError()
        );
    }

    //AUT-11. Поле «Месяц»: значение 01 (минимально допустимая граница)
    @Test
    void shouldMakeSuccessfulPaymentWithMinimumValidMonth() throws SQLException{
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("01");
        paymentPage.fillYear(DataHelper.getNextYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
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

        assertEquals(
                "Неверно указан срок действия карты",
                paymentPage.getMonthPeriodError()
        );
    }

    // AUT-13a. Поле «Месяц»: введены буквы латиницы
    @Test
    void shouldShowErrorForLatinLettersInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("AB");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getMonthFormatError()
        );
    }

    // AUT-13b. Поле «Месяц»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicLettersInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("АБ");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getMonthFormatError()
        );
    }

    // AUT-14a. Поле «Месяц»: введены специальные символы с цифрами
    @Test
    void shouldIgnoreSpecialCharactersInMonth() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("@1#2");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-14b. Поле «Месяц»: только спецсимволы (без цифр)
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("@#$");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getMonthFormatError()
        );
    }

    // AUT-14c. Поле «Месяц»: только пробелы (без цифр)
    @Test
    void shouldShowErrorForSpacesOnlyInMonth() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth("   ");
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getMonthFormatError()
        );
    }

    // AUT-14d. Поле «Месяц»: цифры с пробелом
    @Test
    void shouldIgnoreSpaceInMonthAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String currentMonth = DataHelper.getCurrentMonth();
        String monthWithSpace = currentMonth.charAt(0) + " " + currentMonth.charAt(1);

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(monthWithSpace);
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }


    // Проверка поля «Год»

// AUT-15a. Поле «Год»: пустое значение
    @Test
    void shouldShowErrorForEmptyYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getYearFormatError()
        );
    }


    // AUT-15b. Поле «Год»: одна цифра
    @Test
    void shouldShowErrorForOneDigitYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("2");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getYearFormatError()
        );
    }

    // AUT-15c. Поле «Год»: три цифры
    @Test
    void shouldLimitYearToTwoDigitsAndShowPeriodError() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("234");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Истёк срок действия карты",
                paymentPage.getYearExpiredError()
        );
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

        assertEquals(
                "Истёк срок действия карты",
                paymentPage.getYearExpiredError()
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

        assertEquals(
                "Истёк срок действия карты",
                paymentPage.getYearExpiredError()
        );
    }

    // AUT-18. Поле «Год»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicLettersInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("АБ");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getYearFormatError()
        );
    }

// AUT-19. Поле «Год»: введены буквы латиницы
    @Test
    void shouldShowErrorForLatinLettersInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("AB");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getYearFormatError()
        );
    }

// AUT-20b. Поле «Год»: введены только специальные символы
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("@#$");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getYearFormatError()
        );
    }

    // AUT-20c. Поле «Год»: только пробелы
    @Test
    void shouldShowErrorForSpacesOnlyInYear() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear("   ");
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getYearFormatError()
        );
    }

    // AUT-20d. Поле «Год»: числа с пробелом
    @Test
    void shouldIgnoreSpaceInYearAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String currentYear = DataHelper.getCurrentYear();
        String yearWithSpace = currentYear.charAt(0) + " " + currentYear.charAt(1);

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(yearWithSpace);
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }


    // Проверка поля «Владелец»

    // AUT-21a. Поле «Владелец»: пустое значение
    @Test
    void shouldShowErrorForEmptyOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Поле обязательно для заполнения",
                paymentPage.getOwnerFormatError()
        );
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

        assertEquals(
                "Неверный формат",
                paymentPage.getOwnerFormatError()
        );
    }

    // AUT-21c. Максимальная длина
    @Test
    void shouldLimitOwnerTo45CharactersAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-22a. Поле «Владелец»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("РУЧНОЕ ТЕСТИРОВАНИЕ");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getOwnerInvalidFormatError()
        );
    }

    // AUT-22b. Поле «Владелец»: нижний регистр латинскими буквами
    @Test
    void shouldConvertOwnerToUppercaseAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("manual test");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "MANUAL TEST",
                paymentPage.getOwnerValue()
        );
        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-23a. Поле «Владелец»: значение с цифрами
    @Test
    void shouldIgnoreDigitsInOwnerAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("MANUAL123");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "MANUAL",
                paymentPage.getOwnerValue()
        );
        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-23b. Поле «Владелец»: только цифры
    @Test
    void shouldShowErrorForDigitsOnlyInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("12345");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Поле обязательно для заполнения",
                paymentPage.getOwnerFormatError()
        );
    }

    // AUT-23c. Поле «Владелец»: три пробела подряд
    @Test
    void shouldShowErrorForSpacesOnlyInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("   ");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Поле обязательно для заполнения",
                paymentPage.getOwnerFormatError()
        );
    }

    // AUT-23d. Поле «Владелец»: множественные пробелы между словами
    @Test
    void shouldAcceptMultipleSpacesBetweenOwnerWords() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("MANUAL   TEST");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-23e. Поле «Владелец»: пробелы в начале и в конце
    @Test
    void shouldTrimSpacesAroundOwnerAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(" MANUAL TEST ");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "MANUAL TEST",
                paymentPage.getOwnerValue()
        );
        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-24a. Поле «Владелец»: буквы со специальными символами
    @Test
    void shouldIgnoreSpecialCharactersInOwnerAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("%MANUAL@ TEST");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "MANUAL TEST",
                paymentPage.getOwnerValue()
        );
        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-24b. Поле «Владелец»: только специальные символы
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInOwner() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("№#\"@@@ $%^");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Поле обязательно для заполнения",
                paymentPage.getOwnerFormatError()
        );
    }

    // AUT-24c. Поле «Владелец»: имя с дефисом
    @Test
    void shouldAcceptHyphenInOwnerAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner("MANUAL TEST-TEST");
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
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
        paymentPage.fillCvc("");

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCvcFormatError()
        );
    }

    //  AUT-26. Поле «CVC/CVV»: две цифры
    @Test
    void shouldShowErrorForTwoDigitsInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("12");

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCvcFormatError()
        );
    }

    // AUT-27. Поле «CVC/CVV»: четыре цифры
    @Test
    void shouldLimitCvcToThreeDigitsAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("1234");

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-28a. Поле «CVC/CVV»: введены буквы латиницы
    @Test
    void shouldShowErrorForLatinLettersInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("ABC");

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCvcFormatError()
        );
    }

    // AUT-28b. Поле «CVC/CVV»: введены буквы кириллицы
    @Test
    void shouldShowErrorForCyrillicLettersInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("АБВ");

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCvcFormatError()
        );
    }

    // AUT-29a. Поле «CVC/CVV»: введены специальные символы с цифрами
    @Test
    void shouldIgnoreSpecialCharactersInCvcAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("1*23");

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-29b. Поле «CVC/CVV»: введены только специальные символы
    @Test
    void shouldShowErrorForSpecialCharactersOnlyInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("@#$");

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCvcFormatError()
        );

        paymentPage.shouldNotShowOwnerRequiredError();
    }

    // AUT-29c. Поле «CVC/CVV»: введены цифры с пробелами
    @Test
    void shouldIgnoreSpaceInCvcAndMakeSuccessfulPayment() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("1 23");

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle());
        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-29d. Поле «CVC/CVV»: введены только пробелы
    @Test
    void shouldShowErrorForSpacesOnlyInCvc() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc("   ");

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCvcFormatError()
        );
    }

    // AUT-30. Отправка формы с пустыми обязательными полями
    @Test
    void shouldShowErrorsForAllEmptyRequiredFields() {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber("");
        paymentPage.fillMonth("");
        paymentPage.fillYear("");
        paymentPage.fillOwner("");
        paymentPage.fillCvc("");

        paymentPage.clickContinue();

        assertEquals(
                "Неверный формат",
                paymentPage.getCardNumberFormatError()
        );

        assertEquals(
                "Неверный формат",
                paymentPage.getMonthFormatError()
        );

        assertEquals(
                "Неверный формат",
                paymentPage.getYearFormatError()
        );

        assertEquals(
                "Поле обязательно для заполнения",
                paymentPage.getOwnerFormatError()
        );

        assertEquals(
                "Неверный формат",
                paymentPage.getCvcFormatError()
        );
    }

    // AUT-31. Снятие подсветки полей после корректного заполнения
    @Test
    void shouldRemoveErrorsAfterCorrectFieldFilling() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        // Этап 1. Пустая форма
        paymentPage.clickContinue();

        // Этап 2. Заполняем все поля валидными данными
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        // Повторно отправляем форму
        paymentPage.clickContinue();

        // Проверяем, что сообщения об ошибках исчезли
        paymentPage.shouldNotShowValidationErrors();

        // Проверяем успешное завершение операции
        assertEquals(
                "Успешно",
                paymentPage.getSuccessTitle()
        );

        assertEquals(
                "Операция одобрена Банком.",
                paymentPage.getSuccessContent()
        );
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }

    // AUT-32. Карта истекает в текущем месяце
    @Test
    void shouldMakeSuccessfulPaymentWhenCardExpiresThisMonth() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(DataHelper.getCurrentMonth());
        paymentPage.fillYear(DataHelper.getCurrentYear());
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals(
                "Успешно", paymentPage.getSuccessTitle()
        );
        assertEquals(
                "Операция одобрена Банком.", paymentPage.getSuccessContent()
        );

        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
        assertEquals(1, SQLHelper.getOrderCount());
    }


    // ### AUT-33. Карта действительна ровно 1 месяц
    @Test
    void shouldMakeSuccessfulPaymentWhenCardIsValidForOneMonth() throws SQLException {
        PaymentPage paymentPage = dashboardPage.clickBuy();

        String nextMonth = String.format(
                "%02d",
                Integer.parseInt(DataHelper.getCurrentMonth()) + 1);

        String nextYear = DataHelper.getNextYear();

        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber());
        paymentPage.fillMonth(nextMonth);
        paymentPage.fillYear(nextYear);
        paymentPage.fillOwner(DataHelper.getValidOwner());
        paymentPage.fillCvc(DataHelper.getValidCvc());

        paymentPage.clickContinue();

        assertEquals("Успешно", paymentPage.getSuccessTitle()
        );
        assertEquals("Операция одобрена Банком.", paymentPage.getSuccessContent()
        );

        assertEquals("APPROVED", SQLHelper.getPaymentStatus()
        );
        assertEquals(1, SQLHelper.getOrderCount()
        );
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

        assertEquals(
                "Неверно указан срок действия карты",
                paymentPage.getYearPeriodError()
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

        assertEquals(
                "Истёк срок действия карты",
                paymentPage.getYearExpiredError()
        );
    }
}
