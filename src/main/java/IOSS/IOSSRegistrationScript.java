package IOSS;

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
// SCRIPT WILL REGISTER TO IOSS SERVICE AND SAVE DETAILS OF REGISTRATION
// YOU NEED GOV GATEWAY ID, VRN AND BP FOR AN ACCOUNT TO BEGIN CREATION
// THE VARIABLES NEEDED ARE A GG ACCOUNT WITH BTA ACCOUNT (use BTACreationWithOutlook to make BTA)
// VRN AND BTA MUST BE VALID TO ALLOW IOSS CREATION
// YO CALL THE SCRIPT USE THE  MAIN FUNCTION AND PUT IN THE VARIABLES NEEDED
// DEMO BEING TRUE WILL SLOW THE AUTOMATION DOWN TO SEE WHAT'S HAPPENING.
// THIS WILL OUTPUT A FILE THAT PRINTS OUT DETAILS FOR A NEW IOSS ACCOUNT
// YOU CAN SCREENSHOT THE FINAL PAYMENT REFERENCE IF YOU WANT
// BY SETTING THE takeScreenShot VARIABLE TO TRUE (FALSE BY DEFAULT)
// ******************************************************************
public class IOSSRegistrationScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSRegistrationScript seleniumScript = new IOSSRegistrationScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        boolean takeScreenShot = false; // If you want a screenshot of the completed payment change this to true.
        String GGIDValue = "13 43 13 24 14 51"; // Replace with the GGId of the account you're using
        String VRNValue = "900000115"; // Use the same VRN used in previous script
        String bpId = "100357716";  // bpID for the account created linked to vrn

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, takeScreenShot, GGIDValue, VRNValue, bpId);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }


    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, boolean takeScreenShot, String govGatewayID, String VRNvalue, String BPID
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
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        ChromeOptions options = new ChromeOptions();
        options.addArguments( "incognito");
        WebDriver driver = new ChromeDriver(options);
        // Implicit wait so selenium retry for 8 seconds if elements do not load instantly.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        // Full screen window
        driver.manage().window().maximize();


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

            //clear cookie banner if demo so screen can be seen clearer
            if (demo){
                // Accept cookies
                driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/button[1]")).click();
                //Clear Banner
                driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/button")).click();
                Thread.sleep(waitTime);
            }


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
            //                      SIGN IN TO GG ACCOUNT
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
            driver.findElement(By.id("value-no")).click();
            driver.findElement(By.id("continue")).click();
            if (demo) { Thread.sleep(waitTime); }

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
            String accountDetailsCreated = govGatewayID + '\t' + createdAt + '\t' + refNumber + '\t' + VRNvalue + '\t' + BPID +"\t" + "false" + '\t'+'\t' + "false" + '\t' +'\t'+'\t'+"none";
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
                    FileHandler.copy(screenshotFile, new File("evidence/screenshots/IOSS/Registrations/IOSSReg_"+refNumber+".png"));
                    result += "Screenshot Saved to: " + "evidence/screenshots/IOSS/Registrations/IOSSReg_"+refNumber+".png"+'\n';
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
