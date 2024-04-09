package IOSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;


public class IOSSAmendsScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSAmendsScript seleniumScript = new IOSSAmendsScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "87 15 52 59 16 48"; // Replace with your value
        String VRNValue = "999111031"; // Use the same VRN used in previous script
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue, VRNValue);
        System.out.println(result);
    }


    public String executeSeleniumScript(boolean demo, String govGatewayID ,String VRNValue) throws IOException, InterruptedException {
        //***************************************************************
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        int waitTime = 1000;

        //***************************************************************
        //              VARIABLES & .env LOADED & TIMER
        //***************************************************************
        // Start Timer
        long startTime = System.currentTimeMillis();
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayBTAStartPoint = dotenv.get("RETURNS_URL"); // Start point to LOG INTO BTA
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used to create and log in
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app
        String iossAmendsLink = dotenv.get("IOSS_AMENDS_LINK");   //Direct link to IOSS Amend account view

        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver2.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        ChromeOptions options = new ChromeOptions();
        options.addArguments( "incognito");
        WebDriver driver = new ChromeDriver(options);
        // Implicit wait so selenium retry for 8 seconds if elements do not load instantly.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        // Full screen window
        driver.manage().window().maximize();

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
        driver.findElement(By.id("confirm-No")).click();
        driver.findElement(By.id("continue")).click();
        if (demo) { Thread.sleep(waitTime); }

        //***************************************************************
        //                      AMEND IOSS ACCOUNT
        //***************************************************************
        //USE DIRECT URL IN NEW TAB SO STILL SIGNED IN
        ((JavascriptExecutor)driver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1)); //switches to new tab
        driver.get(iossAmendsLink);

        // Change contact name
        Thread.sleep(1000);
        //driver.findElement(By.xpath("/html/body/div[2]/main/div/div/dl[2]/div[5]/dd[2]/a")).click();
        //driver.findElement(By.id("fullName")).sendKeys("1");
        Thread.sleep(500);
        //driver.findElement(By.id("continue")).click();
        Thread.sleep(500);
        //Confirm the amendment
        //driver.findElement(By.xpath("/html/body/div[2]/main/div/div/form/div/button")).click();

        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "IOSS AMEND ACCOUNT SCRIPT RAN" + "\n";
        result += "GOV GATEWAY ID: " + govGatewayID + " VRN: " + VRNValue +"\n";
        //***************************************************************
        //                          END TIMER
        //***************************************************************
        long finishTime = System.currentTimeMillis();
        double timeElapsedInSeconds = (finishTime - startTime)/1000d;
        result += "Time to Run Script: " + timeElapsedInSeconds + " seconds.";

        // Return final result string
        return result;
    }
}
