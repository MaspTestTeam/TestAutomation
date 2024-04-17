package IOSS;

import Components.ChromeDriverInit;
import Components.SignIn;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ******************************************************************
// SCRIPT WILL REGISTER TO IOSS SERVICE WITH A FIXED ESTABLISHMENT AND SAVE DETAILS OF REGISTRATION
// YOU NEED GOV GATEWAY ID, VRN AND BP FOR AN ACCOUNT TO BEGIN CREATION
// THE VARIABLES NEEDED ARE A GG ACCOUNT WITH BTA ACCOUNT (use BTACreationWithOutlook to make BTA)
// VRN AND BTA MUST BE VALID TO ALLOW IOSS CREATION
// YO CALL THE SCRIPT USE THE  MAIN FUNCTION AND PUT IN THE VARIABLES NEEDED
// DEMO BEING TRUE WILL SLOW THE AUTOMATION DOWN TO SEE WHAT'S HAPPENING.
// THIS WILL OUTPUT A FILE THAT PRINTS OUT DETAILS FOR A NEW IOSS ACCOUNT
// YOU CAN SCREENSHOT THE FINAL PAYMENT REFERENCE IF YOU WANT
// BY SETTING THE takeScreenShot VARIABLE TO TRUE (FALSE BY DEFAULT)
// ******************************************************************
public class IOSSRegistrationWithFeScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSRegistrationWithFeScript seleniumScript = new IOSSRegistrationWithFeScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        boolean takeScreenShot = false; // If you want a screenshot of the completed reg change this to true.
        String GGIDValue = "48 19 56 90 51 44"; // Replace with the GGId of the account you're using
        String VRNValue = "889000038"; // Use the same VRN used in previous script
        String bpId = "100005038";  // bpID for the account created linked to vrn
        String FeCountry = "Hungray"; // The country your using as fixed establishment
        String FeVATNumber = "HU11444444"; // The VAT number used to register in the FE country

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, takeScreenShot, GGIDValue, VRNValue, bpId, FeCountry, FeVATNumber);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }


    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, boolean takeScreenShot, String govGatewayID, String VRNvalue, String BPID, String FeCountry, String FeVATNumber
    ) throws IOException, InterruptedException {
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
        String govGatewayStartPointURL = dotenv.get("IOSS_REGISTRATION_LINK"); // Start point to create IOSS registration
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used to create and log in
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app
        String outlookEmail = dotenv.get("OUTLOOK_EMAIL");  //Outlook email account needed
        String ibanCode = dotenv.get("IBAN_CODE"); //Payment IBAN required for set up


        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        ChromeDriverInit chromeDriverInit = new ChromeDriverInit(); // Initialise chrome driver component
        WebDriver driver = chromeDriverInit.driverInit();


        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="accounts/ioss_created_accounts.txt";
        File file = new File(filepath);
        //class to write to the file loaded
        FileWriter fileWriter = new FileWriter(file, true);
        //class used to edit the file
        BufferedWriter buffedWriter = new BufferedWriter(fileWriter);


        try{
            //***************************************************************
            //******************* AUTOMATION START POINT ********************
            //***************************************************************
            //                      OPEN GOV GATEWAY
            //***************************************************************
            // Re-open start point URL but log in this time.
            driver.get(govGatewayStartPointURL);

            //Is your business already registered for the Import One-Stop Shop scheme in an EU country?
            driver.findElement(By.id("value-no")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Does your business sell goods from countries outside the EU to consumers in the EU or Northern Ireland?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Do any of these goods have a consignment value of £135 or less?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Is your business registered for VAT in the UK?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Are you a Northern Ireland business?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            // Click continue
            Thread.sleep(500);
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //***************************************************************
            //                      SIGN IN
            //***************************************************************
            SignIn signIn = new SignIn(); // Initialise the sign in component
            signIn.signInAutomationSteps(driver, govGatewayID, govGatewayPassword, authenticationCode);

            //***************************************************************
            //REPEAT INITIAL STEPS NOW LOGGED IN TO GET TO THE RIGHT START POINT
            //***************************************************************
            driver.get(govGatewayStartPointURL);
            //Is your business already registered for the Import One-Stop Shop scheme in an EU country?
            driver.findElement(By.id("value-no")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Does your business sell goods from countries outside the EU to consumers in the EU or Northern Ireland?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Do any of these goods have a consignment value of £135 or less?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Is your business registered for VAT in the UK?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Are you a Northern Ireland business?
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            // Click continue
            Thread.sleep(500);
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //***************************************************************
            //                  ENTER DETAILS
            //***************************************************************
            //Do you want to register this business for the Import One-Stop Shop Union scheme?
            driver.findElement(By.id("value_0")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Does your business trade using a name that is different?
            driver.findElement(By.id("value-no")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Has your business ever registered for an Import One-Stop Shop scheme in an EU country?
            driver.findElement(By.id("value-no")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Is your business registered for VAT in EU countries?
            // Click yes
            driver.findElement(By.id("value")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //***************************************************************
            //                  FIXED ESTABLISHMENT DETAILS
            //***************************************************************
            // Enter an EU country where your business is registered for tax
            if (demo) { Thread.sleep(waitTime); }
            // Enter the name of the country into the input box
            String inputCache=driver.findElement(By.id("value")).getAttribute("value");
            if (inputCache.isEmpty()) {
                driver.findElement(By.id("value")).sendKeys(FeCountry);
                Thread.sleep(700);
                // Click continue twice
                driver.findElement(By.id("continue")).click();
                driver.findElement(By.id("continue")).click();
            } else{
                // Click continue
                driver.findElement(By.id("continue")).click();
            }

            //Does your business have a fixed establishment in XXXXXX
            //Click yes
            driver.findElement(By.id("value")).click();
            if (demo) { Thread.sleep(waitTime); }
            //Click continue
            driver.findElement(By.id("continue")).click();

            // What sort of registration do you have in XXXXXX?
            // Click VAT number
            driver.findElement(By.id("value_0")).click();
            if (demo) { Thread.sleep(waitTime); }
            // Click continue
            driver.findElement(By.id("continue")).click();

            // What is your VAT registration number for XXXXXX?
            // Enter the VAT number in the input box
            driver.findElement(By.id("value")).sendKeys(FeVATNumber);
            Thread.sleep(400);
            // Click continue
            driver.findElement(By.id("continue")).click();

            // What is your trading name in XXXXXX?
            // Enter the Trading name
            driver.findElement(By.id("value")).sendKeys("Fixed Establishment VATECOM");
            Thread.sleep(400);
            // Click continue
            driver.findElement(By.id("continue")).click();

            // What is the fixed establishment address in Austria?
            // Address line 1
            driver.findElement(By.id("line1")).sendKeys("16 Grove Lane");
            //Town or City
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("townOrCity")).sendKeys("TEST");
            if (demo) { Thread.sleep(waitTime); }
            // Click continue
            driver.findElement(By.id("continue")).click();

            //Check your answers for XXXXXX
            // Click continue
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();

            // You added tax details for one EU country
            // Add tax details for another EU country?
            //Click no
            driver.findElement(By.id("value-no")).click();
            if (demo) { Thread.sleep(waitTime); }
            // Click continue
            driver.findElement(By.id("continue")).click();

            //***************************************************************
            //                  CONTINUE NON FE DETAILS
            //***************************************************************
            //Enter a website you use to sell your goods
            driver.findElement(By.id("value")).sendKeys("www.testsite.com");
            Thread.sleep(500);
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Add another website address?
            driver.findElement(By.id("value-no")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Business contact details
            //Enter Business contact details
            Thread.sleep(2000);
            // Enter contact name
            driver.findElement(By.id("fullName")).sendKeys("Release72");
            if (demo) { Thread.sleep(waitTime); }
            // Enter telephone number
            driver.findElement(By.id("telephoneNumber")).sendKeys("01111111111");
            if (demo) { Thread.sleep(waitTime); }
            // Enter Email address
            driver.findElement(By.id("emailAddress")).sendKeys(outlookEmail);
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();

            //Enter your bank or building society account details
            // Name on the account
            Thread.sleep(2000);
            driver.findElement(By.id("accountName")).sendKeys("MASP Testteam");
            if (demo) { Thread.sleep(waitTime); }
            // Fill in the IBAN
            driver.findElement(By.id("iban")).sendKeys(ibanCode);
            if (demo) { Thread.sleep(waitTime); }
            //Click continue
            driver.findElement(By.xpath("/html/body/div/main/div/div/form/div[4]/button")).click();

            //Check Your Answers + Click register
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

            //Save reference number after IOSS account is created
            Thread.sleep(10000);
            String refNumber = driver.findElement(By.xpath("/html/body/div[2]/main/div/div/div[1]/p/span")).getText().strip();


            //***************************************************************
            //                     SAVE ACCOUNT DETAILS
            //***************************************************************
            // Create a formatted timestamp
            LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String createdAt = dateTimeNow.format(dateTimeFormat);
            // Create a formatted string to save
            String accountDetailsCreated = govGatewayID + '\t' + createdAt + '\t' + refNumber + '\t' + VRNvalue + '\t' + BPID +"\t" + "false" + '\t'+'\t' + FeVATNumber + '\t'+'\t'+"none";
            //write the string to the file
            buffedWriter.write(accountDetailsCreated);
            //start a new line so the next variable appended is on a new line
            buffedWriter.newLine();
            //close classes once file has been edited
            buffedWriter.close();
            fileWriter.close();


            //***************************************************************
            //             TAKE AND SAVE SCREENSHOT OF REG
            //***************************************************************
            String result = "Demo Selected: " + demo + "\n";
            //Check if user wants screenshot before taking the screenshot of the final payment
            if (takeScreenShot){
                // Scroll down to view more information on the screen
                // if the payment reference doesn't scroll into view you can change the (x,y) values of the scrollBy function
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,400)");
                // Take screenshot
                TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
                File screenshotFile = screenshotDriver.getScreenshotAs(OutputType.FILE);
                // Save screenshot
                try {
                    FileHandler.copy(screenshotFile, new File("evidence/screenshots/IOSS/Registrations/IOSSReg_WithFE_"+refNumber+".png"));
                    result += "Screenshot Saved to: " + "evidence/screenshots/IOSS/Registrations/IOSSReg_WithFE_"+refNumber+".png"+'\n';
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


            // Include demoSelected and gatewayIDValue in the result
            result += "IOSS Account created with Gov GatewayID: " + govGatewayID + "\n";
            result += "Details saved to: " + filepath + "\n";


            //***************************************************************
            //                          END TIMER
            //***************************************************************
            long finishTime = System.currentTimeMillis();
            double timeElapsedInSeconds = (finishTime - startTime)/1000d;
            result += "Time to Run Script: " + timeElapsedInSeconds + " seconds.";

            // Return final result string
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
