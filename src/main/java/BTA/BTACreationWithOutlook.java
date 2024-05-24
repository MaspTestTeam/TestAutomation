package BTA;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


// ******************************************************************
// SCRIPT WILL CREATE A GOV GATEWAY ACCOUNT LINKED TO AN OUTLOOK EMAIL
// ENSURE OUTLOOK INBOX IS EMPTY BEFORE RUNNING
// SCRIPT WILL THEN MAKE THE BTA ACCOUNT LINKED TO THE GG ACCOUNT AS LONG
// AS A VALID vrn AND LINKING BP IS ADDED TO THE SCRIPT ON CREATION
// tO CALL THE SCRIPT USE THE  MAIN FUNCTION AND PUT IN THE VARIABLES NEEDED
// DEMO BEING TRUE WILL SLOW THE AUTOMATION DOWN TO SEE WHAT'S HAPPENING.
// ******************************************************************
public class BTACreationWithOutlook {
    public static void main(String[] args) throws IOException, InterruptedException {
        BTACreationWithOutlook seleniumScript = new BTACreationWithOutlook();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String VRNValue = "888650171"; // Replace with your VRN value
        String BPValue = "100381967";   //Replace with your BP value

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(VRNValue, BPValue, demoSelected);
        //Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }

    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(String VRNValue, String BPValue, boolean demo) throws IOException, InterruptedException {
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
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayStartPointURL = dotenv.get("GOV_GATEWAY_START_POINT_URL"); //Start point to create GG account
        String outlookEmail = dotenv.get("OUTLOOK_EMAIL");  //Outlook email account needed
        String outlookPassword = dotenv.get("OUTLOOK_PASSWORD");    //Outlook email password
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used to create and log in
        String authenticatorName = dotenv.get("AUTHENTICATOR_NAME");    //Name for authentication app
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app
        String btaUserDetailsUrl = dotenv.get("BTA_USER_DETAILS_URL");  //Point to get userId/ group ID for users GG account
        String outlookEmailUrl = dotenv.get("OUTLOOK_EMAIL_URL");    //Url for Outlook email auto log in
        String btaCreationUrl = dotenv.get("BTA_CREATION_URL"); //URL used to create a BTA account using the back end

        // Variables scraped from sites/email needed
        String emailVerificationCode = null;
        String govGatewayID = null;
        String userId = "6535364130532634";
        String groupId = "16CEABDA-4894-4161-BECA-E65AACCD3955";


        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("incognito");
        WebDriver driver = new ChromeDriver(options);
        // Implicit wait so selenium retry for 8 seconds if elements do not load instantly.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        // Wait time for driver that will check when a button is enabled
        WebDriverWait driverWaitTime = new WebDriverWait(driver, Duration.ofSeconds(5));

        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="accounts/bta_created_accounts.txt";
        File file = new File(filepath);
        //class to write to the file loaded
        FileWriter fileWriter = new FileWriter(file, true);
        //class used to edit the file
        BufferedWriter buffedWriter = new BufferedWriter(fileWriter);


        //***************************************************************
        //******************* AUTOMATION START POINT ********************
        //***************************************************************
        //                      OPEN GOV GATEWAY
        //***************************************************************
        // Open Start point URL
        driver.get(govGatewayStartPointURL);
        // Get the current window handle
        String govGatewayWindowHandle = driver.getWindowHandle();
        //clear cookie banner if demo so screen can be seen clearer
        if (demo){
            // Accept cookies
            driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/button[1]")).click();
            //Clear Banner
            driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/button")).click();
            Thread.sleep(waitTime);
        }

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
        // Click to create new gateway account
        driver.findElement(By.id("no-account")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Enter email address
        driver.findElement(By.id("emailAddress")).sendKeys(outlookEmail);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driverWaitTime.until(ExpectedConditions.elementToBeClickable(By.id("continue")));
        driver.findElement(By.xpath("/html/body/div/main/div[1]/div/form/button")).click();
        if (demo) { Thread.sleep(waitTime); }


        //***************************************************************
        //              OPEN EMAIL GET VERIFICATION CODE OUTLOOK
        //***************************************************************
        driver.switchTo().newWindow(WindowType.WINDOW);
        String outlookWindowHandle = driver.getWindowHandle();
        //OUTLOOK LOG IN
        driver.get(outlookEmailUrl);
        driver.findElement(By.xpath("//*[@id=\"i0116\"]")).sendKeys(outlookEmail);
        driverWaitTime.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
        driver.findElement(By.id("idSIButton9")).click();
        driver.findElement(By.xpath("//*[@id=\"i0118\"]")).sendKeys(outlookPassword);
        driverWaitTime.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
        driver.findElement(By.id("idSIButton9")).click();
        //Stay signed in Click - NO
        //Check for one of the buttons, if it isn't there do the other one.
        int elem=driver.findElements(By.xpath("/html/body/div[1]/div/div[2]/div[1]/div/div/div/div/div[2]/div[2]/div/div/form/div[3]/div[2]/div/div[1]/button")).size();
        if (elem==1){
            driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[1]/div/div/div/div/div[2]/div[2]/div/div/form/div[3]/div[2]/div/div[1]/button")).click();
        }
        else {
            driver.findElement(By.xpath("//*[@id=\"acceptButton\"]")).click();
        }

        //Wait for email to arrive
        Thread.sleep(500);
        //Open email
        driver.findElement(By.xpath(
                "/html/body/div[1]/div/div[2]/div/div[2]/div[2]/div[1]/div/div/div[2]/div/div/div[1]/div[2]/div/div/div/div/div/div/div/div[2]/div/div/div/div/div[2]/div[2]/div[2]"
        )).click();
        Thread.sleep(300);
        String verificationCode = driver.findElement(By.xpath(
                        "//*[@id=\"x_background\"]/table[4]/tbody/tr/td/p[3]/b"
                )).getText();
        //Delete the verification email once the code has been ADDED
        driver.findElement(By.xpath("//*[@id=\"innerRibbonContainer\"]/div[1]/div/div/div/div[2]/div/div/span/button[1]")).click();
        emailVerificationCode = verificationCode.strip();


        //***************************************************************
        //                  VERIFY EMAIL ON GOV GATEWAY
        //***************************************************************
        // Switch window back to Gov Gateway Verification
        driver.switchTo().window(govGatewayWindowHandle);
        // Enter Verification code from the email
        driver.findElement(By.id("code")).sendKeys(emailVerificationCode);
        if (demo) { Thread.sleep(waitTime); }
        // Click confirm
        driver.findElement(By.id("continue")).click();
        // Click continue
        driver.findElement(By.id("continue")).click();


        //***************************************************************
        //                  CREATE GOV GATEWAY PASSWORD
        //***************************************************************
        // Enter name
        driver.findElement(By.id("name")).sendKeys("MASP Testteam");
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();
        // Create password
        driver.findElement(By.id("newPassword")).sendKeys(govGatewayPassword);
        if (demo) { Thread.sleep(waitTime); }
        // Confirm password
        driver.findElement(By.id("confirmPassword")).sendKeys(govGatewayPassword);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Save Gov Gateway ID and print out
        govGatewayID = driver.findElement(By.xpath("//*[@id=\"skip-target\"]/div[1]/div/div/div")).getText();


        //***************************************************************
        //               GOV GATEWAY VERIFICATION SET UP
        //***************************************************************
        // Click continue
        driver.findElement(By.id("continue")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Choose the type of account you need - click organisation
        driver.findElement(By.id("organisation")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("submit-continue")).click();

        // Click continue
        driver.findElement(By.id("continue")).click();

        // Choose how to access the authentication codes - click authenticator
        driver.findElement(By.id("method-Totp")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Click continue
        driver.findElement(By.id("continue")).click();

        // Click continue
        driver.findElement(By.id("continue")).click();

        // Enter authenticator verification code
        driver.findElement(By.id("oneTimePassword")).sendKeys(authenticationCode);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Create Authentication app name for reference
        driver.findElement(By.id("appName")).sendKeys(authenticatorName);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Click continue
        driver.findElement(By.id("continue")).click();


        //***************************************************************
        //           OPEN EMAIL AND DELETE CONFIRMATION EMAIL
        //***************************************************************
        // Switch window back to Gov Gateway Verification
        driver.switchTo().window(outlookWindowHandle);
        //Wait for email to arrive
        Thread.sleep(1500);
        //Open email
        driver.findElement(By.xpath(
                "/html/body/div[1]/div/div[2]/div/div[2]/div[2]/div[1]/div/div/div[2]/div/div/div[1]/div[2]/div/div/div/div/div/div/div/div[2]/div/div/div/div/div[2]/div[2]/div[2]"
        )).click();
         //Delete the verification email once the code has been ADDED
        driver.findElement(By.xpath(
                "//*[@id=\"innerRibbonContainer\"]/div[1]/div/div/div/div[2]/div/div/span/button[1]"
        )).click();


        //***************************************************************
        //                        BTA SET UP
        //***************************************************************
        // Open new link for user details on govGateway Api
        driver.switchTo().window(govGatewayWindowHandle);
        driver.get(btaUserDetailsUrl);

        // This is where all the BTA details for account creation come from
        // Save userId as variable
        userId = driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/table/tbody[1]/tr/td[2]/code")).getText();

        //Save groupId as variable
        groupId = driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/table/tbody[9]/tr/td[2]/code")).getText();
        // Create String URL needed for ESP
        // Format enrolment-store/groups/{GROUP ID VARIABLE}/enrolments/HMRC-MTD-VAT~VRN~{VRN VARIABLE}
        String enrolmentStringUrl = "enrolment-store/groups/"+groupId+"/enrolments/HMRC-MTD-VAT~VRN~"+VRNValue;

        // Create JSON object  as string needed for ESP
        String enrolmentJson = "{\"userId\" : \"" + userId + "\", \"type\" : \"principal\", \"action\" : \"enrolAndActivate\"}";

        // Open new link for Enrollment Store Proxy (ESP) on Gov gateway API
        driver.get(btaCreationUrl);
        // Click ESP
        driver.findElement(By.id("endpoint")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Click True
        driver.findElement(By.id("gatewayCompatible")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Enter URL enrolmentStringUrl
        driver.findElement(By.id("url")).sendKeys(enrolmentStringUrl);
        if (demo) { Thread.sleep(waitTime); }

        // Enter JSON  enrolmentJson
        driver.findElement(By.id("body")).sendKeys(enrolmentJson);
        if (demo) { Thread.sleep(waitTime); }

        //Change http to POST
        Select dropDownMenu = new Select(driver.findElement(By.id("httpVerb")));
        dropDownMenu.selectByValue("POST");
        if (demo) { Thread.sleep(waitTime); }

        //Click continue // ************* BTA ACCOUNT CREATED HERE
        driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/form/div[2]/button")).click();

        //If object isn't created end the script
        if (!Objects.equals(driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/p")).getText(), "201 - Created")){
            // End timer and return script failure error
            long finishTime = System.currentTimeMillis();
            double timeElapsedInSeconds = (finishTime - startTime)/1000d;
            return "FAILED TO CREATE BTA DETAILS NOT SAVED. Time to Run Script: " + timeElapsedInSeconds + " seconds." +"\n";
        }


        //***************************************************************
        //                     SAVE ACCOUNT DETAILS
        //***************************************************************
        // Create a formatted timestamp
        LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdAt = dateTimeNow.format(dateTimeFormat);
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + VRNValue + '\t' + BPValue + '\t' + userId + '\t' + groupId + '\t' + createdAt + '\t' + "false";
        //write the string to the file
        buffedWriter.write(accountDetailsCreated);
        //start a new line so the next variable appended is on a new line
        buffedWriter.newLine();
        //close classes once file has been edited
        buffedWriter.close();
        fileWriter.close();

        // Format the results string
        String result = "Demo Selected: " + demo + "\n";
        result += "BTA account created and saved details to  " + filepath + "\n";
        result += "GOV GATEWAY ID: " + govGatewayID + " VRN: " + VRNValue +"\n";


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
