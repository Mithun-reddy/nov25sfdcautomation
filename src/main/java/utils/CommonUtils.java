package utils;

import java.io.File;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CommonUtils {
	
	/**
	 * 
	 * @param driver
	 * @return
	 */
	public static String getScreenshotOfPage(WebDriver driver) {
		String screenshotPath = System.getProperty("user.dir")+"/target/screenshots/"+System.currentTimeMillis()+".png";
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source =  ts.getScreenshotAs(OutputType.FILE);
		File dst = new File(screenshotPath);
		try {
			FileUtils.copyFile(source, dst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return screenshotPath;
	}
	
	public static String getScreenshotOfWebElement(WebDriver driver, WebElement element) {
		String screenshotPath = System.getProperty("user.dir")+"/target/screenshots/"+System.currentTimeMillis()+".png";
		File source =  element.getScreenshotAs(OutputType.FILE);
		File dst = new File(screenshotPath);
		try {
			FileUtils.copyFile(source, dst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return screenshotPath;
	}

	public static void mouseHover(WebDriver driver, WebElement element) {
		Actions action = new Actions(driver);
		action.moveToElement(element).perform();
	}
	
	public static void clickUsingJs(WebDriver driver, WebElement element ) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();", element);
	}
	
	public static void setAttributeValueOfElement(WebDriver driver, WebElement element, String attributeName, String value) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].setAttribute('"+attributeName+"', "+value+")", element);
	}
}
