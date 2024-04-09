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
// THIS SCRIPT WILL MAKE A PAYMENT USING CREDIT CARD DETAILS WITHOUT LOGGING IN
// THE PAYMENT CAN BE FULL OR PARTIAL YOU MUST KNOW THE OUTSTANDING AMOUNT AHEAD OF TIME
// TO COMPLETE A PAYMENT IT MUST BE PROCESSED CORRECTLY
// THIS REQUIRES PAYMENT SERVICE AND THEN ETMP TO PROCESS
// UNTIL THE PAYMENT IN PROCESSED THE PAYMENT WILL BE OUTSTANDING IN YOUR ACCOUNT
// ************************************************************
public class IOSSLoggedOutPaymentScript {

    public static void main(String[] args) throws IOException, InterruptedException {
        IOSSLoggedOutPaymentScript seleniumScript = new IOSSLoggedOutPaymentScript();
        //***************************************************************
        //                 VARIABLES TO RUN SCRIPT MANUALLY
        //***************************************************************
        boolean demoSelected = false; // Replace with your value
        String IOSSNumber = "IM9000004151"; // This script requires you to have your IOSS reference number not GGid
        String periodMonth = "1"; // Which month you want to pay the outstanding payment
        String periodYear = "2024"; // Which year you want to pay the outstanding payment.
        String paymentAmount = "10.00"; // Must be in the form of xx.xx to work.
        String result = seleniumScript.executeSeleniumScript(demoSelected, IOSSNumber, periodMonth, periodYear, paymentAmount);
        System.out.println(result);
    }


    public String executeSeleniumScript(
            boolean demo, String IOSSNumber, String periodMonth, String periodYear, String paymentAmount
    ) throws IOException, InterruptedException {
        //***************************************************************
        //                  DEMO VARIABLE FOR SHOWCASE
        //***************************************************************
        // demo=true to slow down the automation to waitTime in ms between steps.
        int waitTime = 1000;


        //***************************************************************
        //              VARIABLES & .env LOADED & TIMER
        //***************************************************************
        // Start Timer
        long startTime = System.currentTimeMillis();
        // Variables loaded in from .env
        Dotenv dotenv = Dotenv.load(); //Needed for .env loading
        String govGatewayStartPoint = dotenv.get("LOGGED_OUT_PAYMENT"); // Start point for logged out trader
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
        driver.get(govGatewayStartPoint);

        //***************************************************************
        //                      LOGGED OUT START POINT
        //***************************************************************
        //Do you want to sign in to your tax account?
        //Click no
        driver.findElement(By.id("log_in-2")).click();
        if (demo) { Thread.sleep(waitTime); }
        //Click continue
        driver.findElement(By.id("next")).click();

        //Do you want to make Import One-Stop Shop VAT payments on distance sales to the EU?
        //Click yes
        driver.findElement(By.id("vat_ioss_check_option")).click();
        if (demo) { Thread.sleep(waitTime); }
        //Click continue
        driver.findElement(By.id("next")).click();

        //What is your IOSS registration number?
        //Enter IOSS Reg number
        driver.findElement(By.id("ioss")).sendKeys(IOSSNumber);
        if (demo) { Thread.sleep(waitTime); }
        //Click continue
        driver.findElement(By.id("next")).click();

        // Which VAT period do you want to pay?
        //Enter the month
        driver.findElement(By.id("month")).sendKeys(periodMonth);
        if (demo) { Thread.sleep(waitTime); }
        //Enter the year
        driver.findElement(By.id("year")).sendKeys(periodYear);
        if (demo) { Thread.sleep(waitTime); }
        //Click continue
        driver.findElement(By.id("next")).click();

        // Enter the amount to pay in pounds
        driver.findElement(By.id("amountToPay")).sendKeys(paymentAmount);
        if (demo) { Thread.sleep(waitTime); }
        //Click continue
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
            String accountDetailsCreated = "Logged Out" + '\t' + '\t'+ '\t'+ IOSSNumber + '\t' + paymentReference + '\t' + paymentAmount + '\t' + '\t' + createdAt;
            //write the string to the file
            buffedWriter.write(accountDetailsCreated);
            //start a new line so the next variable appended is on a new line
            buffedWriter.newLine();
            //close classes once file has been edited
            buffedWriter.close();
            fileWriter.close();

            // Return the input and results string
            result += "GOV GATEWAY ID: " + "Logged Out" + " PaymentRef: " + paymentReference + " Saved to: " + filepath + '\n';
        } else{
            result += "IOSS PAYMENT LOGGED OUT SCRIPT RAN AND FAILED" + "\n";
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
