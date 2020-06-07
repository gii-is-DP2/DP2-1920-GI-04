package org.springframework.samples.petclinic.ui;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class BeautySolutionUITest {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  @LocalServerPort
  private int port;

  @BeforeEach
  public void setUp() throws Exception {
	//System.setProperty("webdriver.chrome.driver", System.getenv("webdriver.chrome.driver"));
	System.setProperty("webdriver.gecko.driver", System.getenv("webdriver.gecko.driver"));
    driver = new FirefoxDriver();
    baseUrl = "https://www.google.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  @DisplayName("US01(+): Create Beauty Solution")
  public void testCreateBeautySolutionUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
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
  @DisplayName("US01(-): Forbid create Beauty Solution with empty title")
  public void testForbidCreateBeautySolutionWithEmptyTitleUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsAdmin();
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	
	driver.findElement(By.linkText("Create new solution")).click();
	driver.findElement(By.id("title")).clear();
	driver.findElement(By.id("title")).sendKeys("");
	new Select(driver.findElement(By.id("type"))).selectByVisibleText("hamster");
	driver.findElement(By.xpath("//option[@value='hamster']")).click();
	driver.findElement(By.xpath("//option[@value='4']")).click();
	driver.findElement(By.id("price")).sendKeys("9898");
	driver.findElement(By.xpath("//button[@type='submit']")).click();

    assertThat(driver.findElements(By.className("has-error"))).isNotEmpty();
  }

  @Test
  @DisplayName("US02(+): List Beauty Solutions")
  public void testListBeautySolutionsUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	
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
  @DisplayName("US02(-): Create non enabled Beauty Solution and does not appear on list")
  public void testCreateAndListNonEnabledBeautySolutionUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
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
  @DisplayName("US03(+): Filter Beauty Solutions")
  public void testFilterBeautySolutionsUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	
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
  @DisplayName("US03(-): Create non enabled Beauty Solution and does not appear on filter")
  public void testCreateAndFilterNonEnabledBeautySolutionUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	selectFilter("hamster");
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
	selectFilter("hamster");
	Integer newNumber = countBeautySolutions();
	assertThat(newNumber).isEqualTo(ownerPreviousNumber);
  }

  
  @Test
  @DisplayName("US04(+): Edit Beauty Solution")
  public void testEditBeautySolutionUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsAdmin();
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();

    WebElement editedSolution = driver.findElement(By.xpath("//a[@href='/beauty-solution/2']")).findElement(By.xpath("./..")).findElement(By.xpath("./.."));
    Double oldPrice = Double.valueOf(editedSolution.findElements(By.tagName("td")).get(3).getText());
    editedSolution.findElement(By.tagName("a")).click();
    driver.findElement(By.linkText("Edit solution")).click();
    driver.findElement(By.id("price")).clear();
    driver.findElement(By.id("price")).sendKeys(String.valueOf(oldPrice + 1.0));
    driver.findElement(By.xpath("//button[@type='submit']")).click();
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();

    editedSolution = driver.findElement(By.xpath("//a[@href='/beauty-solution/2']")).findElement(By.xpath("./..")).findElement(By.xpath("./.."));
    assertThat(editedSolution.findElements(By.tagName("td")).get(2).getText()).isEqualTo("Mock1 Vet1");
	assertThat(Double.valueOf(editedSolution.findElements(By.tagName("td")).get(3).getText()))
	.isEqualTo(oldPrice + 1.0);
  }

  
  @Test
  @DisplayName("US04(-): Forbid edit Beauty Solution with empty title")
  public void testForbidEditBeautySolutionWithNoTitleUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsAdmin();
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();

    WebElement editedSolution = driver.findElement(By.xpath("//a[@href='/beauty-solution/2']")).findElement(By.xpath("./..")).findElement(By.xpath("./.."));
    editedSolution.findElement(By.tagName("a")).click();
    driver.findElement(By.linkText("Edit solution")).click();
    driver.findElement(By.id("title")).clear();
    driver.findElement(By.id("title")).sendKeys("");
    driver.findElement(By.xpath("//button[@type='submit']")).click();

    assertThat(driver.findElements(By.className("has-error"))).isNotEmpty();
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
