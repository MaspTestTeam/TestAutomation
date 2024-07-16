package OSS;

import Components.ChromeDriverInit;
import Components.SignIn;
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
        String GGIDValue = "40 26 71 06 36 99"; // Replace with your value for GGID

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue);
        //Print out the results/information after the selenium script has finished running
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
        //               VARIABLES & .env LOADED & TIMER
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
        //                      VIEW OSS ACCOUNT
        //***************************************************************
        //USE HYPERLINK
        driver.findElement(By.id("oss-view-account")).click();

        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "OSS LOGIN SCRIPT RAN" + "\n";
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
