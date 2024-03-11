package IOSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.time.Duration;

// ************************************************************
// THIS SCRIPT WILL LOG INTO THE SIT ENVIRONMENT
// cLICK THE VIEW IOSS ACCOUNT DASHBOARD AND IF THAT FAILS
// DIRECTLY OPEN THE IOSS ACCOUNT DASHBOARD IN ANOTHER TAB
// ************************************************************
public class IOSSMakeReturnScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSMakeReturnScript seleniumScript = new IOSSMakeReturnScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "49 96 79 65 89 59"; // Replace with your value
        String VRNValue = "990011242"; // Use the same VRN used in previous script rejoin IM9000002633
        String IOSSId = "IM9000002917"; // USe the IOSS ID to save the information linked to IOSS account
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue, VRNValue, IOSSId);
        System.out.println(result);
    }


    public String executeSeleniumScript(boolean demo, String govGatewayID ,String VRNValue, String IOSSId) throws IOException, InterruptedException {
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
        String govGatewayBTAStartPoint = dotenv.get("RETURNS_URL"); // Start point to LOG INTO BTA
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used to create and log in
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app
        String iossAccountLink = dotenv.get("IOSS_ACCOUNT_LINK");   //Direct link to IOSS account view

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

        //***************************************************************
        //******************* AUTOMATION START POINT ********************
        //***************************************************************
        //                      OPEN GOV GATEWAY
        //***************************************************************
        // Open start point URL but log in this time.
        driver.get(govGatewayBTAStartPoint);
        //String govGatewayBTAWindowHandle = driver.getWindowHandle();

        //clear cookie banner if demo so screen can be seen clearer
        if (demo){
            // Accept cookies
            driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/button[1]")).click();
            //Clear Banner
            driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/button")).click();
            Thread.sleep(waitTime);
        }

        //***************************************************************
        //                      SIGN IN
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
        //                      VIEW IOSS ACCOUNT
        //***************************************************************
        //USE HYPERLINK
        driver.findElement(By.id("ioss-view-account")).click();

        //USE DIRECT URL IN NEW TAB SO STILL SIGNED IN
        //((JavascriptExecutor)driver).executeScript("window.open()");
        //ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        //driver.switchTo().window(tabs.get(1)); //switches to new tab
        //driver.get(iossAccountLink);
        //String govGatewayIOSSAccountWindowHandle = driver.getWindowHandle();

        //Click return link

        //Do you want to start your return for January 2024?
        //YES

        //Did you make eligible sales into the EU or Northern Ireland in January 2024?
        //Yes

        //Which country did you sell to?

        //Which VAT rates did you sell goods at?
        //Pick top one

        //What were your sales at x% rate excluding VAT?
        // Send 100

        //How much VAT did you charge on sales of £100 at x% VAT rate
        //Pick top one

        //Add another VAT rate - NO and confirm

        //Add sales to another country?
        //NO

        //Check answers and submit

        // Save data and the return ref


        // Return the input and results string
        String result = "Demo Selected: " + demo + "\n";
        result += "IOSS MAKE RETURN SCRIPT RAN" + "\n";
        result += "GOV GATEWAY ID: " + govGatewayID + " VRN: " + VRNValue + " IOSS ID:"+ IOSSId+"\n";
        return result;

    }
}
