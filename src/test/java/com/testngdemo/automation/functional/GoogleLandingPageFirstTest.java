package com.testngdemo.automation.functional;

import com.testngdemo.automation.pages.GoogleLandingPage;
import com.testngdemo.automation.parallelexecution.TLDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.Title;

/**
 * First test suite for demo testNg purposes
 *
 * @author Eugene Polschikov
 * @date 2019-04-11
 */
public class GoogleLandingPageFirstTest extends TestSuitesBase {


    private final String QUERY_PATTERN_FIRST = "experitest";
    private final String QUERY_PATTERN_SECOND = "appium";

    @Title("Google search landing page, first test")
    @Features("TestNG demo")
    @Stories("User opens google landing page and performs search")
    @Test(groups = {"SMOKE.SUITE"})
    @Parameters({"browser"})
    public void googleDoSimpleSearchByQuery() throws Exception {
        WebDriver driver;
        driver = TLDriverFactory.getDriver();
        log.info("test started! Current threadID: {}", Thread.currentThread().getId());
        driver.get(baseUrl);

        log.info("do search on google landing page by string query");
        GoogleLandingPage myLandingPage = new GoogleLandingPage(driver);
        myLandingPage.enterQueryToSearchFor(QUERY_PATTERN_FIRST)
                .doTheSearchAfterQueryEntered();
        log.info("checking that at least 1 result is displayed in search");
        Assert.assertTrue(myLandingPage.getNumberOfFoundResults() > 0, "ooops, looks nothing have been found in search. Please contact test developers for investigation");
    }

    @Title("negative test")
    @Features("negative test TestNG demo")
    @Stories("negative test This is failing test for debugging and demo reporting purposes")
    @Test(groups = {"SMOKE.SUITE"})
    @Parameters({"browser"})
    public void negativeFailingTestSearchByQuery() throws Exception {
        WebDriver driver;
        driver = TLDriverFactory.getDriver();
        log.info("test started! Current threadID: {}", Thread.currentThread().getId());
        driver.get(baseUrl);

        log.info("do search on google landing page by string query");
        GoogleLandingPage myLandingPage = new GoogleLandingPage(driver);
        myLandingPage.enterQueryToSearchFor(QUERY_PATTERN_FIRST)
                .doTheSearchAfterQueryEntered();
        log.info("checking that at least 1 result is displayed in search");
        Assert.assertEquals(myLandingPage.getNumberOfFoundResults(), 0, "ooops, looks nothing have been found in search. Please contact test developers for investigation");
    }


    @Title("Google search landing page, second test")
    @Features("TestNG demo")
    @Stories("User opens google landing page and performs search")
    @Test(groups = {"REGRESSION.SUITE"})
    @Parameters({"browser"})
    public void googleDoAnotherSimpleSearchByQuery() throws Exception {
        WebDriver driver;
        driver = TLDriverFactory.getDriver();
        log.info("test started! Current threadID: {}", Thread.currentThread().getId());
        driver.get(baseUrl);

        log.info("do search on google landing page by string query");
        GoogleLandingPage myLandingPage = new GoogleLandingPage(driver);
        myLandingPage.enterQueryToSearchFor(QUERY_PATTERN_SECOND)
                .doTheSearchAfterQueryEntered();
        log.info("checking that at least 1 result is displayed in search");
        Assert.assertTrue(myLandingPage.getNumberOfFoundResults() > 0, "ooops, looks nothing have been found in search. Please contact test developers for investigation");
    }
}