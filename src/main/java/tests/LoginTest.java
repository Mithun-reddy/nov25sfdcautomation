package tests;

import java.lang.reflect.Method;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import pages.HomePage;
import pages.LoginPage;
import utils.GmailUtil;
import utils.ListenersUtil;
import utils.ReportUtils;

@Listeners(ListenersUtil.class)
/**
 * All the login module test cases can be found here
 * Add the new
 * @author mithun, sasi
 */
public class LoginTest extends BaseTest {
	
	
	@Test
	public void loginErroMessage_TC001() throws InterruptedException {
		WebDriver driver =  BaseTest.getBrowser();
		ExtentTest test = ReportUtils.getTest();
		driver.get("https://login.salesforce.com");
		test.info("Login url launched in browser");
		LoginPage lp = new LoginPage(driver);
		lp.enterUsername("jul22.mithun@ta.com");
		lp.clearPassword();
		lp.clickLogin();
		test.info("Login button clicked");
		String actualErrorText = lp.getErrorMessage(driver);
		
		String expectedErrorText = "Error: Please enter your password.";
		Assert.assertEquals(actualErrorText, expectedErrorText);
	}

	@Test
	public void loginOTPLogin_TC002() throws InterruptedException {
		WebDriver driver =  BaseTest.getBrowser();
		ExtentTest test = ReportUtils.getTest();
		driver.get("https://login.salesforce.com");
		LoginPage lp = new LoginPage(driver);
		lp.enterUsername("jul22.mithun@ta.com");
		lp.enterPassword("November@2025");
		lp.rememberMeCheckbox.click();
		lp.clickLogin();
		
		String otp = null;
		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) < 30000) {
			otp = GmailUtil.getOTP("Verify your identity in Salesforce", "noreply@salesforce.com");
			if (otp != null) {
				break;
			}
			Thread.sleep(5000); // Poll every 5 seconds
		}

		if (otp == null) {
			Assert.fail("OTP not received within 30 seconds");
		}

		test.info("Retrieved otp "+ otp, null);
		lp.enterVerificationCode(otp);
//		String loginUrl = BaseT est.getSfdcDirectLoginUrl();
//		driver.get(loginUrl);
		HomePage hp = lp.clickOnVerifyButton(driver); 
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userNavLabel")));
	}
}