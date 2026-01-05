package utils;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import tests.BaseTest;

public class ListenersUtil implements ITestListener {

	private ExtentReports reports;

	public void onStart(ITestContext context) {
		reports = ReportUtils.getInstance();
	}

	public void onFinish(ITestContext context) {
		ReportUtils.flushReport();
	}

	public void onTestStart(ITestResult result) {
		ExtentTest test = reports.createTest(result.getMethod().getMethodName());
		ReportUtils.setTest(test);
	}

	public void onTestSuccess(ITestResult result) {
		ReportUtils.getTest().pass(MarkupHelper.createLabel("Test Passed: " + result.getName(), ExtentColor.GREEN));
		ReportUtils.removeTest();
	}

	public void onTestFailure(ITestResult result) {
		ReportUtils.getTest().fail(MarkupHelper.createLabel("Test FAILED: " + result.getName(), ExtentColor.RED));
		ReportUtils.getTest().fail(result.getThrowable());
		try {
			Object testObject = result.getInstance();
			Class<?> clazz = testObject.getClass();
			if (BaseTest.class.isAssignableFrom(clazz)) {
				WebDriver driver = ((BaseTest) testObject).getBrowser();
				if (driver != null) {
					String screenshotPath = CommonUtils.getScreenshotOfPage(driver);
					ReportUtils.getTest().addScreenCaptureFromPath(screenshotPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ReportUtils.removeTest();
	}

	public void onTestSkip(ITestResult result) {
		ReportUtils.getTest().skip(MarkupHelper.createLabel("Test Skipped: " + result.getName(), ExtentColor.YELLOW));
		ReportUtils.removeTest();
	}

}