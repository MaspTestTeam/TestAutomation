package OSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;


import java.io.File;
import java.io.IOException;
import java.time.Duration;


public class OSSRegWithGmail {

    public static void main(String[] args) throws InterruptedException {
        //***************************************************************
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        boolean demo = false;
        int waitTime = 1000;

        String VRN = "100390065";
        String govGatewayID = "96 37 47 63 32 15";


        //***************************************************************
        //                  VARIABLES & .env LOADED
        //***************************************************************
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayLoginUrl = dotenv.get("GOV_GATEWAY_LOGIN_URL");
        String govGatewayStartPointURL = dotenv.get("GOV_GATEWAY_START_POINT_URL");
        String btaCreationUrl = dotenv.get("BTA_CREATION_URL");
        String btaUserDetailsUrl = dotenv.get("BTA_USER_DETAILS_URL");
        String gmailInboxURL = dotenv.get("GMAIL_INBOX_URL");
        String signUpEmail = dotenv.get("SIGN_UP_EMAIL");
        String emailPassword = dotenv.get("GMAIL_PASSWORD");
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");
        String authenticatorName = dotenv.get("AUTHENTICATOR_NAME");
        String ibanCode = dotenv.get("IBAN_CODE");
        String hmrcEMAIL = dotenv.get("HMRC_EMAIL");

        // Variables scraped from sites/email
        String emailVerificationCode = null;
        String userId = null;
        String groupId = null;


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
        Actions actions = new Actions(driver); //needed for right click and scroll


        //***************************************************************
        //                      OSS REGISTRATION
        //***************************************************************
        // Re-open start point URL but log in this time.
        driver.get(govGatewayStartPointURL);
        String govGatewayWindowHandle = driver.getWindowHandle();
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
        driver.findElement(By.id("emailAddress")).sendKeys(hmrcEMAIL); // Can only be HMRC account
        if (demo) { Thread.sleep(waitTime); }
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
        WebElement unreadVerificationEmail = driver.findElement(By.xpath(
                "/html/body/div[7]/div[3]/div/div[2]/div[2]/div/div/div/div/div[2]/div/div[1]/div/div/div[5]/div[1]/div/table/tbody/tr/td[6]"
        ));
        unreadVerificationEmail.click();
        //Find Verification code and save verification code to variable
        Thread.sleep(3000);
        emailVerificationCode = driver.findElement(By.xpath("//*/td/table/tbody/tr/td/p/b")).getText();

        //Delete the email
        //driver.findElement(By.xpath("//*[@id=\":4\"]/div[2]/div[1]/div/div[2]/div[3]/div")).click();

        // Close window for gmail
        //driver.close();

        //***************************************************************
        //                    RETURN BACK TO OSS
        //***************************************************************
        // Switch window back to Gov Gateway Verification
        driver.switchTo().window(govGatewayWindowHandle);
        // Enter Verification code from the email
        driver.findElement(By.id("passcode")).sendKeys(emailVerificationCode);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.xpath("/html/body/div[3]/main/div/div/form[1]/div[2]/button")).click();

        // Click continue
        driver.findElement(By.id("continue")).click();

        // Name on the account
        driver.findElement(By.id("accountName")).sendKeys("MASP Testteam");

        // Fill in the IBAN
        driver.findElement(By.id("iban")).sendKeys(ibanCode);

        //Click continue
        driver.findElement(By.xpath("/html/body/div/main/div/div/form/div[4]/button")).click();

        // Click register
        driver.findElement(By.id("continue")).click();

        //Verify Reference number matches VRN
        String OSSRefNumber = driver.findElement(By.xpath("/html/body/div/main/div/div/div[1]/div[4]/span")).getText();
        System.out.println("OSS REF NUMBER: " + OSSRefNumber);
        System.out.println("VRN:" + VRN);

        //***************************************************************
        //                  TAKE AND SAVE SCREENSHOT
        //***************************************************************
        Thread.sleep(2000); //give time for page to load
        // Scroll page to view the OSS reference to confirm account creation
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", OSSRefNumber);
        // Take screenshot
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        File screenshotFile = screenshotDriver.getScreenshotAs(OutputType.FILE);
        // Save screenshot
        try {
            FileHandler.copy(screenshotFile, new File("evidence/OSSRegistration_"+VRN +".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}