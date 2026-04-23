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

/*Test case 2 Cart Functionality validation
  It searches for headphones on Amazon, adds to cart, verify cart details
  and removes item
*/
public class TestCase2CartValidation extends Components_Amazon
{
    String productName  = "";
    String productPrice = "";
    boolean testPassed  = true;
    @BeforeClass
    public void setup() { setUp(); }
    @AfterClass
    public void teardown()
    {
        writeResult("Test Case 2", testPassed ? "Passed" : "Failed");
        tearDown();
    }

    //Use Javascript to extract product detail page URL//
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
        System.out.println("JS extracted " + urls.size() + " product URLs.");
        return urls;
    }

    //Step - 1 Seach for headphones and checks for bot detection//
    @Test(priority = 1)
    public void searchForHeadphones()
    {
        driver.get("https://www.amazon.in");
        try 
        { 
            Thread.sleep(3000); 
        } 
        catch(InterruptedException ignored) {}
        String url = driver.getCurrentUrl();
        if(url.contains("errors") || url.contains("sorry"))
        {
            System.out.println("Fail : Amazon bot-detection page. URL: " + url);
            testPassed = false;
            return;
        }
        try
        {
            WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
            searchBox.clear();
            searchBox.sendKeys("Headphones");
            wait.until(ExpectedConditions.elementToBeClickable(
                By.id("nav-search-submit-button"))).click();
            System.out.println("Pass Search for headphones begins");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error occurred while searching: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 2 Wait for search results and open first product page//
    @Test(priority = 2)
    public void openFirstProduct()
    {
        try
        {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-component-type='s-search-result']")));
            takeScreenshot("screenshots/test_case_2/search_results.png");
            List<String> urls = getProductUrls();
            if(urls.isEmpty())
            {
                System.out.println("Fail : No product URLs found via JS extraction");
                testPassed = false;
                return;
            }
            String productUrl = urls.get(0);
            System.out.println("Navigating to first product: " + productUrl);
            driver.get(productUrl);
            wait.until(ExpectedConditions.urlContains("/dp/"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
            System.out.println("Pass : First product opened");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error opening first product: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 3 Verify the product name and price are visible on the product page//
    @Test(priority = 3)
    public void verifyProductNameAndPrice()
    {
        try
        {
            WebElement titleElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
            productName = titleElement.getText().trim();

            if(!productName.isEmpty())
                System.out.println("Pass product name verified: " + productName);
            else
            {
                System.out.println("Fail product name is empty");
                testPassed = false;
            }
            List<WebElement> priceElements = driver.findElements(
                By.cssSelector(".a-price .a-offscreen"));
            if (priceElements.size() > 0)
            {
                productPrice = priceElements.get(0).getText();
                System.out.println("Pass product price verified: " + productPrice);
            }
            else
                System.out.println("Warn product price not found (may require variant selection)");

            takeScreenshot("screenshots/test_case_2/product_details.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify product details: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 4 Click to add to cart button//
    @Test(priority = 4)
    public void addToCart()
    {
        try
        {
            WebElement addToCartButton = null;
            String[] idSelectors = { "add-to-cart-button", "buy-now-button" };
            for (String id : idSelectors)
            {
                try
                {
                    addToCartButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(id)));
                    System.out.println("Found add-to-cart via id: " + id);
                    break;
                }
                catch (Exception ignored) {}
            }
            if (addToCartButton == null)
            {
                try
                {
                    addToCartButton = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.cssSelector("input[name='submit.add-to-cart'], " +
                                           "input[name='submit.buy-now']")));
                    System.out.println("Found add-to-cart via CSS fallback");
                }
                catch (Exception ignored) {}
            }
            if(addToCartButton == null)
            {
                System.out.println("Fail could not find add-to-cart button");
                testPassed = false;
                return;
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);
            System.out.println("Pass clicked add to cart");
            Thread.sleep(2500);
            try
            {
                WebElement noThanks = driver.findElement(By.cssSelector(
                    "#attachSiNoCoverage, #siNoCoverage-announce, [id*='no-coverage']"));
                noThanks.click();
                System.out.println("Pass dismissed protection plan modal");
                Thread.sleep(1000);
            }
            catch (Exception ignored) {}
            takeScreenshot("screenshots/test_case_2/added_to_cart.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail could not add to cart: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 5 Navigate to the cart page and verify the item is present//
    @Test(priority = 5)
    public void goToCartAndVerify()
    {
        try
        {
            driver.get("https://www.amazon.in/gp/cart/view.html");
            Thread.sleep(2500);
            takeScreenshot("screenshots/test_case_2/cart_page.png");
            WebElement cartItem = null;
            String[][] cartSelectors = {
                { ".sc-list-item-content",              "signed-in cart item"   },
                { ".sc-grid-item",                      "grid cart item"        },
                { "[data-item-index]",                  "indexed cart item"     },
                { "#sc-active-cart [data-asin]",        "ASIN in active cart"   },
                { "#CART [data-asin]",                  "ASIN in CART section"  },
                { ".a-spacing-mini.sc-list-item",       "mini list item"        },
                { "[class*='sc-list-item']",            "sc-list-item wildcard" }
            };
            for (String[] pair : cartSelectors)
            {
                try
                {
                    cartItem = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector(pair[0])));
                    System.out.println("Pass cart item found (" + pair[1] + ")");
                    break;
                }
                catch (Exception ignored) {}
            }
            if (cartItem == null)
            {
                System.out.println("Fail could not find cart items. URL: " + driver.getCurrentUrl());
                System.out.println("Page source snippet: " +
                    driver.getPageSource().substring(0, Math.min(500, driver.getPageSource().length())));
                testPassed = false;
                return;
            }
            List<WebElement> cartTitles = driver.findElements(By.cssSelector(
                ".sc-product-title span, .a-truncate-cut, [class*='product-title']"));
            if (cartTitles.size() > 0)
                System.out.println("Pass item in cart: " + cartTitles.get(0).getText().trim());
            else
                System.out.println("Warn product title not found in cart DOM but item exists");
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify cart: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 6 Verify cart shows correct quantity and subtotal amount//
    @Test(priority = 6)
    public void verifyCartQuantityAndTotal()
    {
        try
        {
            List<WebElement> quantity = driver.findElements(By.cssSelector(
                ".sc-quantity-textfield, input[name='quantity'], " +
                "select[name='quantity'], " +
                ".a-dropdown-container[data-name='quantity'] .a-button-text"));
            if(quantity.size() > 0)
            {
                String qtyVal = quantity.get(0).getAttribute("value");
                if (qtyVal == null || qtyVal.isEmpty())
                    qtyVal = quantity.get(0).getText().trim();
                System.out.println("Pass Cart quantity: " + qtyVal);
            }
            else
            {
                System.out.println("Fail cart quantity field not found");
                testPassed = false;
            }
            List<WebElement> subtotal = driver.findElements(By.cssSelector(
                "#sc-subtotal-amount-activecart span, " +
                ".sc-subtotal-amount-activecart span, " +
                "[id*='subtotal'] span.a-color-base, " +
                ".a-color-price.sc-price"));
            if(subtotal.size() > 0)
                System.out.println("Pass Cart subtotal: " + subtotal.get(0).getText().trim());
            else
            {
                System.out.println("Fail cart subtotal not found");
                testPassed = false;
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify cart quantities: " + e.getMessage());
            testPassed = false;
        }
    }
    
    //Step - 7 Remove the item from cart using JavaScript click//
    @Test(priority = 7)
    public void removeItemAndVerifyEmptyCart()
    {
        try
        {
            Boolean clicked = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var selectors = [" +
                "  '[data-action=\"delete\"] input'," +
                "  'input[data-action=\"delete\"]'," +
                "  'span[data-action=\"delete\"]'," +
                "  '[data-action=\"delete\"] span.a-declarative'," +
                "  '.sc-action-delete input'," +
                "  'input[value=\"Delete\"]'," +
                "  'a[href*=\"delete\"]'" +
                "];" +
                "for (var i = 0; i < selectors.length; i++) {" +
                "  var el = document.querySelector(selectors[i]);" +
                "  if (el) { el.click(); return true; }" +
                "}" +
                "return false;"
            );
            if (!Boolean.TRUE.equals(clicked))
            {
                System.out.println("Fail could not find delete button with any known selector");
                testPassed = false;
                return;
            }
            System.out.println("Pass clicked delete button");
            Thread.sleep(2500);
            takeScreenshot("screenshots/test_case_2/empty_cart.png");
            List<WebElement> emptyMessage = driver.findElements(By.cssSelector(
                ".sc-your-amazon-cart-is-empty, [class*='empty-cart'], " +
                "h2[class*='empty'], #sc-active-cart h2"));
            if(emptyMessage.size() > 0)
            {
                System.out.println("Pass Cart is empty: " + emptyMessage.get(0).getText().trim());
            }
            else
            {
                List<WebElement> remainingItems = driver.findElements(By.cssSelector(
                    ".sc-list-item-content, .sc-grid-item, [data-item-index]"));
                if (remainingItems.isEmpty())
                    System.out.println("Pass cart is empty (no items remaining)");
                else
                {
                    System.out.println("Fail cart still has " + remainingItems.size() +
                                       " item(s) after deletion");
                    testPassed = false;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Fail could not remove item from cart: " + e.getMessage());
            testPassed = false;
        }
    }
}