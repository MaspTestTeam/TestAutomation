package Components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// *****************************************************************************
// THIS CLASS COMPONENT WILL CLEAR COOKIES AND SIGN THE USER IN
// BY DEFAULT IT WILL WAIT FOR THE BUTTONS TO BECOME ACTIVE BEFORE CONTINUING
// *****************************************************************************
public class SignIn {
    // FUNCTION THAT WILL SIGN IN TO A BTA ACCOUNT USING GGID AND PASSWORD WITH AUTHENTICATION CODE
    public void signInAutomationSteps(
            WebDriver driver, String govGatewayID, String govGatewayPassword, String authenticationCode
    ) throws InterruptedException {
        // Wait time for driver that will check when a button is enabled
        WebDriverWait driverWaitTime = new WebDriverWait(driver, Duration.ofSeconds(5));
        //***************************************************************
        //                      CLEAR COOKIE BANNER
        //***************************************************************
        driverWaitTime.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/form/div/div/div[2]/button[1]")));
        driver.findElement(By.xpath("/html/body/form/div/div/div[2]/button[1]")).click();
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/a")).click();

        //***************************************************************
        //                      SIGN IN
        //***************************************************************
        driver.findElement(By.id("user_id")).sendKeys(govGatewayID);
        driver.findElement(By.id("password")).sendKeys(govGatewayPassword);
        // Wait for continue button to be enabled
        driverWaitTime.until(ExpectedConditions.elementToBeClickable(By.id("continue")));
        driver.findElement(By.id("continue")).click();

        // Authentication code
        driver.findElement(By.id("oneTimePassword")).sendKeys(authenticationCode);
        // Wait for continue button to be enabled
        driverWaitTime.until(ExpectedConditions.elementToBeClickable(By.id("continue")));
        driver.findElement(By.id("continue")).click();


        //***************************************************************
        //                      SKIP ACTIVITIES
        // This is currently not needed in SIT but can be added
        //***************************************************************
        // driver.findElement(By.id("confirm-No")).click();
        //driver.findElement(By.id("continue")).click();
    }
}
