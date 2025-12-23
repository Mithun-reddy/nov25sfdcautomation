package utils;

import java.time.Duration;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

	public static boolean waitForElementVisiblity(WebElement elementToWait, WebDriver driver) {
		boolean isElementVisible = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			wait.until(ExpectedConditions.visibilityOf(elementToWait));
			isElementVisible = true;
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
		
		return isElementVisible;
	}

	
	public static boolean waitForElementInVisiblity(WebElement elementToWait, WebDriver driver) {
		boolean isElementInVisible = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			wait.until(ExpectedConditions.invisibilityOf(elementToWait));
			isElementInVisible = true;
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
		
		return isElementInVisible;
	}
	
	public static boolean fluentlyWait(WebDriver driver, String xpath) {
		Wait<WebDriver> fwait = new FluentWait<WebDriver>(driver)
				.withTimeout(Duration.ofSeconds(45))
				.pollingEvery(Duration.ofMillis(100));
		
		WebElement ele = fwait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath(xpath));
			}
		});
		return ele.isDisplayed();
	}
}
