package com.testngdemo.automation.parallelexecution;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Level;

/**
 * https://www.swtestacademy.com/selenium-parallel-tests-grid-junit/
 * https://www.swtestacademy.com/selenium-parallel-tests-grid-testng/
 * Paralleling test execution in different browsers
 *
 * @author created by Eugene Polschikov on 2019-04-11
 */
public class OptionsManager {

    //Get Chrome Options
    public DesiredCapabilities getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");

        //enabling logging for chrome
        DesiredCapabilities capability = DesiredCapabilities.chrome();
        capability.setCapability(ChromeOptions.CAPABILITY, options);
        capability.setBrowserName("chrome");
        capability.setCapability("nativeEvents", true);
        LoggingPreferences logs = new LoggingPreferences();
        //Javascript console logs from the browser
        logs.enable(LogType.BROWSER, Level.WARNING);
        logs.enable(LogType.PERFORMANCE, Level.ALL);
        capability.setCapability(CapabilityType.LOGGING_PREFS, logs);
        return capability;
    }

    //Get Firefox Options
    public DesiredCapabilities getFirefoxOptions() {

        FirefoxOptions options = new FirefoxOptions();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.proxy.type", 0);
        options.setProfile(profile);
        //enabling logging for firefox
        DesiredCapabilities capability = DesiredCapabilities.firefox();
        capability.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
        capability.setBrowserName("firefox");
        capability.setCapability("nativeEvents", true);
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.WARNING);
        logs.enable(LogType.DRIVER, Level.ALL);
        logs.enable(LogType.SERVER, Level.ALL);
        logs.enable(LogType.PERFORMANCE, Level.ALL);
        capability.setCapability(CapabilityType.LOGGING_PREFS, logs);
        return capability;
    }
}