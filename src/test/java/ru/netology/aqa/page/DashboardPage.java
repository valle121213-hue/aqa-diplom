package ru.netology.aqa.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;


public class DashboardPage {

    private final SelenideElement buyButton =
            $x("//button[.//span[text()='Купить']]");

    public PaymentPage clickBuy() {
        buyButton.click();
        return new PaymentPage();
    }
}
