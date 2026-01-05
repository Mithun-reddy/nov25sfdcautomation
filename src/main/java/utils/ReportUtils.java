package utils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import constants.FIleConstants;

public class ReportUtils {
	
	private static ExtentReports reports = null;
	private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();
	
	public static ExtentReports getInstance() {
		if(reports == null) {
			synchronized (ReportUtils.class) {
				reports = createInstance();
			}
		} else {
			System.out.println("Returning the existing instance");
		}
		
		return reports;
	}
	
	private static ExtentReports createInstance() {
		reports = new ExtentReports();
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(FIleConstants.REPORT_PATH);
		reports.attachReporter(sparkReporter);
		return reports;
	}
	
	public static ExtentTest getTest() {
		return extentTest.get();
	}
	
	public static void setTest(ExtentTest test) {
		extentTest.set(test);
	}
	
	public static void removeTest() {
		extentTest.remove();
	}
	
	public static void flushReport() {
		if(reports != null) {
			reports.flush();
		}
	}
	
}