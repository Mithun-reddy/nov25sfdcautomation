package demo;

import org.apache.poi.hpsf.Array;
import org.openqa.selenium.ElementClickInterceptedException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class TestngDemo {

	// _[A-Z][a-z][0-9] --> default priority

	
	@BeforeSuite
	public void initialSetup() {
		System.out.println("First one to run . . .");
	}
	
	@BeforeGroups
	public void groups() {
		System.out.println("Runs before a group");
	}
	
	@AfterSuite
	public void closeConfig() {
		System.out.println("last one to run . . .");
	}
	
	@BeforeMethod(onlyForGroups = "home", groups = "home", alwaysRun = true) 
	public void setup() {
		System.out.println("launching browser");
	}
	
	@AfterMethod
	public void tearDown() {
		System.out.println("Closing browser");
	}
	
	@BeforeClass()
	public void bCLass() {
		System.out.println("Confing .. . .");
	}
	
	@AfterClass(timeOut = 10000)
	public void aCLass() {
		System.out.println("closing confing .. . .");
	}
	
	@BeforeTest
	public void btest(){
		System.out.println("pre condition to test ");
	}
	
//	@Test(priority = 100, groups = {"login", "home"}, expectedExceptions = ElementClickInterceptedException.class)
	public void login() {
		System.out.println("Running demo method 2");
		throw new ArithmeticException("unbale to click login");
	}

//	@Test(priority = -100, groups = "login", enabled = true, dependsOnMethods = "login")
	public void home() {
		System.out.println("Running demo method 1");
	}

//	@Test(priority = 0, groups = "home")
	public void demo3() {
		System.out.println("Running demo method 4");
	}

//	@Test(priority = 1)
	public void demo2() {
		System.out.println("Running demo method 3");
	}

	@Test(priority = 50, invocationCount = 1, timeOut = 1000, dataProvider = "login data" )
	public void demo4(String username, String pass) throws InterruptedException {
//		soft
		SoftAssert sa = new SoftAssert();
		System.out.println("Running demo method 5");
		Thread.sleep(200);
		// hard
//		sa.assertAll();
		System.out.println(username+" "+pass);
		
	}
	
	@DataProvider(name = "login data")
	public Object[][] loginTestData(){
		return new Object[][] {
			{"mithun","12435"},
			{"harika","12345"},
			{"sasi","7890"},
			{"sdvcdbjsh","32456"}
		};
	}

}
