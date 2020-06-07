package org.springframework.samples.petclinic.ui;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class PromotionUITest {
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
  @DisplayName("US11(+): Setting up Promotions")
  public void testCreatePromotion() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsAdmin();
	
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	driver.findElement(By.xpath("//a[@href='/beauty-solution/2']")).click();
    driver.findElement(By.linkText("Create a promotion")).click();
    
    driver.findElement(By.id("discount")).sendKeys("35");
    driver.findElement(By.id("startDate")).sendKeys("2021/06/05 12:00:00");
    driver.findElement(By.id("endDate")).sendKeys("2021/06/07 12:00:00");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    List<WebElement> promotions = driver.findElement(By.id("promotionsTable")).findElements(By.tagName("tr"));
	assertThat(promotions.get(promotions.size() - 1).findElements(By.tagName("td")).get(0).getText()).isEqualTo("35%");
  }

  @Test
  @DisplayName("US11(-): Forbid creating promotions on the same time")
  public void testForbidCreatingPromotionOnSameTime() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsAdmin();
	
	driver.findElement(By.linkText("BEAUTY SOLUTIONS")).click();
	driver.findElement(By.xpath("//a[@href='/beauty-solution/3']")).click();
    driver.findElement(By.linkText("Create a promotion")).click();
    
    driver.findElement(By.id("discount")).sendKeys("35");
    driver.findElement(By.id("startDate")).sendKeys("2021/06/05 12:00:00");
    driver.findElement(By.id("endDate")).sendKeys("2021/06/07 12:00:00");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    driver.findElement(By.linkText("Create a promotion")).click();
    driver.findElement(By.id("discount")).sendKeys("35");
    driver.findElement(By.id("startDate")).sendKeys("2021/06/06 12:00:00");
    driver.findElement(By.id("endDate")).sendKeys("2021/06/08 12:00:00");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    
    assertThat(driver.findElements(By.className("error-message")).get(0).getText()).isEqualTo("The selected solution already has a promotion that overlaps its dates with the one you're trying to create");
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
