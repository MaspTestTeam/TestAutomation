package Components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// *****************************************************************************
// THIS CLASS COMPONENT WILL COMPLETE THE IOSS STUB LOGIN FORM
// BY DEFAULT IT WILL WAIT FOR THE BUTTONS TO BECOME ACTIVE BEFORE CONTINUING
// *****************************************************************************
public class IOSSStubSignIn {
    // FUNCTION THAT WILL SIGN IN TO A BTA ACCOUNT USING GGID AND PASSWORD WITH AUTHENTICATION CODE
    public void signInWithStubs(
            WebDriver driver, String vrn,  String iossId
    ) throws InterruptedException {
        // Wait time for driver that will check when a button is enabled
        WebDriverWait driverWaitTime = new WebDriverWait(driver, Duration.ofSeconds(5));

        //Enter the redirect URL
        driver.findElement(By.id("redirectionUrl")).sendKeys("/pay-vat-on-goods-sold-to-eu/import-one-stop-shop-returns-payments");

        // Set affinity group to organisation from drop down menu
        Select organisationDropDown = new Select(driver.findElement(By.id("affinityGroupSelect")));
        organisationDropDown.selectByValue("Organisation");

        // Set VRN Enrollment
        driver.findElement(By.id("enrolment[0].name")).sendKeys("HMRC-MTD-VAT");    // Enrolment key
        driver.findElement(By.id("input-0-0-name")).sendKeys("VRN"); // Identifier
        driver.findElement(By.id("input-0-0-value")).sendKeys(vrn); // Identifier value

        // Set IOSS ID Enrollment
        driver.findElement(By.id("enrolment[1].name")).sendKeys("HMRC-IOSS-ORG");    // Enrolment key
        driver.findElement(By.id("input-1-0-name")).sendKeys("IOSSNumber"); // Identifier
        driver.findElement(By.id("input-1-0-value")).sendKeys(iossId); // Identifier value

        // Click submit
        driver.findElement(By.xpath("/html/body/div/main/div/div/form/input[2]")).click();
    }
}
