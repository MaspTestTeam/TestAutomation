package IOSS;

import Components.ChromeDriverInit;
import Components.IOSSStubSignIn;
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
        String GGIDValue = "70 37 17 77 61 20"; // Replace with the GGId of the account you're using
        // ONLY NEED TO CHANGE IF NORMAL LOG IN NOT WORKING
        boolean stubLogin = false;     // Set to true if normal login isn't working
        String vrn = "991122105";   // VRN for account needed to run Stub login - not needed if stub login isn't needed
        String iossId = "IM9000005802";  // IOSS ID for stub log in, not needed if stub login isn't needed.

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue, vrn, iossId, stubLogin);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }

    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(boolean demo, String govGatewayID, String vrn, String iossId, boolean stubLogin) throws IOException, InterruptedException {
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
        String govGatewayStubLoginURL = dotenv.get("STUBS_LOGIN_URL"); // Start point to log in with stubs
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

        if (!stubLogin) {
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
        } else {
            //***************************************************************
            //                      OPEN GOV GATEWAY
            //***************************************************************
            // Open start point URL for stub login.
            driver.get(govGatewayStubLoginURL);

            //***************************************************************
            //                      SIGN IN WITH STUBS
            //***************************************************************
            IOSSStubSignIn iossStubSignIn = new IOSSStubSignIn();
            iossStubSignIn.signInWithStubs(driver, vrn, iossId);
        }

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
