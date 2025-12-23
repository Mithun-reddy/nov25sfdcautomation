package utils;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import tests.BaseTest;

public class ListenersUtil implements ITestListener {
	
	public void onTestStart(ITestResult result) {
		ExtentReports report = ReportUtils.getInstance();
		ExtentTest test = report.createTest(result.getMethod().getMethodName());
		ReportUtils.setTest(test);
	}

	public void onTestSuccess(ITestResult result) {
		ReportUtils.getTest().pass("Test Passed "+result.getName());
	}

	public void onTestFailure(ITestResult result) {
//		WebDriver driver = BaseTest.getBrowser();
//		ReportUtils.getTest().addScreenCaptureFromPath(CommonUtils.getScreenshotOfPage(driver));
		ReportUtils.getTest().fail("Test FAILED "+result.getName());
	}

	public void onTestSkip(ITestResult result) {
		ReportUtils.getTest().skip("Test Skipped "+result.getName());
	}
	
	
	
	

}
