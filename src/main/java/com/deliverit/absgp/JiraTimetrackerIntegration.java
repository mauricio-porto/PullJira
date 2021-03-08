package com.deliverit.absgp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

public class JiraTimetrackerIntegration {

    public static List<JiraData> consume(String username, String password, String initialDate, String finalDate) {
        ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");

        ChromeDriver driver = new ChromeDriver(options);
        List<JiraData> data = new ArrayList<>();
        try {
            driver.get("http://tools.dootax.com.br:8080/jira/login.jsp");
            driver.findElementById("login-form-username").sendKeys(username);
            driver.findElementById("login-form-password").sendKeys(password);
            driver.findElementById("login-form-submit").click();
            checkErrors(driver);
            driver.get("http://tools.dootax.com.br:8080/jira/secure/ReportingWebAction!default.jspa");
            driver.findElementById("dateFrom").clear();
            driver.findElementById("dateFrom").sendKeys(initialDate);
            driver.findElementById("dateTo").clear();
            driver.findElementById("dateTo").sendKeys(finalDate);
            driver.findElementById("create-report-button").click();

            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.elementToBeClickable(By.id("create-report-button")));

            WebElement table = driver.findElementById("worklogDetailsTable");
            table.findElements(By.xpath("./tbody/tr")).forEach(element -> {
                String issueKey = element.findElement(By.cssSelector("td[class='jtrp_col_issueKey']")).getText();
                String startTime = element.findElement(By.cssSelector("td[class='jtrp_col_startTime']")).getText();
                String timeSpent = element.findElement(By.cssSelector("td[class='jtrp_col_timeSpent']")).getText();

                String timeWithout = timeSpent.replaceAll("[m|h]", "");
                String[] time = timeWithout.split(" ");
                int timeSpentSeconds = 0;
                if(time.length > 1) {
                    timeSpentSeconds += Integer.parseInt(time[0]) * 60 * 60;
                    timeSpentSeconds += Integer.parseInt(time[1]) * 60;
                } else {
                    timeSpentSeconds += Integer.parseInt(time[0]) * 60;
                }
                data.add(new JiraData(timeSpentSeconds,issueKey, LocalDateTime.parse(startTime, getFormatter())));
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return data;
    }

    private static void checkErrors(ChromeDriver driver) throws RuntimeException {
        driver.findElementsByCssSelector("div[class='aui-message error'").stream().findFirst().ifPresent(element -> {
            throw new RuntimeException(element.getText());
        });
    }

    private static DateTimeFormatter getFormatter() {
        return new DateTimeFormatterBuilder().appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")).appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm")).toFormatter();
    }

}
