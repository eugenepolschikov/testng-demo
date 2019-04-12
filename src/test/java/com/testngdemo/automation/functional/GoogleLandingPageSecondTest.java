package com.testngdemo.automation.functional;

import com.testngdemo.automation.listeners.paralleltests.InvokedMethodListenerWithScreenshots;
import com.testngdemo.automation.listeners.retrymechanism.RetryListener;
import org.testng.annotations.Listeners;

/**
 * Second test suite for demo testNG purposes
 *
 * @author Eugene Polschikov
 * @date 2019-04-11
 */
@Listeners({ InvokedMethodListenerWithScreenshots.class, RetryListener.class })
public class GoogleLandingPageSecondTest extends TestSuitesBase {
}
