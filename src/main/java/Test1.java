import javaslang.control.Try;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.TestException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by ElenaK on 10.09.2017.
 */
public class Test1 {
    private WebDriver driver;
    private WebDriverWait wait;

    private final String URL = "http://www.sberbank.ru/ru/person";
    private final String SBERBANK_LOGO = "//img[@alt='Sberbank']";
    private final String NEW_REGION = "Нижегородская область";
    private final String CURRENT_REGION = "//div[@class='region-list']//span[@class='region-list__name']";

    @BeforeSuite
    public void beforeTests() {
        File file = new File("C:\\Users\\PC-юшка\\IdeaProjects\\lessons\\src\\main\\resources\\webdrivers\\IEDriverServer.exe");
        System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
        driver = new InternetExplorerDriver();
        wait = new WebDriverWait(driver, 40);
        driver.manage().deleteAllCookies();
        driver.get(URL);
        driver.manage().window().maximize();
        wait.ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SBERBANK_LOGO)));
    }


    @Test
    public void clickOnCurrentRegion() {
        Try.run(() -> driver.findElement(By.xpath(CURRENT_REGION)).click())
                .getOrElseThrow(() -> new TestException("Ошибка при попытке перейти к выбору региона ...\n"));
    }

    @Test(dependsOnMethods = {"clickOnCurrentRegion"})
    public void selectNewRegion() {
        Try.run(() -> {
                    wait.ignoring(NoSuchElementException.class)
                            .ignoring(StaleElementReferenceException.class)
                            .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Введите название региона']"))).sendKeys(NEW_REGION);
                    driver.findElement(By.xpath("//span[@class='region-search-box__option']//u")).click();
                }
        ).getOrElseThrow(() -> new TestException("Ошибка при выборе региона ...\n"));

    }

    @Test(dependsOnMethods = {"selectNewRegion"})
    public void checkNewRegion() {
        wait.ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SBERBANK_LOGO)));
        Assert.assertEquals("Текущий регион на странице отличается от ожидаемого - " + NEW_REGION, NEW_REGION, driver.findElement(By.xpath(CURRENT_REGION)).getText());
    }

    @Test(dependsOnMethods = {"checkNewRegion"})
    public void scrollToFooder(){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", driver.findElement(By.xpath("//div[contains(@class,'footer-information')]")));
        driver.findElements(By.xpath("//li[@class='social__item']")).forEach(e ->
            Assert.assertTrue("Ошибка при проверке видимости веб-элемента ...\n", e.isDisplayed())
        );

    }

    @AfterSuite
    public void afterTests() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
