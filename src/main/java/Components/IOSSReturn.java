package Components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Objects;

// ************************************************
// THIS FUNCTION WILL HOLD FUNCTIONS AND METHODS
// THAT ARE RPEATEAD WHEN MAKING AN IOSS RETURN
// ************************************************
public class IOSSReturn {

    //This function will execute the steps to declare a trade with a given country(country) for an amount(amountTraded).
    // It needs variables from the main script like demo and waitTime as well as access to the driver to continue the script.
    public void declareTradeWithCountry(WebDriver driver, boolean demo, int waitTime, String country, String amountTraded) throws InterruptedException {
        // Which country did you sell to from Northern Ireland?
        // Check input is empty before typing in another value - this is filled if been completed before.
        WebElement countrySoldToSecondInput = driver.findElement(By.id("value"));
        if ((countrySoldToSecondInput.getAttribute("value").isEmpty()))
        {
            driver.findElement(By.id("value")).sendKeys(country);
            if (demo) { Thread.sleep(waitTime); }
            // Double click needed
            driver.findElement(By.id("continue")).click();
            driver.findElement(By.id("continue")).click();
        } else if ((!Objects.equals(countrySoldToSecondInput.getAttribute("value"), country))) {
            for (int i =0; i<=30; i++) {
                driver.findElement(By.id("value")).sendKeys(Keys.BACK_SPACE);
            }
            driver.findElement(By.id("value")).sendKeys(country);
            // Click continue
            driver.findElement(By.id("continue")).click();
            driver.findElement(By.id("continue")).click();
        }
        else {
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        }

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
        driver.findElement(By.id("choice")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Check your answers
        //Add another VAT rate?
        //Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

    }
}
