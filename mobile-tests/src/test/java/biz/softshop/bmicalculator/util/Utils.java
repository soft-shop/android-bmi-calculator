package biz.softshop.bmicalculator.util;


import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This class contains various utility methods/wrapper
 * for the automation framework
 *
 * @author Softshop
 */
public class Utils {

    private AndroidDriver<MobileElement> driver;
    private Properties suiteProperties;

    public Utils(AndroidDriver<MobileElement> driver) {
        this.driver = driver;
    }

    /**
     * Checks if element is present
     *
     * @param locator
     * @return true/false
     */
    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Wrapper for setting implicit wait
     *
     * @param timeoutInSeconds
     */
    public void setImplicitWait(int timeoutInSeconds) {
        driver.manage()
                .timeouts()
                .implicitlyWait(timeoutInSeconds, TimeUnit.SECONDS);
    }

    /**
     * Captures screenshot from Appium
     *
     * @param name screenshot file name
     * @return
     */
    public boolean takeScreenshot(final String name) {
        String screenshotDirectory = System.getProperty("appium.screenshots.dir",
                System.getProperty("java.io.tmpdir", ""));
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        return screenshot.renameTo(new File(screenshotDirectory, String.format("%s.png", name)));
    }

    /**
     * Wrapper for hardcoded wait. Please avoid using this method
     *
     * @param timeout
     * @throws Exception
     */
    public void waitFor(long timeout) throws Exception {
        Thread.sleep(timeout);
    }
}
