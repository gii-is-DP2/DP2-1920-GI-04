package org.springframework.samples.petclinic.ui;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class BeautyServiceUITest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @BeforeEach
  public void setUp() throws Exception {
	//String pathToGeckoDriver="C:\\Users\\ysjde\\Desktop\\Projects\\DP2"; 
	//System.setProperty("webdriver.gecko.driver", pathToGeckoDriver + "\\geckodriver.exe");
	//System.setProperty("webdriver.chrome.driver", System.getenv("webdriver.chrome.driver"));
	System.setProperty("webdriver.gecko.driver", System.getenv("webdriver.gecko.driver"));
    driver = new FirefoxDriver();
    baseUrl = "https://www.google.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testCreateBeautyServiceUI() throws Exception {
	driver.get("http://localhost:8080/");
	loginAsAdmin();
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	
	Integer previousNumber = countBeautyServices();
	String serviceName = "ui test check " + previousNumber;
	
	driver.findElement(By.linkText("Create new service")).click();
	driver.findElement(By.id("title")).clear();
	driver.findElement(By.id("title")).sendKeys(serviceName);
	new Select(driver.findElement(By.id("type"))).selectByVisibleText("hamster");
	driver.findElement(By.xpath("//option[@value='hamster']")).click();
	new Select(driver.findElement(By.id("vet"))).selectByVisibleText("Rafael");
	driver.findElement(By.xpath("//option[@value='4']")).click();
	driver.findElement(By.id("price")).clear();
	driver.findElement(By.id("price")).sendKeys("9898");
	driver.findElement(By.xpath("//button[@type='submit']")).click();
	assertEquals(serviceName, driver.findElement(By.xpath("//h2")).getText());
	
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	Integer newNumber = countBeautyServices();
	assertThat(newNumber).isEqualTo(previousNumber + 1);
	
	assertEquals(serviceName, getListedBeautyService(previousNumber).findElement(By.tagName("a")).getText());
  }

  @Test
  public void testCreateNonEnabledBeautyServiceUI() throws Exception {
	driver.get("http://localhost:8080/");
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	
	Integer ownerPreviousNumber = countBeautyServices();
	
	loginAsAdmin();

	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	Integer previousNumber = countBeautyServices();
	String serviceName = "ui test check " + previousNumber;
	
	driver.findElement(By.linkText("Create new service")).click();
	driver.findElement(By.id("title")).clear();
	driver.findElement(By.id("title")).sendKeys(serviceName);
	new Select(driver.findElement(By.id("type"))).selectByVisibleText("hamster");
	driver.findElement(By.xpath("//option[@value='hamster']")).click();
	new Select(driver.findElement(By.id("vet"))).selectByVisibleText("Rafael");
	driver.findElement(By.xpath("//option[@value='4']")).click();
	driver.findElement(By.id("price")).clear();
	driver.findElement(By.id("price")).sendKeys("9898");
	driver.findElement(By.name("enabled")).click();
	driver.findElement(By.xpath("//button[@type='submit']")).click();
	assertEquals(serviceName, driver.findElement(By.xpath("//h2")).getText());
	
	logout();
	loginAsOwner("owner1");
	
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	Integer newNumber = countBeautyServices();
	assertThat(newNumber).isEqualTo(ownerPreviousNumber);
  }


  @Test
  public void testListBeautyServicesUI() throws Exception {
	driver.get("http://localhost:8080/");
	
	// Check that user sees some services listed
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	Integer ownerNumber = countBeautyServices();
	assertThat(ownerNumber).isGreaterThan(0);
	
	loginAsAdmin();
	
	// Check that an admin sees more than the standard user (sees not enabled services)
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	Integer adminNumber = countBeautyServices();
	assertThat(adminNumber).isGreaterThan(ownerNumber);
  }

  
  @Test
  public void testFilterBeautyServicesUI() throws Exception {
	driver.get("http://localhost:8080/");
	
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();
	Integer standardNumber = countBeautyServices();

	List<WebElement> typeFilters = driver.findElement(By.id("filterText")).findElements(By.tagName("option"));
	
	typeFilters.remove(0);
	Integer random = (int) Math.floor(Math.random() * (typeFilters.size() - 1));
	
	String filter = typeFilters.get(random).getText();
	
	selectFilter(filter);

	// Check that every result of a random filter belongs to that filtered type
    assertThat(
		driver.findElement(By.tagName("tbody"))
		.findElements(By.tagName("tr"))
    ).allMatch(x ->  x.findElements(By.tagName("td")).get(1).getText().equals(filter));
    
    // Check that it's not bigger than the unfiltered results
    assertThat(countBeautyServices()).isLessThanOrEqualTo(standardNumber);
	
  }
  
  @Test
  public void testEditBeautyServiceUI() throws Exception {
	driver.get("http://localhost:8080/");
	loginAsAdmin();
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();

    WebElement editedService = driver.findElement(By.linkText("Modern haircut")).findElement(By.xpath("./..")).findElement(By.xpath("./.."));
    Double oldPrice = Double.valueOf(editedService.findElements(By.tagName("td")).get(3).getText());
    editedService.findElement(By.tagName("a")).click();
    driver.findElement(By.linkText("Edit service")).click();
    new Select(driver.findElement(By.id("vet"))).selectByVisibleText("Rafael");
    //driver.findElement(By.id("price")).click();
    driver.findElement(By.id("price")).clear();
    driver.findElement(By.id("price")).sendKeys(String.valueOf(oldPrice + 1.0));
    driver.findElement(By.xpath("//button[@type='submit']")).click();
	driver.findElement(By.linkText("BEAUTY SERVICES")).click();

    editedService = driver.findElement(By.linkText("Modern haircut")).findElement(By.xpath("./..")).findElement(By.xpath("./.."));
    assertThat(editedService.findElements(By.tagName("td")).get(2).getText().equals("Rafael Ortega"));
	assertThat(Double.valueOf(editedService.findElements(By.tagName("td")).get(3).getText()))
	.isEqualTo(oldPrice + 1.0);
  }
  
  // AUXILIARY METHODS

  private void loginAsAdmin() {
    driver.findElement(By.linkText("LOGIN")).click();
    driver.findElement(By.id("username")).clear();
    driver.findElement(By.id("username")).sendKeys("admin1");
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys("admin");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
  }
  
  private void loginAsOwner(String owner) {
    driver.findElement(By.linkText("LOGIN")).click();
    driver.findElement(By.id("username")).clear();
    driver.findElement(By.id("username")).sendKeys(owner);
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys("owner");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
  }
  
  private void logout() {
    driver.findElement(By.xpath("//div[@id='main-navbar']/ul[2]/li/a")).click();
    driver.findElement(By.linkText("Logout")).click();
    driver.findElement(By.xpath("//button[@type='submit']")).click();	  
  }
  
  public Integer countBeautyServices() {
	  return driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr")).size();
  }
  
  public WebElement getListedBeautyService(Integer index) {
	  return driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr")).get(index);
  }
  
  private void selectFilter(String filter) {
    driver.findElement(By.id("filterText")).click();
    new Select(driver.findElement(By.id("filterText"))).selectByVisibleText(filter);
    driver.findElement(By.xpath("//button[@onclick='filterByPetType()']")).click();	
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
