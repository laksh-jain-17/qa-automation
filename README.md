QA Automation

Selenium WebDriver test scripts written in Java with TestNG framework.

What this tests

- Amazon product search and product detail verification
- Amazon cart add, verify and remove functionality
- Apple account page form field validations

Tools Used

- Java 11
- Maven
- Selenium WebDriver 4.18.1
- TestNG 7.9.0
- WebDriverManager 5.7.0
- Google Chrome

How to setup the environment

Make sure you have Java and Maven installed.

Clone the repo and run:
mvn install

Run commands
mvn clean test

This will open Chrome automatically and run all 3 test cases.

Results

- Screenshots are saved in the `screenshots` folder under each test case folder
- Pass/fail summary is written to `testResults.txt` after tests complete

Notes
- Tested on Windows 11 with Chrome
- Amazon may show CAPTCHA during testing which can affect results
- Internet connection is required
- No real purchases or accounts are created