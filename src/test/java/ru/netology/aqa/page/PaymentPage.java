package ru.netology.aqa.page;

import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$x;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.text;

import static com.codeborne.selenide.Selenide.$$x;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.netology.aqa.data.DataHelper.getCurrentYear;

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

    public String getCardNumberValue() {
        return cardNumber.getValue();
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

    public String getYearValue() {
        return year.getValue();
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

    // для ошибочного формата карты
    public String getCardNumberFormatError() {
        return cardNumberFormatError
                .shouldBe(visible)
                .getText();
    }

    //для ошибочного формата месяца
    public String getMonthFormatError() {
        return monthFormatError
                .shouldBe(visible)
                .getText();
    }

    public String getMonthPeriodError() {
        return monthPeriodError
                .shouldBe(visible)
                .getText();
    }

    //Для года
    public String getYearFormatError() {
        return yearFormatError
                .shouldBe(visible)
                .getText();
    }

    public String getYearExpiredError() {
        return yearExpiredError
                .shouldBe(visible)
                .getText();
    }

    public String getYearPeriodError() {
        return yearPeriodError.shouldBe(visible).getText();
    }

    // Для Владелец
    public String getOwnerFormatError() {
        return ownerFormatError
                .shouldBe(visible)
                .getText();
    }

    public String getOwnerInvalidFormatError() {
        return ownerInvalidFormatError
                .shouldBe(visible)
                .getText();
    }

    //ПРОВЕРКА ВЕРХНЕГО РЕГИСТРА
    public String getOwnerValue() {
        return owner.getValue();
    }

    //CVC
    public String getCvcFormatError() {
        return cvcFormatError
                .shouldBe(visible)
                .getText();
    }

    public void shouldNotShowOwnerRequiredError() {
        ownerFormatError.shouldNotBe(visible);
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

