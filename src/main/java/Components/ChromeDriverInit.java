package Components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

// ************************************************************
// THIS COMPONENT WILL RETURN THE DIVER SET UP FOR EACH TEST
// ************************************************************
public class ChromeDriverInit {
    // FUNCTION TO INITIALISE THE DRIVER AND RETURN A DRIVER SET UP
    public WebDriver driverInit(){
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        // Initialize the WebDriver (in this case, using Chrome)
        ChromeOptions options = new ChromeOptions();
        options.addArguments( "incognito");
        WebDriver driver = new ChromeDriver(options);
        // Implicit wait so selenium retry for x seconds if elements do not load instantly.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
        // Full screen window
        driver.manage().window().maximize();

        return driver;
    }
}
