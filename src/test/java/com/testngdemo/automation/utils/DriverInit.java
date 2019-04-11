package com.testngdemo.automation.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

/**
 * Utility method for initializing driver instance
 *
 * @author Eugene Polschikov
 * @date 2019-04-11
 */
public class DriverInit {
    private final static Logger log = LoggerFactory.getLogger(DriverInit.class);

    public static WebDriver driverSetUp(WebDriver driver, String hubIP, String hubPort) throws MalformedURLException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("-incognito");
        DesiredCapabilities capability = DesiredCapabilities.chrome();
        capability.setCapability(ChromeOptions.CAPABILITY, options);
        //System.setProperty("webdriver.chrome.driver", System.getProperty("user.home")+"/Documents/:Proj_folder:/chromedriver");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        capability.setBrowserName("chrome");
        capability.setCapability("nativeEvents", true);
        LoggingPreferences logs = new LoggingPreferences();
        //Javascript console logs from the browser
        logs.enable(LogType.BROWSER, Level.WARNING);
        logs.enable(LogType.PERFORMANCE, Level.ALL);
        capability.setCapability(CapabilityType.LOGGING_PREFS, logs);
        String webDriverURL = String.format("http://%s:%s/wd/hub",hubIP,hubPort) ;
        log.info("creating driver instance on the URL :#### " + webDriverURL);
        driver = new RemoteWebDriver(new URL(webDriverURL), capability);
        driver.manage().window().maximize();
        return driver;
    }
}
