/**
 * 
 */
package com.deliverit.absgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author mapo
 *
 */
public class PullJira {

	ChromeDriver driver;
	BufferedReader reader;
	private int nrLinhas;

	private static final String DIA_MES_ANO_HORA = "dd/MM/yyyy HH:mm";
	private SimpleDateFormat sdf = new SimpleDateFormat(DIA_MES_ANO_HORA);

	private static final String EMAIL_SELECTOR = "#login > input:nth-child(2)";
	private static final String PWD_SELECTOR = "#login > input:nth-child(3)";
	private static final String LOGIN_BTN_SELECTOR = "#login > button";
	private static final String TIMESHEET_URL_XPATH = "/html/body/div[1]/div/div[2]/ul[1]/li[3]/a";
	private static final String ADICIONAR_SELECTOR = "#content > div > div:nth-child(5) > div > button";
	private static final String DATE_ENTRY_SELECTOR = "#content > div > div.row.table-row > form > div:nth-child(1) > div > input";
	private static final String START_TIME_SELECTOR = "#content > div > div.row.table-row > form > div:nth-child(2) > div > input";
	private static final String END_TIME_SELECTOR = "#content > div > div.row.table-row > form > div:nth-child(3) > div > input";

	// Deve selecionar projeto por Value "Flux IT" -> <select style="width:
	// 20%;"><option value="1">Deliver IT</option><option
	// value="18">Dootax</option></select>
	private static final String PROJETO_SELECTOR = "#content > div > div.row.table-row > form > div.col-md-6 > div > div > select:nth-child(1)";
	private static final String PROJETO_DOOTAX = "Dootax";

	// Deve selecionar em função do semestre corrente -> <select style="width:
	// 20%;"><option value="310">2019/1 - Baseline</option></select>
	private static final String FASE_SELECTOR = "#content > div > div.row.table-row > form > div.col-md-6 > div > div > select:nth-child(2)";
	private static final String FASE_CORRENTE = "2019/1 - Baseline";

	// Deve selecionar de acordo com o tipo de card (Issue Key: DOON, BB8 ou DLC)
	// By.Text: DSV - DOON (aka value 7069), DSV - BB8 (aka value 7060) ou DSV - DLC
	// (ainda sem entrada)
	private static final String ISSUE_KEY_SELECTOR = "#content > div > div.row.table-row > form > div.col-md-6 > div > div > select:nth-child(3)";
	private static final String ISSUE_CERIMONIAS = "Reunião - [Sprints] DOON-1089 Cerimônias"; // Reunião - [Sprints] DOON-1089 Cerimônias
	private static final String ISSUE_DSV_DOON = "DSV - DOON";
	private static final String ISSUE_DSV_BB8 = "DSV - BB8";

	private static final String PERCENTUAL_SELECTOR = "#content > div > div.row.table-row > form > div:nth-child(6) > div > input";
	private static final String PERCENTUAL_DEFAULT = "10";

	private static final String SALVAR_SELECTOR = "#content > div > div.row.table-row > div > button.fake-btn.text-success";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Estão faltando os argumentos.");
			System.err.println("Uso: java -jar PullJira.jar <emailId> <password> <inputFile>");
			System.exit(1);
		}
		if (!new File(args[2]).exists()) {
		    System.err.println("Arquivo de entrada inexistente.");
		    System.exit(1);
		}

		PullJira me = new PullJira();
		me.execute(args[0], args[1], args[2]);
	}

	private void execute(String emailId, String pwd, String filePath) {
		driver = new ChromeDriver();
		driver.get("http://gp.absoluta.net/login");
		sendKeysByJsScript(EMAIL_SELECTOR, emailId);
		sendKeysByJsScript(PWD_SELECTOR, pwd);
		driver.findElementByCssSelector(LOGIN_BTN_SELECTOR).click();
		driver.findElementByXPath(TIMESHEET_URL_XPATH).click();

		try {
	        WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(ADICIONAR_SELECTOR)));
			processInputFile(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch(WebDriverException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// Nothing to do
				}
			}
			driver.quit();
		}
        System.out.printf("Importadas %d linhas.%n", nrLinhas);
	}

	private void processInputFile(String filePath) throws IOException {
		reader = new BufferedReader(new FileReader(filePath));
		String line;
		reader.readLine(); // Pula o cabeçalho
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("DOON-") || line.startsWith("BB8-") || line.startsWith("DLC-")) {
				try {
					insertLine(line);
					nrLinhas++;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.err.println("Linha não reconhecida: " + line);
			}
		}
	}

	private void insertLine(String line) throws ParseException {
		String issueId = null;
		String[] data = line.split(",");
		if ("DOON-1089".equalsIgnoreCase(data[0])) {
			issueId = ISSUE_CERIMONIAS;
		} else if (data[0].startsWith("DOON-")) {
			issueId = ISSUE_DSV_DOON;
		} else if (data[0].startsWith("BB8-")) {
			issueId = ISSUE_DSV_BB8;
		}
		driver.findElementByCssSelector(ADICIONAR_SELECTOR).click();

		sendKeysByJsScript(DATE_ENTRY_SELECTOR, data[1].substring(0, 10));
        dispatchJsEvent(DATE_ENTRY_SELECTOR, "change");

		driver.findElementByCssSelector(START_TIME_SELECTOR).click();
		sendKeysByJsScript(START_TIME_SELECTOR, String.format("%04d", Integer.parseInt(data[1].substring(11).replace(":", ""))));
        dispatchJsEvent(START_TIME_SELECTOR, "change");

        driver.findElementByCssSelector(END_TIME_SELECTOR).click();
		//sendKeysByJsScript(END_TIME_SELECTOR, String.format("%04d", Integer.parseInt(calculaTermino(data[1], data[2]).replace(":", ""))));
        sendKeysByJsScript(END_TIME_SELECTOR, calculaTermino(data[1], data[2]));
		dispatchJsEvent(END_TIME_SELECTOR, "change");

		if (!selectOption(PROJETO_SELECTOR, PROJETO_DOOTAX)) {
			System.err.println("Erro ao selecionar o projeto " + PROJETO_DOOTAX);
			return;
		}
		if (!selectOption(FASE_SELECTOR, FASE_CORRENTE)) {
			System.err.println("Erro ao selecionar a fase " + FASE_CORRENTE);
			return;
		}
		if (!selectOption(ISSUE_KEY_SELECTOR, issueId)) {
			System.err.println("Erro ao selecionar a tarefa " + issueId);
			return;
		}
		sendKeysByJsScript(PERCENTUAL_SELECTOR, PERCENTUAL_DEFAULT);
		driver.findElementByCssSelector(SALVAR_SELECTOR).click();

		if (hasErrors()) {
		    System.err.printf("Erro ao inserir a linha %s%n", line);
		    System.err.println("\t\tPrecisará inserir manualmente.");
		}
	}

	private <T> void sendKeysByJsScript(String jsSelector, T value) {
		driver.executeScript("document.querySelector(arguments[0]).value = arguments[1];", jsSelector, value);
	}

	private boolean selectOption(String selectId, String value) {
		Select selection = new Select(driver.findElementByCssSelector(selectId));
		List<WebElement> selectionOptions = selection.getOptions();
		boolean found = false;
		for (WebElement selectioOption : selectionOptions) {
			if (selectioOption.getText().equalsIgnoreCase(value)) {
				selection.selectByValue(selectioOption.getAttribute("value"));
				found = true;
				break;
			}
		}
		return found;
	}
	
	private String calculaTermino(String inicio, String duracao) throws ParseException {
		Date date = sdf.parse(inicio);
		LocalDateTime someTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		Long seconds = Long.parseLong(duracao);
		someTime = someTime.plus(seconds, ChronoUnit.SECONDS);
		return String.format("%02d", someTime.getHour()) + ":" + String.format("%02d", someTime.getMinute());
	}

	private void dispatchJsEvent(String selector, String event) {
	    WebElement element = driver.findElementByCssSelector(selector);
	    driver.executeScript("arguments[0].dispatchEvent(new Event(arguments[1]));", element, event);
	}

	private boolean hasErrors() {
	    boolean result = false;
	    WebDriverWait wait = new WebDriverWait(driver, 2);
	    try {
	        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("has-error")));
	        result = true;
	    } catch(WebDriverException e) {
	        // Just leave result false
	    }
	    return result;
	}
}
