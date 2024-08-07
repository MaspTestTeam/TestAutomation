package IOSS;

import Components.ChromeDriverInit;
import Components.IOSSReturn;
import Components.IOSSStubSignIn;
import Components.SignIn;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;

import org.openqa.selenium.io.FileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// ********************************************************************
// THIS SCRIPT WILL MAKE A RETURN TO ONE COUNTRY FROM NI
// yOU MUST HAVE THE GOV GATEWAY ID TO MAKE THE RETURN
// THE ACCOUNT MUST HAVE OUTSTANDING RETURNS TO WORK
// THE ACCOUNT CAN HAVE ONE OR MULTIPLE OUTSTANDING RETURN
// THE SCRIPT WILL AUTOMATICALLY COMPLETE THE EARLIEST OUTSTANDING RETURN
// THE RETURN REFERENCE WILL BE SAVED  TO A FILE IN THE EVIDENCE FOLDERS
// YOU CAN SCREENSHOT THE FINAL PAYMENT REFERENCE IF YOU WANT
// BY SETTING THE takeScreenShot VARIABLE TO TRUE (FALSE BY DEFAULT)
// ********************************************************************
public class IOSSMakeReturnOneCountryScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSMakeReturnOneCountryScript seleniumScript = new IOSSMakeReturnOneCountryScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        boolean takeScreenShot = false; // If you want a screenshot of the completed return change this to true.
        String GGIDValue = "70 37 17 77 61 20"; // Replace with the GGId of the account you're using
        String countryTradedWith = "Greece";   // Country you are declaring trading with (make sure the first letter is capitalised)
        String amountTraded = "160.00";   // Goods traded in pounds(£), remember the pence in the number (.00)
        // ONLY NEED TO CHANGE IF NORMAL LOG IN NOT WORKING
        boolean stubLogin = false;     // Set to true if normal login isn't working
        String vrn = "991122105";   // VRN for account needed to run Stub login - not needed if stub login isn't needed
        String iossId = "IM9000005802";  // IOSS ID for stub log in, not needed if stub login isn't needed.

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(
                demoSelected, takeScreenShot, GGIDValue, countryTradedWith, amountTraded, vrn, iossId, stubLogin
        );
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }


    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, boolean takeScreenShot, String govGatewayID, String countryTradedWith, String amountTraded, String vrn, String iossId, boolean stubLogin
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
        String govGatewayBTAStartPoint = dotenv.get("RETURNS_URL"); // Start point to LOG INTO BTA
        String govGatewayStubLoginURL = dotenv.get("STUBS_LOGIN_URL"); // Start point to log in with stubs
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used to create and log in
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app


        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="evidence/IOSS/Returns/ioss_return_references.txt";
        File file = new File(filepath);
        //class to write to the file loaded
        FileWriter fileWriter = new FileWriter(file, true);
        //class used to edit the file
        BufferedWriter buffedWriter = new BufferedWriter(fileWriter);


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
            //                      CLICK IOSS RETURN
            //***************************************************************
            // IOSS vat click start your return link
            driver.findElement(By.id("ioss-start-return")).click();
            if (demo) { Thread.sleep(waitTime); }
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

            //Click outstanding return link in dashboard to begin
            driver.findElement(By.id("start-your-return")).click();
        }

        // Do you want to start your return?
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        //***************************************************************
        //                      RETURN STARTS HERE
        //***************************************************************
        // Did you make eligible sales from Northern Ireland to the EU during this period?
        // Click Yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Function that will run all the steps needed to declare a trade with a country
        IOSSReturn iossReturn = new IOSSReturn(); // Initiate the class with functions
        iossReturn.iossDeclareTradeWithCountry(driver, demo, waitTime, countryTradedWith, amountTraded);

        //Add sales to another country?
        //Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Do you want to correct a previous return?
        // This only pops up if a return has been previously made.
        // Check for inputs on this page and if there are then click no, otherwise carry on
        List<WebElement> previousReturnInputs = driver.findElements(By.id("value-no"));
        if (!previousReturnInputs.isEmpty()){
            driver.findElement(By.id("value-no")).click();
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();

        }

        // Check your answers and click submit
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();


        //***************************************************************
        //                     SAVE RETURN DETAILS
        //***************************************************************
        // Create a formatted timestamp
        LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdAt = dateTimeNow.format(dateTimeFormat);
        // Save the Return reference number
        String returnReference = driver.findElement(By.xpath("/html/body/div[2]/main/div/div/div[1]/div/strong")).getText();
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + returnReference + '\t' + amountTraded + '\t' + '\t' + createdAt + '\t' + "false" + '\t' + "none";
        //write the string to the file
        buffedWriter.write(accountDetailsCreated);
        //start a new line so the next variable appended is on a new line
        buffedWriter.newLine();
        //close classes once file has been edited
        buffedWriter.close();
        fileWriter.close();

        //***************************************************************
        //             TAKE AND SAVE SCREENSHOT OF RETURN
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
            //Remove / From the return reference so the file can be saved correctly
            String formattedReturnReference = returnReference.replace("/","");
            try {
                FileHandler.copy(screenshotFile, new File("evidence/screenshots/IOSS/Returns/IOSSReturn_"+govGatewayID+"_"+formattedReturnReference+".png"));
                result += "Screenshot Saved to: " + "evidence/screenshots/IOSS/Returns/IOSSReturn_"+govGatewayID+"_"+formattedReturnReference+".png"+'\n';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        // Return the input and results string
        result += "GOV GATEWAY ID: " + govGatewayID + "\n";
        result += "Return made: "+ returnReference + " Saved to: " + filepath + '\n';


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
