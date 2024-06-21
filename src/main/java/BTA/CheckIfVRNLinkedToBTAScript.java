package BTA;

import Components.ChromeDriverInit;
import Components.SignIn;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


// ******************************************************************
// SCRIPT WILL LOG INTO THE ENROLLMENT STORE PROXY FRONT END
// AND MAKE A GET REQUEST FOR A GIVEN VRN
// THIS GET REQUEST WILL THEN RETURN INFORMATION ON IF THE VRN
// IS LINKED TO A BTA ACCOUNT ALREADY. RETURNING 204 MEANS THAT THE
// VRN IS NOT LINKED AND CAN BE USED GOING FORWARD.
// NOTE: ANY GG ACCOUNT ALREADY MADE IS NEEDED TO LOG IN TO GET TO
// THE URL NEEDED TO MAKE THE REQUEST.
// ******************************************************************
public class CheckIfVRNLinkedToBTAScript {
    //***************************************************************
    //           SCRIPT TO LOG IN TO ACCOUNT GIVEN A VRN
    //***************************************************************
    public static void main(String[] args) throws IOException, InterruptedException {
        CheckIfVRNLinkedToBTAScript seleniumScript = new CheckIfVRNLinkedToBTAScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String VRN = "991122111"; // Replace with your VRN

        //The filepath for unused VRNs
        String filePath = "accounts/unused_VRNS.txt";
        List<String> vrns = ReadAndParseVRNS.readAndParseVrn(filePath);

        // Print out all the VRNS now in the List
        for (String vrn : vrns){
            System.out.println(vrn);
        }

        //TODO: Run script for each VRN

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, VRN);
        //Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }


    //***************************************************************
    //                 CHECK IF ACCOUNT IS LINKED
    //***************************************************************
    public String executeSeleniumScript(boolean demo,  String vrn) throws InterruptedException {
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
        if (demo) { Thread.sleep(waitTime); }

        // Click Endpoint to ESP
        driver.findElement(By.id("endpoint")).click();
        if (demo) { Thread.sleep(waitTime); }

        //Click Gateway compatible to true
        driver.findElement(By.id("gatewayCompatible")).click();
        if (demo) { Thread.sleep(waitTime); }

        //Enter URL
        String urlConstructed="/enrolment-store/enrolments/HMRC-MTD-VAT~VRN~"+vrn+"/groups";
        driver.findElement(By.id("url")).sendKeys(urlConstructed);
        if (demo) { Thread.sleep(waitTime); }

        //Set Input Content Type to text/plain
        //Change http to POST
        Select dropDownMenu = new Select(driver.findElement(By.id("inputContentType")));
        dropDownMenu.selectByValue("text/plain");
        if (demo) { Thread.sleep(waitTime); }

        //Click submit button
        driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/form/div[2]/button")).click();

        // Check the results
        String results = driver.findElement(By.xpath("//*[@id=\"main-content\"]/div/div/div/div/p")).getText();
        if (Objects.equals(results, "204 - NoContent")){
            //TODO: Write it to the end of unused_VRNS that hold valid VRNS once checked.
            return "\n RESULTS\n The "+vrn+" has no BTA linked to it.";
        } else{
            //TODO: Continue the loop and dont write it to file.
            return "\n RESULTS\n The "+vrn+" has a BTA linked to it and the VRN is invalid.";
        }
    }
}
