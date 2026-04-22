package com.example.qa.components;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
public class Components_Amazon 
{
    protected WebDriver driver;
    protected WebDriverWait wait;
    public void setUp() 
    {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
            System.out.println("Screenshot taken: " + screenshotPath);
        }
        catch (IOException e)
        {
            System.err.println("Error occurred while taking screenshot: " + e.getMessage());
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
            System.err.println("Error occurred while writing result: " + e.getMessage());
        }
    }
}