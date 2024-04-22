package Components;

import org.openqa.selenium.WebDriver;

//This component will tear down and dispose of the running chromedriver
// This ensures that if a user wants to rename or delete the chromedriver
// executable in resources they can reliably do so.
public class ChromeDriverTearDown {
    public void tearDownDriver(WebDriver driver){
        driver.quit();
    }
}
