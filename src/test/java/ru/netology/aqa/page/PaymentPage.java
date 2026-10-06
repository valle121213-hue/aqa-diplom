package ru.netology.aqa.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;

import static com.codeborne.selenide.Selenide.$$x;
import static org.junit.jupiter.api.Assertions.assertAll;


public class PaymentPage {

    private final SelenideElement cardNumber =
            $x("//span[text()='Номер карты']/following::input[1]");

    private final SelenideElement month =
            $x("//span[text()='Месяц']/following::input[1]");

    private final SelenideElement year =
            $x("//span[text()='Год']/following::input[1]");

    private final SelenideElement owner =
            $x("//span[text()='Владелец']/following::input[1]");

    private final SelenideElement cvc =
            $x("//span[text()='CVC/CVV']/following::input[1]");

    private final SelenideElement continueButton =
            $x("//button[.//span[text()='Продолжить']]");

    private final SelenideElement successTitle =
            $$x("//div[contains(@class, 'notification__title')]")
                    .filter(visible)
                    .findBy(com.codeborne.selenide.Condition.text("Успешно"));

    private final SelenideElement successContent =
            $$x("//div[contains(@class, 'notification__content')]")
                    .filter(visible)
                    .findBy(com.codeborne.selenide.Condition.text("Операция одобрена Банком."));

    //Невалидная карта

    private final SelenideElement invalidCardErrorTitle =
            $x("//div[contains(@class, 'notification_status_error')]//div[contains(@class, 'notification__title')]");

    private final SelenideElement invalidCardErrorContent =
            $x("//div[contains(@class, 'notification_status_error')]//div[contains(@class, 'notification__content')]");

    private final SelenideElement cardNumberFormatError =
            $x("//span[text()='Номер карты']/following::span[contains(text(), 'Неверный формат')][1]");

    private final SelenideElement monthFormatError =
            $x("//span[text()='Месяц']/following::span[contains(text(), 'Неверный формат')][1]");

    private final SelenideElement monthPeriodError =
            $x("//span[text()='Месяц']/following::span[contains(text(), 'Неверно указан срок действия карты')][1]");

    private final SelenideElement yearFormatError =
            $x("//span[text()='Год']/following::span[contains(text(), 'Неверный формат')][1]");

    private final SelenideElement yearExpiredError =
            $x("//span[text()='Год']/following::span[contains(text(), 'Истёк срок действия карты')][1]");

    private final SelenideElement ownerFormatError =
            $x("//span[text()='Владелец']/following::span[contains(text(), 'Поле обязательно для заполнения')][1]");

    private final SelenideElement ownerInvalidFormatError =
            $x("//span[text()='Владелец']/following::span[contains(text(), 'Неверный формат')][1]");

    private final SelenideElement cvcFormatError =
            $x("//span[text()='CVC/CVV']/following::span[contains(text(), 'Неверный формат')][1]");

    private final SelenideElement yearPeriodError =
            $x("//span[text()='Год']/following::span[contains(text(), 'Неверно указан срок действия карты')][1]");

    public void fillCardNumber(String value) {
        cardNumber.setValue(value);
    }

    public void shouldHaveCardNumberValue(String expectedValue) {
        cardNumber
                .shouldBe(visible)
                .shouldHave(value(expectedValue));
    }

    public void fillMonth(String value) {
        month.setValue(value);
    }

    public void fillYear(String value) {
        year.setValue(value);
    }

    public void fillOwner(String value) {
        owner.setValue(value);
    }

    public void fillCvc(String value) {
        cvc.setValue(value);
    }

    public void shouldHaveYearValue(String expectedValue) {
        year
                .shouldBe(visible)
                .shouldHave(value(expectedValue));
    }

    public void shouldHaveMonthValue(String expectedValue) {
        month
                .shouldBe(visible)
                .shouldHave(value(expectedValue));
    }

    public void shouldHaveCvcValue(String expectedValue) {
        cvc
                .shouldBe(visible)
                .shouldHave(value(expectedValue));
    }

    public void clickContinue() {
        continueButton.click();
    }

    public void shouldShowSuccessTitle(String expectedText) {
        successTitle
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldShowSuccessContent(String expectedText) {
        successContent
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldShowInvalidCardErrorTitle(String expectedText) {
        invalidCardErrorTitle
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldShowInvalidCardErrorContent(String expectedText) {
        invalidCardErrorContent
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    // Для ошибочного формата карты
    public void shouldShowCardNumberFormatError(String expectedText) {
        cardNumberFormatError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    // Для ошибочного формата месяца
    public void shouldShowMonthFormatError(String expectedText) {
        monthFormatError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldShowMonthPeriodError(String expectedText) {
        monthPeriodError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    // Для года
    public void shouldShowYearFormatError(String expectedText) {
        yearFormatError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldShowYearExpiredError(String expectedText) {
        yearExpiredError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldShowYearPeriodError(String expectedText) {
        yearPeriodError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    // Для владельца
    public void shouldShowOwnerFormatError(String expectedText) {
        ownerFormatError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldShowOwnerInvalidFormatError(String expectedText) {
        ownerInvalidFormatError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldHaveOwnerValue(String expectedValue) {
        owner
                .shouldBe(visible)
                .shouldHave(value(expectedValue));
    }

    // CVC
    public void shouldShowCvcFormatError(String expectedText) {
        cvcFormatError
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    public void shouldNotShowOwnerRequiredError() {
        ownerFormatError.shouldNotBe(visible);
    }

    //Под полем «Владелец» не появилось сообщение «Неверный формат».
    public void shouldNotShowOwnerInvalidFormatError() {
        ownerInvalidFormatError.shouldNotBe(visible);
    }

    public void shouldHaveCardNumberLength(int expectedLength) {
        cardNumber.shouldBe(visible);

        String actualValue = cardNumber.getValue();
        String digitsOnly = actualValue.replace(" ", "");

        if (digitsOnly.length() != expectedLength) {
            throw new AssertionError(
                    "Ожидалось " + expectedLength
                            + " цифр, но получено " + digitsOnly.length()
            );
        }
    }

    public void shouldHaveMonthLength(int expectedLength) {
        month.shouldBe(visible);

        String actualValue = month.getValue();

        if (actualValue.length() != expectedLength) {
            throw new AssertionError(
                    "Ожидалось " + expectedLength
                            + " цифры, но получено " + actualValue.length()
            );
        }
    }

    public void shouldHaveYearLength(int expectedLength) {
        year.shouldBe(visible);

        String actualValue = year.getValue();

        if (actualValue.length() != expectedLength) {
            throw new AssertionError(
                    "Ожидалось " + expectedLength
                            + " цифры, но получено " + actualValue.length()
            );
        }
    }

    public void shouldHaveOwnerLength(int expectedLength) {
        owner.shouldBe(visible);

        String actualValue = owner.getValue();

        if (actualValue.length() != expectedLength) {
            throw new AssertionError(
                    "Ожидалось " + expectedLength
                            + " символов, но получено " + actualValue.length()
            );
        }
    }

    // проверка полей месяц и год
    public void shouldNotShowMonthErrors() {
        assertAll(
                () -> monthFormatError.shouldNotBe(visible),
                () -> monthPeriodError.shouldNotBe(visible)
        );
    }

    public void shouldNotShowYearErrors() {
        assertAll(
                () -> yearFormatError.shouldNotBe(visible),
                () -> yearExpiredError.shouldNotBe(visible),
                () -> yearPeriodError.shouldNotBe(visible)
        );
    }


    public void shouldNotShowValidationErrors() {
        assertAll(
                () -> cardNumberFormatError.shouldNotBe(visible),
                () -> monthFormatError.shouldNotBe(visible),
                () -> monthPeriodError.shouldNotBe(visible),
                () -> yearFormatError.shouldNotBe(visible),
                () -> yearExpiredError.shouldNotBe(visible),
                () -> ownerFormatError.shouldNotBe(visible),
                () -> ownerInvalidFormatError.shouldNotBe(visible),
                () -> cvcFormatError.shouldNotBe(visible)
        );
    }

}

