package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_HomeView extends PO_NavView {

	static public void checkWelcome(WebDriver driver) {
		SeleniumUtils.EsperaCargaPagina(driver, "text", "¡Bienvenido a SocialNetwork!", getTimeout());
	}
	
	public static void findUser(WebDriver driver, String search) {
		findUser(driver, search, true);
	}

	public static void findUser(WebDriver driver, String search, boolean click) {
		WebElement field = (new WebDriverWait(driver, timeout))
				.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));

		field.click();
		field.clear();
		field.sendKeys(search);

		if (click) {
			By boton = By.id("search-bt");
			driver.findElement(boton).click();
		}
	}
}
