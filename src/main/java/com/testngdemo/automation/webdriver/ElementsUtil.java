package com.testngdemo.automation.webdriver;


import com.google.common.base.Function;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import static com.google.common.collect.Lists.transform;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.testngdemo.automation.utils.CaptureSnapshot.captureSnapshotForAllure;
import static java.time.Duration.ofSeconds;

/**
 * WebDriver operations on WebElements
 *
 * @author Eugene Polschikov
 * @date 2019-04-11
 */

public class ElementsUtil {

    private static final Logger log = LoggerFactory.getLogger(ElementsUtil.class);

    private final static int ATTEMPTS_NUMBER_TO_RETRY = 5;
    private final static int MINIMUM_DOPRODOWNLIST_INDEX_TO_START_SELECTION_WITH = 1;

    //    WebDriver waits intervals
    private final static int SHORTEST_TIME_INTERVAL_SEC = 1;
    private final static int SHORT_TIME_INTERVAL_SEC = 3;
    private final static int MEDIUM_TIME_INTERVAL_SEC = 5;
    private final static int STANDARD_TIME_INTERVAL_SEC = 10;
    private final static int LONG_TIME_INTERVAL_SEC = 15;


    public static void waitForPageLoaded(WebDriver driver) {
        ExpectedCondition<Boolean> expectation = driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete");
        Wait<WebDriver> wait = new WebDriverWait(driver, LONG_TIME_INTERVAL_SEC);
        wait.until(expectation);
    }

    public static boolean waitForPageJQueryRendered(WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, STANDARD_TIME_INTERVAL_SEC);
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = driver -> {
            try {
                return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
            } catch (TimeoutException e) {
                log.error("Timeout waiting for Page Load Request to complete.", e.getMessage(), e);
                // no jQuery present
                return true;
            }
        };
        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState")
                .toString().equals("complete");
        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }

    public static void waitForElementToBeClickable(WebDriver driver, By locator) {
        Wait<WebDriver> wait = new WebDriverWait(driver, MEDIUM_TIME_INTERVAL_SEC);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitForElementToBeClickable(WebDriver driver, WebElement element) {
        Wait<WebDriver> wait = new WebDriverWait(driver, MEDIUM_TIME_INTERVAL_SEC);
        log.info("waiting for element located by '{}'  becomes clickable", getLocatorOfWebElement(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * wait for webelement to disappear on the page
     *
     * @param driver  instance
     * @param locator 'By' locator
     */
    public static void waitForElementNotPresent(WebDriver driver, By locator) {
        Wait<WebDriver> wait = new WebDriverWait(driver, SHORT_TIME_INTERVAL_SEC);
        wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(locator)));
    }

    public static void waitForElementWithTextNotVisible(WebDriver driver, By locator, String text) {
        Wait<WebDriver> wait = new WebDriverWait(driver, SHORTEST_TIME_INTERVAL_SEC * 2);
        wait.until(ExpectedConditions.invisibilityOfElementWithText(locator, text));
    }

    public static void waitForElementNotVisible(WebDriver driver, By locator) {
        log.info("waiting until element with locator '{}' becomes invisible ", locator.toString());
        Wait<WebDriver> wait = new WebDriverWait(driver, SHORTEST_TIME_INTERVAL_SEC * 2);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static void waitForElementNotVisible(WebDriver driver, WebElement webElement) {
        Wait<WebDriver> wait = new WebDriverWait(driver, SHORTEST_TIME_INTERVAL_SEC * 2);
        wait.until(ExpectedConditions.invisibilityOf(webElement));
    }

    public static WebElement fluentWait(WebDriver driver, final By locator) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(ofSeconds(MEDIUM_TIME_INTERVAL_SEC))
                .pollingEvery(ofSeconds(SHORTEST_TIME_INTERVAL_SEC / 5))
                .ignoring(NoSuchElementException.class);
        return wait.until(
                (Function<WebDriver, WebElement>) driver1 -> driver1.findElement(locator)
        );
    }

    public static WebElement fluentWait(WebDriver driver, final By locator, long customTimeout) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(ofSeconds(customTimeout))
                .pollingEvery(ofSeconds(SHORTEST_TIME_INTERVAL_SEC / 5))
                .ignoring(NoSuchElementException.class);
        return wait.until(
                (Function<WebDriver, WebElement>) driver1 -> driver1.findElement(locator)
        );
    }

    public static WebElement fluentWait(WebDriver driver, final WebElement element) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(ofSeconds(MEDIUM_TIME_INTERVAL_SEC))
                .pollingEvery(ofSeconds(SHORTEST_TIME_INTERVAL_SEC / 5))
                .ignoring(NoSuchElementException.class);

        return wait.until(
                (Function<WebDriver, WebElement>) driver1 -> element
        );
    }

    public static void waitForElementGetsVisible(WebDriver driver, WebElement element) {
        new WebDriverWait(driver, MEDIUM_TIME_INTERVAL_SEC).until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForWebElementListBecomesVisible(WebDriver driver, By locator) {
        log.info("awaiting for webelement list isolated by locator {} becomes visible on the page {}", locator.toString(), driver.getCurrentUrl());
        List<WebElement> webElements = new WebDriverWait(driver, MEDIUM_TIME_INTERVAL_SEC * 3).
                until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        log.info("webelements list size is  {}", webElements.size());
    }

    public static void waitForListOfElementsToBeVisibleOnThePage(WebDriver driver, By selector) {
        List<WebElement> elementList = null;

        boolean listNotEmpty = false;
        int attempts = 0;
        while (attempts < 3) {
            try {
                elementList = driver.findElements(selector);
                listNotEmpty = elementList.size() > 0;
                sleepUninterruptibly(getShortestTimeIntervalSec(), TimeUnit.MILLISECONDS);
                break;
            } catch (WebDriverException e) {
                log.error("error when waiting for some elements", e.getMessage(), e);
            }
            attempts++;
        }
        Assert.assertTrue((listNotEmpty && elementList != null), "oops,  was unable to find elements on the page " + driver.getCurrentUrl() +
                " By selector:" + selector);

        log.info("waiting for every element in the list to become visible");
        for (WebElement iterator : elementList) {
            waitForElementGetsVisible(driver, iterator);
        }
    }

    /**
     * handling stale element exception
     *
     * @param driver instance
     * @param by     Locator
     * @return boolean  value
     */
    public static boolean clickWithStaleRefHandle(WebDriver driver, By by) {
        boolean result = false;
        int attempts = 0;
        while (attempts < 2) {
            try {
                driver.findElement(by).click();
                result = true;
                break;
            } catch (StaleElementReferenceException e) {
                log.error("stale element excception caught!", e.getMessage(), e);
            }
            attempts++;
        }
        return result;
    }

    public static boolean clickWithExceptionHandle(WebDriver driver, WebElement element) {
        boolean result = false;
        int attempts = 0;
        while (attempts < 3) {
            try {
                element.click();
                result = true;
                break;
            } catch (WebDriverException e) {
                log.error("error happened when clicking on the element", e.getMessage(), e);
            }
            attempts++;
        }
        return result;
    }

    public static boolean isElementPresent(WebDriver driver, By locatorKey) {
        WebDriver.Timeouts timeouts = driver.manage().timeouts();
        timeouts.implicitlyWait(MEDIUM_TIME_INTERVAL_SEC, TimeUnit.MILLISECONDS);
        try {
            driver.findElement(locatorKey);
            return true;
        } catch (WebDriverException e) {
            return false;
        }
    }

    /**
     * serving for finding element list by provided locator and checking that arr size > 0 .
     *
     * @param driver  instance
     * @param locator xpath,css,id, etc
     * @return false is not found ; True if element found;
     */
    public static boolean isElementPresentByWebelementListLen(WebDriver driver, By locator) {
        return driver.findElements(locator).size() > 0;
    }


    public static boolean isElementDisplayedAndEnabled(WebElement element) {
        return (element.isDisplayed() && element.isEnabled());
    }

    public static boolean isElementDisplayed(WebElement element) {
        return element.isDisplayed();
    }


    public static void actionBuilderFocusInputEnterValue(WebDriver driver, String cssSelector, String valueToEnter) {
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.cssSelector(cssSelector)));
        actions.click();
        actions.sendKeys(valueToEnter);
        actions.build().perform();
    }

    /**
     * @param driver  instance
     * @param element to perform mouse hover and click
     */
    public static void actionBuilderMouserOverAndCLick(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.click(element);
        actions.build().perform();
        waitForPageLoaded(driver);
    }

    public static void moveToElementActionbuilder(WebDriver driver, WebElement element) {
        log.info("moving to set the focus on webelement {}", element);
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.build().perform();
        sleepUninterruptibly(getShortestTimeIntervalSec() / 2, TimeUnit.MILLISECONDS);
    }


    public static void waitForFrameToBeAvailableAndSwitchToIt(WebDriver driver, String frameName) {
        new WebDriverWait(driver, SHORT_TIME_INTERVAL_SEC)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
    }

    public static void waitForFrameToBeAvailableAndSwitchToIt(WebDriver driver, By locator) {
        new WebDriverWait(driver, SHORT_TIME_INTERVAL_SEC)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    public static void waitForFrameToBeAvailableAndSwitchToIt(WebDriver driver, WebElement webElement) {
        new WebDriverWait(driver, SHORT_TIME_INTERVAL_SEC)
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(webElement));
    }


    public static void switchToReportWindow(WebDriver driver) {
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
        }
    }


    private static String getLocatorOfWebElement(WebElement element) {
        String elemParsed = element.toString();
        return elemParsed.substring(elemParsed.indexOf(">") + 2, elemParsed.length() - 1);
    }


    /**
     * filing in   Select webelement in a random way
     *
     * @param select          dropdown select
     * @param highestBoundary highest index value tos select
     */
    public static void selectRandomizer(WebElement select, int highestBoundary) {

        Select realSelect = new Select(select);
        int randomIndex = ThreadLocalRandom.current().nextInt(1, highestBoundary);
        realSelect.selectByIndex(randomIndex);
    }


    /**
     * random selection of the option in dropdown List
     *
     * @param driver              instance
     * @param button              to click to make dropdown options appear
     * @param dynamicListOfValues dropdown options
     */
    @Step("selecting DDL option in a random way (NOTE: with NO DDL options validation)")
    public static void ulListRandomizer(WebDriver driver, WebElement button, List<WebElement> dynamicListOfValues) {

        dropdownListButtonClickWithRetries(driver, button, dynamicListOfValues);
        captureSnapshotForAllure(driver);
        dynamicListOfValues.get(ThreadLocalRandom.current().nextInt(MINIMUM_DOPRODOWNLIST_INDEX_TO_START_SELECTION_WITH, dynamicListOfValues.size())).click();
        waitForPageLoaded(driver);
        sleepUninterruptibly(getShortestTimeIntervalSec() / 2, TimeUnit.MILLISECONDS);
    }

    @Step("selecting DDL option in a random way (NOTE: with NO DDL options validation)")
    public static void ulListRandomizer(WebDriver driver, WebElement button, List<WebElement> dynamicListOfValues,
                                        String textToSelect) {

        dropdownListButtonClickWithRetries(driver, button, dynamicListOfValues);
        captureSnapshotForAllure(driver);
        Function<WebElement, String> getText = (WebElement w) -> w.getText().trim();


        int indexToClick = transform(dynamicListOfValues, getText).indexOf(textToSelect);
        // soft assert, i.e if no of this element, then we'll be picking up random from dropdown
        if (indexToClick < 0) {
            log.warn("ooops, there is no '" + textToSelect + "' element among DDL options: " + transform(dynamicListOfValues, getText).toString());
            dynamicListOfValues.get(ThreadLocalRandom.current().nextInt(MINIMUM_DOPRODOWNLIST_INDEX_TO_START_SELECTION_WITH, dynamicListOfValues.size())).click();
        } else {
            dynamicListOfValues.get(indexToClick).click();
        }
        waitForPageLoaded(driver);
    }


    @Step("selecting multi-option dropdown")
    public static void selectAllMultiOptions(WebDriver driver, WebElement button, List<WebElement> multiOptionList){
        log.info("clicking on the button to make option list appear");
        button.click();
        log.info("selecting all options in dropdown list");
        try {
            for (WebElement iter : multiOptionList) {
                jsScrollIntoView(driver,iter);
                iter.click();
            }
            captureSnapshotForAllure(driver);
        } catch (WebDriverException e) {
            log.info("ooops some error occured during group selection");
            log.info(e.getMessage(), e);
        }
        log.info("clicking on same button to make popup disappear");
        button.click();
        captureSnapshotForAllure(driver);
    }


    /**
     * clicking on button to make DDL  options to appear ,
     * verifies that all expected options appeared,
     * and picks up a DDL option in a random way
     *
     * @param driver              instance
     * @param button              to click
     * @param dynamicListOfValues dropdown options appeared
     * @param expectedOptions     expected dropdown options
     */
    @Step("selecting DDL option in a random way with validation of all DDL options")
    public static void ddlOptionsVerifyAndRandomPickup(SoftAssert softAssertion, WebDriver driver, WebElement button,
                                                       List<WebElement> dynamicListOfValues, String[] expectedOptions) {

        dropdownListButtonClickWithRetries(driver, button, dynamicListOfValues);
        captureSnapshotForAllure(driver);
        Function<WebElement, String> getElemsText = (WebElement w) -> w.getText().trim();
        Set<String> actual = transform(dynamicListOfValues, getElemsText).stream().collect(Collectors.toSet());
        Set<String> expected = new HashSet<>(Arrays.asList(expectedOptions));
        String actualStringified = Arrays.toString(actual.toArray());
        String expectedStringified = Arrays.toString(expected.toArray());
        log.info("ACTUAL: {}", actualStringified);
        log.info("EXPECTED: {}", expectedStringified);
        log.info("Checking match between actual dropdown options and expected dropdown options");

        // analyzing difference between actual and expected dropdown options
        actual.removeAll(expected);
        softAssertion.assertEquals(actual.size(), 0,
                String.format("Page: %s \n Oops, actual DDL options number is different from expected ones. ###Actual: %s #####   Expected: %s  #####.   'Actual - Expected' DIFF: '%s'",
                        driver.getCurrentUrl(), actualStringified, expectedStringified, Arrays.toString(actual.toArray())));

        dynamicListOfValues.get(ThreadLocalRandom.current().nextInt(MINIMUM_DOPRODOWNLIST_INDEX_TO_START_SELECTION_WITH, dynamicListOfValues.size())).click();
        waitForPageLoaded(driver);
    }

    /**
     * clicking on button to make DDL  options to appear ,
     * verifies that all expected options appeared,
     * and picks up a DDL option in a random way
     *
     * @param driver              instance
     * @param button              to click
     * @param dynamicListOfValues dropdown options appeared
     * @param expectedOptions     expected dropdown options
     */
    @Step("selecting DDL option in a random way with validation of all DDL options")
    public static void ddlOptionsVerifyAndSpecificOptionPickup(SoftAssert softAssertion, WebDriver driver, WebElement button,
                                                               List<WebElement> dynamicListOfValues, String[] expectedOptions, String optionToSelect) {

        //dropdownListButtonClickWithRetries(driver, button, dynamicListOfValues);
        //captureSnapshotForAllure(driver);
        Function<WebElement, String> getElemsText = (WebElement w) -> w.getText().trim();
        Set<String> actual = transform(dynamicListOfValues, getElemsText).stream().collect(Collectors.toSet());
        Set<String> expected = new HashSet<>(Arrays.asList(expectedOptions));
        String actualStringified = Arrays.toString(actual.toArray());
        String expectedStringified = Arrays.toString(expected.toArray());
        log.info("ACTUAL: {}", actualStringified);
        log.info("EXPECTED: {}", expectedStringified);
        log.info("Checking match between actual dropdown options and expected dropdown options");

        // analyzing difference between actual and expected dropdown options
        actual.removeAll(expected);
        softAssertion.assertEquals(actual.size(), 0,
                String.format("Page: %s \n Oops, actual DDL options number is different from expected ones. ###Actual: %s #####   Expected: %s  #####.   'Actual - Expected' DIFF: '%s'",
                        driver.getCurrentUrl(), actualStringified, expectedStringified, Arrays.toString(actual.toArray())));
        ulListRandomizer(driver,button,dynamicListOfValues,optionToSelect);
    }

    /**
     * Utility method serving for clickin dropdown button with retries attempts
     *
     * @param driver              instance
     * @param button              to click
     * @param dynamicListOfValues dropdown options appeared
     */
    @Step("clicking several times on dropdown button with retries")
    private static void dropdownListButtonClickWithRetries(WebDriver driver, WebElement button, List<WebElement> dynamicListOfValues) {
        int attempts = 0;
        while (attempts < ATTEMPTS_NUMBER_TO_RETRY) {
            try {
                log.info("clicking on ddl button {} time", String.valueOf(attempts + 1));
                button.click();
                sleepUninterruptibly(getShortestTimeIntervalSec() / 2, TimeUnit.MILLISECONDS);

                if (dynamicListOfValues.size() > 0)
                    log.info("list of DDL options is not empty");
                fluentWait(driver, dynamicListOfValues.get(dynamicListOfValues.size() - 1));
                break;
            } catch (WebDriverException e) {
                log.error(e.getMessage(), e);
            }
            attempts++;
        }
    }


    /**
     * @param driver       instance
     * @param titlePattern URL pattern pattern (occurrency)
     */
    public static void waitTillPageUrlChanges(WebDriver driver, String titlePattern) {
        log.info("waiting {} seconds until page URL contains {}", LONG_TIME_INTERVAL_SEC, titlePattern);
        WebDriverWait wait = new WebDriverWait(driver, LONG_TIME_INTERVAL_SEC);
        wait.until(ExpectedConditions.urlContains(titlePattern));
    }

    public static void waitTillPageUrlChanges(WebDriver driver, String titlePattern, int extendedTimeout) {
        log.info("waiting {} seconds until page URL contains {}", extendedTimeout, titlePattern);
        WebDriverWait wait = new WebDriverWait(driver, extendedTimeout);
        wait.until(ExpectedConditions.urlContains(titlePattern));
    }

    /**
     * @param driver       instance
     * @param pageSubTitle sub-url of the page which user navigated from (after specific action)
     */
    public static void waitUntilRedirectHappensFromParticularPage(WebDriver driver, String pageSubTitle) {

        WebDriverWait wait = new WebDriverWait(driver, LONG_TIME_INTERVAL_SEC);
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains(pageSubTitle)));
    }

    public static void jsScrollIntoView(WebDriver driver, By locator) {
        WebElement webElement = fluentWait(driver, locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);
    }

    public static void jsScrollIntoView(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public static WebElement jsGetParentElementOfTheCurrentOne(WebDriver driver, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        return (WebElement) executor.executeScript("return arguments[0].parentNode;", element);
    }

    public static int getShortestTimeIntervalSec() {
        return SHORTEST_TIME_INTERVAL_SEC;
    }

    public static int getShortTimeIntervalSec() {
        return SHORT_TIME_INTERVAL_SEC;
    }

    public static int getMediumTimeIntervalSec() {
        return MEDIUM_TIME_INTERVAL_SEC;
    }

    public static int getStandardTimeIntervalSec() {
        return STANDARD_TIME_INTERVAL_SEC;
    }

    public static int getLongTimeIntervalSec() {
        return LONG_TIME_INTERVAL_SEC;
    }
}