package IOSS;

import Components.ChromeDriverInit;
import Components.SignIn;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;


import java.io.IOException;
import java.util.Objects;


public class IOSSAmendsScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSAmendsScript seleniumScript = new IOSSAmendsScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        String GGIDValue = "70 37 17 77 61 20"; // Replace with the GGId of the account you're using
        String newContactName = "Release73"; // The new contact name you want to change to


        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue, newContactName);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }

    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(boolean demo, String govGatewayID, String newContactName) throws IOException, InterruptedException {
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
        //                      AMEND IOSS ACCOUNT
        //***************************************************************
        //Click view ioss account
        driver.findElement(By.id("ioss-view-account")).click();

        //Click change your registration
        driver.findElement(By.id("change-your-registration")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Click change Contact name or business department
        driver.findElement(By.xpath("/html/body/div[2]/main/div/div/dl[2]/div[5]/dd[2]/a")).click();
        if (demo) { Thread.sleep(waitTime); }

        //Check Contact name does not equal what to change to - the amend won't work if it's the same
        String currentContact = driver.findElement(By.id("fullName")).getAttribute("value");
        if (Objects.equals(currentContact, newContactName)){
            return "\nSCRIPT FAILED \nChange newContactName to something else, this is already the Contact Name";
        } else{
            //Update with new contact details.
            for (int i=0; i<currentContact.length(); i++){
                //Delete current input value text
                driver.findElement(By.id("fullName")).sendKeys(Keys.BACK_SPACE);
            }
            // Input new contact details
            driver.findElement(By.id("fullName")).sendKeys(newContactName);
        }

        //Click continue
        driver.findElement(By.id("contonue")).click();

        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "IOSS AMEND ACCOUNT SCRIPT RAN" + "\n";
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
