package OSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// ********************************************************************
// THIS SCRIPT WILL MAKE RETURN
// YOU CAN MAKE A RETURN TO TWO DIFFERENT COUNTRIES FOR TWO DIFFERENT AMOUNTS WITH THIS SCRIPT
// YOU MUST HAVE THE GOV GATEWAY ID TO MAKE THE RETURN
// THE ACCOUNT MUST HAVE OUTSTANDING RETURNS TO WORK
// THE ACCOUNT CAN HAVE ONE OR MULTIPLE OUTSTANDING RETURN
// THE SCRIPT WILL AUTOMATICALLY COMPLETE THE EARLIEST OUTSTANDING RETURN
// THE RETURN REFERENCE WILL BE SAVED  TO A FILE IN THE EVIDENCE FOLDERS
// ********************************************************************
public class OSSMakeReturnTwoCountriesScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        OSSMakeReturnTwoCountriesScript seleniumScript = new OSSMakeReturnTwoCountriesScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "75 46 24 97 58 71"; // Replace with your value
        String firstCountryTradedWith = "Portugal";   // Country you are declaring trading with first (make sure the first letter is capitalised)
        String firstAmountTraded = "1000.00";   // Goods traded in pounds(£), remember the pence in the number (.00)
        String secondCountryTradedWith = "Finland";   // Country you are declaring trading with second (make sure the first letter is capitalised)
        String secondAmountTraded = "1200.00";   // Goods traded in pounds(£), remember the pence in the number (.00))
        String result = seleniumScript.executeSeleniumScript(
                demoSelected, GGIDValue, firstCountryTradedWith, firstAmountTraded, secondCountryTradedWith, secondAmountTraded
        );
        System.out.println(result);
    }

    public String executeSeleniumScript(
            boolean demo, String govGatewayID, String firstCountryTradedWith, String firstAmountTraded, String secondCountryTradedWith, String secondAmountTraded
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

        //***************************************************************
        //                      FIRST COUNTRY
        //***************************************************************
        // Did you make eligible sales from Northern Ireland to the EU during this period?
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Function that will run all the steps needed to declare a trade with a country
        declareTradeWithCountry(driver, demo, waitTime, firstCountryTradedWith, firstAmountTraded);

        //***************************************************************
        //                      SECOND COUNTRY
        //***************************************************************
        // Add sales from Northern Ireland to another EU country?
        // Click yes
        driver.findElement(By.id("value")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Function that will run all the steps needed to declare a trade with a country
        declareTradeWithCountry(driver, demo, waitTime, secondCountryTradedWith, secondAmountTraded);

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
            driver.findElement(By.id("continue")).click();

        }
        // Check your answers and click submit
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Click submit
        Thread.sleep(1200);
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
        // Find the total amount of the return to two countries
        double totalAmount = Double.parseDouble(firstAmountTraded) + Double.parseDouble(secondAmountTraded);
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + returnReference + '\t' + totalAmount + '\t' + '\t' + '\t' + createdAt;
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
            FileHandler.copy(screenshotFile, new File("evidence/returns/OSSReturn_"+GGIDNoSpaces +".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         */

        // Include demoSelected and gatewayIDValue in the result
        String result = "Demo Selected: " + demo + "\n";
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


    //This function will execute the steps to declare a trade with a given country(country) for an amount(amountTraded).
    // It needs variables from the main script like demo and waitTime as well as access to the driver to continue the script.
    private static void declareTradeWithCountry(WebDriver driver, boolean demo, int waitTime, String country, String amountTraded) throws InterruptedException {
        // Which country did you sell to from Northern Ireland?
        // Check input is empty before typing in another value - this is filled if been completed before.
        WebElement countrySoldToSecondInput = driver.findElement(By.id("value"));
        if ((countrySoldToSecondInput.getAttribute("value").isEmpty()))
        {
            driver.findElement(By.id("value")).sendKeys(country);
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

        // How much VAT did you charge on sales of £x at xx% VAT rate?
        driver.findElement(By.id("value_0")).click();
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();

        // Check your answers
        if (demo) { Thread.sleep(waitTime); }
        driver.findElement(By.id("continue")).click();
    }
}