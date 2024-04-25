package Components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// ************************************************
// THIS FUNCTION WILL HOLD FUNCTIONS AND METHODS
// THAT ARE REPEATED WHEN MAKING AN OSS RETURN
// ************************************************
public class OSSReturn {

    //This function will execute the steps to declare a trade with a given country(country) for an amount(amountTraded).
    // It needs variables from the main script like demo and waitTime as well as access to the driver to continue the script.
    public void ossDeclareTradeWithCountry(WebDriver driver, boolean demo, int waitTime, String country, String amountTraded) throws InterruptedException {
        SuiteUtils utils = new SuiteUtils();
        // Which country did you sell to from Northern Ireland?
        utils.preventInputDuplication("value", country, driver);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();
        driver.findElement(By.id("continue")).click();

        // Which VAT rates did you charge?
        // Click just top value
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // What were your sales at xx% rate excluding VAT?
        driver.findElement(By.id("value")).sendKeys(amountTraded);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // How much VAT did you charge on sales of £x at xx% VAT rate?
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Check your answers
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();
    }


    //This function will execute the steps to declare a trade with a two EU countries(countrySellingFrom and countrySellingTo) for a given amount(amountTraded)
    // It needs variables from the main script like demo and waitTime as well as access to the driver to continue the script.
    public void declareEUtoEUTrade(WebDriver driver, boolean demo, int waitTime, String countrySellingFrom, String countrySellingTo, String amountTraded) throws InterruptedException {
        SuiteUtils utils = new SuiteUtils();
        // Which EU country did you sell goods from?
        utils.preventInputDuplication("value", countrySellingFrom, driver);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();
        driver.findElement(By.id("continue")).click();

        // Where did you sell to from XXXXXX? Pick any country except NI
        utils.preventInputDuplication("value", countrySellingTo, driver);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();
        driver.findElement(By.id("continue")).click();

        // Which VAT rates did you charge?
        // Click top value
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        //What were your sales at XX% rate excluding VAT?
        driver.findElement(By.id("value_0")).sendKeys(amountTraded);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // How much VAT did you charge on sales of £XXXXX at XX% VAT rate?
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Check your answers
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Add sales from XXXXXX to another EU country?
        if (demo) { Thread.sleep(waitTime); }
        //Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();
    }
}
