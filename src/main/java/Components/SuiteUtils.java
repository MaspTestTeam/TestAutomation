package Components;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Objects;

// ****************************************************
// CLASS THAT WILL HOLD WRAPPER FUNCTIONS AND METHODS
// THIS SCRIPT CAN BE USED IN ANY SCRIPT WITHIN
// THE SUITE
// *****************************************************
public class SuiteUtils {

    // When re-running scripts even in incognito there is a cache that lasts 15-30minutes.
    // This function can be wrapped around an input with the desired value
    // The function will check if the value is already there, if its different or empty and
    // Fill it out accordingly.
    // This will also assume there is only one input on the page
    public void preventInputDuplicationWithContinue(String elementID, String inputValue, String continueButtonID, WebDriver driver){
        WebElement inputValueElement = driver.findElement(By.id(elementID));
        if ((inputValueElement.getAttribute("value").isEmpty())){
            driver.findElement(By.id(elementID)).sendKeys(inputValue);
            driver.findElement(By.id(continueButtonID)).click();
        } else if ((!Objects.equals(inputValueElement.getAttribute("value"), inputValue))){
            for (int i =0; i<=40; i++) {
                driver.findElement(By.id(elementID)).sendKeys(Keys.BACK_SPACE);
            }
            driver.findElement(By.id(elementID)).sendKeys(inputValue);
            driver.findElement(By.id(continueButtonID)).click();
        }
        else{
            driver.findElement(By.id(continueButtonID)).click();
        }
    }

    // When re-running scripts even in incognito there is a cache that lasts 15-30minutes.
    // This function can be wrapped around an input with the desired value
    // The function will check if the value is already there, if its different or empty and
    // Fill it out accordingly.
    public void preventInputDuplication(String elementID, String inputValue, WebDriver driver){
        WebElement inputValueElement = driver.findElement(By.id(elementID));
        if ((inputValueElement.getAttribute("value").isEmpty())){
            driver.findElement(By.id(elementID)).sendKeys(inputValue);
        } else if ((!Objects.equals(inputValueElement.getAttribute("value"), inputValue))){
            for (int i =0; i<=40; i++) {
                driver.findElement(By.id(elementID)).sendKeys(Keys.BACK_SPACE);
            }
            driver.findElement(By.id(elementID)).sendKeys(inputValue);

        }
    }

}
