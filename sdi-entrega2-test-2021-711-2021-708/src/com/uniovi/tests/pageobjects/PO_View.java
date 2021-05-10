package com.uniovi.tests.pageobjects;

import static org.junit.Assert.*;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_View {

	protected static int timeout = 10;

	public static int getTimeout() {
		return timeout;
	}

	public static void setTimeout(int timeout) {
		PO_View.timeout = timeout;
	}

	/**
	 * Espera por la visibilidad de un texto correspondiente a la propiedad key en
	 * el idioma locale en la vista actualmente cargandose en driver..
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param key: clave del archivo de propiedades.
	 * @param locale: Retorna el índice correspondient al idioma. 0 p.SPANISH y 1
	 *        p.ENGLISH.
	 * @return Se retornará la lista de elementos resultantes de la búsqueda.
	 */
	static public List<WebElement> check(WebDriver driver, String message) {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", message, getTimeout());
		return elementos;
	}

	static public void checkNoMsg(WebDriver driver, String message) {
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, message, getTimeout());
	}

	/**
	 * Espera por la visibilidad de un elemento/s en la vista actualmente cargandose
	 * en driver..
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param type:
	 * @param text:
	 * @return Se retornará la lista de elementos resultantes de la búsqueda.
	 */
	static public List<WebElement> checkElement(WebDriver driver, String type, String text) {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, type, text, getTimeout());
		return elementos;
	}

	static public void login(WebDriver driver, String email) {
		login(driver, email, "awd");
	}

	static public void login(WebDriver driver, String email, String pwd) {
		login(driver, email, pwd, "Usuarios", true);
	}

	static public void login(WebDriver driver, String email, String pwd, String msg, boolean section) {
		if (section)
			PO_HomeView.clickOption(driver, "login", "id", "login-bt");
		PO_LoginView.fillForm(driver, email, pwd);
		if (msg != null)
			PO_View.check(driver, msg);
	}

	public static List<WebElement> checkElement(WebDriver driver, String xpath, int index) {
		List<WebElement> elementos = checkElement(driver, "free", xpath);
		elementos.get(index).click();
		return elementos;
	}

	public static List<WebElement> checkElement(WebDriver driver, String xpath) {
		List<WebElement> elementos = checkElement(driver, "free", xpath);
		return elementos;
	}

	public static void click(WebDriver driver, String criterio, String textOption, String criterio2,
			String textoDestino) {
		click(driver, criterio, textOption);

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, criterio2, textoDestino, getTimeout());
		assertTrue(elementos.size() == 1);
	}

	public static void click(WebDriver driver, String criterio, String textOption) {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, criterio, textOption, getTimeout());
		assertEquals(1, elementos.size());
		elementos.get(0).click();
	}

	public static List<WebElement> checkTableSize(WebDriver driver, int expected) {
		List<WebElement> elementos = (new WebDriverWait(driver, timeout))
				.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//tbody/tr"), expected));
		assertTrue(elementos.size() == expected);

		return elementos;
	}

	public static void checkNoTableElements(WebDriver driver) {
		List<WebElement> elementos = (new WebDriverWait(driver, timeout))
				.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//tbody/tr"), 0));
		assertTrue(elementos.isEmpty());
	}
}
