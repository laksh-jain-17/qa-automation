package com.example.qa.testcases;

import com.example.qa.components.Components_Amazon;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestCase1SearchVerification extends Components_Amazon
{
    boolean testPassed = true;

    @BeforeClass
    public void setup() { setUp(); }

    @AfterClass
    public void teardown()
    {
        writeResult("Test Case 1", testPassed ? "Passed" : "Failed");
        tearDown();
    }

    private boolean waitForHomepage()
    {
        try
        {
            Thread.sleep(3000);
            String url   = driver.getCurrentUrl();
            String title = driver.getTitle();
            System.out.println("Current URL: " + url);
            System.out.println("Page title : " + title);

            if (url.contains("amazon.in/errors") || url.contains("sorry") ||
                title.toLowerCase().contains("sorry") || title.isEmpty())
            {
                System.out.println("Fail : Amazon bot-detection page. Cannot continue.");
                return false;
            }
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
            return true;
        }
        catch (Exception e)
        {
            System.out.println("Fail : Homepage did not reach a usable state: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Uses JavaScript to pull the first /dp/ link from every result card.
    // This is selector-class-agnostic — works regardless of Amazon's markup.
    // -------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private List<String> getProductUrls()
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<String> urls = (List<String>) js.executeScript(
            "var cards = document.querySelectorAll(\"[data-component-type='s-search-result']\");" +
            "var hrefs = [];" +
            "for (var i = 0; i < cards.length; i++) {" +
            "  var anchors = cards[i].querySelectorAll('a[href]');" +
            "  for (var j = 0; j < anchors.length; j++) {" +
            "    var href = anchors[j].getAttribute('href');" +
            "    if (href && href.indexOf('/dp/') !== -1) {" +
            "      hrefs.push(href.startsWith('http') ? href : 'https://www.amazon.in' + href);" +
            "      break;" +
            "    }" +
            "  }" +
            "}" +
            "return hrefs;"
        );
        if (urls == null) urls = new ArrayList<>();
        System.out.println("JS extracted " + urls.size() + " product URLs from result cards.");
        return urls;
    }

    @Test(priority = 1)
    public void openAmazonHomepage()
    {
        driver.get("https://www.amazon.in");
        boolean loaded = waitForHomepage();
        if (loaded)
            System.out.println("Pass : Amazon homepage loaded. Title: " + driver.getTitle());
        else
        {
            System.out.println("Fail : Amazon homepage did not load usably.");
            testPassed = false;
        }
        takeScreenshot("screenshots/test_case_1/homepage.png");
    }

    @Test(priority = 2)
    public void searchForLaptop()
    {
        try
        {
            WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
            searchBox.clear();
            searchBox.sendKeys("Laptop");
            wait.until(ExpectedConditions.elementToBeClickable(
                By.id("nav-search-submit-button"))).click();
            System.out.println("Pass Search for laptop begins");
        }
        catch (Exception e)
        {
            System.out.println("Fail could not perform search: " + e.getMessage());
            testPassed = false;
        }
    }

    @Test(priority = 3)
    public void verifySearchResults()
    {
        try
        {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-component-type='s-search-result']")));
            List<WebElement> results = driver.findElements(
                By.cssSelector("[data-component-type='s-search-result']"));
            if (results.size() > 0)
                System.out.println("Pass Search results appeared. Count: " + results.size());
            else { System.out.println("Fail no search results found"); testPassed = false; }
            takeScreenshot("screenshots/test_case_1/search_results.png");
        }
        catch (Exception e)
        {
            System.out.println("Fail search results did not load: " + e.getMessage());
            testPassed = false;
        }
    }

    @Test(priority = 4)
    public void verifyResultHasTitlePriceRating()
    {
        try
        {
            List<WebElement> titles = driver.findElements(
                By.cssSelector("h2.a-size-medium, h2.a-size-base-plus, h2 span.a-text-normal"));
            List<WebElement> prices = driver.findElements(
                By.cssSelector(".a-price .a-offscreen, .a-price-whole"));
            List<WebElement> ratings = driver.findElements(
                By.cssSelector("[aria-label*='out of 5 stars'], i[class*='a-star'] .a-icon-alt"));

            if (titles.size() > 0)
                System.out.println("Pass product titles found count: " + titles.size());
            else { System.out.println("Fail no product titles found"); testPassed = false; }

            if (prices.size() > 0)
                System.out.println("Pass product prices found count: " + prices.size());
            else { System.out.println("Fail no product prices found"); testPassed = false; }

            if (ratings.size() > 0)
                System.out.println("Pass product ratings found count: " + ratings.size());
            else
                System.out.println("Warn no product ratings found (some results may not have ratings)");
        }
        catch (Exception e)
        {
            System.out.println("Fail could not verify result details: " + e.getMessage());
            testPassed = false;
        }
    }

    private void visitAndVerifyProduct(int index, String label, String screenshotPath,
                                       List<String> urls, String searchResultsUrl)
    {
        try
        {
            if (urls.size() <= index)
            {
                System.out.println("Fail not enough product URLs for " + label +
                                   ". Found: " + urls.size());
                testPassed = false;
                return;
            }

            String url = urls.get(index);
            System.out.println("Navigating to " + label + ": " + url);
            driver.get(url);

            wait.until(ExpectedConditions.urlContains("/dp/"));
            WebElement productTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));

            String titleText = productTitle.getText().trim();
            if (!titleText.isEmpty())
                System.out.println("Pass " + label + " title verified: " + titleText);
            else
            {
                System.out.println("Fail " + label + " title is empty");
                testPassed = false;
            }

            List<WebElement> priceElements = driver.findElements(
                By.cssSelector(".a-price .a-offscreen"));
            if (priceElements.size() > 0)
                System.out.println("Pass " + label + " price: " + priceElements.get(0).getText());
            else
                System.out.println("Warn " + label + " price not found");

            List<WebElement> availability = driver.findElements(By.id("availability"));
            if (availability.size() > 0)
                System.out.println("Pass " + label + " availability: " +
                                   availability.get(0).getText().trim());
            else
                System.out.println("Warn " + label + " availability not found");

            takeScreenshot(screenshotPath);

            driver.get(searchResultsUrl);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-component-type='s-search-result']")));
            Thread.sleep(1000);
        }
        catch (Exception e)
        {
            System.out.println("Fail could not verify " + label + " details: " + e.getMessage());
            testPassed = false;
            try
            {
                driver.get(searchResultsUrl);
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-component-type='s-search-result']")));
            }
            catch (Exception ignored) {}
        }
    }

    @Test(priority = 5)
    public void verifyFirstProduct()
    {
        String searchUrl = driver.getCurrentUrl();
        List<String> urls = getProductUrls();
        visitAndVerifyProduct(0, "product 1", "screenshots/test_case_1/product1_details.png",
                              urls, searchUrl);
    }

    @Test(priority = 6)
    public void verifySecondProduct()
    {
        String searchUrl = driver.getCurrentUrl();
        List<String> urls = getProductUrls();
        visitAndVerifyProduct(1, "product 2", "screenshots/test_case_1/product2_details.png",
                              urls, searchUrl);
    }

    @Test(priority = 7)
    public void verifyThirdProduct()
    {
        String searchUrl = driver.getCurrentUrl();
        List<String> urls = getProductUrls();
        visitAndVerifyProduct(2, "product 3", "screenshots/test_case_1/product3_details.png",
                              urls, searchUrl);
    }
}