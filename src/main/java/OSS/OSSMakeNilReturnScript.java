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
import java.util.List;

// ********************************************************************
// THIS SCRIPT WILL MAKE A NIL RETURN
// yOU MUST HAVE THE GOV GATEWAY ID TO MAKE THE RETURN
// THE ACCOUNT MUST HAVE OUTSTANDING RETURNS TO WORK
// THE ACCOUNT CAN HAVE ONE OR MULTIPLE OUTSTANDING RETURN
// THE SCRIPT WILL AUTOMATICALLY COMPLETE THE EARLIEST OUTSTANDING RETURN
// THE RETURN REFERENCE WILL BE SAVED  TO A FILE IN THE EVIDENCE FOLDERS
// ********************************************************************
public class OSSMakeNilReturnScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        OSSMakeNilReturnScript seleniumScript = new OSSMakeNilReturnScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "37 61 28 22 51 24"; // Replace with your value
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue);
        System.out.println(result);
    }

    public String executeSeleniumScript(boolean demo, String govGatewayID) throws IOException, InterruptedException {
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
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        int waitTime = 1000;


        //***************************************************************
        //                  VARIABLES & .env LOADED
        //***************************************************************
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading

        // Variables loaded in from .env
        String returnsURL = dotenv.get("RETURNS_URL");
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");


        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="evidence/OSS/Returns/return_references.txt";
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
        driver.get(returnsURL);

        // Log in to gov gateway account
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("user_id")).sendKeys(govGatewayID);
        driver.findElement(By.id("password")).sendKeys(govGatewayPassword);
        Thread.sleep(1000);
        driver.findElement(By.id("continue")).click();
        //Authentication code
        driver.findElement(By.id("oneTimePassword")).sendKeys(authenticationCode);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // one-stop shop vat click start your return link
        driver.findElement(By.id("oss-start-return")).click();
        if (demo) { Thread.sleep(waitTime); }

        // Do you want to start your return?
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Did you make eligible sales from Northern Ireland to the EU during this period?
        // Click No
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Did you make eligible sales from an EU country to other EU countries during this period?
        // Click No
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Do you want to correct a previous return?
        // This only pops up if a return has been previously made.
        // Check for inputs on this page and if there are then click no, otherwise carry on
        List<WebElement> previousReturnInputs = driver.findElements(By.id("value-no"));
        if (!previousReturnInputs.isEmpty()){
            driver.findElement(By.id("value-no")).click();
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();

        }
        // Check your answers and click submit
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();


        //***************************************************************
        //                     SAVE RETURN DETAILS
        //***************************************************************
        // Create a formatted timestamp
        LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdAt = dateTimeNow.format(dateTimeFormat);
        // Save the Return reference number
        String returnReference = driver.findElement(By.xpath("/html/body/div[2]/main/div/div/div[1]/div/div/strong")).getText();
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + returnReference + '\t' + "nil" + '\t' + '\t' + createdAt;
        //write the string to the file
        buffedWriter.write(accountDetailsCreated);
        //start a new line so the next variable appended is on a new line
        buffedWriter.newLine();
        //close classes once file has been edited
        buffedWriter.close();
        fileWriter.close();


        /*
        //UNCOMMENT THIS BLOCK IF YOU WANT A SCREENSHOT
        //***************************************************************
        //             TAKE AND SAVE SCREENSHOT OF RETURN
        //***************************************************************
        Thread.sleep(2000); //give time for page to load
        // Scroll down to view more information on the screen
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", returnReference);
        // Take screenshot
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        File screenshotFile = screenshotDriver.getScreenshotAs(OutputType.FILE);
        // Save screenshot
        try {
            String GGIDNoSpaces = govGatewayID.replaceAll("\\s", "");
            FileHandler.copy(screenshotFile, new File("evidence/screenshots/OSS/returns/OSSReturn_"+GGIDNoSpaces+returnReference +".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */

        // Include demoSelected and gatewayIDValue in the result
        String result = "Demo Selected: " + demo + "\n";
        result += "GGID: " + govGatewayID + "\n";
        result += "Return made: "+ returnReference + " Saved to: " + filepath;

        // Return the input and results string
        return result;
    }
}
