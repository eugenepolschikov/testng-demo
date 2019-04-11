package com.testngdemo.automation.listeners.paralleltests;

import com.testngdemo.automation.parallelexecution.TLDriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

import static com.testngdemo.automation.utils.SeleniumGridConfig.SELENIUM_GRID_HUB_IP;
import static com.testngdemo.automation.utils.SeleniumGridConfig.SELENIUM_GRID_HUB_PORT;

/**
 *
 */
public class InvokedMethodListenerWithScreenshots implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            System.out.println("Test Method BeforeInvocation is started. Current ThreadId:" + Thread.currentThread().getId());
            String browserName = method.getTestMethod().getXmlTest().getLocalParameters().get("browser");
            TLDriverFactory.setDriver(browserName, SELENIUM_GRID_HUB_IP, SELENIUM_GRID_HUB_PORT);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            System.out.println("Test Method AfterInvocation is started. Current ThreadId:" + Thread.currentThread().getId());
            WebDriver driver = TLDriverFactory.getDriver();
            byte[] srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            saveScreenshot(srcFile);
            postBrowserLogs(driver);

            try {
                driver.close();
            } catch (WebDriverException e) {
                System.out.println("#### oops, seems driver instance have been already closed." + e.getMessage() + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    private byte[] saveScreenshot(byte[] screenshot) {
        return screenshot;
    }


    @Attachment(value = "WebDriver browser console logs", type = "text/html")
    public byte[] postBrowserLogs(WebDriver driver) {

        String resultingBrowserLogs = " ";

        // extracting logs allowed for Chrome only
        // https://github.com/seleniumhq/selenium/issues/1161
        //possible but old solution: https://github.com/mguillem/JSErrorCollector  ???
        //extract firefox logs with javascript client: https://advancedweb.hu/2016/03/29/selenium-tes-logs/
        //logs extraction examples: https://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.logging.LogEntries

        // gihub active issues (relevant per 2018-09-14) related to Ffox logging:
        // https://github.com/w3c/webdriver/issues/406
        // https://github.com/mozilla/geckodriver/issues/284
        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
        Logs logz = driver.manage().logs();
        LogEntries logEntries = null;

        if (Objects.equals(cap.getBrowserName(), "chrome")) {
            logEntries = logz.get(LogType.BROWSER);
        } else if ((Objects.equals(cap.getBrowserName(), "firefox"))) {
            logEntries = null;
        }

        if (logEntries != null) {

            if (logEntries.getAll().size() > 0) {
                String lowSeverityLogSummary = String.format("##############LOW-SEVERITY '%s' BROWSER LOGS:###########\n", cap.getBrowserName().toUpperCase());
                String highSeverityLogSummary = String.format("#####ERRRR!!###HIGH-SEVERITY '%s' BROWSER LOGS:###ERRR!!!#####\n", cap.getBrowserName().toUpperCase());


                for (LogEntry logEntry : logEntries) {
                    if (logEntry.getLevel() == Level.SEVERE && !logEntry.getMessage().contains("chrome-extension")) {
                        highSeverityLogSummary += "ERROR ENCOUNTERED!:###### PAGE:'" + driver.getCurrentUrl() +
                                "'\n##########" + "<mark>" + logEntry.getMessage() + "</mark>";
                    } else if (!logEntry.getMessage().contains("chrome-extension")) {
                        lowSeverityLogSummary += "<b>" + logEntry.getMessage() + "</b>" + "\n";
                        lowSeverityLogSummary += "#######\n";
                    }
                }
                resultingBrowserLogs = String.format("%s%s", highSeverityLogSummary, lowSeverityLogSummary);
            }
        }
        return resultingBrowserLogs.getBytes(Charset.forName("UTF-8"));
    }
}
