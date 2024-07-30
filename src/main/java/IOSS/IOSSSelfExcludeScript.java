package IOSS;

import Components.ChromeDriverInit;
import Components.IOSSStubSignIn;
import Components.SignIn;
import Components.SuiteUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

// ******************************************************************
// SCRIPT WILL LOG THE USER INTO THEIR DASHBOARD ACCOUNT AND THEN
// REQUEST TO LEAVE THE SERVICE DEPENDING ON THE REASON CODE
// THE TEST REQUIRES, WHICH IS INPUT AS A VARIABLE.
// IF YOU ARE JOINING A NEW COUNTRY THEN YOU MUST SUPPLY THAT INFORMATION TOO
// IF YOU ARE NOT JOINING A NEW COUNTRY YOU CAN LEAVE IT AS IT IS, IT WON'T BE ADDED.
// reason Code 3 = no longer carry out supplies of goods covered by scheme.
// reason code 5 = leave without explicit reason.
// reason code 6 = Join new MSID.
// ******************************************************************
public class IOSSSelfExcludeScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSSelfExcludeScript seleniumScript = new IOSSSelfExcludeScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "70 37 17 77 61 20"; // Replace with your value for GGID
        // Replace the reasonCode you are testing the self exclusion with
        String reasonCode = "1";
        // If you are joining another country you need the country Tax Code and name - reason code 6
        String newCountry = "Austria";
        //String newCountryTaxCode = "EL123456789"; //Greece example tax code
        String newCountryTaxCode = "ATU12345678"; // Austria example tax code
        // ONLY NEED TO CHANGE IF NORMAL LOG IN NOT WORKING
        boolean stubLogin = false;     // Set to true if normal login isn't working
        String vrn = "991122105";   // VRN for account needed to run Stub login - not needed if stub login isn't needed
        String iossId = "IM9000005802";  // IOSS ID for stub log in, not needed if stub login isn't needed.

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(
                demoSelected, GGIDValue, reasonCode, newCountry, newCountryTaxCode, vrn, iossId, stubLogin
        );
        //Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }

    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, String govGatewayID, String reasonCode, String newCountry, String newCountryTaxCode, String vrn, String iossId, boolean stubLogin
    ) throws IOException, InterruptedException {
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

        // Click leave the service hyperlink
        driver.findElement(By.id("leave-scheme")).click();

        SuiteUtils utils = new SuiteUtils();
        //***************************************************************
        //                      SELF EXCLUDE FORMS
        //***************************************************************
        // Are you moving your business to an EU country?
        if (Objects.equals(reasonCode, "6")) {
            // Reason code 6 is moving to a new country so click yes.
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();

            //Which country is your business moving to?
            utils.preventInputDuplicationWithContinue("value", newCountry, "continue", driver);

            // When did or when will your business move to xxxxxxxxx?
            // Use today's date
            String currentDay =Integer.toString(LocalDate.now().getDayOfMonth());
            String currentMonth =Integer.toString(LocalDate.now().getMonthValue());
            String currentYear =Integer.toString(LocalDate.now().getYear());
            utils.preventInputDuplication("value.day", currentDay, driver);
            utils.preventInputDuplication("value.month", currentMonth, driver);
            utils.preventInputDuplication("value.year", currentYear, driver);
            //Click continue
            driver.findElement(By.id("continue")).click();

            //What is your VAT registration number for Austria?
            utils.preventInputDuplicationWithContinue("value", newCountryTaxCode, "continue", driver);

            // Check your answers
            // Click continue
            driver.findElement(By.id("continue"));

        } else {
            // Reason code 1 and 5 require you to click no
            driver.findElement(By.id("value-no")).click();
            driver.findElement(By.id("continue")).click();

            //Has your business stopped selling eligible goods to customers in the EU or Northern Ireland?
            if (Objects.equals(reasonCode, "1")) {
                // Stopped supplying goods
                //Click yes
                driver.findElement(By.id("value")).click();
                driver.findElement(By.id("continue")).click();

                // When did or when will your business stop selling eligible goods?
                // Use today's date
                String currentDay =Integer.toString(LocalDate.now().getDayOfMonth());
                String currentMonth =Integer.toString(LocalDate.now().getMonthValue());
                String currentYear =Integer.toString(LocalDate.now().getYear());
                utils.preventInputDuplication("value.day", currentDay, driver);
                utils.preventInputDuplication("value.month", currentMonth, driver);
                utils.preventInputDuplication("value.year", currentYear, driver);
                //Click continue
                driver.findElement(By.id("continue")).click();

            } else {
                //NO explicit reason
                //Click no
                driver.findElement(By.id("value-no")).click();
                driver.findElement(By.id("continue")).click();

                // Are you sure you want to leave the One-Stop Shop scheme?
                //Click yes
                driver.findElement(By.id("value")).click();
                driver.findElement(By.id("continue")).click();
            }
        }

        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "IOSS SELF EXCLUDES SCRIPT RAN" + "\n";
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
