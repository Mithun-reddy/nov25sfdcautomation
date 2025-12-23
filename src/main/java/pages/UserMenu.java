package pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserMenu extends BasePage {
	
	public UserMenu(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(id = "userNavLabel")
	public WebElement userMenu;
	
	@FindBy(css = "[id=\"userNav-menuItems\"] > a")
	public List<WebElement> usermenuOptions;
	
	@FindBy(css = "[title=\"My Profile\"]")
	public WebElement myProfile;
	

	public ArrayList<String> getUserMenuOptions() {
		if((myProfile.isDisplayed())) {
			System.out.println("Do nothing");
		} else {
			userMenu.click();
		}
		ArrayList<String> menuOptions = new ArrayList<String>();
		for(WebElement element: usermenuOptions) {
			menuOptions.add(element.getText());
		}
		return menuOptions;
	}
	
	public MyProfilePage selectMyProfile(WebDriver driver) {
		if(!(myProfile.isDisplayed())) {
			userMenu.click();
			myProfile.click();
		} else {
			myProfile.click();
		}
		return new MyProfilePage(driver);
	}

}
