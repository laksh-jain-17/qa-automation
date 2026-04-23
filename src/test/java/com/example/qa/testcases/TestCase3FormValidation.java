package com.example.qa.testcases;
import com.example.qa.components.Components_Apple;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;

/*Test case 3 - Form Validation on Apple Account Page*/

public class TestCase3FormValidation extends Components_Apple
{
    boolean testPassed = true;
    boolean iframeReady = false;
    @BeforeClass
    public void setup()
    {
        setUp();
    }
    @AfterClass
    public void teardown()
    {
        writeResult("Test Case 3", testPassed ? "Passed" : "Failed");
        tearDown();
    }

    //This method helps in switching Webdriver focus into Apple account form iframe//
    private boolean switchToFormFrame()
    {
        try
        {
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
            String[] iframeSelectors = {
                "iframe#aid-auth-widget-iFrame",
                "iframe[name*='aid']",
                "iframe[id*='apple']",
                "iframe[src*='account.apple.com']",
                "iframe[src*='appleid']"
            };
            boolean switched = false;
            for(String sel : iframeSelectors)
            {
                try 
                {
                    WebElement frame = driver.findElement(By.cssSelector(sel));
                    driver.switchTo().frame(frame);
                    switched = true;
                    System.out.println("Switched into iframe: " + sel);
                    break;
                } 
                catch (Exception ignored) {}
            }
            if(!switched)
            {
                List<WebElement> frames = driver.findElements(By.tagName("iframe"));
                if(frames.isEmpty())
                {
                    System.out.println("No iframes found on page");
                    return false;
                }
                driver.switchTo().frame(frames.get(0));
                System.out.println("Switched into first iframe (fallback). Total iframes: " + frames.size());
            }
            List<WebElement> nestedFrames = driver.findElements(By.tagName("iframe"));
            if(!nestedFrames.isEmpty())
            {
                try 
                {
                    driver.switchTo().frame(nestedFrames.get(0));
                    System.out.println("Switched into nested iframe");
                }
                catch (Exception ignored) {

                }
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form, input")));
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Could not switch to form iframe: " + e.getMessage());
            return false;
        }
    }

    //Step 1 - Navigate to Apple account page and verify it loads//
    @Test(priority = 1)
    public void openAppleAccountPage()
    {
        try
        {
            driver.get("https://account.apple.com/account");
            Thread.sleep(5000); 
            String title = driver.getTitle();
            String url   = driver.getCurrentUrl();
            System.out.println("Page title: " + title);
            System.out.println("Current URL: " + url);
            iframeReady = switchToFormFrame();
            if(iframeReady)
            {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form")));
                takeScreenshot("screenshots/test_case_3/page_loaded.png");
                System.out.println("Pass : Apple account page loaded successfully");
            }
            else
            {
                System.out.println("Fail : Could not enter form iframe");
                testPassed = false;
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail : Apple account page did not load: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 2 Verify that form fields are visible on the page//
    @Test(priority = 2)
    public void verifyFormFieldsVisible()
    {
        if(!iframeReady) 
        { 
            System.out.println("Skip : iframe not ready"); 
            testPassed = false; 
            return; 
        }
        try
        {
            switchToFormFrame();
            List<WebElement> allInputs = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("input:not([type='hidden'])")
                )
            );
            List<WebElement> visibleInputs = allInputs.stream()
                .filter(WebElement::isDisplayed)
                .collect(java.util.stream.Collectors.toList());
            if(visibleInputs.size() > 0)
                System.out.println("Pass : Form fields visible. Count: " + visibleInputs.size());
            else
            {
                System.out.println("Fail : No visible form fields found (total present: " + allInputs.size() + ")");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_3/form_fields_visible.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Form fields not visible: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 3 Submit the form without filling any fields//
    @Test(priority = 3)
    public void verifyRequiredFieldsOnBlankSubmit()
    {
        if(!iframeReady) 
        { 
            System.out.println("Skip : iframe not ready"); 
            testPassed = false; 
            return; 
        }
        try
        {
            switchToFormFrame();
            WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'], input[type='submit']")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
            Thread.sleep(2000);
            List<WebElement> errorMessages = driver.findElements(
                By.cssSelector(
                    "[aria-invalid='true'], " +
                    ".form-message-error, " +
                    ".error-message, " +
                    "[class*='error'], " +
                    "[role='alert']"
                )
            );
            if(errorMessages.size() > 0)
                System.out.println("Pass : Required fields validation triggered. Errors found: " + errorMessages.size());
            else
            {
                System.out.println("Fail : No error messages after blank submit");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_3/required_fields_error.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error submitting blank form: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 4 Enter an invalid email format and verify error message//
    @Test(priority = 4)
    public void verifyInvalidEmailFormat()
    {
        if(!iframeReady) 
        { 
            System.out.println("Skip : iframe not ready"); 
            testPassed = false; 
            return; 
        }
        try
        {
            switchToFormFrame();
            WebElement emailField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(
                        "input[type='email'], " +
                        "input[name*='email'], " +
                        "input[id*='email'], " +
                        "input[autocomplete='email']"
                    )
                )
            );
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", emailField
            );
            emailField.clear();
            emailField.sendKeys("invalidemail");
            emailField.sendKeys(org.openqa.selenium.Keys.TAB);
            Thread.sleep(2000);
            List<WebElement> emailErrors = driver.findElements(
                By.cssSelector("[aria-invalid='true'], .form-message-error, [class*='error'], [role='alert']")
            );
            if (emailErrors.size() > 0)
                System.out.println("Pass : Invalid email error shown");
            else
            {
                System.out.println("Fail : No error shown for invalid email");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_3/invalid_email.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error validating email: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 5 Enter a weak/ short password and verify the error message//
    @Test(priority = 5)
    public void verifyWeakPasswordError()
    {
        if(!iframeReady) 
        { 
            System.out.println("Skip : iframe not ready"); 
            testPassed = false; 
            return; 
        }
        try
        {
            switchToFormFrame();
            WebElement passwordField = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(
                        "input[type='password'], " +
                        "input[name*='password'], " +
                        "input[id*='password'], " +
                        "input[autocomplete='new-password']"
                    )
                )
            );
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", passwordField
            );
            passwordField.clear();
            passwordField.sendKeys("123");
            passwordField.sendKeys(org.openqa.selenium.Keys.TAB);
            Thread.sleep(1500);
            List<WebElement> passwordErrors = driver.findElements(
                By.cssSelector("[aria-invalid='true'], .form-message-error, [class*='error'], [role='alert']")
            );
            if(passwordErrors.size() > 0)
                System.out.println("Pass : Weak password error shown");
            else
            {
                System.out.println("Fail : No error for weak password");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_case_3/weak_password_error.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error validating password strength: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 6 Enter mismatched passwords in password and confirm fields//
    @Test(priority = 6)
    public void verifyPasswordMismatchError()
    {
        if(!iframeReady) 
        { 
            System.out.println("Skip : iframe not ready"); 
            testPassed = false; 
            return; 
        }
        try
        {
            switchToFormFrame();
            List<WebElement> passwordFields = driver.findElements(
                By.cssSelector("input[type='password']")
            );
            if (passwordFields.size() >= 2)
            {
                passwordFields.get(0).clear();
                passwordFields.get(0).sendKeys("ValidPass@123");

                passwordFields.get(1).clear();
                passwordFields.get(1).sendKeys("DifferentPass@456");
                passwordFields.get(1).sendKeys(org.openqa.selenium.Keys.TAB);
                Thread.sleep(1500);
                List<WebElement> mismatchErrors = driver.findElements(
                    By.cssSelector("[aria-invalid='true'], .form-message-error, [class*='error'], [role='alert']")
                );
                if (mismatchErrors.size() > 0)
                    System.out.println("Pass : Password mismatch error shown");
                else
                {
                    System.out.println("Fail : No error for password mismatch");
                    testPassed = false;
                }
                takeScreenshot("screenshots/test_case_3/password_mismatch_error.png");
            }
            else
            {
                System.out.println("Fail : Could not find both password fields. Found: " + passwordFields.size());
                testPassed = false;
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error verifying password mismatch: " + e.getMessage());
            testPassed = false;
        }
    }

    //Step - 7 Fill all the visible form fields with valid data//
    @Test(priority = 7)
    public void verifyValidInputsAccepted()
    {
        if(!iframeReady) 
        { 
            System.out.println("Skip : iframe not ready"); 
            testPassed = false; 
            return; 
        }
        try
        {
            driver.switchTo().defaultContent();
            driver.get("https://account.apple.com/account");
            Thread.sleep(5000);
            boolean entered = switchToFormFrame();
            if(!entered)
            {
                System.out.println("Fail : Could not re-enter iframe for valid inputs test");
                testPassed = false;
                return;
            }
            List<WebElement> allInputs = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("input:not([type='hidden'])")
                )
            );
            List<WebElement> visibleInputs = allInputs.stream()
                .filter(WebElement::isDisplayed)
                .collect(java.util.stream.Collectors.toList());

            System.out.println("Visible input fields: " + visibleInputs.size());
            int filledCount = 0;
            for(WebElement input : visibleInputs)
            {
                String type       = input.getAttribute("type");
                String name       = input.getAttribute("name");
                String id         = input.getAttribute("id");
                String autoComp   = input.getAttribute("autocomplete");

                name     = name     != null ? name.toLowerCase()     : "";
                id       = id       != null ? id.toLowerCase()       : "";
                autoComp = autoComp != null ? autoComp.toLowerCase() : "";

                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", input
                );

                if(name.contains("first") || id.contains("first"))
                {
                    input.clear(); input.sendKeys("Test");
                    System.out.println("Pass Filled first name"); filledCount++;
                }
                else if(name.contains("last") || id.contains("last"))
                {
                    input.clear(); input.sendKeys("User");
                    System.out.println("Pass Filled last name"); filledCount++;
                }
                else if("email".equals(type) || name.contains("email") ||
                         id.contains("email") || autoComp.contains("email"))
                {
                    input.clear(); input.sendKeys("testuser" + System.currentTimeMillis() + "@example.com");
                    System.out.println("Pass Filled email"); filledCount++;
                }
                else if("password".equals(type) || name.contains("password") ||
                         id.contains("password") || autoComp.contains("password"))
                {
                    input.clear(); input.sendKeys("ValidPass@123");
                    System.out.println("Pass Filled password field"); filledCount++;
                }
            }
            takeScreenshot("screenshots/test_case_3/valid_inputs.png");
            if(filledCount > 0)
                System.out.println("Pass : Valid inputs entered successfully (" + filledCount + " fields filled)");
            else
            {
                System.out.println("Fail : Could not identify and fill any fields");
                testPassed = false;
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error entering valid inputs: " + e.getMessage());
            testPassed = false;
        }
    }
}