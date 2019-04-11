package com.testngdemo.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.testngdemo.automation.webdriver.ElementsUtil.*;

public class GoogleLandingPage extends Page {
    public GoogleLandingPage(WebDriver driver) {
        super(driver);
    }
    private final static String SEARCH_INPUT_CSS = "input[name=\"q\"]";
    @FindBy(css = SEARCH_INPUT_CSS)
    private WebElement searchInput;

    public GoogleLandingPage enterQueryToSearchFor(String googleQuery){
        log.info("entering query to search in google input");
        waitForPageLoaded(driver);
        waitForElementToBeClickable(driver,searchInput);
        searchInput.clear();
        searchInput.sendKeys(googleQuery);
        return this;
    }
}
