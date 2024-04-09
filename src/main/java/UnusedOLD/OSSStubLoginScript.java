package UnusedOLD;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.time.Duration;

public class OSSStubLoginScript {
    //***************************************************************
    //           SCRIPT TO LOG IN TO ACCOUNT GIVEN A VRN
    //***************************************************************
    public static void main(String[] args) throws IOException, InterruptedException {
        OSSStubLoginScript seleniumScript = new OSSStubLoginScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String vrn = "888667048"; // Replace with your value
        String result = seleniumScript.executeSeleniumScript(demoSelected, vrn);
        System.out.println(result);
    }


    //***************************************************************
    //                 ALTERNATIVE LOG IN
    //***************************************************************
    public String executeSeleniumScript(boolean demo,  String vrn) throws InterruptedException {
        //***************************************************************
        //                  CHROME DRIVER INIT
        //***************************************************************
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        WebDriver driver = new ChromeDriver();
        // Maximize the window
        driver.manage().window().maximize();
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
        String stubsLoginUrl = dotenv.get("STUBS_LOGIN_URL");
        String redirectLink = dotenv.get("REDIRECT_LINK");


        //***************************************************************
        //******************* AUTOMATION START POINT ********************
        //***************************************************************
        //                      OPEN STUB LOGIN
        //***************************************************************
        // Open Start point URL
        driver.get(stubsLoginUrl);
        // Get the current window handle
        //String stubsWindowHandle = driver.getWindowHandle();

        // Complete the redirect input box
        driver.findElement(By.id("redirectionUrl")).sendKeys(redirectLink);
        if (demo) { Thread.sleep(waitTime); }

        // Set affinity group to organisation from drop down menu
        Select organisationDropDown = new Select(driver.findElement(By.id("affinityGroupSelect")));
        organisationDropDown.selectByValue("Organisation");
        if (demo) { Thread.sleep(waitTime); }

        // Enter Enrollment key 1
        //enrolment key 1 HMRC-MDT-VAT, identifier - VRN and value as vrn
        driver.findElement(By.id("enrolment[0].name")).sendKeys("HMRC-MDT-VAT");
        driver.findElement(By.id("input-0-0-name")).sendKeys("VRN");
        driver.findElement(By.id("input-0-0-value")).sendKeys(vrn);

        // Enter Enrollment key 2
        // enrolment key 2 HMRC-OSS-ORG, identifier - VRN and value as vrn
        driver.findElement(By.id("enrolment[1].name")).sendKeys("HMRC-OSS-ORG");
        driver.findElement(By.id("input-1-0-name")).sendKeys("VRN");
        driver.findElement(By.id("input-1-0-value")).sendKeys(vrn);

        // Click Submit
        driver.findElement(By.xpath("/html/body/div/main/div/div/form/input[2]")).click();

        return "Logged in to BTA for " +vrn;
    }
}
