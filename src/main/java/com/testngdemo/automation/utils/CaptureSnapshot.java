package com.testngdemo.automation.utils;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Method responsible for capturing page snapshot
 *
 * @author Eugene Polschikov
 * @date 2019-04-11
 */

public class CaptureSnapshot {
    private final static Logger log = LoggerFactory.getLogger(CaptureSnapshot.class);

    public static String captureScreenshotForExtentHtmlReporter(WebDriver driver, String screenshotNamePattern) {

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String fileNameDestination = "target" + File.separator + "surefire-reports" + File.separator + "screenshots" +
                    File.separator + screenshotNamePattern + "_" + new SimpleDateFormat("yyyyMMddhhmmssSS'.png'").format(new Date());
            File destination = new File(fileNameDestination);
            FileUtils.copyFile(src, destination);
            log.info("screenshot have been taken and placed\n" + destination.getAbsolutePath());
            return destination.getAbsolutePath();
        } catch (Exception e) {
            log.error("unable to make screenshot");
            return e.getMessage();
        }
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public static byte[] captureSnapshotForAllure(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}

