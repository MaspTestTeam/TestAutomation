package IOSS;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

// ************************************************************
// note: THIS SCRIPT WILL ALWAYS DO THE MOST OUTSTANDING PAYMENT ON THE ACCOUNT
// THIS SCRIPT WILL LOG THE TRADER IN AND MAKE A PAYMENT USING CREDIT CARD DETAILS
// THE PAYMENT CAN BE FULL OR PARTIAL BY CHANGING THE PERCENTAGE PAYMENT VARIABLE
// TO COMPLETE A PAYMENT IT MUST BE PROCESSED CORRECTLY
// THIS REQUIRES PAYMENT SERVICE AND THEN ETMP TO PROCESS
// UNTIL THE PAYMENT IN PROCESSED THE PAYMENT WILL BE OUTSTANDING IN YOUR ACCOUNT
// ************************************************************
public class IOSSLoggedInPaymentScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSLoggedInPaymentScript seleniumScript = new IOSSLoggedInPaymentScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String GGIDValue = "29 29 16 24 99 89"; // Replace with your value
        int percentPayment = 40; // Integer only, no decimals. 100 -> 100% = full payment
        String result = seleniumScript.executeSeleniumScript(demoSelected, GGIDValue, percentPayment);
        System.out.println(result);
    }


    public String executeSeleniumScript(boolean demo, String govGatewayID, int percentPayment) throws IOException, InterruptedException {
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
        // Initialise Decimal formatting for rounding payments later
        DecimalFormat df = new DecimalFormat("0.00");
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayBTAStartPoint = dotenv.get("RETURNS_URL"); // Start point to LOG INTO BTA
        String govGatewayPassword = dotenv.get("GOV_GATEWAY_PASSWORD"); //GG account password used to create and log in
        String authenticationCode = dotenv.get("AUTHENTICATOR_CODE");   //Code used for authentication app
        String outlookEmailUrl = dotenv.get("OUTLOOK_EMAIL");    //Url for Outlook email to send confirmation to
        String creditCardNumber = dotenv.get("CREDIT_CARD_NUMBER");    //Card details used to make the payment


        //***************************************************************
        //                  FILE READER AND WRITER INIT
        //***************************************************************
        //filepath to be edited
        String filepath ="evidence/IOSS/Payments/payment_references.txt";
        File file = new File(filepath);
        //class to write to the file loaded
        FileWriter fileWriter = new FileWriter(file, true);
        //class used to edit the file
        BufferedWriter buffedWriter = new BufferedWriter(fileWriter);


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
        //******************* AUTOMATION START POINT ********************
        //***************************************************************
        //                      OPEN GOV GATEWAY
        //***************************************************************
        // Open start point URL but log in this time.
        driver.get(govGatewayBTAStartPoint);

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
        //                  CLICK PAYMENT OUTSTANDING
        //***************************************************************
        //USE HYPERLINK
        driver.findElement(By.id("ioss-make-payment")).click();

        // Get the outstanding balance
        String[] amountDue = driver.findElement(
                By.xpath("/html/body/div[2]/main/div/div/div[1]/form/div/fieldset/div/div/div[1]/label")).getText().trim().split("£", 2);
        // Find the amount to pay based on the percentage input by the user
        Double amountToPay = (percentPayment/100d)*Double.parseDouble(amountDue[1]);
        if (demo) { Thread.sleep(waitTime); }
        //Format the string so that the payment is £xx.xx as a string to input
        String amountToPayString = df.format(amountToPay);
        // Click pay different amount
        driver.findElement(By.id("dontUsePrepopulatedAmount")).click();
        Thread.sleep(200);
        //Enter the amount to pay
        driver.findElement(By.id("amountEntered")).sendKeys(amountToPayString);
        Thread.sleep(200);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("next")).click();

        //Choose a way to pay
        //Click pay by card
        driver.findElement(By.id("way_to_pay-2")).click();
        if (demo) { Thread.sleep(waitTime); }
        //Click continue
        driver.findElement(By.id("next")).click();

        // Cared fees
        //Click continue
        driver.findElement(By.id("continue")).click();

        //What is your email address? (optional)
        driver.findElement(By.id("email")).sendKeys(outlookEmailUrl);
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("next")).click();

        // Card billing address
        //  Line 1 address
        driver.findElement(By.id("line1")).sendKeys("7");
        if (demo) { Thread.sleep(waitTime); }
        // Line 2 address
        driver.findElement(By.id("line2")).sendKeys("Test Street");
        if (demo) { Thread.sleep(waitTime); }
        // Postcode
        driver.findElement(By.id("postcode")).sendKeys("WS89LA");
        if (demo) { Thread.sleep(waitTime); }
        // Pick country from drop down menu
        Select countrySelect = new Select(driver.findElement(By.id("country")));
        countrySelect.selectByVisibleText("United Kingdom");
        if (demo) { Thread.sleep(waitTime); }
        // Click continue
        driver.findElement(By.id("next")).click();

        //Check your details
        if (demo) { Thread.sleep(waitTime); }
        // Get the IOSS number for the account while here
        String IOSSNumber = driver.findElement(By.xpath("/html/body/div[2]/main/div/div/dl/div[3]/dd")).getText().trim();
        //Get the payment reference number while here
        String paymentReference = driver.findElement(By.xpath("/html/body/div[2]/main/div/div/dl/div[5]/dd")).getText().trim();
        //Click continue
        driver.findElement(By.id("next")).click();

        // Make your payment
        //Switch into new iframe because the VISA secure is another frame inside the site.
        driver.switchTo().frame("card-details-frame");
        // Enter Name on Card
        driver.findElement(By.id("cardholderName")).sendKeys("Test Name");
        if (demo) { Thread.sleep(waitTime); }
        // Card number, this must be entered one char at a time due to validation checks on VISA's end
        for (int i = 0; i< Objects.requireNonNull(creditCardNumber).length(); i++){
            driver.findElement(By.id("cardNumber")).sendKeys(String.valueOf(creditCardNumber.charAt(i)));
        }
        if (demo) { Thread.sleep(waitTime); }
        // Expiry Dates
        // Month
        Select monthSelect = new Select(driver.findElement(By.id("expiryMonth")));
        monthSelect.selectByVisibleText("1");
        // Year
        Select yearSelect = new Select(driver.findElement(By.id("expiryYear")));
        yearSelect.selectByVisibleText("28");
        // Card security code
        driver.findElement(By.id("csc")).sendKeys("123");
        if (demo) { Thread.sleep(waitTime); }
        // Click Pay now
        driver.findElement(By.id("btnSubmit")).click();
        // Exit the iframe to return to GG website
        driver.switchTo().defaultContent();


        //***************************************************************
        //                     SAVE RETURN DETAILS
        //***************************************************************
        // If payment completes then save details
        String result = "Demo Selected: " + demo + "\n";
        result += "IOSS PAYMENT SCRIPT RAN"+ "\n";
        // Check the payment has completed and loaded
        List<WebElement> paymentReferenceWebEle = driver.findElements(By.xpath("/html/body/div[2]/main/div/div/div[1]/div/h3"));
        if (!paymentReferenceWebEle.isEmpty()) {
            // Create a formatted timestamp
            LocalDateTime dateTimeNow = LocalDateTime.now(); // Create a date object
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String createdAt = dateTimeNow.format(dateTimeFormat);
            // Create a formatted string to save
            String accountDetailsCreated = govGatewayID + '\t' + IOSSNumber + '\t' + paymentReference + '\t' + amountToPayString + '\t' + '\t' + createdAt;
            //write the string to the file
            buffedWriter.write(accountDetailsCreated);
            //start a new line so the next variable appended is on a new line
            buffedWriter.newLine();
            //close classes once file has been edited
            buffedWriter.close();
            fileWriter.close();

            // Return the input and results string
            result += "GOV GATEWAY ID: " + govGatewayID + " PaymentRef: " + paymentReference + " Saved to: " + filepath +'\n';
        } else{
            result += "IOSS PAYMENT SCRIPT RAN AND FAILED"+ "\n";
        }

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