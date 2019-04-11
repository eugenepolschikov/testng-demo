package com.testngdemo.automation.parallelexecution;

/**
 * https://www.swtestacademy.com/selenium-parallel-tests-grid-junit/
 * https://www.swtestacademy.com/selenium-parallel-tests-grid-testng/
 * <p>
 * Paralleling tests execution in browsers: chrome, Firefox
 */
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URL;
import static com.testngdemo.automation.webdriver.ElementsUtil.getStandardTimeIntervalSec;


public class TLDriverFactory {
    private final static Logger log = LoggerFactory.getLogger(TLDriverFactory.class);

    private static OptionsManager optionsManager = new OptionsManager();
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static synchronized void setDriver(String browser,String hubIP, String hubPort) {
        String webDriverURL = String.format("http://%s:%s/wd/hub", hubIP, hubPort);
        log.info("creating driver instance on the runnning GRID INFRA: {}#### ", webDriverURL);
        if (browser.equals("firefox")) {
            log.info("setting driver instance specifically for firefox");
            try {
                tlDriver.set(new RemoteWebDriver(new URL(webDriverURL), optionsManager.getFirefoxOptions()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (browser.equals("chrome")) {
            log.info("setting driver instance specifically for chrome");
            try {
                tlDriver.set(new RemoteWebDriver(new URL(webDriverURL), optionsManager.getChromeOptions()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, getStandardTimeIntervalSec() * 2);
    }

    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }
}