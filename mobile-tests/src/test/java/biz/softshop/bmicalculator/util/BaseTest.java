package biz.softshop.bmicalculator.util;


import biz.softshop.bmicalculator.ui.App;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.*;

import java.net.URL;

public class BaseTest {
    protected static AndroidDriver<MobileElement> driver;
    protected static Utils utils;
    protected static App app;

    @BeforeSuite
    public static void setUpSuite() throws Exception {
        // Create a new instance of Android driver and connect to Appium server
        driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"),
                new DesiredCapabilities());

        utils = new Utils(driver);
        app = new App(driver);

        //Use a higher value if your mobile elements take time to show up
        utils.setImplicitWait(FrameworkConstants.IMPLICIT_WAIT_TIMEOUT);
    }

    @AfterSuite
    public static void tearDown() {
        driver.closeApp();
        driver.quit();
    }
}
