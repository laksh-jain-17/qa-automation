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
import java.time.Duration;
import java.util.Arrays;
public class Components_Apple
{
    protected WebDriver driver;
    protected WebDriverWait wait;
    public void setUp()
    {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        new File("screenshots/test_case_1").mkdirs();
        new File("screenshots/test_case_2").mkdirs();
        new File("screenshots/test_case_3").mkdirs();
        driver = new ChromeDriver(options);
        ((JavascriptExecutor) driver).executeScript(
            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
        );
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    public void tearDown()
    {
        if(driver != null)
        {
            driver.quit();
        }
    }
    public void takeScreenshot(String screenshotPath)
    {
        try
        {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(),Paths.get(screenshotPath));
            System.out.println("Screenshot saved " + screenshotPath);
        }
        catch (IOException e)
        {
            System.out.println("Failed to save screenshots " + e.getMessage());
        }
    }
    public void writeResult(String testCase, String result)
    {
        try
        {
            String content = testCase + ": " + result + "\n";
            Files.write(Paths.get("testResults.txt"), 
                content.getBytes(), 
                java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.APPEND
            );
            System.out.println(testCase + ": " + result);
        }
        catch(IOException e)
        {
            System.out.println("Failed to write results " + e.getMessage());
        }
    }
}