package tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;

import constants.FIleConstants;
import pages.HomePage;
import pages.LoginPage;
import pages.MyProfilePage;
import pages.UserMenu;
import utils.FileUtils;
import utils.GmailUtil;
import utils.ListenersUtil;
import utils.ReportUtils;

@Listeners(ListenersUtil.class)
public class MyProfilePageTest extends BaseTest {
	
	@Test
	public void myProfile_TC06() throws InterruptedException {
		WebDriver driver =  BaseTest.getBrowser();
		ExtentTest test = ReportUtils.getTest();
//		driver.get(BaseTest.loginURLOauth); 
		driver.get("https://login.salesforce.com");
		LoginPage lp = new LoginPage(driver);
		lp.enterUsername("jul22.mithun@ta.com");
		lp.enterPassword("November@2025");
		lp.rememberMeCheckbox.click();
		lp.clickLogin();
		
		String otp = null;
		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) < 30000) {
			otp = GmailUtil.getOTP();
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
		HomePage hp = lp.clickOnVerifyButton(driver); 
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userNavLabel")));

		UserMenu um = new UserMenu(driver);
		String[] expectedUserMenuOptions= FileUtils.readPropertiesFile(FIleConstants.TEST_DATA_FILE_PATH, "user.menu.options").split(",");
		String[] actualUserMenuOptions = um.getUserMenuOptions().toArray(new String[0]);
		Assert.assertEquals(expectedUserMenuOptions, actualUserMenuOptions);
		MyProfilePage profile = um.selectMyProfile(driver);
		test.info("Myprofile page is selected");
		Assert.assertTrue(profile.isProfilePageLoaded(driver), "Profile page should be loaded");
		profile.waitAndClickOnEditIcon(driver);
		Assert.assertTrue(profile.verifyEditProfilePopUpIsVisible(driver), "Edit profile pop up should be visible");
		Assert.assertTrue(profile.verifyChangeLastNameInAboutTab(driver, "Abcd"), "Lastname should be updated");
		String postMessage = FileUtils.readPropertiesFile(FIleConstants.TEST_DATA_FILE_PATH, "post.message");
		Assert.assertTrue(profile.verifyCreatePost(driver, postMessage));
		Assert.assertTrue(profile.verifyFileUpload(driver, FIleConstants.TEST_DATA_FILE_TO_UPLOAD));
		Assert.assertTrue(profile.verifyAddPhoto(driver, FIleConstants.PHOTO_UPLOAD_PATH));
	}

}
