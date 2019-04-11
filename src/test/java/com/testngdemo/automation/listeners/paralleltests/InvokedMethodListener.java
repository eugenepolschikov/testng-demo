package com.testngdemo.automation.listeners.paralleltests;


import com.testngdemo.automation.parallelexecution.TLDriverFactory;
import com.testngdemo.automation.utils.SeleniumGridConfig;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import static com.testngdemo.automation.utils.SeleniumGridConfig.*;
import static com.testngdemo.automation.utils.SeleniumGridConfig.SELENIUM_GRID_HUB_PORT;

/**
 *
 */
public class InvokedMethodListener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            System.out.println("Test Method BeforeInvocation is started. " + Thread.currentThread().getId());
            String browserName = method.getTestMethod().getXmlTest().getLocalParameters().get("browser");
            TLDriverFactory.setDriver(browserName, SELENIUM_GRID_HUB_IP, SELENIUM_GRID_HUB_PORT);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            System.out.println("Test Method AfterInvocation is started. " + Thread.currentThread().getId());
            WebDriver driver = TLDriverFactory.getDriver();
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
