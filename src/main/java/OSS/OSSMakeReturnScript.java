package OSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OSSMakeReturnScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        OSSMakeReturnScript seleniumScript = new OSSMakeReturnScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = true; // Replace with your value
        String GGIDValue = "38 60 23 92 30 01"; // Replace with your value
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue);
        System.out.println(result);
    }

    public String executeSeleniumScript(boolean demo, String govGatewayID) throws IOException, InterruptedException {
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
        String returnsURL = dotenv.get("RETURNS_URL");
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD");
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");

        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="evidence/returns/return_references.txt";
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
        // Get the current window handle
        //String returnsWindowHandle = driver.getWindowHandle();

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

        // Do you want to start your return for 1 January to 31 March 2023?
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
            System.out.println("Country value is EMPTY");
            driver.findElement(By.id("value")).sendKeys("Portugal");
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
        driver.findElement(By.id("value")).sendKeys("5000.00");
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

        // Do you want to correct a previous return?
        // This only pops up if a return has been previously made.
        // Check for inputs on this page and if there are then click no, otherwise carry on
        List<WebElement> previousReturnInputs = driver.findElements(By.id("value-no"));
        if (!previousReturnInputs.isEmpty()){
            System.out.println("Do you want to correct a previous return? Page Loaded");
            driver.findElement(By.id("value-no")).click();
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
            // Check your answers and click submit
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        }
        else{
            // Check your answers and click submit
            System.out.println("Check your answers Page Loaded");
            if (demo) { Thread.sleep(waitTime); }
            driver.findElement(By.id("continue")).click();
        }

        // https://www.qa.tax.service.gov.uk/pay-vat-on-goods-sold-to-eu/northern-ireland-returns-payments/2023-Q1/return-submitted
        //***************************************************************
        //                     SAVE RETURN DETAILS
        //***************************************************************
        // Create a formatted timestamp
        LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String createdAt = dateTimeNow.format(dateTimeFormat);
        // Save the Return reference number
        String returnReference = driver.findElement(By.xpath("/html/body/div[2]/main/div/div/div[1]/div/div/strong")).getText();
        System.out.println("return Reference " + returnReference);
        // Create a formatted string to save
        String accountDetailsCreated = govGatewayID + '\t' + returnReference + '\t' + createdAt;
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

        // Include demoSelected and gatewayIDValue in the result
        String result = "Demo Selected: " + demo + "\n";
        result += "GGID: " + govGatewayID + "\n";
        result += "Return made.";

        // Return the input and results string
        return result;
    }
}
