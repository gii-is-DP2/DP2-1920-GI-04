package org.springframework.samples.petclinic.ui;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class BeautyContestUITest {
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
  @DisplayName("US17(+): Choosing a Beauty Contest Winner")
  public void testSelectContestWinnerUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsAdmin();
	driver.findElement(By.linkText("CONTESTS")).click();
	WebElement contestElement = driver.findElement(By.xpath("//a[@href='/beauty-contest/49']"));
	assertThat(contestElement.findElement(By.xpath("./..")).findElement(By.xpath("./..")).findElements(By.tagName("td")).get(1).getText()).isEqualTo("Not decided yet");
	contestElement.click();
	
	assertThat(driver.findElements(By.className("participation-winner-image")).size()).isEqualTo(0);
    driver.findElement(By.linkText("Select as winner")).click();
	
	assertThat(driver.findElements(By.className("participation-winner-image")).size()).isEqualTo(1);
	driver.findElement(By.linkText("CONTESTS")).click();

	contestElement = driver.findElement(By.xpath("//a[@href='/beauty-contest/49']"));
	assertThat(contestElement.findElement(By.xpath("./..")).findElement(By.xpath("./..")).findElements(By.tagName("td")).get(1).getText()).isNotEqualTo("Not decided yet");
	
  }

  @Test
  @DisplayName("US17(-): Forbid choosing winner on current contest")
  public void testForbidSelectingWinnerForCurrentContestUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsAdmin();
	driver.findElement(By.linkText("CONTESTS")).click();
	WebElement contestElement = driver.findElement(By.id("beautyContestsTable")).findElements(By.tagName("td")).get(0).findElement(By.tagName("a"));
	assertThat(contestElement.findElement(By.xpath("./..")).findElement(By.xpath("./..")).findElements(By.tagName("td")).get(1).getText()).isEqualTo("Not decided yet");
	contestElement.click();
	
	assertThat(driver.findElements(By.className("participation-winner-image")).size()).isEqualTo(0);
    assertThat(driver.findElements(By.linkText("Select as winner")).size()).isEqualTo(0);
	
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
