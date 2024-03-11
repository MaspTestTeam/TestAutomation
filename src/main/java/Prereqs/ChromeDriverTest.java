package Prereqs;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


// ******************************************************************
// SCRIPT THAT WILL OPEN A TEST SITE AND GET THE WEB PAGE TITLE.
// THIS SCRIPT IS USED TO TEST CHROME DRIVER OPENS AND THE WEBSITE
// TEST AREA CAN BE REACHED CORRECTLY
// ENSURE ZSCALER IS OFF AND AWS OPEN VPN IS CONNECTED.
// EXPECTED BEHAVIOR - OPEN CHROME, GET INFO FROM PAGE, CLOSE CHROME
// ******************************************************************
public class ChromeDriverTest {

    public static void main(String[] args) {
        ChromeDriverTest seleniumScript = new ChromeDriverTest();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "87 15 52 59 16 48"; // Replace with your value
        String VRNValue = "999111031"; // Use the same VRN used in previous script
        String bpID = "100377803"; // Use this value to track the Business partner id for the BTA
        String result = seleniumScript.executeSeleniumScript(demoSelected, VRNValue);
        System.out.println(result);
    }

    public String executeSeleniumScript(boolean demo, String VRNValue) {
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


        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        ChromeOptions options = new ChromeOptions();
        options.addArguments( "incognito");
        WebDriver driver = new ChromeDriver(options);

        try {
            //***************************************************************
            //******************* AUTOMATION START POINT ********************
            //***************************************************************
            //                      OPEN GOV GATEWAY
            //***************************************************************
            // Open start point URL but log in this time.
            driver.get(govGatewayBTAStartPoint);
            String govGatewayBTAWindowHandle = driver.getWindowHandle();

            //clear cookie banner if demo so screen can be seen clearer
            if (demo){
                // Accept cookies
                driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/button[1]")).click();
                //Clear Banner
                driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/button")).click();
                Thread.sleep(waitTime);
            }

            // Return the input and results string
            return "TEST";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while scraping the website.";
        }
    }
}
