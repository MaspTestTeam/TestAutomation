package OSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.Duration;

// ************************************************************
// THIS SCRIPT WILL LOG INTO THE SIT ENVIRONMENT
// CLICK THE VIEW OSS ACCOUNT DASHBOARD AND IF THAT FAILS
// ************************************************************
public class OSSDashboardLoginScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        OSSDashboardLoginScript seleniumScript = new OSSDashboardLoginScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "75 46 24 97 58 71"; // Replace with your value for GGID
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue);
        System.out.println(result);
    }


    public String executeSeleniumScript(boolean demo, String govGatewayID) throws IOException, InterruptedException {
        //***************************************************************
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        int waitTime = 1000;

        //***************************************************************
        //                  VARIABLES & .env LOADED
        //***************************************************************
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayBTAStartPoint = dotenv.get("RETURNS_URL"); // Start point to LOG INTO BTA
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used to create and log in
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app

        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        ChromeOptions options = new ChromeOptions();
        options.addArguments( "incognito");
        WebDriver driver = new ChromeDriver(options);
        // Implicit wait so selenium retry for 8 seconds if elements do not load instantly.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        //***************************************************************
        //******************* AUTOMATION START POINT ********************
        //***************************************************************
        //                      OPEN GOV GATEWAY
        //***************************************************************
        // Open start point URL but log in this time.
        driver.get(govGatewayBTAStartPoint);

        //clear cookie banner if demo so screen can be seen clearer
        if (demo){
            // Accept cookies
            driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/button[1]")).click();
            //Clear Banner
            driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/button")).click();
            Thread.sleep(waitTime);
        }

        //***************************************************************
        //                      SIGN IN
        //***************************************************************
        driver.findElement(By.id("user_id")).sendKeys(govGatewayID);
        driver.findElement(By.id("password")).sendKeys(govGatewayPassword);
        Thread.sleep(2000);
        WebElement continueElement =driver.findElement(By.id("continue"));
        if (continueElement.isDisplayed() && continueElement.isEnabled()) {
            continueElement.click();
        }
        // Authentication code
        driver.findElement(By.id("oneTimePassword")).sendKeys(authenticationCode);
        Thread.sleep(2000);
        WebElement authContinueElement =driver.findElement(By.id("continue"));
        if (authContinueElement.isDisplayed() && authContinueElement.isEnabled()) {
            authContinueElement.click();
        }
        // Skip activities
        //driver.findElement(By.id("confirm-No")).click();
        //driver.findElement(By.id("continue")).click();
        if (demo) { Thread.sleep(waitTime); }

        //***************************************************************
        //                      VIEW OSS ACCOUNT
        //***************************************************************
        //USE HYPERLINK
        driver.findElement(By.id("oss-view-account")).click();

        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "OSS LOGIN SCRIPT RAN" + "\n";
        result += "GOV GATEWAY ID: " + govGatewayID + "\n";
        return result;

    }
}
