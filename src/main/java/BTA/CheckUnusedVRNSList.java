package BTA;


import Components.ChromeDriverInit;
import Components.SignIn;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

// ******************************************************************
// SCRIPT WILL LOG INTO THE ENROLLMENT STORE PROXY FRONT END
// AND MAKE A GET REQUEST FOR A GIVEN VRN
// THIS IS REPEATED FOR EVERY VRN IN THE unused_VRNS.txt file
// IF A VRN IS VALID IT WILL PUT IT INTO valid_VRNS.txt
// THIS GET REQUEST WILL THEN RETURN INFORMATION ON IF THE VRN
// IS LINKED TO A BTA ACCOUNT ALREADY. RETURNING 204 MEANS THAT THE
// VRN IS NOT LINKED AND CAN BE USED GOING FORWARD.
// NOTE: ANY GG ACCOUNT ALREADY MADE IS NEEDED TO LOG IN TO GET TO
// THE URL NEEDED TO MAKE THE REQUEST.
// ******************************************************************
public class CheckUnusedVRNSList {
    //***************************************************************
    //           SCRIPT TO LOG IN TO ACCOUNT GIVEN A VRN
    //***************************************************************
    public static void main(String[] args) throws IOException, InterruptedException {
        CheckUnusedVRNSList seleniumScript = new CheckUnusedVRNSList();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String VRN = "991122111"; // Replace with your VRN

        //The filepath for unused VRNs
        String filePath = "accounts/unused_VRNS.txt";
        List<String> bpAndVrn = ReadAndParseVRNS.readAndParseVrn(filePath);

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, VRN, bpAndVrn);
        //Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }


    //***************************************************************
    //                 CHECK IF ACCOUNT IS LINKED
    //***************************************************************
    public String executeSeleniumScript(boolean demo,  String vrn, List<String> vrnList) throws InterruptedException, IOException {
        //***************************************************************
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        int waitTime = 1000;


        //***************************************************************
        //                  VARIABLES & .env LOADED
        //***************************************************************
        // Start Timer
        long startTime = System.currentTimeMillis();
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayBTAStartPoint = dotenv.get("BTA_CREATION_URL"); // Start point to get to ESP frontend
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used log in
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app


        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        ChromeDriverInit chromeDriverInit = new ChromeDriverInit(); // Initialise chrome driver component
        WebDriver driver = chromeDriverInit.driverInit();


        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="accounts/valid_VRNS.txt";
        File file = new File(filepath);
        //class to write to the file loaded
        FileWriter fileWriter = new FileWriter(file, true);
        //class used to edit the file
        BufferedWriter buffedWriter = new BufferedWriter(fileWriter);


        //***************************************************************
        //******************* AUTOMATION START POINT ********************
        //***************************************************************
        //                      OPEN STUB LOGIN
        //***************************************************************
        driver.get(govGatewayBTAStartPoint);

        //***************************************************************
        //                      SIGN IN
        //***************************************************************
        SignIn signIn = new SignIn(); // Initialise the sign in component
        //We can use any GG-ID
        signIn.signInAutomationSteps(driver, "54 43 66 98 39 15", govGatewayPassword, authenticationCode);

        //***************************************************************
        //                  ENROLMENT STORE PROXY
        //***************************************************************
        // Click Start now
        driver.findElement(By.xpath("/html/body/div/main/div/div/div/div[1]/form/div/button")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Click Enrollment Store Proxy API call Generator link
        driver.findElement(By.id("esp-call-generator-page-link")).click();

        // Run through the ESP submission form
        for (String eachLine: vrnList){
            // Split string into vrn and bp
            String eachBp = eachLine.split(",")[0];
            String eachVrn = eachLine.split(",")[1];
            boolean isVRNValid = checkIsVRNValid(driver, eachVrn);
            System.out.println("CHECKED FOR " + eachVrn + " VALID: " + isVRNValid);

            // Only save details of VRNS that haven't been used and can be linked
            if (isVRNValid){
                //***************************************************************
                //                     SAVE The VRN details
                //***************************************************************
                // Create a formatted timestamp
                LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String verifiedAt = dateTimeNow.format(dateTimeFormat);
                // Create a formatted string to save
                String vrnDetailsString= eachBp + "\t" + eachVrn + "\t" + verifiedAt + "\t false";
                //write the string to the file
                buffedWriter.write(vrnDetailsString);
                //start a new line so the next variable appended is on a new line
                buffedWriter.newLine();
            }

            // Return back to form
            driver.get(govGatewayBTAStartPoint);
        }

        //close the writer helpers once file has been edited and all vrns have been checked
        buffedWriter.close();
        fileWriter.close();

        // Close all windows
        driver.quit();

        //***************************************************************
        //                          END TIMER
        //***************************************************************
        long finishTime = System.currentTimeMillis();
        double timeElapsedInSeconds = (finishTime - startTime)/1000d;

        return "\nTime taken: " + timeElapsedInSeconds+ "\nunused_VRN.txt has been scanned and any valid VRNS have been put into valid_VRNS.txt";
    }

    // This function will run the ESP page submission for a different VRN and return True if the vrn is valid
    private static boolean checkIsVRNValid(WebDriver driver, String vrn){
        // Click Endpoint to ESP
        driver.findElement(By.id("endpoint")).click();

        //Click Gateway compatible to true
        driver.findElement(By.id("gatewayCompatible")).click();

        //Enter URL
        String urlConstructed="/enrolment-store/enrolments/HMRC-MTD-VAT~VRN~"+vrn+"/groups";
        driver.findElement(By.id("url")).sendKeys(urlConstructed);

        //Set Input Content Type to text/plain
        //Change http to POST
        Select dropDownMenu = new Select(driver.findElement(By.id("inputContentType")));
        dropDownMenu.selectByValue("text/plain");

        //Click submit button
        driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/form/div[2]/button")).click();

        // Check the results
        String results = driver.findElement(By.xpath("//*[@id=\"main-content\"]/div/div/div/div/p")).getText();

        return Objects.equals(results, "204 - NoContent");
    }
}
