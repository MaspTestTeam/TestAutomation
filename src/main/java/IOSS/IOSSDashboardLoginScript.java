package IOSS;

import Components.ChromeDriverInit;
import Components.SignIn;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

// ************************************************************
// THIS SCRIPT WILL LOG INTO THE SIT ENVIRONMENT
// CLICK THE VIEW IOSS ACCOUNT DASHBOARD
// ************************************************************
public class IOSSDashboardLoginScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSDashboardLoginScript seleniumScript = new IOSSDashboardLoginScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        String GGIDValue = "29 29 16 24 99 89"; // Replace with the GGId of the account you're using

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }

    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(boolean demo, String govGatewayID) throws IOException, InterruptedException {
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


        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        ChromeDriverInit chromeDriverInit = new ChromeDriverInit(); // Initialise chrome driver component
        WebDriver driver = chromeDriverInit.driverInit();


        //***************************************************************
        //******************* AUTOMATION START POINT ********************
        //***************************************************************
        //                      OPEN GOV GATEWAY
        //***************************************************************
        // Open start point URL but log in this time.
        driver.get(govGatewayBTAStartPoint);

        //***************************************************************
        //                      SIGN IN
        //***************************************************************
        SignIn signIn = new SignIn(); // Initialise the sign in component
        signIn.signInAutomationSteps(driver, govGatewayID, govGatewayPassword, authenticationCode);

        //***************************************************************
        //                      VIEW IOSS ACCOUNT
        //***************************************************************
        //USE HYPERLINK
        driver.findElement(By.id("ioss-view-account")).click();

        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "IOSS LOGIN SCRIPT RAN" + "\n";
        result += "GOV GATEWAY ID: " + govGatewayID + "\n";


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
