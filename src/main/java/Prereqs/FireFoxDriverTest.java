package Prereqs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


// ******************************************************************
// SCRIPT THAT WILL OPEN A TEST SITE AND GET THE WEB PAGE TITLE.
// THIS SCRIPT IS USED TO TEST CHROME DRIVER OPENS AND THE WEBSITE
// TEST AREA CAN BE REACHED CORRECTLY
// ENSURE ZSCALER IS OFF AND AWS OPEN VPN IS CONNECTED.
// EXPECTED BEHAVIOR - OPEN FIREFOX, GET INFO FROM PAGE, CLOSE FIREFOX
// ******************************************************************
public class FireFoxDriverTest {

    public String executeSeleniumScript(boolean demoSelected, String VRNValue) {

        System.setProperty("webdriver.gecko.driver", "resources/geckodriver.exe");
        //Initialize the WebDriver (in this case, using firefox)
        WebDriver driver = new FirefoxDriver();

        //System.setProperty("webdriver.chrome.driver", "resources/chromedriver2.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        //ChromeOptions options = new ChromeOptions();
        //options.addArguments( "incognito");
        //WebDriver driver = new ChromeDriver(options);

        try {
            // Navigate to a website
            driver.get("https://www.ete.access.service.gov.uk/login/signin/creds");

            // Return the input and results string
            return "TEST";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while scraping the website.";
        } finally {
            // Quit the WebDriver
            //driver.quit();
        }
    }


    public static void main(String[] args) {
        FireFoxDriverTest seleniumScript = new FireFoxDriverTest();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = true; // Replace with your value
        String VRNValue = "12345"; // Replace with your value
        String result = seleniumScript.executeSeleniumScript(demoSelected, VRNValue);
        System.out.println(result);
    }
}
