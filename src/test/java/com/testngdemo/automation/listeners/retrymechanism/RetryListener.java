package com.testngdemo.automation.listeners.retrymechanism;


import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Retry mechanism for failed tests
 * http://toolsqa.com/selenium-webdriver/retry-failed-tests-testng/
 * https://developers.perfectomobile.com/display/TT/TestNG+-+Add+and+Control+Test+Failure+Retries
 * <p>
 * The RetryListener class below overrides TestNG's annotation functionality
 * so that every test will have the Retry annotation functionality and no additional class
 * or test level retry annotations are necessary.
 */
public class RetryListener implements IAnnotationTransformer {
    public RetryListener() {
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        IRetryAnalyzer retry = annotation.getRetryAnalyzer();
        if (retry == null) {
            annotation.setRetryAnalyzer(Retry.class);
        }
    }
}