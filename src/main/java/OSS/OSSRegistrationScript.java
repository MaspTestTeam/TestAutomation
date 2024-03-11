package OSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ******************************************************************
// SCRIPT WILL REGISTER TO OSS SERVICE AND SAVE DETAILS OF REGISTRATION
// YOU NEED GOV GATEWAY ID, VRN AND BP FOR AN ACCOUNT TO BEGIN CREATION
// THE VARIABLES NEEDED ARE A GG ACCOUNT WITH BTA ACCOUNT (use BTACreationWithOutlook to make BTA)
// VRN AND BTA MUST BE VALID TO ALLOW OSS CREATION
// YO CALL THE SCRIPT USE THE  MAIN FUNCTION AND PUT IN THE VARIABLES NEEDED
// DEMO BEING TRUE WILL SLOW THE AUTOMATION DOWN TO SEE WHAT'S HAPPENING.
// THIS WILL OUTPUT A FILE THAT PRINTS OUT DETAILS FOR A NEW OSS ACCOUNT
// ******************************************************************
public class OSSRegistrationScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        OSSRegistrationScript seleniumScript = new OSSRegistrationScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "67 84 81 03 29 97"; // Replace with your value
        String VRNValue = "888667930"; // Use the same VRN used in previous script
        String bpId = "100391116";  // bpID for the account created linked to vrn
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue, VRNValue, bpId);
        System.out.println(result);
    }

    public String executeSeleniumScript(boolean demo, String govGatewayID, String VRNvalue, String BPID) throws IOException, InterruptedException {
        //***************************************************************
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        int waitTime = 1000;

        //***************************************************************
        //                  VARIABLES & .env LOADED
        //***************************************************************
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayStartPointURL = dotenv.get("GOV_GATEWAY_START_POINT_URL");
        String gmailInboxURL = dotenv.get("GMAIL_INBOX_URL");
        String signUpEmail = dotenv.get("SIGN_UP_EMAIL");
        String emailPassword = dotenv.get("GMAIL_PASSWORD");
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");
        String ibanCode = dotenv.get("IBAN_CODE");
        //String hmrcEMAIL = dotenv.get("HMRC_EMAIL");

        // Variables scraped from sites/email
        String emailVerificationCode = null;


        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        WebDriver driver;
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        driver = new ChromeDriver();
        //window full screen
        driver.manage().window().maximize();
        //implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));


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
        String govGatewayWindowHandle = driver.getWindowHandle();

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

        // Date of your first eligible sale since 1 July 2023
        driver.findElement(By.id("value.day")).sendKeys("2");
        driver.findElement(By.id("value.month")).sendKeys("7");
        driver.findElement(By.id("value.year")).sendKeys("2023");
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
        driver.findElement(By.id("value")).sendKeys("www.test-website.com");
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
        driver.findElement(By.id("emailAddress")).sendKeys(signUpEmail);
        if (demo) { Thread.sleep(waitTime); }

        /*
        // Warning for user about following step
        // Create a JavaScriptExecutor instance
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        // Execute JavaScript code to open an alert
        jsExecutor.executeScript("alert('Next page requires manual step and will wait 45s for manual verification code sent to HMRC email input.');");
        // Wait briefly to allow the alert to appear (you can use WebDriverWait for a more robust wait strategy)
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Switch to the alert and accept it
        driver.switchTo().alert().accept();
         */

        // Click continue
        driver.findElement(By.id("continue")).click();

        Thread.sleep(4000); //Wait for code forwarding


        //***************************************************************
        //            OPEN EMAIL AND GET VERIFICATION CODE
        //***************************************************************
        // Open new window for gmail
        driver.switchTo().newWindow(WindowType.WINDOW);
        driver.get(gmailInboxURL);
        // Get the current window handle
        //String gmailInboxWindowHandle = driver.getWindowHandle();

        // Enter email
        driver.findElement(By.id("identifierId")).sendKeys(signUpEmail);
        // Click next
        driver.findElement(By.xpath("//*[@id=\"identifierNext\"]/div/button/span")).click();

        // Enter password
        driver.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input")).sendKeys(emailPassword);
        // Click next
        driver.findElement(By.xpath("//*[@id=\"passwordNext\"]/div/button/span")).click();

        // Find the top unread email and click
        Thread.sleep(8000);
        //"/html/body/div[7]/div[3]/div/div[2]/div[2]/div/div/div/div/div[2]/div/div[1]/div/div/div[5]/div[1]/div/table/tbody/tr/td[6]"
        WebElement unreadVerificationEmail = driver.findElement(By.xpath(
                "/html/body/div[7]/div[3]/div/div[2]/div[2]/div/div/div/div[2]/div/div[1]/div/div/div[5]/div[1]/div/table/tbody/tr/td[6]"
        ));
        unreadVerificationEmail.click();

        //Find Verification code and save verification code to variable
        Thread.sleep(3000);
        emailVerificationCode = driver.findElement(By.xpath("//*/td/table/tbody/tr/td/p/b")).getText();

        //Delete the email
        driver.findElement(By.xpath("//*[@id=\":4\"]/div[2]/div[1]/div/div[2]/div[3]/div")).click();


        //***************************************************************
        //                    RETURN BACK TO OSS
        //***************************************************************
        // Switch window back to Gov Gateway Verification
        driver.switchTo().window(govGatewayWindowHandle);

        //***************************************************************
        //         MANUAL STEP WAIT FOR HMRC VERIFICATION CODE
        //***************************************************************
        //WebElement emailVerification = driver.findElement(By.id("passcode"));
        //wait for a value to be present in email verification element of length 6
        /*
        wait.until(new ExpectedCondition<Boolean>() {
               @Override
               public Boolean apply(WebDriver driver) {
                   String inputValue = emailVerification.getAttribute("value");
                   int expectedLength = 6; // Replace with your desired value length
                   return inputValue.length() == expectedLength;
               }
        });
        */

        // Enter Verification code from the email
        driver.findElement(By.id("passcode")).sendKeys(emailVerificationCode);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.xpath("/html/body/div[3]/main/div/div/form[1]/div[2]/button")).click();

        // Click continue
        driver.findElement(By.id("continue")).click();

        // Name on the account
        Thread.sleep(2000);
        driver.findElement(By.id("accountName")).sendKeys("MASP Testteam");

        // Fill in the IBAN
        driver.findElement(By.id("iban")).sendKeys(ibanCode);

        //Click continue
        driver.findElement(By.xpath("/html/body/div/main/div/div/form/div[4]/button")).click();

        // Click register
        driver.findElement(By.id("continue")).click();

        //TODO: Get reference number of account created
        String refNumber = "OSS143542352";


        //***************************************************************
        //                     SAVE ACCOUNT DETAILS TODO: Save better details like ioss
        //***************************************************************
        // Create a formatted timestamp
        LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdAt = dateTimeNow.format(dateTimeFormat);
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + createdAt + '\t' + refNumber + '\t' + VRNvalue + '\t' + BPID +"\t" + "false" + '\t'+'\t' + "none";
        //write the string to the file
        buffedWriter.write(accountDetailsCreated);
        //start a new line so the next variable appended is on a new line
        buffedWriter.newLine();
        //close classes once file has been edited
        buffedWriter.close();
        fileWriter.close();

        // Include demoSelected and gatewayIDValue in the result
        String result = "Demo Selected: " + demo + "\n";
        result += "OSS Account created with Gov GatewayID: " + govGatewayID + "\n";
        result += "Details saved to: " + filepath + "\n";

        // Return the input and results string
        return result;
    }
}

