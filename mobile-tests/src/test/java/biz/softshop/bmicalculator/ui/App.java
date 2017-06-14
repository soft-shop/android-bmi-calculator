package biz.softshop.bmicalculator.ui;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.By;

/**
 * This class represents the App UI
 *
 * @author Softshop
 */
public class App {
    private AndroidDriver<MobileElement> driver;


    public App(AndroidDriver<MobileElement> driver) {
        this.driver = driver;
    }

    /**
     * Calculates the BMI
     * @param weight
     * @param height
     */
    public void calculateBmi(String weight, String height) {
        driver.findElement(By.id("weightField")).sendKeys(weight);
        driver.findElement(By.id("heightField")).sendKeys(height);
        driver.hideKeyboard();
        driver.findElement(By.id("calculateButton")).click();
    }

    /**
     * Gets the BMI value
     * @return
     */
    public String getBmi() {
        return driver.findElement(By.id("bmiLabel")).getText();
    }

    /**
     * Gets the BMI Category value
     * @return
     */
    public String getBmiCategory() {
        return driver.findElement(By.id("bmiCatLabel")).getText();
    }
}
