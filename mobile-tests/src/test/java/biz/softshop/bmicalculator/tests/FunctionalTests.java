package biz.softshop.bmicalculator.tests;


import biz.softshop.bmicalculator.util.BaseTest;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * Functional Tests
 *
 * @author Softshop
 */
public class FunctionalTests extends BaseTest {

    @Test (description = "This is a functional test for BMI Calculation")
    public void shouldCalculateBMI() throws Exception {

        utils.takeScreenshot("Before Calculate");
        app.calculateBmi("80", "181");
        utils.takeScreenshot("After Calculate");

        Assert.assertEquals(app.getBmi(), "24.419281");
        Assert.assertEquals(app.getBmiCategory(), "Normal");
    }
 }
