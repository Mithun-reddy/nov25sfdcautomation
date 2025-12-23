package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class BasePage {

	public BasePage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
	
	public void enterText(WebElement ele, String textValue) {
		
		ele.sendKeys(textValue);
	}

}
