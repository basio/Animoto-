import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.Select;

public class VideosGenerator {
	public String url = "https://animoto.com/sign_in?return_to=%2Fcreate";
	public String p_url = "http://shoes.footsteps.it/parser.php";
	public String logo_url = "http://www.logoinn.net/wp-content/uploads/2010/03/Nike-Logo-300x300.jpg";
	public String images_url = "http://www.damesschoenen.nl/damesschoenen/sneakers/witte+merrell+sneaker+28408";
	public String audio_url = "http://static.animoto.com/images/quickstart_audio.mp3";
	public String header = "h";
	public String main_text = "t";
	public String design_id = "2";
	// public static String create_url = "http://animoto.com/create";
	// public static String project_url = "http://animoto.com/project/";
	public int minute = 60;

	protected FirefoxDriver driver;

	public String email = "gerwin@footsteps.nl";
	public String password = "smartersoftware";
	public String pojectName = "Project name1 ";

	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	public void Teardown() throws Exception {
		driver.close();
	}

	public void produce() {
		String current_urls = driver.getCurrentUrl();
		if (current_urls.contains("project")) {
			try {
				click("css=#produce-video > span.ui-button-text");
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	public void setStyle(String index) {
		String current_url = driver.getCurrentUrl();
		if (current_url.contains("project")) {
			try {

				waitForVisible("id=select-style");
				click("id=select-style");
				if (isElementPresent("//div[@id='style-picker-canvas']/ul/li["
						+ index + "]/div")) {
					click("//div[@id='style-picker-canvas']/ul/li[" + index
							+ "]/div");
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			} finally {
				click("//button[@type='button']");
			}
		}
	}

	public void addText() {
		String current_url = driver.getCurrentUrl();
		if (current_url.contains("project")) {
			try {
				waitForVisible("id=add-title");
				click("id=add-title");
				waitForVisible("id=firstLine");
				clear("id=firstLine");
				type("id=firstLine", header);
				waitForVisible("id=secondLine");
				clear("id=secondLine");
				type("id=secondLine", main_text);

				click("//button[@type='button']");
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	public void api_quickstart() {
		String current_url = driver.getCurrentUrl();
		if (current_url.contains("parser.php")) {
			try {
				clear("id=element_1");
				type("id=element_1", logo_url);
				clear("id=element_2");
				type("id=element_2", images_url);
				clear("id=element_3");
				type("id=element_3", audio_url);
				clear("id=element_4");
				type("id=element_4", "user");
				waitForVisible("id=saveForm");
				click("id=saveForm");

				waitForVisible("css=input[type=\"image\"]");
				click("css=input[type=\"image\"]");
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

	}

	public void editProjectDetails() {
		String current_url = driver.getCurrentUrl();
		if (current_url.contains("project")) {
			try {
				waitForVisible("css=span.ui-button-text");
				click("css=span.ui-button-text");
				waitForVisible("id=video-title");
				clear("id=video-title");
				type("id=video-title", pojectName);
				click("//button[@type='button']");
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	public void login() {
		String current_url = driver.getCurrentUrl();
		if (current_url.contains("sign_in")) {

			try {
				waitForVisible("id=password");
				type("id=password", password);
				waitForVisible("id=email");
				type("id=email", email);
				click("css=button.button");
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	public void gen() {
		String current_url = "";
		int rc = 0;
		try {

			setUp();
			open(p_url);

			api_quickstart();
			login();
			Thread.sleep(1000);
			current_url = driver.getCurrentUrl();

			while (current_url.contains("create") == false) {
				current_url = driver.getCurrentUrl();
				if (isElementPresent("id=link-skip"))
					break;
				Thread.sleep(100);
			}
			open(current_url);
			waitForVisible("id=link-skip");
			click("id=link-skip");

			current_url = driver.getCurrentUrl();
			while (current_url.contains("project") == false) {
				current_url = driver.getCurrentUrl();
				Thread.sleep(100);
			}
			editProjectDetails();
			Thread.sleep(100);
			setStyle(design_id);
			if (!(header.isEmpty() && main_text.isEmpty()))
				addText();
			produce();
			while (current_url.contains("play") == false) {
				current_url = driver.getCurrentUrl();
				Thread.sleep(100);
			}
			Thread.sleep(100);
			driver.quit();
			// gen.Teardown();
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
		}
	}

	public static void main(String[] args) {
		VideosGenerator gen = new VideosGenerator();
		gen.gen();
	}

	public String createTestFBUser() throws InterruptedException {

		open(url + "/admin/testfbusergen/all");
		for (int second = 0;; second++) {
			if (second >= minute)
				System.out.println("Could not find Test Userid!");
			try {
				if (isElementPresent("id=userid"))
					break;
			} catch (Exception e) {
				System.out.println("Could not find Test Userid");
			}
			Thread.sleep(1000);
		}

		String user_id = getText("id=userid");
		String fb_user_login_url = getText("loginurl");

		open(fb_user_login_url);

		if (!isElementPresent("//h1[@id='pageLogo']/a")) {
			open(fb_user_login_url);
		}

		if (!isElementPresent("//h1[@id='pageLogo']/a"))
			System.out.println("Could not create test FB user!");
		else {
			// Full_fb_user_name =
			// getText("//div[@id='pagelet_header_personal']/div/div[2]/h1/span");

			// fb_user_name = Full_fb_user_name.substring(0,
			// Full_fb_user_name.indexOf(' '))+Full_fb_user_name.substring(Full_fb_user_name.lastIndexOf(' '),Full_fb_user_name.length());
			// System.out.println("Test FB user created, ID = " +user_id +
			// ", Name is " + fb_user_name );
			return user_id;

		}
		return null;

	}

	public void loginTempFBUser() throws Exception {
		open(url + "/");
		waitForVisible("//img[@alt='Join now']");
		// click login
		click("//img[@alt='Join now']");

		waitForElement("//img[@alt='Login with Facebook']");
		click("//img[@alt='Login with Facebook']");

		isElementPresent("css=img[alt=Diveboard]");
		waitForElement("css=span.header_title");

		click("id=user_submit");

		System.out.println("WD will fail on FF7!");

		waitForVisible("//img[@alt='logout']");

		for (int second = 0;; second++) {
			if (second >= minute)
				System.out.println("Main page was not opened for new user");
			try {

				String first_user_name = "";// = fb_user_name.substring(0,
				// fb_user_name.indexOf(' '));

				if ((getText("//span[@class='header_title']")
						.startsWith(first_user_name))
						&& (getText("css=span.half_box_data").equals("0"))) // Dives
					// on
					// Diveboard:0
					break;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return;

			}
			Thread.sleep(1000);
		}

		{
			System.out.println("Test user logged in sucessful, url: "
					+ driver.getCurrentUrl());

		}

	}

	protected int waitForVisible(String element) throws InterruptedException {

		for (int second = 0;; second++) {
			if (second >= minute) {
				System.out.println("FAIL: Element " + element
						+ " was not found on page " + driver.getCurrentUrl());
				return 1;
			}
			try {
				if (isVisible(element))
					return 0;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			Thread.sleep(1000);

		}
	}

	protected void makeScreenshot(String pathname) throws IOException {
		File scrFile = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		// Needs Commons IO library
		FileUtils.copyFile(scrFile, new File(pathname));
	}

	protected void waitForElement(String element) throws InterruptedException {

		for (int second = 0;; second++) {
			if (second >= minute)
				System.out.println("FAIL: Element " + element
						+ " was not found on page " + driver.getCurrentUrl());
			try {
				if (isElementPresent(element))
					break;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			Thread.sleep(1000);

		}

	}

	protected void waitNoElement(String element) throws InterruptedException {

		for (int second = 0;; second++) {
			if (second >= minute)
				System.out.println("FAIL: Element " + element
						+ " is still on page " + driver.getCurrentUrl());
			try {
				if (!isElementPresent(element))
					break;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			Thread.sleep(1000);

		}

	}

	public void open(String url) {
		driver.get(url);
	}

	public String getLocation() {
		return driver.getCurrentUrl();

	}

	public void click(String element) {

		getElement(element).click();

	}

	public void clear(String element) {
		// clears textbox
		getElement(element).clear();

	}

	public void type(String element, String text) {
		getElement(element).sendKeys(text);

	}

	public String getText(String element) {

		return getElement(element).getText();

	}

	public void select(String element, String value) {
		Select select = new Select(getElement(element));
		if (value.startsWith("value="))
			select.selectByValue(value.replace("value=", ""));
		else if (value.startsWith("label="))
			select.selectByVisibleText(value.replace("label=", ""));
		else
			select.selectByVisibleText(value);

	}

	public void mouseOver(String element) {

		// build and perform the mouseOver with Advanced User Interactions API
		Actions builder = new Actions(driver);
		builder.moveToElement(getElement(element)).build().perform();

	}

	public void mouseDown(String element) {

		Locatable hoverItem = (Locatable) getElement(element);

		Mouse mouse = ((HasInputDevices) driver).getMouse();
		mouse.mouseMove(hoverItem.getCoordinates());

	}

	public void dragAndDrop(String element, int x, int y) {

		// build and perform the dragAndDropBy with Advanced User Interactions
		// API
		Actions builder = new Actions(driver);
		builder.dragAndDropBy(getElement(element), x, y).build().perform();

	}

	public void dragAndDropToObject(String element, String element1) {

		// build and perform the dragAndDropBy with Advanced User Interactions
		// API
		Actions builder = new Actions(driver);
		builder.dragAndDrop(getElement(element), getElement(element1)).build()
				.perform();

	}

	public void closeBrowser() {

		driver.close();
	}

	public boolean isVisible(String element) {
		try {
			boolean result = getElement(element).isDisplayed();
			return result;
		} catch (Exception e) {

			System.out.println(e.getMessage());
			return false;
		}

	}

	public boolean isElementPresent(String element) {
		if (element.startsWith("//")) {
			try {
				return (driver.findElements(By.xpath(element)).size() > 0) ? true
						: false;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
			// System.out.println(temp);
			// return ( temp > 0) ? true : false;
		} else if (element.startsWith("css=")) {
			element = element.substring(4, element.length());
			return (driver.findElements(By.cssSelector(element)).size() > 0) ? true
					: false;

		} else if (element.startsWith("id=")) {
			element = element.substring(3, element.length());
			return (driver.findElements(By.id(element)).size() > 0) ? true
					: false;
		}

		else if (element.startsWith("link=")) {
			element = element.substring(5, element.length());
			return (driver.findElements(By.linkText(element)).size() > 0) ? true
					: false;
		} else
			return (driver.findElements(By.id(element)).size() > 0) ? true
					: false;

	}

	WebElement getElement(String element) {

		if (element.startsWith("//"))
			return driver.findElement(By.xpath(element));

		else if (element.startsWith("css=")) {
			element = element.substring(4, element.length());
			return driver.findElement(By.cssSelector(element));
		} else if (element.startsWith("id=")) {
			element = element.substring(3, element.length());
			return driver.findElement(By.id(element));
		} else if (element.startsWith("plink=")) {
			element = element.substring(6, element.length());
			return driver.findElement(By.partialLinkText(element));
		} else if (element.startsWith("link=")) {
			element = element.substring(5, element.length());
			return driver.findElement(By.linkText(element));
		} else if (element.startsWith("name=")) {
			element = element.substring(5, element.length());
			return driver.findElement(By.name(element));
		}

		else
			return driver.findElement(By.id(element));

	}

}
