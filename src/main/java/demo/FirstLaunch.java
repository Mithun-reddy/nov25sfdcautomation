package demo;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class FirstLaunch {
	
	@BeforeSuite
	public void initialSetup() {
		System.out.println("First one to run . . .");
	}
	
	@Test
	public void sample() {
		System.out.println("From first launch");
	}
	
	public static void main(String[] args) throws InterruptedException, AWTException {
		
//		ChromeOptions co = new ChromeOptions();
//		co.addArguments("--headless");
		
		WebDriver driver = new ChromeDriver();
		
		Actions action = new Actions(driver);
		
		driver.get("https://selenium-prd.firebaseapp.com/");
		
		driver.manage().window().maximize();
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		
		driver.findElement(By.id("email_field")).sendKeys("admin123@gmail.com");
		driver.findElement(By.id("password_field")).sendKeys("admin123");
		
		driver.findElement(By.xpath("//button[@onclick=\"login()\"]")).click();
		action.keyDown(Keys.ENTER).perform();
		WebElement home = driver.findElement(By.xpath("//a[text()='Home']"));
		home.click();
		System.out.println("Logged in successfully");
		
		// to handle keyboard and mouse events
//		Robot r = new Robot();
//		r.keyPress(KeyEvent.VK_ENTER);
//		r.delay(200);
//		r.keyRelease(KeyEvent.VK_ENTER);
		
//		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//		
//		Thread.sleep(3000);
//		
//		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[text()='Home']"))));
//		
//		
//		Thread.sleep(3000);
//		driver.findElement(By.xpath("//button[contains(text(),'Widget')]")).click();
//		
//		wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.xpath("//a[text()='Table']"))));
//		
//		driver.findElement(By.xpath("//a[text()='Table']")).click();
//		
//		Thread.sleep(3000);
//		
//		List<WebElement> tableHeader = driver.findElements(By.xpath("//table/tbody/tr[1]/th"));
//		
//		for(WebElement ele: tableHeader) {
//			System.out.print(ele.getText()+" ");
//		}
		
//		List<WebElement> tableData = driver.findElements(By.xpath("//table/tbody/tr/td/input"));
		
//		for(WebElement ele: tableData) {
//			String attValue = ele.getAttribute("value");
//			System.out.print(attValue+" ");
//		}
		
//		//table/tbody/tr[5]/td[2]/input
		
//		for (int i=2; i<=5; i++) {
//			String xpath = "//table/tbody/tr["+i+"]/td[2]/input";
//			String data = driver.findElement(By.xpath(xpath)).getAttribute("value");
//			System.out.println(data);
//		}
		
//		driver.findElement(By.xpath("//input[@value='female']")).click();
//		
//		WebElement cityDropDown = driver.findElement(By.id("city"));
//		
//		Select city = new Select(cityDropDown);
//		
//		city.selectByIndex(2);
//		
//		
//		driver.switchTo().alert().accept();
//		
//		WebElement checkbox = driver.findElement(By.id("rememberUn"));
//		
//		if(!checkbox.isEnabled()) {
//			checkbox.click();
//		}
		
		
		
//		driver.findElement(By.linkText("Home")).click();
//		
//		
//		driver.findElement(By.tagName("a")).click();
//		
//		driver.findElement(By.className("main-div")).click();
		
		
		
		//<textarea jsname="yZiJbe" class="gLFyf" aria-controls="Alh6id" aria-owns="Alh6id" autofocus="" title="Search" value="" aria-label="Search" placeholder="" aria-autocomplete="both" aria-expanded="false" aria-haspopup="false" autocapitalize="off" autocomplete="off" autocorrect="off" id="APjFqb" maxlength="2048" name="q" role="combobox" rows="1" spellcheck="false" data-ved="0ahUKEwjVuM3Vgp2RAxWeSmwGHfWBKFsQ39UDCA8"></textarea>
		
		// Locator strategies -> 8
		
		
//		Wait<WebDriver> fwait = new FluentWait<WebDriver>(driver)
//				.withTimeout(Duration.ofSeconds(20))
//				.pollingEvery(Duration.ofMillis(100))
//				.ignoring(NoSuchMethodException.class);
//		
//		WebElement ele = fwait.until(new Function<WebDriver, WebElement>() {
//
//			@Override
//			public WebElement apply(WebDriver driver) {
//				// TODO Auto-generated method stub
//				return driver.findElement(By.id(""));
//			}
//			
//		});

		
//		driver.quit();
		driver.close();
		
		
		
	}

}
