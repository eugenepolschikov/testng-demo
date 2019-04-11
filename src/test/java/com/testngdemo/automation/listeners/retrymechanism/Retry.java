package com.testngdemo.automation.listeners.retrymechanism;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry mechanism for failed tests
 * http://toolsqa.com/selenium-webdriver/retry-failed-tests-testng/
 * https://developers.perfectomobile.com/display/TT/TestNG+-+Add+and+Control+Test+Failure+Retries
 * <p>
 * allows you to specify how many times TestNG will run a failed test again with the maxRetryCount variable.
 * Best practice is to specify the maxRetryCount with a parameter from the TestNG xml or a property file.
 * In the example below TestNG will rerun the same failed test up to 'MAX_RETRY_COUNT' times.
 */
public class Retry implements IRetryAnalyzer {
    private int retryCount = 1;
    private final static int MAX_RETRY_COUNT = 3;

    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println("Retry #" + retryCount + " for test: " + result.getMethod().getMethodName() + ", on thread: " + Thread.currentThread().getName());
            return true;
        }
        return false;
    }
}
