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
public class BeautySolutionVisitUITest {
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
  @DisplayName("US15(+): Participating on a Beauty Contest")
  public void testParticipateOnContestUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsOwner("owner1");
	
	driver.findElement(By.linkText("CONTESTS")).click();
	driver.findElement(By.id("beautyContestsTable")).findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).click();

	Integer previousNumber = driver.findElement(By.id("beautyContestParticipationsTable")).findElements(By.tagName("tr")).size();
    driver.findElement(By.linkText("Enter the contest")).click();
    
    driver.findElement(By.id("participationPhoto")).sendKeys("https://lizardowner.com/wp-content/uploads/animal-biology-blur-407037-1024x683.jpg");
    driver.findElement(By.xpath("//button[@type='submit']")).click();

	Integer newNumber = driver.findElement(By.id("beautyContestParticipationsTable")).findElements(By.tagName("tr")).size();
	assertThat(newNumber - 1).isEqualTo(previousNumber);
	assertThat(driver.findElement(By.id("beautyContestParticipationsTable")).findElements(By.tagName("tr")).get(previousNumber).findElements(By.tagName("td")).get(1).getText()).isEqualTo("owner1");
  }

  @Test
  @DisplayName("US15(-): Forbid participating on a Beauty Contest with no visits")
  public void testForbidParticipateOnContestWithNoVisitsUI() throws Exception {
	driver.get("http://localhost:" + port + "/");
	loginAsOwner("owner10");
	
	driver.findElement(By.linkText("CONTESTS")).click();
	driver.findElement(By.id("beautyContestsTable")).findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).click();
    driver.findElement(By.linkText("Enter the contest")).click();
    
    assertThat(driver.findElements(By.className("no-creation-resources"))).isNotEmpty();
    
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
