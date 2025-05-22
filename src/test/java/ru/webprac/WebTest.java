package ru.webprac;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebTest {
    private ChromeDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:8080/";
        driver = new ChromeDriver();
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1920, 1200));
        driver.get(baseUrl);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void TitlesTest() {
        driver.findElement(By.id("courses")).click();
        wait.until(ExpectedConditions.titleIs("Курсы"));

        driver.findElement(By.id("teachers")).click();
        wait.until(ExpectedConditions.titleIs("Преподаватели"));

        driver.findElement(By.id("timetable")).click();
        wait.until(ExpectedConditions.titleIs("Расписание"));

        driver.findElement(By.id("main_page")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl));
    }

    private void login_admin() {
        WebElement Button = driver.findElement(By.id("login"));
        Button.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginUsername")));
        assertEquals("Вход", driver.getTitle());

        driver.findElement(By.id("loginUsername")).sendKeys("admin");
        driver.findElement(By.id("loginPassword")).sendKeys("admin");
        driver.findElement(By.id("loginButton")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl));
        assertEquals("Учебный центр", driver.getTitle());
    }

    @Test
    public void LoginTest() {
        login_admin();
        driver.findElement(By.id("userName")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout")));
        driver.findElement(By.id("logout")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "logout"));
    }

    @Test
    public void ProfileTest() {
        login_admin();
        driver.findElement(By.id("userName")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profile")));
        driver.findElement(By.id("profile")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "profile"));
        driver.findElement(By.id("update")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "profile/update"));
        driver.findElement(By.id("profileFullName")).sendKeys("Admin");
        driver.findElement(By.id("profilePassword")).sendKeys("admin");
        driver.findElement(By.id("button")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "profile"));
    }

    @Test
    public void CourseTest() {
        login_admin();

        driver.findElement(By.id("courses")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "courses"));
        assertEquals("Курсы", driver.getTitle());

        driver.findElement(By.id("addCourse")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "courses/add"));
        assertEquals("Добавление курса", driver.getTitle());

        driver.findElement(By.id("courseName")).sendKeys("Тестовый курс");
        driver.findElement(By.id("courseDesc")).sendKeys("Тестовое описание");
        driver.findElement(By.id("addCourseButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStudent")));
        assertEquals("Тестовый курс", driver.getTitle());
        String url = driver.getCurrentUrl();

        driver.findElement(By.id("updateCourse")).click();
        wait.until(ExpectedConditions.urlToBe(url + "/update"));
        driver.findElement(By.id("courseDesc")).sendKeys("Обновленное тестовое описание");
        driver.findElement(By.id("updateButton")).click();
        wait.until(ExpectedConditions.urlToBe(url));
        assertEquals("Тестовый курс", driver.getTitle());

        driver.findElement(By.id("addStudent")).click();
        wait.until(ExpectedConditions.urlToBe(url + "/addStudent"));
        Select select = new Select(driver.findElement(By.id("studentLogin")));
        select.selectByIndex(1);
        driver.findElement(By.id("button")).click();
        wait.until(ExpectedConditions.urlToBe(url));
        assertEquals("Тестовый курс", driver.getTitle());

        driver.findElement(By.id("delStudent")).click();
        wait.until(ExpectedConditions.urlToBe(url + "/delStudent"));
        select = new Select(driver.findElement(By.id("studentLoginDel")));
        select.selectByIndex(1);
        driver.findElement(By.id("button")).click();
        wait.until(ExpectedConditions.urlToBe(url));
        assertEquals("Тестовый курс", driver.getTitle());

        driver.findElement(By.id("addTeacher")).click();
        wait.until(ExpectedConditions.urlToBe(url + "/addTeacher"));
        select = new Select(driver.findElement(By.id("teacherLogin")));
        select.selectByValue("m.zhukov");
        driver.findElement(By.id("button")).click();
        wait.until(ExpectedConditions.urlToBe(url));
        assertEquals("Тестовый курс", driver.getTitle());

        driver.findElement(By.id("delTeacher")).click();
        wait.until(ExpectedConditions.urlToBe(url + "/delTeacher"));
        select = new Select(driver.findElement(By.id("teacherLoginDel")));
        select.selectByValue("m.zhukov");
        driver.findElement(By.id("button")).click();
        wait.until(ExpectedConditions.urlToBe(url));
        assertEquals("Тестовый курс", driver.getTitle());

    }

    @Test
    public void TimetableTest() {
        driver.findElement(By.id("timetable")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ttForm")));
        Select select = new Select(driver.findElement(By.id("ttLogin")));
        select.selectByIndex(1);
        driver.findElement(By.id("button")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user")));
        assertEquals("Расписание пользователя", driver.getTitle());
    }

}
