package com.example.qa.testcases;
import com.example.qa.components.Components_Apple;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;
public class TestCase3FormValidation extends Components_Apple
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
        writeResult("Test Case 3",testPassed ? "Passed" : "Failed");
        tearDown();
    }
    @Test(priority = 1)
    public void openAppleSupportPage()
    {
        try
        {
            driver.get("https://account.apple.com/account");
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("form")
                )
            );
            String title = driver.getTitle();
            System.out.println("Page title " + title);
            takeScreenshot("screenshots/test_case_3/page_loaded.png");
            System.out.println("Pass : Apple account page loaded successfully");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Apple account page did not load " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 2)
    public void verifyFormFieldsVisible() 
    {
        try
        {
            List<WebElement> inputFields = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector("input")
                )
            );
            if(inputFields.size() > 0)
            {
                System.out.println("Pass : Form fields are visible count " + inputFields.size());
            }
            else{
                System.out.println("Fail : No form fields are found");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_Case_3/form_fields_visible.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Form fields are not visible " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 3)
    public void verifyRequiredFieldsOnBlankSubmit() 
    {
        try
        {
            WebElement continueButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
                )
            );
            continueButton.click();
            Thread.sleep(2000);
            List<WebElement> errorMessages = driver.findElements(
                By.cssSelector(".form-message-error, .error, [aria-invalid='true']")
            );
            if(errorMessages.size() > 0)
            {
                System.out.println("Pass : Required fields validation triggered " + errorMessages.size());
            }
            else{
                System.out.println("Fail : No error messages found");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_Case_3/required_fields_error.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error occurred while submitting form " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 4)
    public void verifyInvalidEmailFormat() 
    {
        try
        {
            WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='email'], input[name*='email'], input[id*='email']")
                )
            );
            emailField.clear();
            emailField.sendKeys("invalidemail");
            driver.findElement(By.cssSelector("body")).click();
            Thread.sleep(2000);
            List<WebElement> emailErrorMessages = driver.findElements(
                By.cssSelector(".form-message-error, .error, [aria-invalid='true']")
            );
            if(emailErrorMessages.size() > 0)
            {
                System.out.println("Pass : Invalid email error shown");
            }
            else{
                System.out.println("Fail : No error shown for invalid email");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_Case_3/invalid_email.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error occurred while validating email format " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 5)
    public void verifyWeakPasswordError()
    {
        try
        {
            WebElement passwordField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='password'], input[name*='password'], input[id*='password']")
                )
            );
            passwordField.clear();
            passwordField.sendKeys("123");
            driver.findElement(By.cssSelector("body")).click();
            Thread.sleep(1500);
            List<WebElement> passwordErrors = driver.findElements(
                By.cssSelector(".form-message-error, .error, [aria-invalid='true']")
            );
            if(passwordErrors.size() > 0)
            {
                System.out.println("Pass : Weak password error shown");
            }
            else{
                System.out.println("Fail : No error shown for weak password");
                testPassed = false;
            }
            takeScreenshot("screenshots/test_Case_3/weak_password_error.png");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error occurred while validating password strength " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 6)
    public void verifyPasswordMismatchError()
    {
        try
        {
            List<WebElement> passwordFields = driver.findElements(
                By.cssSelector("input[type='password']")
            );
            if(passwordFields.size() >= 2)
            {
                passwordFields.get(0).clear();
                passwordFields.get(0).sendKeys("ValidPass@123");
                passwordFields.get(1).clear();
                passwordFields.get(1).sendKeys("DifferentPass@456");
                driver.findElement(By.cssSelector("body")).click();
                Thread.sleep(1500);
                List<WebElement> mismatchErrors = driver.findElements(
                    By.cssSelector(".form-message-error, .error, [aria-invalid='true']")
                );
                if(mismatchErrors.size() > 0)
                {
                    System.out.println("Pass : Password mismatch error shown");
                }
                else{
                    System.out.println("Fail : No error shown for password mismatch");
                    testPassed = false;
                }
                takeScreenshot("screenshots/test_case_3/password_mismatch_error.png");
            }
            else{
                System.out.println("Fail : could not find confirm password field");
                testPassed = false;
            }
        }
        catch(Exception e)
        {
            System.out.println("Fail could not verify password mismatch " + e.getMessage());
            testPassed = false;
        }
    }
    @Test(priority = 7)
    public void verifyValidInputsAccepted()
    {
        try
        {
            driver.get("https://account.apple.com/account");
            Thread.sleep(2000);
            List<WebElement> allInputs = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector("input:not([type='hidden'])")
                )
            );
            System.out.println("Found " + allInputs.size() + " input fields");
            for(WebElement input : allInputs)
            {
                String inputType = input.getAttribute("type");
                String inputName = input.getAttribute("name") != null ? input.getAttribute("name"): "";
                String inputId = input.getAttribute("id") != null ? input.getAttribute("id") : "";
                if(inputName.contains("firstName") || inputId.contains("firstName"))
                {
                    input.clear();
                    input.sendKeys("Test");
                    System.out.println("Pass Filled first name field");
                }
                else if(inputName.contains("lastName") || inputId.contains("lastName"))
                {
                    input.clear();
                    input.sendKeys("Test");
                    System.out.println("Pass Filled last name field");
                }
                else if("email".equals(inputType) || inputName.contains("email") || inputId.contains("email"))
                {
                    input.clear();
                    input.sendKeys("testuser@example.com");
                    System.out.println("Pass Filled email field");
                }
                else if("password".equals(inputType))
                {
                    input.clear();
                    input.sendKeys("ValidPass@123");
                    System.out.println("Pass Filled password field");
                }
            }
            takeScreenshot("screenshots/test_Case_3/valid_inputs.png");
            System.out.println("Pass : Valid inputs entered successfully");
        }
        catch(Exception e)
        {
            System.out.println("Fail : Error occurred while entering valid inputs " + e.getMessage());
            testPassed = false;
        }
    }
}