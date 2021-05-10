package com.uniovi.tests;

//Paquetes Java
import java.util.List;
//Paquetes JUnit 
import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertTrue;
//Paquetes Selenium 
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.*;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SdiEntrega2Tests {
	// En Windows (Debe ser la versión 65.0.1 y desactivar las actualizacioens
	// automáticas)):
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	// static String Geckdriver024 = "C:\\Path\\geckodriver024win64.exe";
	// En MACOSX (Debe ser la versión 65.0.1 y desactivar las actualizacioens
	// automáticas):
	// static String PathFirefox65 = "/Applications/Firefox
	// 2.app/Contents/MacOS/firefox-bin";
	// static String PathFirefox64 =
	// "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
	static String Geckdriver024 = "/Users/delacal/Documents/SDI1718/firefox/geckodriver024mac";
	// static String Geckdriver022 =
	// "/Users/delacal/Documents/SDI1718/firefox/geckodriver023mac";
	// Común a Windows y a MACOSX
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "https://localhost:8081";

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}

	@Before
	public void setUp() {
		driver.navigate().to(URL);
	}

	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	@BeforeClass
	static public void begin() {
		// COnfiguramos las pruebas.
		// Fijamos el timeout en cada opción de carga de una vista. 2 segundos.
		PO_View.setTimeout(3);

	}

	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// PR01. Registro usuario, datos validos /
	@Test
	public void PR01() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "pedro@gmail.com", "Pedro", "Garcia", "123456", "123456");
		// Comprobamos que entramos en la secci�n privada
		PO_View.checkElement(driver, "text", "pedro@gmail.com");
	}

	/*
	 * Registro de Usuario con datos inv�lidos (email vac�o, nombre vac�o, apellidos
	 * vac�os)
	 */
	@Test
	public void PR02() {
		PO_HomeView.clickOption(driver, "signup", "id", "signup-bt");
		PO_RegisterView.fillForm(driver, "", "", "", "123456", "123456");
		SeleniumUtils.EsperaCargaPagina(driver, "id", "signup-bt", PO_View.getTimeout());
	}

	/*
	 * Registro de Usuario con datos inv�lidos (email existente).
	 */
	@Test
	public void PR03() {
		PO_HomeView.clickOption(driver, "signup", "id", "signup-bt");
		PO_RegisterView.fillForm(driver, "jose@gmail.com", "Jose", "Martinez", "123456", "123456");
		PO_View.check(driver, "Las contrase�as no coinciden");
	}

	/*
	 * Registro de Usuario con datos inv�lidos (email existente).
	 */
	@Test
	public void PR04() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "pedro@gmail.com", "Pedro", "Garcia", "123456", "123456");
		// Comprobamos que entramos en la secci�n privada
		PO_View.check(driver, "Este email ya est� registrado. Intentelo de nuevo");
	}

	/*
	 * Inicio de sesi�n con datos v�lidos (usuario est�ndar).
	 */
	@Test
	public void PR05() {
		PO_View.login(driver, "pedro@gmail.com");
	}

	/*
	 * Inicio de sesi�n con datos inv�lidos (usuario est�ndar, campo email y
	 * contrase�a vac�os).
	 */
	@Test
	public void PR06() {
		PO_View.login(driver, "", "", null, true);
		SeleniumUtils.EsperaCargaPagina(driver, "id", "login-bt", PO_View.getTimeout());
	}

	/*
	 * Inicio de sesi�n con datos v�lidos (usuario est�ndar, email existente, pero
	 * contrase�a incorrecta).
	 */
	@Test
	public void PR07() {
		PO_View.login(driver, "pedro@gmail.com", "comofue", "Email o password incorrectos", true);
	}

	/*
	 * Inicio de sesi�n con datos inv�lidos (usuario est�ndar, email no existente y
	 * contrase�a no vac�a).
	 */
	@Test
	public void PR08() {
		PO_View.login(driver, "comofue@gmail.com", "comofue", "Email o password incorrectos", true);
	}

	/*
	 * Hacer click en la opci�n de salir de sesi�n y comprobar que se redirige a la
	 * p�gina de inicio de sesi�n (Login).
	 */
	@Test
	public void PR09() {
		// Vamos al formulario de logueo.
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "admin@email.es", "123456");
		// COmprobamos que entramos en la pagina privada de usuario
		PO_View.checkElement(driver, "text", "Gesti�n de ofertas");
		// Ahora nos desconectamos
		PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
		// PO_PrivateView.logOut(driver, "Desconectar");
		//PO_View.checkElement(driver, "text", "Identif�cate");
		SeleniumUtils.EsperaCargaPagina(driver, "id", "login-bt", PO_View.getTimeout());
	}

	/*
	 * Comprobar que el bot�n cerrar sesi�n no est� visible si el usuario no est�
	 * autenticado.
	 */
	@Test
	public void PR10() {
		PO_View.checkNoMsg(driver, "Desconectar");
	}

	// PR11. Sin hacer /
	@Test
	public void PR11() {
		assertTrue("PR11 sin hacer", false);
	}

	// PR12. Sin hacer /
	@Test
	public void PR12() {
		assertTrue("PR12 sin hacer", false);
	}

	// PR13. Sin hacer /
	@Test
	public void PR13() {
		assertTrue("PR13 sin hacer", false);
	}

	// PR14. Sin hacer /
	@Test
	public void PR14() {
		assertTrue("PR14 sin hacer", false);
	}

	// PR15. Sin hacer /
	@Test
	public void PR15() {
		assertTrue("PR15 sin hacer", false);
	}

	// PR16. Sin hacer /
	@Test
	public void PR16() {
		assertTrue("PR16 sin hacer", false);
	}

	// PR017. Sin hacer /
	@Test
	public void PR17() {
		assertTrue("PR17 sin hacer", false);
	}

	// PR18. Sin hacer /
	@Test
	public void PR18() {
		assertTrue("PR18 sin hacer", false);
	}

	// PR19. Sin hacer /
	@Test
	public void PR19() {
		assertTrue("PR19 sin hacer", false);
	}

	// P20. Sin hacer /
	@Test
	public void PR20() {
		assertTrue("PR20 sin hacer", false);
	}

	// PR21. Sin hacer /
	@Test
	public void PR21() {
		assertTrue("PR21 sin hacer", false);
	}

	// PR22. Sin hacer /
	@Test
	public void PR22() {
		assertTrue("PR22 sin hacer", false);
	}

	// PR23. Sin hacer /
	@Test
	public void PR23() {
		assertTrue("PR23 sin hacer", false);
	}

	// PR24. Sin hacer /
	@Test
	public void PR24() {
		assertTrue("PR24 sin hacer", false);
	}

	// PR25. Sin hacer /
	@Test
	public void PR25() {
		assertTrue("PR25 sin hacer", false);
	}

	// PR26. Sin hacer /
	@Test
	public void PR26() {
		assertTrue("PR26 sin hacer", false);
	}

	// PR27. Sin hacer /
	@Test
	public void PR27() {
		assertTrue("PR27 sin hacer", false);
	}

	// PR029. Sin hacer /
	@Test
	public void PR29() {
		assertTrue("PR29 sin hacer", false);
	}

	// PR030. Sin hacer /
	@Test
	public void PR30() {
		assertTrue("PR30 sin hacer", false);
	}

	// PR031. Sin hacer /
	@Test
	public void PR31() {
		assertTrue("PR31 sin hacer", false);
	}

}
