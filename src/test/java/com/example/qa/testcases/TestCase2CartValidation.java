package com.example.qa.testcases;
import com.example.qa.components.Components_Amazon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;
public class TestCase2CartValidation extends Components_Amazon
{
    String productName = "";
    String productPrice = "";
    boolean testPassed = true;
    @BeforeClass
    public void setup()
    {
        setUp();
    }
    @AfterClass
    public void teardown()
    {
        writeResult("Test Case 2",testPassed ? "Passed" : "Failed");
        tearDown();
    }
    @Test(priority = 1)
    public void openAmazonHomepage()
    {
        driver.get("https://www.amazon.in");
        try
        {
            WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox"))
            );
            searchBox.clear();
            searchBox.sendKeys("Headphones");
            WebElement searchButton = driver.findElement(By.id("nav-search-submit-button"));
            searchButton.click();
            System.out.println("Pass Search for headphones begins");
        }
        catch (Exception e)
        {
            System.out.println("Fail : Error occurred while searching for headphones " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 2)
    public void openFirstProduct()
    {
        try
        {
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-component-type='s-search-result'] h2 a")
                )
            );
            takeScreenshot("screenshots/test_case_2/search_results.png");
            WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("h2 a.a-link-normal.s-line-clamp-2, h2 a.a-link-normal")
                )
            );
            firstProduct.click();
            System.out.println("Pass : First product opened");
        }
        catch (Exception e)
        {
            System.out.println("Fail : Error occurred while opening first product " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 3)
    public void verifyProductNameAndPrice()
    {
        try
        {
            WebElement titleElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("productTitle"))
            );
            productName = titleElement.getText().trim();
            if(!productName.isEmpty())
            {
                System.out.println("Pass product name verified " + productName);
            }
            else{
                System.out.println("Fail product name is empty");
                testPassed = false;
            }
            List<WebElement> priceElements = driver.findElements(
                By.cssSelector(".a-price .a-offscreen")
            );
            if(priceElements.size() > 0)
            {
                productPrice = priceElements.get(0).getText();
                System.out.println("Pass product price verified " + productPrice);
            }
            else{
                System.out.println("Fail product price not found");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_2/product_details.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify product details " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 4)
    public void addToCart()
    {
        try
        {
            WebElement addToCartButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button"))
            );
            addToCartButton.click();
            System.out.println("Pass clicked add to cart button");
            Thread.sleep(2000);
            takeScreenshot("screenshots/test_case_2/added_to_cart.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail could not add to cart " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 5)
    public void goToCartAndVerify() 
    {
        try
        {
            driver.get("https://www.amazon.in/gp/cart/view.html");
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".sc-list-item")
                )
            );
            takeScreenshot("screenshots/test_case_2/cart_page.png");
            List<WebElement> cartItems = driver.findElements(
                By.cssSelector(".sc-product-title")
            );
            if(cartItems.size() > 0)
            {
                System.out.println("Pass item found in cart " + cartItems.get(0).getText());
            }
            else{
                System.out.println("Fail no items found in cart");
                testPassed = false;
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify cart " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 6)
    public void verifyCartQuantityAndTotal()
    {
        try
        {
            List<WebElement> quantity = driver.findElements(
                By.cssSelector(".sc-quantity-textfield")
            );
            if(quantity.size() > 0)
            {
                System.out.println("Pass Cart quantity verified " + quantity.get(0).getAttribute("value"));
            }
            else{
                System.out.println("Fail cart quantity not found");
                testPassed = false;
            }
            List<WebElement> subtotal = driver.findElements(
                By.cssSelector(".sc-subtotal-amount-activecart")
            );
            if(subtotal.size() > 0)
            {
                System.out.println("Pass Cart subtotal verified " + subtotal.get(0).getText());
            }
            else{
                System.out.println("Fail cart subtotal not found");
                testPassed = false;
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify cart quantities " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 7)
    public void removeItemAndVerifyEmptyCart()
    {
        try
        {
            WebElement deleteButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[value='Delete']")
                )
            );
            deleteButton.click();
            System.out.println("Pass clicked delete button to remove item");
            Thread.sleep(2000);
            takeScreenshot("screenshots/test_case_2/empty_cart.png");
            List<WebElement> emptyMessage = driver.findElements(
                By.cssSelector(".sc-your-amazon-cart-is-empty")
            );
            if(emptyMessage.size() > 0)
            {
                System.out.println("Pass Cart is empty after deletion");
            }
            else{
                List<WebElement> remainingItems = driver.findElements(
                    By.cssSelector(".sc-list-item")
                );
                if(remainingItems.size() == 0)
                {
                    System.out.println("Pass cart is empty after deletion");
                }
                else{
                    System.out.println("Fail cart still has items after deletion");
                    testPassed = false;
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail could not remove item from cart " + e.getMessage());
            testPassed = false;
        }
    }
}