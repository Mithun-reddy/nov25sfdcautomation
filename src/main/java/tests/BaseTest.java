package tests;

import java.lang.reflect.Method;
import java.time.Duration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import com.salesforce.oauth.SalesforceAuth;
import utils.ReportUtils;

public class BaseTest {
	
	public static String loginURLOauth = "";
	
	private static ThreadLocal<WebDriver> threadLocal = new ThreadLocal<WebDriver>();
	public static Logger logger = LogManager.getLogger(BaseTest.class);
	
	public void generateOAuthURLToLogin() {
		 BaseTest.loginURLOauth = BaseTest.getSfdcDirectLoginUrl();
		 logger.log(Level.INFO, "Login oauth url generated "+loginURLOauth);;
	}
	
	@BeforeMethod
	public void init(Method method) {
		BaseTest.setDriver("chrome", false);
	}
	
	@AfterMethod
	public void quitBrowserInstance() {
		BaseTest.getBrowser().quit();
	}
	
	
	public static void setDriver(String browserName, boolean headless) {
		WebDriver driver = BaseTest.getDriver(browserName, headless);
		if(driver == null) {
			throw new RuntimeException("Webdriver initialization failed");
		}
		threadLocal.set(driver);
	}
	
	public static WebDriver getBrowser() {
		return threadLocal.get();
	}
	

	public static WebDriver getDriver(String browserName, boolean isHeadless) {
		WebDriver driver = null;
		switch (browserName.toLowerCase()) {
		case "chrome":
			if(isHeadless) {
				ChromeOptions co = new ChromeOptions();
				co.addArguments("--headless");
				driver = new ChromeDriver(co);
				logger.log(Level.INFO, "Chrome driver object is created with headless mode");
			} else {
				driver = new ChromeDriver();
				logger.log(Level.INFO, "Chrome driver object is created  ");
			}
			break;

		case "safari":
			driver = new SafariDriver();
			break;

		case "firefox":
			driver = new FirefoxDriver();
			break;

		default:
			System.out.println("Allowed browsers are chrome, safari, firefox. Any other syntax fails to return driver");
			break;
		}
		if(driver!=null) {
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		}

		return driver;

	}
	
	public static String getSfdcDirectLoginUrl() {
		final String CONSUMER_KEY = "3MVG9gTv.DiE8cKRQpLRPb_WDDm.8goZAv.mGXgVl0nMuc04RrtsVlSN8EKvFn_m9hgmH7XWTw7XjfMgBUD5v";
	    final String CONSUMER_SECRET = "7FF4D19060EECA01241D292A744B6A07C39DEADA8ABA334E86FEC2CD899CBC2E";    
	    
		SalesforceAuth auth = new SalesforceAuth(CONSUMER_KEY, CONSUMER_SECRET, false);
		String url = auth.start();
		System.out.println(url);
		return url;
	}
	

}
