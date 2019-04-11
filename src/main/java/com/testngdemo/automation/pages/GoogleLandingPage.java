package com.testngdemo.automation.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.testngdemo.automation.webdriver.ElementsUtil.*;

/**
 * Google landing page object: webelements and methods operating on the provided webelements
 *
 * @author Eugene Polschikov
 * @date 2019-04-11
 */
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

    public GoogleLandingPage doTheSearchAfterQueryEntered(){
        log.info("sending 'Enter' key");
        searchInput.sendKeys(Keys.ENTER);
        waitForPageLoaded(driver);
        return new GoogleLandingPage(driver);
    }

    public GoogleLandingPage ensureThatAtLeastOneSearchResultAppeared(){


        return new GoogleLandingPage(driver);
    }
}
