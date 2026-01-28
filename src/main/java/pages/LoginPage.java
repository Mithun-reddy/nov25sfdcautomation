package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import constants.FIleConstants;
import utils.FileUtils;
import utils.GmailUtil;
import utils.WaitUtils;

public class LoginPage extends BasePage {

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id = "username")
	public WebElement username;

	@FindBy(id = "password")
	public WebElement password;

	@FindBy(id = "Login")
	public WebElement loginButton;

	@FindBy(id = "error")
	public WebElement errorMessage;

	@FindBy(id = "rememberUn")
	public WebElement rememberMeCheckbox;

	@FindBy(xpath = "//*[@id='forgot_password_link']")
	public WebElement forgotPassword;
	
	@FindBy(xpath = "//input[@name='emc']")
	public WebElement verificationCodeInput;
	
	@FindBy(css = "[name='save']")
	public WebElement verifyButton;

	public boolean isLoginPageVisible() {
		return this.loginButton.isDisplayed();
	}
	
	public HomePage loginToApp(WebDriver driver) throws InterruptedException {
		enterUsername(FileUtils.readPropertiesFile(FIleConstants.TEST_DATA_FILE_PATH, "username"));
		enterPassword(FileUtils.readPropertiesFile(FIleConstants.TEST_DATA_FILE_PATH, "password"));
		rememberMeCheckbox.click();
		clickLogin();
		Thread.sleep(10000);
		String otp = GmailUtil.getOTP();
		enterVerificationCode(otp);
		clickOnVerifyButton(driver);
		return new HomePage(driver);
	}

	public void enterUsername(String userId) {
		if (username.isDisplayed()) {
			username.sendKeys(userId);
		} else {
			System.out.println("Username element is not displayed");
		}
	}
	
	public void clickForgotPass(String userId) {
		if (forgotPassword.isDisplayed()) {
			forgotPassword.click();
		} else {
			System.out.println("Username element is not displayed");
		}
	}

	public void clearPassword() {
		password.clear();
	}

	/**
	 * Can be called to enter password
	 * @param pass
	 */
	public void enterPassword(String pass) {
		password.sendKeys(pass);
	}

	public void clickLogin() {
		loginButton.click();
	}
	
	public void enterVerificationCode(String otp) {
		if (verificationCodeInput.isDisplayed()) {
			verificationCodeInput.sendKeys(otp);
		} else {
			System.out.println("Verification code screen not visible");
		}
	}

	public HomePage clickOnVerifyButton(WebDriver driver) {
		verifyButton.click();
		return new HomePage(driver);
	}
	/**
	 * This method will get login error text
	 * 
	 * @param driver, WebDriver instance
	 * @return {String} error text if found else null
	 * @throws InterruptedException
	 */
	public String getErrorMessage(WebDriver driver) throws InterruptedException {
		if (WaitUtils.waitForElementVisiblity(errorMessage, driver)) {
			return errorMessage.getText();
			
		} else {
			return null;
		}
	}
}
