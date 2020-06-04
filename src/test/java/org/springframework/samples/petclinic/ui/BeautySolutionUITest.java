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

public class BeautySolutionUITest {
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
  public void testCreateBeautySolutionUI() throws Exception {
	driver.get("http://localhost:8080/");
	loginAsAdmin();
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	
	Integer previousNumber = countBeautySolutions();
	String solutionName = "ui test check " + previousNumber;
	
	driver.findElement(By.linkText("Create new solution")).click();
	driver.findElement(By.id("title")).clear();
	driver.findElement(By.id("title")).sendKeys(solutionName);
	new Select(driver.findElement(By.id("type"))).selectByVisibleText("hamster");
	driver.findElement(By.xpath("//option[@value='hamster']")).click();
	driver.findElement(By.xpath("//option[@value='4']")).click();
	driver.findElement(By.id("price")).clear();
	driver.findElement(By.id("price")).sendKeys("9898");
	driver.findElement(By.xpath("//button[@type='submit']")).click();
	assertEquals(solutionName, driver.findElement(By.xpath("//h2")).getText());
	
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	Integer newNumber = countBeautySolutions();
	assertThat(newNumber).isEqualTo(previousNumber + 1);
	
	assertEquals(solutionName, getListedBeautySolution(previousNumber).findElement(By.tagName("a")).getText());
  }

  @Test
  public void testCreateNonEnabledBeautySolutionUI() throws Exception {
	driver.get("http://localhost:8080/");
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	
	Integer ownerPreviousNumber = countBeautySolutions();
	
	loginAsAdmin();

	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	Integer previousNumber = countBeautySolutions();
	String solutionName = "ui test check " + previousNumber;
	
	driver.findElement(By.linkText("Create new solution")).click();
	driver.findElement(By.id("title")).clear();
	driver.findElement(By.id("title")).sendKeys(solutionName);
	new Select(driver.findElement(By.id("type"))).selectByVisibleText("hamster");
	driver.findElement(By.xpath("//option[@value='hamster']")).click();
	driver.findElement(By.xpath("//option[@value='4']")).click();
	driver.findElement(By.id("price")).clear();
	driver.findElement(By.id("price")).sendKeys("9898");
	driver.findElement(By.name("enabled")).click();
	driver.findElement(By.xpath("//button[@type='submit']")).click();
	assertEquals(solutionName, driver.findElement(By.xpath("//h2")).getText());
	
	logout();
	loginAsOwner("owner1");
	
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	Integer newNumber = countBeautySolutions();
	assertThat(newNumber).isEqualTo(ownerPreviousNumber);
  }


  @Test
  public void testListBeautySolutionsUI() throws Exception {
	driver.get("http://localhost:8080/");
	
	// Check that user sees some solutions listed
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	Integer ownerNumber = countBeautySolutions();
	assertThat(ownerNumber).isGreaterThan(0);
	
	loginAsAdmin();
	
	// Check that an admin sees more than the standard user (sees not enabled solutions)
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	Integer adminNumber = countBeautySolutions();
	assertThat(adminNumber).isGreaterThan(ownerNumber);
  }

  
  @Test
  public void testFilterBeautySolutionsUI() throws Exception {
	driver.get("http://localhost:8080/");
	
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	Integer standardNumber = countBeautySolutions();

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
    assertThat(countBeautySolutions()).isLessThanOrEqualTo(standardNumber);
	
  }
  
  @Test
  public void testEditBeautySolutionUI() throws Exception {
	driver.get("http://localhost:8080/");
	loginAsAdmin();
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();

    WebElement editedSolution = driver.findElement(By.linkText("Modern haircut")).findElement(By.xpath("./..")).findElement(By.xpath("./.."));
    Double oldPrice = Double.valueOf(editedSolution.findElements(By.tagName("td")).get(3).getText());
    editedSolution.findElement(By.tagName("a")).click();
    driver.findElement(By.linkText("Edit solution")).click();
    driver.findElement(By.id("price")).clear();
    driver.findElement(By.id("price")).sendKeys(String.valueOf(oldPrice + 1.0));
    driver.findElement(By.xpath("//button[@type='submit']")).click();
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();

    editedSolution = driver.findElement(By.linkText("Modern haircut")).findElement(By.xpath("./..")).findElement(By.xpath("./.."));
    assertThat(editedSolution.findElements(By.tagName("td")).get(2).getText()).isEqualTo("Mock8 Vet8");
	assertThat(Double.valueOf(editedSolution.findElements(By.tagName("td")).get(3).getText()))
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
  
  public Integer countBeautySolutions() {
	  return driver.findElement(By.tagName("tbody")).findElements(By.tagName("tr")).size();
  }
  
  public WebElement getListedBeautySolution(Integer index) {
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
