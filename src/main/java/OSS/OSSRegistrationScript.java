package OSS;

import Components.ChromeDriverInit;
import Components.SignIn;
import Components.SuiteUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ******************************************************************
// SCRIPT WILL REGISTER TO OSS SERVICE AND SAVE DETAILS OF REGISTRATION
// YOU NEED GOV GATEWAY ID, VRN AND BP FOR AN ACCOUNT TO BEGIN CREATION
// THE VARIABLES NEEDED ARE A GG ACCOUNT WITH BTA ACCOUNT (use BTACreationWithOutlook to make BTA)
// VRN AND BTA MUST BE VALID TO ALLOW OSS CREATION
// ONLY CHANGE THE DATE OF FIRST SALE VARIABLE IF THE SCRIPT FAILS THIS MAY NEED TO BE UPDATED
// YO CALL THE SCRIPT USE THE  MAIN FUNCTION AND PUT IN THE VARIABLES NEEDED
// DEMO BEING TRUE WILL SLOW THE AUTOMATION DOWN TO SEE WHAT'S HAPPENING.
// THIS WILL OUTPUT A FILE THAT PRINTS OUT DETAILS FOR A NEW OSS ACCOUNT
// NOTE: THIS SCRIPT ONLY WORKS IS EMAIL VERIFICATION IS TURNED OFF
// YOU CAN SCREENSHOT THE FINAL PAYMENT REFERENCE IF YOU WANT
// BY SETTING THE takeScreenShot VARIABLE TO TRUE (FALSE BY DEFAULT)
// ******************************************************************
public class OSSRegistrationScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        OSSRegistrationScript seleniumScript = new OSSRegistrationScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        boolean takeScreenShot = false; // If you want a screenshot of the completed reg change this to true.
        String GGIDValue = "40 26 71 06 36 99"; // Replace with the GGId of the account you're using
        String VRNValue = "904528110"; // Use the same VRN used in previous script
        String bpId = "100381247";  // bpID for the account created linked to vrn

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, takeScreenShot, GGIDValue, VRNValue, bpId);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }

    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, boolean takeScreenShot, String govGatewayID, String VRNValue, String BPid)
            throws IOException, InterruptedException {
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
        // Init the utils to improve duplication and inputs
        SuiteUtils utils = new SuiteUtils();
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayStartPointURL = dotenv.get("GOV_GATEWAY_START_POINT_URL");
        String outlookEmail = dotenv.get("OUTLOOK_EMAIL");  //Outlook email account needed
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");
        String ibanCode = dotenv.get("IBAN_CODE");


        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        ChromeDriverInit chromeDriverInit = new ChromeDriverInit(); // Initialise chrome driver component
        WebDriver driver = chromeDriverInit.driverInit();


        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="accounts/oss_created_accounts.txt";
        File file = new File(filepath);
        //class to write to the file loaded
        FileWriter fileWriter = new FileWriter(file, true);
        //class used to edit the file
        BufferedWriter buffedWriter = new BufferedWriter(fileWriter);


        //***************************************************************
        //                      OSS REGISTRATION
        //***************************************************************
        // Open start point URL but log in this time.
        driver.get(govGatewayStartPointURL);

        // Is your business already registered for the One-Stop Shop Union scheme in an EU country?
        // Click NO
        driver.findElement(By.id("value-no")).click();
        // Click continue
        driver.findElement(By.id("continue")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Will your business sell goods from Northern Ireland to consumers in the EU?
        // Click YES
        driver.findElement(By.id("value")).click();
        // Click continue
        driver.findElement(By.id("continue")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Is your principal place of business in Northern Ireland?
        // Click YES
        driver.findElement(By.id("value")).click();
        // Click continue
        driver.findElement(By.id("continue")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Click continue
        driver.findElement(By.id("continue")).click();
        if (demo) { Thread.sleep(waitTime); }

        //***************************************************************
        //                      SIGN IN
        //***************************************************************
        SignIn signIn = new SignIn(); // Initialise the sign in component
        signIn.signInAutomationSteps(driver, govGatewayID, govGatewayPassword, authenticationCode);

        // Do you want to register this business for the One-Stop Shop Union scheme?
        // Click yes
        driver.findElement(By.id("value_0")).click();
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Does your business trade using a name that is different from Show and Tell WI218 in the UK?
        // Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Have you already made eligible sales?
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Date of your first eligible sale
        // This returns a string of the format "For example, 1 3 2024"
        String exampleDateText = driver.findElement(By.xpath("/html/body/div[2]/main/div/div/form/div[1]/fieldset/div[1]")).getText();
        //Parse the string to get three values 1, 3, 2024 as an array [1, 3, 2024]:[day, month, year]
        String[] dates = exampleDateText.split(",",2)[1].trim().split(" ", 3);
        // Input the three example values
        driver.findElement(By.id("value.day")).sendKeys(dates[0]);
        driver.findElement(By.id("value.month")).sendKeys(dates[1]);
        driver.findElement(By.id("value.year")).sendKeys(dates[2]);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Has your business ever registered for a One-Stop Shop scheme in an EU country?
        // Click no
        driver.findElement(By.id("value-no")).click();
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Click continue
        driver.findElement(By.id("continue")).click();

        // Is your business registered for VAT or other taxes in EU countries?
        // Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Do other businesses sell goods on your website or app?
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Do you sell your goods online?
        // Click yes
        driver.findElement(By.id("value")).click();
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Enter website used to sell goods
        utils.preventInputDuplicationWithContinue("value", "www.testsite.com", "continue", driver);

        // Add another website address?
        // Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        //Business contact details
        // Enter contact name
        utils.preventInputDuplication("fullName", "Release72", driver);
        if (demo) { Thread.sleep(waitTime); }
        // Enter telephone number
        utils.preventInputDuplication("telephoneNumber", "01111111111", driver);
        if (demo) { Thread.sleep(waitTime); }
        // Enter Email address
        utils.preventInputDuplication("emailAddress", outlookEmail, driver);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        //Enter your bank or building society account details
        // Name on the account
        utils.preventInputDuplication("accountName", "MASP Testteam", driver);
        if (demo) { Thread.sleep(waitTime); }
        // Fill in the IBAN
        utils.preventInputDuplication("iban", ibanCode, driver);
        if (demo) { Thread.sleep(waitTime); }
        //Click continue
        driver.findElement(By.xpath("/html/body/div/main/div/div/form/div[4]/button")).click();

        // Click register
        driver.findElement(By.id("continue")).click();


        //***************************************************************
        //                     SAVE ACCOUNT DETAILS
        //***************************************************************
        // Create a formatted timestamp
        LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdAt = dateTimeNow.format(dateTimeFormat);
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + createdAt + '\t' + VRNValue + '\t' + BPid +"\t" + "false" + '\t'+'\t' +"false" +'\t'+'\t'+'\t' + "none";
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
            Thread.sleep(10000);
            // Scroll down to view more information on the screen
            // if the payment reference doesn't scroll into view you can change the (x,y) values of the scrollBy function
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500)");
            // Take screenshot
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            File screenshotFile = screenshotDriver.getScreenshotAs(OutputType.FILE);
            // Save screenshot
            try {
                FileHandler.copy(screenshotFile, new File("evidence/screenshots/OSS/Registrations/OSSReg_"+VRNValue+".png"));
                result += "Screenshot Saved to: " + "evidence/screenshots/OSS/Registrations/OSSReg_"+VRNValue+".png"+'\n';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Include demoSelected and gatewayIDValue in the result
        result += "OSS Account created with Gov GatewayID: " + govGatewayID + "\n";
        result += "Details saved to: " + filepath + "\n";


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

