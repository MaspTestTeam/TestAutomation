package BTA.Prereqs;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ********************************************************************
// Create Gov Gateway account
// Create BTA account
// Need a VRN set up to connect to a BTA account
// ********************************************************************

public class BTACreationWithGmail {

    public static void main(String[] args) throws IOException, InterruptedException {
        BTACreationWithGmail seleniumScript = new BTACreationWithGmail();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String VRNValue = "907600821"; // Replace with your value
        String result = seleniumScript.executeSeleniumScript(demoSelected, VRNValue);
        System.out.println(result);
    }

    public String executeSeleniumScript(boolean demo, String VRN) throws IOException, InterruptedException {
        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        // Initialize the WebDriver (in this case, using Chrome);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("incognito");
        WebDriver driver = new ChromeDriver(options);
        // Maximize the window
        //driver.manage().window().maximize();
        // Implicit wait so selenium retry for 8 seconds if elements do not load instantly.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));


        //***************************************************************
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        int waitTime = 1000;

        //***************************************************************
        //                  VARIABLES & .env LOADED
        //***************************************************************
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading

        // Variables loaded in from .env
        String govGatewayStartPointURL = dotenv.get("GOV_GATEWAY_START_POINT_URL");
        String btaCreationUrl = dotenv.get("BTA_CREATION_URL");
        String btaUserDetailsUrl = dotenv.get("BTA_USER_DETAILS_URL");
        String gmailInboxURL = dotenv.get("GMAIL_INBOX_URL");
        String signUpEmail = dotenv.get("SIGN_UP_EMAIL");
        String emailPassword = dotenv.get("GMAIL_PASSWORD");
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");
        String authenticatorName = dotenv.get("AUTHENTICATOR_NAME");
        String outlookEmail = dotenv.get("OUTLOOK_EMAIL");
        String outlookPassword = dotenv.get("OUTLOOK_PASSWORD");

        // Variables scraped from sites/email
        String emailVerificationCode = null;
        String govGatewayID = null;
        String userId = null;
        String groupId = null;


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
 //--
        // Click to create new gateway account
        driver.findElement(By.id("no-account")).click();
        if (demo) { Thread.sleep(waitTime); }
        Thread.sleep(2000);
        // Enter email address
        driver.findElement(By.id("emailAddress")).sendKeys(outlookEmail);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        WebElement element =driver.findElement(By.xpath("//*[@id=\"continue\"]"));
        if (element.isDisplayed() && element.isEnabled()) {
            element.click();
        }
        //driver.findElement(By.xpath("//*[@id=\"continue\"]")).click();
        if (demo) { Thread.sleep(waitTime); }


        //***************************************************************
        //            OPEN EMAIL AND GET VERIFICATION CODE
        //***************************************************************
        /*
        // Open new window for gmail
        driver.switchTo().newWindow(WindowType.WINDOW);
        driver.get(gmailInboxURL);
        // Get the current window handle
        String gmailInboxWindowHandle = driver.getWindowHandle();

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
        */
        //emailVerificationCode = driver.findElement(By.xpath("//*/td/table/tbody/tr/td/p/b")).getText();
        //Delete the email
        //driver.findElement(By.xpath("//*[@id=\":4\"]/div[2]/div[1]/div/div[2]/div[3]/div")).click();



        //***************************************************************
        //              OPEN EMAIL GET VERIFICATION CODE OUTLOOK
        //***************************************************************
        driver.switchTo().newWindow(WindowType.WINDOW);
        String outlookWindowHandle = driver.getWindowHandle();
        //OUTLOOK LOG IN
        driver.get("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=19&ct=1704283323&rver=7.0.6738.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fcobrandid%3dab0455a0-8d03-46b9-b18b-df2f57b9e44c%26nlp%3d1%26deeplink%3dowa%252f%26RpsCsrfState%3d869e1e54-d4c5-e519-f08a-0d1bb9063d44&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=ab0455a0-8d03-46b9-b18b-df2f57b9e44c");
        driver.findElement(By.xpath("//*[@id=\"i0116\"]")).sendKeys(outlookEmail);
        driver.findElement(By.id("idSIButton9")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"i0118\"]")).sendKeys(outlookPassword);
        driver.findElement(By.id("idSIButton9")).click();
        Thread.sleep(500);
        driver.findElement(By.id("idBtn_Back")).click();

        //Wait for email to arrive
        Thread.sleep(10000);
        //Open email
        driver.findElement(By.xpath(
                "/html/body/div[1]/div/div[2]/div/div[2]/div[2]/div[1]/div/div/div[2]/div/div/div[1]/div[2]/div/div/div/div/div/div/div/div[2]/div/div/div/div/div[2]/div[2]/div[2]"
        )).click();
        Thread.sleep(1500);
        String verificationCode = driver.findElement(By.xpath(
                "/html/body/div[1]/div/div[2]/div/div[2]/div[2]/div[1]/div/div/div[2]/div/div/div[3]/div/div/div/div/div/div/div[3]/div[1]/div/div/div/div/div[3]/div/div/div/div/table[4]/tbody/tr/td/p[3]/b"
        )).getText();
        //Delete the verification email once the code has been ADDED
        driver.findElement(By.xpath(
                "//*[@id=\"innerRibbonContainer\"]/div[1]/div/div/div/div[2]/div/div/span/button[1]"
        )).click();
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
        //                  COMPLETE GOV GATEWAY ACCOUNT
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
        //                        BTA SET UP
        //***************************************************************
        // Open new link for user details on govGateway Api
        driver.get(btaUserDetailsUrl);

        // Save userId as variable
        userId = driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/table/tbody[1]/tr/td[2]/code")).getText();

        //Save groupId as variable
        groupId = driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/table/tbody[9]/tr/td[2]/code")).getText();

        // Create String URL needed for ESP
        // Format enrolment-store/groups/{GROUP ID VARIABLE}/enrolments/HMRC-MTD-VAT~VRN~{VRN VARIABLE}
        String enrolmentStringUrl = "enrolment-store/groups/"+groupId+"/enrolments/HMRC-MTD-VAT~VRN~"+VRN;

        // Create JSON object  as string needed for ESP
        String enrolmentJson = "{\"userId\" : \"" + userId + "\", \"type\" : \"principal\", \"action\" : \"enrolAndActivate\"}";
        System.out.println("JSON FOR ENROLMENT: "+enrolmentJson);

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

        //Click continue
        driver.findElement(By.xpath("/html/body/div/main/div/div/div/div/form/div[2]/button")).click();
        Thread.sleep(5000);


        //***************************************************************
        //                     SAVE ACCOUNT DETAILS
        //***************************************************************
        // Create a formatted timestamp
        LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdAt = dateTimeNow.format(dateTimeFormat);
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + VRN + '\t' + userId + '\t' + groupId + '\t' + createdAt;
        //write the string to the file
        buffedWriter.write(accountDetailsCreated);
        //start a new line so the next variable appended is on a new line
        buffedWriter.newLine();
        //close classes once file has been edited
        buffedWriter.close();
        fileWriter.close();


        //***************************************************************
        //                DELETE GOV GATEWAY ID EMAIL
        //***************************************************************
        /*
        //switch back to gmail window
        driver.switchTo().window(gmailInboxWindowHandle);

        // Find the top unread email and click
        Thread.sleep(2000);
        WebElement unreadGovGatewayIdEmail = driver.findElement(By.xpath(
                "/html/body/div[7]/div[3]/div/div[2]/div[2]/div/div/div/div/div[2]/div/div[1]/div/div/div[5]/div[1]/div/table/tbody/tr/td[6]"
        ));
        unreadGovGatewayIdEmail.click();

        //Delete the email
        driver.findElement(By.xpath("//*[@id=\":4\"]/div[2]/div[1]/div/div[2]/div[3]/div")).click();

        */



        // Include demoSelected and gatewayIDValue in the result
        String result = "Demo Selected: " + demo + "\n";
        result += "VRN: " + VRN + "\n";
        result += "BTA Account created with Gov GatewayID: " + govGatewayID + "\n";
        result += "Account details saved to .../resources/bta_created_accounts.txt";


        // Return the input and results string
        return result;
    }
}


