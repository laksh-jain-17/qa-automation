package com.example.qa.testcases;
import com.example.qa.components.Components_Amazon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;
public class TestCase1SearchVerification extends Components_Amazon
{
    boolean testPassed = true;
    @BeforeClass
    public void setup()
    {
        setUp();
    }
    @AfterClass
    public void teardown()
    {
        writeResult("Test Case 1",testPassed ? "Passed" : "Failed");
        tearDown();
    }
    @Test(priority = 1)
    public void openAmazonHomepage()
    {
        driver.get("https://www.amazon.in");
        String title = driver.getTitle();
        if(title.toLowerCase().contains("amazon"))
        {
            System.out.println("Pass : Amazon homepage loaded Title " + title);
        }
        else{
            System.out.println("Fail : Amazon homepage did not load. Title " + title);
            testPassed = false;
        }
        takeScreenshot("screenshots/test_case_1/homepage.png");
    }
    @Test(priority = 2)
    public void searchForLaptop()
    {
        try
        {
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
            searchBox.clear();
            searchBox.sendKeys("Laptop");
            WebElement searchButton = driver.findElement(By.id("nav-search-submit-button"));
            searchButton.click();
            System.out.println("Pass Search for laptop begins");
        }
        catch(Exception e)
        {
            System.out.println("Fail cound not perform search " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 3)
    public void varifySearchResults()
    {
        try
        {
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-component-type='s-search-result']")
                )
            );
            List<WebElement> results = driver.findElements(
                By.cssSelector("[data-component-type='s-search-result']")
            );
            if(results.size() > 0)
            {
                System.out.println("Pass Search results appeared. Count " + results.size());
            }
            else{
                System.out.println("Fail no search results found");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_1/search_results.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail search results did not load " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 4)
    public void verifyResultHasTitlePriceRating() 
    {
        try
        {
            List<WebElement> titles = driver.findElements(
                By.cssSelector("h2.a-size-medium, h2.a-size-base-plus")
            );
            List<WebElement> prices = driver.findElements(
                By.cssSelector(".a-price .a-offscreen")
            );
            List<WebElement> ratings = driver.findElements(
                By.cssSelector(".a-icon-star-small .a-icon-alt")
            );
            if(titles.size() > 0)
            {
                System.out.println("Pass product titles found count " + titles.size());
            }
            else{
                System.out.println("Fail no product titles found");
                testPassed = false;
            }
            if(prices.size() > 0)
            {
                System.out.println("Pass product prices found count " + prices.size());
            }
            else{
                System.out.println("Fail no product prices found");
                testPassed = false;
            }
            if(ratings.size() > 0)
            {
                System.out.println("Pass product ratings found count " + ratings.size());
            }
            else{
                System.out.println("Fail no product ratings found");
                testPassed = false;
            }

        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify result details " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 5)
    public void verifyFirstProduct() 
    {
        try
        {
            WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("[data-component-type='s-search-result'] h2 a")
                )
            );
            String productName = firstProduct.getText();
            System.out.println("Clicking on product " + productName);
            firstProduct.click();
            WebElement productTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("productTitle"))
            );
            if(!productTitle.getText().isEmpty())
            {
                System.out.println("Pass product 1 title verified " + productTitle.getText().trim());
            }
            else{
                System.out.println("Fail product 1 title is empty");
                testPassed = false;
            }
            List<WebElement> priceElement = driver.findElements(
                By.cssSelector(".a-price .a-offscreen")
            );
            if(priceElement.size() > 0)
            {
                System.out.println("Pass product 1 price verified " + priceElement.get(0).getText());
            }
            else{
                System.out.println("Fail product 1 price not found");
                testPassed = false;
            }
            List<WebElement> availability = driver.findElements(By.id("availability"));
            if(availability.size() > 0)
            {
                System.out.println("Pass product 1 availability verified " + availability.get(0).getText());
            }
            else{
                System.out.println("Fail product 1 availability not found");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_1/product1_details.png");
            driver.navigate().back();
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify first product details " + e.getMessage());
            testPassed = false;
            driver.navigate().back();
        }
    }
    @Test(priority = 6)
    public void verifySecondProduct()
    {
        try
        {
            List<WebElement> products = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector("[data-component-type='s-search-result'] h2 a")
                )
            );
            String productName = products.get(1).getText();
            System.out.println("Clicking on product 2" + productName);
            products.get(1).click();
            WebElement productTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("productTitle"))
            );
            if(!productTitle.getText().isEmpty())
            {
                System.out.println("Pass product 2 title verified " + productTitle.getText().trim());
            }
            else{
                System.out.println("Fail product 2 title is empty");
                testPassed = false;
            }
            List<WebElement> priceElement = driver.findElements(
                By.cssSelector(".a-price .a-offscreen")
            );
            if(priceElement.size() > 0)
            {
                System.out.println("Pass product 2 price verified " + priceElement.get(0).getText());
            }
            else{
                System.out.println("Fail product 2 price not found");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_1/product2_details.png");
            driver.navigate().back();
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify second product details " + e.getMessage());
            testPassed = false;
            driver.navigate().back();
        }
    }
    @Test(priority = 7)
    public void verifyThirdProduct()
    {
        try
        {
            List<WebElement> products = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector("[data-component-type='s-search-result'] h2 a")
                )
            );
            String productName = products.get(2).getText();
            System.out.println("Clicking on product 3 " + productName);
            products.get(2).click();
            WebElement productTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("productTitle"))
            );
            if(!productTitle.getText().isEmpty())
            {
                System.out.println("Pass product 3 title verified " + productTitle.getText().trim());
            }
            else{
                System.out.println("Fail product 3 title is empty");
                testPassed = false;
            }
            List<WebElement> priceElement = driver.findElements(
                By.cssSelector(".a-price .a-offscreen")
            );
            if(priceElement.size() > 0)
            {
                System.out.println("Pass product 3 price verified " + priceElement.get(0).getText());
            }
            else{
                System.out.println("Fail product 3 price not found");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_1/product3_details.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify third product details " + e.getMessage());
            testPassed = false;
        }
    }
}