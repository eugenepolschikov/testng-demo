package com.testngdemo.automation.functional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test suite base: for demo purposes contains logging only!
 * @author Eugene Polschikov
 * @date 2019-04-11
 */
public abstract class TestSuitesBase {
    protected final static Logger log = LoggerFactory.getLogger(TestSuitesBase.class);
    protected  String baseUrl = "https://www.google.com";
}
