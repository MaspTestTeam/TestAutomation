package OSS;

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
// SCRIPT WILL REGISTER TO OSS SERVICE WITH A FIXED ESTABLISHMENT AND SAVE DETAILS OF REGISTRATION
// THIS SCRIPT WILL NEED ADDITIONAL INFO FOR FIXED ESTABLISHMENTS
// YOU NEED GOV GATEWAY ID, VRN AND BP FOR AN ACCOUNT TO BEGIN CREATION
// THE VARIABLES NEEDED ARE A GG ACCOUNT WITH BTA ACCOUNT (use BTACreationWithOutlook to make BTA)
// VRN AND BTA MUST BE VALID TO ALLOW OSS CREATION
// YO CALL THE SCRIPT USE THE  MAIN FUNCTION AND PUT IN THE VARIABLES NEEDED
// DEMO BEING TRUE WILL SLOW THE AUTOMATION DOWN TO SEE WHAT'S HAPPENING.
// THIS WILL OUTPUT A FILE THAT PRINTS OUT DETAILS FOR A NEW OSS ACCOUNT
// NOTE: THIS SCRIPT ONLY WORKS IS EMAIL VERIFICATION IS TURNED OFF
// YOU CAN SCREENSHOT THE FINAL PAYMENT REFERENCE IF YOU WANT
// BY SETTING THE takeScreenShot VARIABLE TO TRUE (FALSE BY DEFAULT)
// ******************************************************************
public class OSSRegistrationWithFeScript {
    public static void main(String[] args) throws IOException, InterruptedException {
        OSSRegistrationWithFeScript seleniumScript = new OSSRegistrationWithFeScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        boolean takeScreenShot = false; // If you want a screenshot of the completed payment change this to true.
        String GGIDValue = "97 35 24 81 61 39"; // Replace with the GGId of the account you're using
        String VRNValue = "888649736"; // Use the same VRN used in previous script
        String bpId = "100381963";  // bpID for the account created linked to vrn
        String FeCountry = "Austria"; // The country your using as fixed establishment
        String FeVATNumber = "ATU12345678"; // The VAT number used to register in the FE country

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, takeScreenShot, GGIDValue, VRNValue, bpId, FeCountry, FeVATNumber);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }


    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, boolean takeScreenShot, String govGatewayID, String VRNValue, String BPid, String FeCountry, String FeVATNumber
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
        String govGatewayStartPointURL = dotenv.get("GOV_GATEWAY_START_POINT_URL");
        String outlookEmail = dotenv.get("OUTLOOK_EMAIL");  //Outlook email account needed
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");
        String ibanCode = dotenv.get("IBAN_CODE");


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
        String filepath ="accounts/oss_created_accounts.txt";
        File file = new File(filepath);
        //class to write to the file loaded
        FileWriter fileWriter = new FileWriter(file, true);
        //class used to edit the file
        BufferedWriter buffedWriter = new BufferedWriter(fileWriter);


        //***************************************************************
        //                      OSS REGISTRATION
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

        // Enter Gov Gateway ID
        Thread.sleep(3000);
        driver.findElement(By.id("user_id")).sendKeys(govGatewayID);
        // Enter password
        driver.findElement(By.id("password")).sendKeys(govGatewayPassword);
        if (demo) { Thread.sleep(waitTime); }
        // Click sign in
        driver.findElement(By.xpath("/html/body/div/main/div[1]/div/form/button")).click();

        // Enter Authentication code
        driver.findElement(By.id("oneTimePassword")).sendKeys(authenticationCode);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

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
        //Parse the string to get three values 1, 3, 2024 as an array [1, 3, 2024]
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
        // Click YES
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        //***************************************************************
        //                  FIXED ESTABLISHMENT DETAILS
        //***************************************************************
        // Enter an EU country where your business is registered for tax
        if (demo) { Thread.sleep(waitTime); }
        // Enter the name of the country into the input box
        driver.findElement(By.id("value")).sendKeys(FeCountry);
        Thread.sleep(700);
        // Click continue twice
        driver.findElement(By.id("continue")).click();
        driver.findElement(By.id("continue")).click();

        //Does your business sell goods from XXXXXX to consumers in the EU?
        // Click YES
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // How does your business sell goods in XXXXXX?
        //Click fixed establishment
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
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
        driver.findElement(By.id("value")).sendKeys("www.testsite.com");
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        // Add another website address?
        // Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("continue")).click();

        //Enter Business contact details
        Thread.sleep(2000);
        // Enter contact name
        driver.findElement(By.id("fullName")).sendKeys("MASP Testteam");
        if (demo) { Thread.sleep(waitTime); }
        // Enter telephone number
        driver.findElement(By.id("telephoneNumber")).sendKeys("01111111111");
        if (demo) { Thread.sleep(waitTime); }
        // Enter Email address
        //driver.findElement(By.id("emailAddress")).sendKeys(hmrcEMAIL); // Can only be HMRC account
        driver.findElement(By.id("emailAddress")).sendKeys(outlookEmail);
        if (demo) { Thread.sleep(waitTime); }

        // Click continue
        driver.findElement(By.id("continue")).click();
        Thread.sleep(2000); //Wait for code forwarding

        // Name on the account
        Thread.sleep(2000);
        driver.findElement(By.id("accountName")).sendKeys("MASP Testteam");

        // Fill in the IBAN
        driver.findElement(By.id("iban")).sendKeys(ibanCode);

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
        String accountDetailsCreated = govGatewayID + '\t' + createdAt + '\t' + VRNValue + '\t' + BPid + "\t" + "false" + '\t'+'\t' + FeVATNumber + '\t'+'\t' + "none";
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
                FileHandler.copy(screenshotFile, new File("evidence/screenshots/OSS/Registrations/OSSReg_WithFE_"+VRNValue+".png"));
                result += "Screenshot Saved to: " + "evidence/screenshots/OSS/Registrations/OSSReg_WithFE_"+VRNValue+".png"+'\n';
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

