package com.example.qa.components;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

/* This is base class for Amazon test cases
    It provides shared Webdriver setup, teardown, screenshot,
    and result-writing utilities used by Testcase 1 and 2.
    */
public class Components_Amazon
{
    protected WebDriver driver;
    protected WebDriverWait wait;

    //Initializes ChromeDriver with options too avoid bot detection//
    public void setUp()
    {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        new File("screenshots/test_case_1").mkdirs();
        new File("screenshots/test_case_2").mkdirs();
        new File("screenshots/test_case_3").mkdirs();

        driver = new ChromeDriver(options);
        try 
        {
            ((org.openqa.selenium.devtools.HasDevTools) driver)
                .getDevTools()
                .createSession();
        } 
        catch(Exception ignored) {}
        try 
        {
            ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
            );
        } 
        catch (Exception ignored) {}
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    //Closes the web browser and ends the WebDriver session after tests complete//
    public void tearDown()
    {
        if (driver != null)
        {
            driver.quit();
        }
    }

    //Takes screenshots and saves it to specific file path//
    public void takeScreenshot(String screenshotPath)
    {
        try
        {
            new File(screenshotPath).getParentFile().mkdirs();
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), Paths.get(screenshotPath),
                StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot saved: " + screenshotPath);
        }
        catch (IOException e)
        {
            System.err.println("Error occurred while taking screenshot: " + screenshotPath + " -> " + e.getMessage());
        }
    }

    //Appends test cases to testResults.txt file//
    public void writeResult(String testCase, String result)
    {
        try
        {
            String content = testCase + ": " + result + "\n";
            Files.write(
                Paths.get("testResults.txt"),
                content.getBytes(),
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
            );
            System.out.println(testCase + ": " + result);
        }
        catch(IOException e)
        {
            System.err.println("Error writing result: " + e.getMessage());
        }
    }
}