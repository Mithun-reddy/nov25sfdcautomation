package tests;


import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;

import constants.FIleConstants;
import pages.LoginPage;
import pages.MyProfilePage;
import pages.UserMenu;
import utils.FileUtils;
import utils.ListenersUtil;
import utils.ReportUtils;

@Listeners(ListenersUtil.class)
public class MyProfilePageTest extends BaseTest {
	
	@Test
	public void myProfile_TC06() throws InterruptedException {
		WebDriver driver =  BaseTest.getBrowser();
		ExtentTest test = ReportUtils.getTest();
		driver.get(BaseTest.loginURLOauth);
		UserMenu um = new UserMenu(driver);
		String[] expectedUserMenuOptions= FileUtils.readPropertiesFile(FIleConstants.TEST_DATA_FILE_PATH, "user.menu.options").split(",");
		String[] actualUserMenuOptions = um.getUserMenuOptions().toArray(new String[0]);
		Assert.assertEquals(expectedUserMenuOptions, actualUserMenuOptions);
		MyProfilePage profile = um.selectMyProfile(driver);
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
