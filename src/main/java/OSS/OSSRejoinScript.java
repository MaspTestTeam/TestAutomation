package OSS;

import Components.ChromeDriverInit;
import Components.SignIn;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

// ******************************************************************
// SCRIPT WILL LOG THE USER INTO THEIR DASHBOARD ACCOUNT AND THEN
// REQUEST TO REJOIN THE SERVICE AFTER SELF EXCLUDING
// SET THE DATE VALUE IF YOU ARE NEEDING TO SET THE DATE BEFORE TODAY'S DATE
// ******************************************************************
public class OSSRejoinScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        OSSRejoinScript seleniumScript = new OSSRejoinScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "67 05 95 02 23 97"; // Replace with your value for GGID
        // IF you require a previous eligible sales set this to true and set the new date required
        boolean eligibleSales = false;
        String salesDay = "3";
        String salesMonth = "7";
        String salesYear = "2024";

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue, eligibleSales, salesDay, salesMonth, salesYear);
        //Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }

    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, String govGatewayID, boolean eligibleSales, String salesDay, String salesMonth, String salesYear
            )throws IOException, InterruptedException {
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
        //                      VIEW OSS DASHBOARD
        //***************************************************************
        //USE HYPERLINK
        driver.findElement(By.id("oss-view-account")).click();

        // Click rejoin the service hyperlink
        driver.findElement(By.id("rejoin-this-service")).click();

        //Have you made eligible sales since 1 July 2024?
        if (eligibleSales){
            //Click yes
            driver.findElement(By.id("value")).click();
            //Click continue
            driver.findElement(By.id("continue")).click();

            //Date of your first eligible sale since xxxx
            driver.findElement(By.id("value.day")).sendKeys(salesDay);
            driver.findElement(By.id("value.month")).sendKeys(salesMonth);
            driver.findElement(By.id("value.year")).sendKeys(salesYear);
            //Click continue
            driver.findElement(By.id("continue")).click();

            //Your start date
            //Click continue
            driver.findElement(By.id("continue")).click();

        } else {
            //Click no
            driver.findElement(By.id("value-no")).click();
            //Click continue
            driver.findElement(By.id("continue")).click();

            //Your start date
            //Click continue
            driver.findElement(By.id("continue")).click();
        }

        //Check your details are still correct
        //Click continue
        driver.findElement(By.id("continue")).click();

        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "OSS SELF EXCLUDES SCRIPT RAN" + "\n";
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
