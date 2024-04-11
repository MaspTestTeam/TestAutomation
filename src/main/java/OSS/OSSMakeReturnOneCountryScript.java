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
import java.util.Objects;

// ********************************************************************
// THIS SCRIPT WILL MAKE RETURN TO A GIVEN COUNTRY FOR THE AMOUNT DESIRED
// yOU MUST HAVE THE GOV GATEWAY ID TO MAKE THE RETURN
// THE ACCOUNT MUST HAVE OUTSTANDING RETURNS TO WORK
// THE ACCOUNT CAN HAVE ONE OR MULTIPLE OUTSTANDING RETURN
// THE SCRIPT WILL AUTOMATICALLY COMPLETE THE EARLIEST OUTSTANDING RETURN
// THE RETURN REFERENCE WILL BE SAVED  TO A FILE IN THE EVIDENCE FOLDERS
// YOU CAN SCREENSHOT THE FINAL PAYMENT REFERENCE IF YOU WANT
// BY SETTING THE takeScreenShot VARIABLE TO TRUE (FALSE BY DEFAULT)
// ********************************************************************
public class OSSMakeReturnOneCountryScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        OSSMakeReturnOneCountryScript seleniumScript = new OSSMakeReturnOneCountryScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // This will slow down the script if set to true, so you can see what is happening
        boolean takeScreenShot = false; // If you want a screenshot of the completed payment change this to true.
        String GGIDValue = "59 77 26 31 87 44"; // Replace with the GGId of the account you're using
        String countryTradedWith = "Portugal";   // Country you are declaring trading with (make sure the first letter is capitalised)
        String amountTraded = "1000.00";   // Goods traded in pounds(£), remember the pence in the number (.00)

        // Run the selenium script
        String result = seleniumScript.executeSeleniumScript(demoSelected, takeScreenShot, GGIDValue, countryTradedWith, amountTraded);
        // Print out the results/information after the selenium script has finished running
        System.out.println(result);
    }


    // The automation script that will execute the steps via selenium
    public String executeSeleniumScript(
            boolean demo, boolean takeScreenShot, String govGatewayID, String countryTradedWith, String amountTraded
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
        String returnsURL = dotenv.get("RETURNS_URL");
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");


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
        String filepath ="evidence/OSS/Returns/oss_return_references.txt";
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
        Thread.sleep(2000);
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
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Which country did you sell to from Northern Ireland?
        // Check input is empty before typing in another value - this is filled if been completed before.
        WebElement countrySoldToInput = driver.findElement(By.id("value"));
        if ((countrySoldToInput.getAttribute("value").isEmpty()))
        {
            driver.findElement(By.id("value")).sendKeys(countryTradedWith);
            if (demo) { Thread.sleep(waitTime); }
            // Double click needed
            driver.findElement(By.id("continue")).click();
            driver.findElement(By.id("continue")).click();
        }else {
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        }

        // Which VAT rates did you charge?
        // Click just top value
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // What were your sales at xx% rate excluding VAT?
        driver.findElement(By.id("value")).sendKeys(amountTraded);
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // How much VAT did you charge on sales of £5,000 at xx% VAT rate?
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Check your answers
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Add sales from Northern Ireland to another EU country?
        // Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        //Did you make eligible sales from an EU country to other EU countries during this period?
        //Click no
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
            //Click continue
            driver.findElement(By.id("continue")).click();
            // Check your answers and click submit
            if (demo) { Thread.sleep(waitTime); }
            //Click continue
            driver.findElement(By.id("continue")).click();
            // Click submit
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        } else{
            // Check your answers and click submit
            if (demo) { Thread.sleep(waitTime); }
            //Click submit
            //System.out.println("SKIPPED PREVIOUS RETURNS CLICK SUBMIT");
            driver.findElement(By.id("continue")).click();
        }

        /*
        // THIS COMMENT CAN BE UNCOMMENTED OUT TO ADD A TRADE DECLARED BETWEEN TWO COUNTRIES NOT NI AND SOMEWHERE
        // Did you make eligible sales from an EU country to other EU countries during this period?
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Which EU country did you sell goods from?
        WebElement countrySellFromInput = driver.findElement(By.id("value"));
        // Check input is empty before typing in another value - this is filled if been completed before.
        if ((countrySellFromInput.getAttribute("value").isEmpty()))
        {
            System.out.println("Country value is EMPTY");
            driver.findElement(By.id("value")).sendKeys("Finland");
            if (demo) { Thread.sleep(waitTime); }
            // Double click needed
            driver.findElement(By.id("continue")).click();
            driver.findElement(By.id("continue")).click();
        } else{
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        }

        // Where did you sell to from Finland? Pick any country except NI
        WebElement countrySoldTo2Input = driver.findElement(By.id("value"));
        // Check input is empty before typing in another value - this is filled if been completed before.
        if ((countrySoldTo2Input.getAttribute("value").isEmpty()))
        {
            System.out.println("Country value is EMPTY");
            driver.findElement(By.id("value")).sendKeys("Sweden");
            if (demo) { Thread.sleep(waitTime); }
            // Double click needed
            driver.findElement(By.id("continue")).click();
            driver.findElement(By.id("continue")).click();
        } else {
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        }

        // Which VAT rates did you charge?
        // Click just top value
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"value_0\"]"));
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // What were your sales at xx% rate excluding VAT?
        List<WebElement> taxSalesInput = driver.findElements(By.id("value"));
        // If input box found then input a value of taxable sales
        if (!taxSalesInput.isEmpty()){
            System.out.println("Input for return on second country needed");
            driver.findElement(By.id("value")).sendKeys("3000.00");
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();

            // How much VAT did you charge on sales of £3,000 at 25% VAT rate?
            driver.findElement(By.id("value_0")).click();
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();

            // Check your answers
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();

        } else{
            // ****************** CACHING INPUT HERE
            System.out.println("No input box on page for second return");
            // Check your answers
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        }

        // Add sales from Finland to another EU country?
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Add sales from another EU country?
        // Click no
        driver.findElement(By.id("value-no")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();
        */


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
        String accountDetailsCreated = govGatewayID + '\t' + returnReference + '\t' + amountTraded + '\t' + '\t' + createdAt + '\t' + "false" + '\t' + "none";
        //write the string to the file
        buffedWriter.write(accountDetailsCreated);
        //start a new line so the next variable appended is on a new line
        buffedWriter.newLine();
        //close classes once file has been edited
        buffedWriter.close();
        fileWriter.close();


        //***************************************************************
        //             TAKE AND SAVE SCREENSHOT OF RETURN
        //***************************************************************
        String result = "Demo Selected: " + demo + "\n";
        //Check if user wants screenshot before taking the screenshot of the final payment
        if (takeScreenShot){
            Thread.sleep(3000); //give time for page to load
            // Scroll down to view more information on the screen
            // if the payment reference doesn't scroll into view you can change the (x,y) values of the scrollBy function
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,400)");
            // Take screenshot
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            File screenshotFile = screenshotDriver.getScreenshotAs(OutputType.FILE);
            // Save screenshot
            //Remove / From the return reference so the file can be saved correctly
            String formattedReturnReference = returnReference.replace("/","");
            try {
                FileHandler.copy(screenshotFile, new File("evidence/screenshots/OSS/Returns/OSSReturn_"+govGatewayID+"_"+formattedReturnReference+".png"));
                result += "Screenshot Saved to: " + "evidence/screenshots/OSS/Returns/OSSReturn_"+govGatewayID+"_"+formattedReturnReference+".png"+'\n';
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Include demoSelected and gatewayIDValue in the result
        result += "GGID: " + govGatewayID + "\n";
        result += "Return made: "+ returnReference + " Saved to: " + filepath + '\n';


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
