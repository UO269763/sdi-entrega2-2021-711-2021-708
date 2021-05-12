package com.uniovi.tests;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

//Paquetes JUnit 
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
//Paquetes Selenium 
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebElement;

import com.uniovi.tests.util.MyUtil;

//Ordenamos las pruebas por el nombre del mÃ©todo
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SdiEntrega2Tests {

	private MyUtil testUtil = new MyUtil(driver);
	// En Windows (Debe ser la versiÃ³n 65.0.1 y desactivar las actualizacioens
	// automÃ¡ticas)):
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	// static String Geckdriver024 =
	// "C:\\Users\\Usuario\\Desktop\\TERCEROINFORMATICA\\SEGUNDOSEMESTRE\\SDI\\lab\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	static String Geckdriver024 = "C:\\Users\\santi\\Downloads\\SDI (2)\\SDI\\Spring\\Practica 5\\PL-SDI-Sesion5-material\\geckodriver024win64.exe";
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "https://localhost:8081";

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
		MyUtil testUtil = new MyUtil(driver);
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("admin@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.className("btn-primary")).click();
		testUtil.waitChangeWeb();
		driver.get(URL + "/admin");
		driver.findElement(By.linkText("reset DB")).click();
		testUtil.waitChangeWeb();

	}

	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}

	// PR01. Registro usuario, datos validos /
	@Test
	public void PR01() {
		driver.get(URL);
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		Random n = new Random();
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys(n.nextInt(10000) + "@gmail.com");
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Ester");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Lopez");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("1234");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("1234");
		driver.findElement(By.id("aceptar")).click();
		testUtil.searchText("Nuevo usuario registrado", true);
	}

	/*
	 * Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos
	 * vacíos)
	 */
	@Test
	public void PR02() {
		driver.get(URL);
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("1234");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("1234");
		driver.findElement(By.id("aceptar")).click();
		testUtil.searchText("Registrar usuario", true);
	}

	/*
	 * Registro de Usuario con datos inválidos (repetición de contraseña inválida).
	 */
	@Test
	public void PR03() {
		driver.get(URL);
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		Random n = new Random();
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys(n.nextInt(10000) + "@gmail.com");
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Ester");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Lopez");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("1234");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("12345");
		driver.findElement(By.id("aceptar")).click();
		testUtil.searchText("Registrar usuario", true);
	}

	/*
	 * Registro de Usuario con datos inválidos (email existente).
	 */
	@Test
	public void PR04() {
		driver.get(URL);
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys("user1@email.com");
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Ester");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Lopez");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("1234");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("1234");
		driver.findElement(By.id("aceptar")).click();
		testUtil.searchText("Este email ya está registrado. Intentelo de nuevo", true);
	}

	/*
	 * Inicio de sesión con datos válidos (usuario estándar).
	 */
	@Test
	public void PR05() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();

		testUtil.searchText("Mis ofertas", true);

	}

	/*
	 * Inicio de sesión con datos inválidos (usuario estándar, campo email y
	 * contraseña vacíos).
	 */
	@Test
	public void PR06() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("ester@gmail.es");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("12345");
		driver.findElement(By.className("btn-primary")).click();
		testUtil.searchText("Identificación de usuario", true);
	}

	/*
	 * Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
	 */
	@Test
	public void PR07() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("ester@gmail.es");
		driver.findElement(By.className("btn-primary")).click();
		testUtil.searchText("Identificación de usuario", true);
	}

	/*
	 * Inicio de sesión con datos inválidos (email no existente en la aplicación).
	 */
	@Test
	public void PR08() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("estergonsalves@gmail.es");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("12345");
		driver.findElement(By.className("btn-primary")).click();
		testUtil.searchText("Email o password incorrecto", true);
	}

	/*
	 * Hacer click en la opción de salir de sesión y comprobar que se redirige a la
	 * página de inicio de sesión (Login).
	 */
	@Test
	public void PR09() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Cerrar sesión")).click();
		testUtil.waitChangeWeb();
		testUtil.searchText("Identificación de usuario", true);
	}

	/*
	 * Comprobar que el botón cerrar sesión no está visible si el usuario no está
	 * autenticado.
	 */
	@Test
	public void PR10() {
		driver.get(URL);
		testUtil.searchText("Cerrar sesión", false);
	}

	// PR11. Mostrar el listado de usuarios y comprobar que se muestran todos los
	// que existen en el
	// sistema.
	@Test
	public void PR11() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("admin@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.id("aceptar")).click();
		testUtil.waitChangeWeb();
		// comprobamos que no se liste el administrador y que se listen usuarios que
		// estan en la base de datos
		testUtil.searchText("Usuarios", true);
		testUtil.searchText("admin@email.com", false);
		testUtil.searchText("user1@email.com", true);
		testUtil.searchText("user2@email.com", true);

	}

	// PR12. Ir a la lista de usuarios, borrar el primer usuario de la lista,
	// comprobar que la lista se
	// actualiza y dicho usuario desaparece.
	// el primer usuario que hemos añadido es el user1
	@Test
	public void PR12() {
		driver.get(URL + "/identificarse");

		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("admin@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.id("aceptar")).click();

		testUtil.searchText("user1@email.com", true);
		driver.findElements(By.className("checkbox")).get(0).click();
		driver.findElement(By.id("btEliminar")).click();
		testUtil.waitChangeWeb();

		testUtil.searchText("Usuarios", true);
		testUtil.searchText("admin@email.com", false);
		testUtil.searchText("user1@email.com", false);
	}

	// PR13. Ir a la lista de usuarios, borrar el último usuario de la lista,
	// comprobar que la lista se
	// actualiza y dicho usuario desaparece.
	// según como está se lista el último el último que se haya añadido
	@Test
	public void PR13() {
		driver.get(URL);
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Estoesunaprueba");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Prueba");
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys("prueba@email.com");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("123456");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("123456");
		driver.findElement(By.id("aceptar")).click();

		driver.get(URL + "/identificarse");

		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("admin@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.id("aceptar")).click();

		testUtil.searchText("prueba@email.com", true);
		driver.findElements(By.id("prueba@email.com")).get(0).click();
		driver.findElement(By.id("btEliminar")).click();
		testUtil.waitChangeWeb();

		testUtil.searchText("Usuarios", true);
		testUtil.searchText("admin@email.com", false);
		testUtil.searchText("prueba@email.com", false);
	}

	// PR14. Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se
	// actualiza y dichos
	// usuarios desaparecen.
	@Test
	public void PR14() {
		driver.get("https://localhost:8081/");
		testUtil.waitChangeWeb();
		// primero registramos los 3 usuarios que borraremos
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Estoesunaprueba1");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Prueba1");
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys("prueba1@email.com");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("123456");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("123456");
		driver.findElement(By.id("aceptar")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Cerrar sesión")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Estoesunaprueba2");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Prueba2");
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys("prueba2@email.com");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("123456");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("123456");
		driver.findElement(By.id("aceptar")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Cerrar sesión")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.linkText("Registrate")).click();
		testUtil.waitChangeWeb();
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Estoesunaprueba3");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Prueba3");
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys("prueba3@email.com");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("123456");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("123456");
		driver.findElement(By.id("aceptar")).click();
		testUtil.waitChangeWeb();
		driver.get(URL + "/identificarse");

		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("admin@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.id("aceptar")).click();

		driver.get(URL + "/usuario/list");
		testUtil.searchText("prueba1@email.com", true);
		testUtil.searchText("prueba2@email.com", true);
		testUtil.searchText("prueba3@email.com", true);
		driver.findElements(By.id("prueba1@email.com")).get(0).click();
		driver.findElements(By.id("prueba2@email.com")).get(0).click();
		driver.findElements(By.id("prueba3@email.com")).get(0).click();
		driver.findElement(By.id("btEliminar")).click();
		testUtil.waitChangeWeb();

		testUtil.searchText("Usuarios", true);
		testUtil.searchText("admin@email.com", false);
		testUtil.searchText("prueba1@email.com", false);
		testUtil.searchText("prueba2@email.com", false);
		testUtil.searchText("prueba3@email.com", false);
	}

	// PR15. Ir al formulario de alta de oferta, rellenarla con datos válidos y
	// pulsar el botón Submit.
	// Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
	@Test
	public void PR15() {
		driver.get(URL + "/identificarse");
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/agregar");
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("Mi primera oferta");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("Sudadera gris oscura de Nike");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("25");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/misofertas");
		testUtil.searchText("Mi primera oferta", true);
		testUtil.searchText("25", true);
	}

	// PR16. Ir al formulario de alta de oferta, rellenarla con datos inválidos
	// (campo título vacío y
	// precio en negativo) y pulsar el botón Submit. Comprobar que se muestra el
	// mensaje de campo
	// obligatorio.
	@Test
	public void PR16() {
		driver.get(URL + "/identificarse");
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/agregar");
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("Mi primera oferta");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("Sudadera gris oscura de Nike");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("-25"); // no se puede añadir un precio negativo
		driver.findElement(By.className("btn-primary")).click();
		testUtil.searchText("Compruebe el precio de su producto", true);
	}

	// PR017. Mostrar el listado de ofertas para dicho usuario y comprobar que se
	// muestran todas las
	// que existen para este usuario.
	@Test
	public void PR17() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/misofertas");
		testUtil.searchText("Mi primera oferta", true); // oferta que añadimos antes en la prueba 15.
	}

	// PR18. Ir a la lista de ofertas, borrar la primera oferta de la lista,
	// comprobar que la lista se
	// actualiza y que la oferta desaparece.
	@Test
	public void PR18() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/misofertas");
		testUtil.searchText("Oferta 1 del usuario user6", true); // oferta que añadimos antes en la prueba 15.
		driver.findElements(By.className("eliminar")).get(0).click();
		driver.findElement(By.id("btEliminar")).click();
		testUtil.waitChangeWeb();
		testUtil.waitChangeWeb();
		testUtil.searchText("Oferta 1 del usuario user6", false);
	}

	// PR19. Ir a la lista de ofertas, borrar la última oferta de la lista,
	// comprobar que la lista se actualiza
	// y que la oferta desaparece.
	@Test
	public void PR19() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/misofertas");
		testUtil.searchText("Oferta 5 del usuario user6", true); // oferta que añadimos antes en la prueba 15.
		driver.findElements(By.className("eliminar")).get(2).click();
		driver.findElement(By.id("btEliminar")).click();
		testUtil.waitChangeWeb();
		testUtil.waitChangeWeb();
		testUtil.searchText("Oferta 5 del usuario user6", false);
	}

	// P20. Hacer una búsqueda con el campo vacío y comprobar que se muestra la
	// página que
	// corresponde con el listado de las ofertas existentes en el sistema
	@Test
	public void PR20() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user3@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user3");

		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/agregar");
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("Prueba");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("Uno");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("1");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/agregar");
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("prueb");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("Uno");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("1");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/identificarse");
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/buscar");

		driver.findElement(By.name("busqueda")).click();
		driver.findElement(By.name("busqueda")).clear();
		driver.findElement(By.className("btn")).click();

		driver.findElement(By.id("lastpage")).click();
		testUtil.waitChangeWeb();

		testUtil.searchText("Prueba", true);
		testUtil.searchText("prueb", true);
	}

	// PR21. Hacer una búsqueda escribiendo en el campo un texto que no exista y
	// comprobar que se
	// muestra la página que corresponde, con la lista de ofertas vacía.
	@Test
	public void PR21() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");

		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/buscar");

		driver.findElement(By.name("busqueda")).click();
		driver.findElement(By.name("busqueda")).clear();
		driver.findElement(By.name("busqueda")).sendKeys("olajfeffekek");
		driver.findElement(By.className("btn")).click();

		testUtil.searchText("prueb", false);
	}

	// PR22. Hacer una búsqueda escribiendo en el campo un texto en minúscula o
	// mayúscula y
	// comprobar que se muestra la página que corresponde, con la lista de ofertas
	// que contengan
	// dicho texto, independientemente que el título esté almacenado en minúsculas o
	// mayúscula
	@Test
	public void PR22() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");

		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/agregar");
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("Prueba");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("Uno");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("1");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/agregar");
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("prueb");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("Uno");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("1");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/identificarse");
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();

		driver.get(URL + "/oferta/buscar");

		driver.findElement(By.name("busqueda")).click();
		driver.findElement(By.name("busqueda")).clear();
		driver.findElement(By.name("busqueda")).sendKeys("PRUE");
		driver.findElement(By.className("btn")).click();

		testUtil.waitChangeWeb();

		testUtil.searchText("Prueba", true);
		testUtil.searchText("prueb", true);
	}

	// PR23. Sobre una búsqueda determinada (a elección de desarrollador), comprar
	// una oferta que
	// deja un saldo positivo en el contador del comprobador. Y comprobar que el
	// contador se
	// actualiza correctamente en la vista del comprador.
	@Test
	public void PR23() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/agregar");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("miofertausuario2");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("unoo");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("1");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/identificarse");
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user6@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user6");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/buscar?busqueda=miofertausuario2");
		driver.findElements(By.className("compra")).get(0).click();
		testUtil.waitChangeWeb();
		driver.get(URL + "/oferta/miscompras");
		testUtil.waitChangeWeb();
		testUtil.searchText("miofertausuario2", true);
	}

	// PR24. Sobre una búsqueda determinada (a elección de desarrollador), comprar
	// una oferta que
	// deja un saldo 0 en el contador del comprobador. Y comprobar que el contador
	// se actualiza
	// correctamente en la vista del comprador.
	@Test
	public void PR24() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/agregar");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("miofertausuario2prueba24");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("24");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("100");
		driver.findElement(By.className("btn-primary")).click();

		// registramos un nuevo usuario que será el que compre la oferta
		driver.get(URL + "/registrarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Estoesunaprueba");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Prueba");
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys("prueba@email.com");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("123456");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("123456");
		driver.findElement(By.id("aceptar")).click();

		driver.get(URL + "/oferta/buscar?busqueda=miofertausuario2prueba24");
		driver.findElements(By.className("compra")).get(0).click();
		testUtil.waitChangeWeb();
		driver.get(URL + "/oferta/miscompras");
		testUtil.waitChangeWeb();
		testUtil.searchText("miofertausuario2prueba24", true);
		// testUtil.searchText("0", true);
	}

	// PR25. Sobre una búsqueda determinada (a elección de desarrollador), intentar
	// comprar una
	// oferta que esté por encima de saldo disponible del comprador. Y comprobar que
	// se muestra el
	// mensaje de saldo no suficiente.
	@Test
	public void PR25() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/agregar");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("nombre")).click();
		driver.findElement(By.name("nombre")).clear();
		driver.findElement(By.name("nombre")).sendKeys("miofertausuario2prueba25");
		driver.findElement(By.name("info")).click();
		driver.findElement(By.name("info")).clear();
		driver.findElement(By.name("info")).sendKeys("24");
		driver.findElement(By.name("precio")).click();
		driver.findElement(By.name("precio")).clear();
		driver.findElement(By.name("precio")).sendKeys("110");
		driver.findElement(By.className("btn-primary")).click();

		// registramos un nuevo usuario que será el que compre la oferta
		driver.get(URL + "/registrarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.id("nombre")).click();
		driver.findElement(By.id("nombre")).clear();
		driver.findElement(By.id("nombre")).sendKeys("Estoesunaprueba");
		driver.findElement(By.id("apellidos")).click();
		driver.findElement(By.id("apellidos")).clear();
		driver.findElement(By.id("apellidos")).sendKeys("Prueba");
		driver.findElement(By.id("email")).click();
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys("prueba2@email.com");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("123456");
		driver.findElement(By.id("rpassword")).click();
		driver.findElement(By.id("rpassword")).clear();
		driver.findElement(By.id("rpassword")).sendKeys("123456");
		driver.findElement(By.id("aceptar")).click();

		driver.get(URL + "/oferta/buscar?busqueda=miofertausuario2prueba25");
		driver.findElements(By.className("compra")).get(0).click();
		testUtil.searchText("Sin saldo suficiente", true);
	}

	// PR26. Ir a la opción de ofertas compradas del usuario y mostrar la lista.
	// Comprobar que
	// aparecen las ofertas que deben aparecer.
	@Test
	public void PR26() {
		driver.get(URL + "/identificarse");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("prueba@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("123456");
		driver.findElement(By.className("btn-primary")).click();
		driver.get(URL + "/oferta/miscompras");
		testUtil.waitChangeWeb();
		testUtil.searchText("miofertausuario2prueba24", true);
	}

	// PR30. Inicio de sesión con datos válidos /
	@Test
	public void PR30() {
		driver.get(URL + "/cliente.html");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");
		driver.findElement(By.id("boton-login")).click();

		testUtil.searchText("Desconectar", true);
	}

	// PR31. Inicio de sesión con datos inválidos (email existente, pero contraseña
	// incorrecta).
	@Test
	public void PR31() {
		driver.get(URL + "/cliente.html");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user");
		driver.findElement(By.id("boton-login")).click();
		testUtil.searchText("Usuario no encontrado", true);
	}

	// PR31. Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
	@Test
	public void PR32() {
		driver.get(URL + "/cliente.html");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("");
		driver.findElement(By.id("boton-login")).click();
		testUtil.searchText("Usuario no encontrado", true);
	}

	// PR33. Mostrar el listado de ofertas disponibles y comprobar que se muestran
	// todas las que
	// existen, menos las del usuario identificado.
	@Test
	public void PR33() {
		driver.get(URL + "/cliente.html");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");
		driver.findElement(By.id("boton-login")).click();
		testUtil.waitChangeWeb();
		testUtil.searchText("Oferta 1 del usuario user1", true);
		testUtil.searchText("Oferta 1 del usuario user2", false);
	}

	// PR34. Sobre una búsqueda determinada de ofertas (a elección de
	// desarrollador), enviar un
	// mensaje a una oferta concreta. Se abriría dicha conversación por primera vez.
	// Comprobar que el
	// mensaje aparece en el listado de mensajes
	@Test
	public void PR34() {
		driver.get(URL + "/cliente.html");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");
		driver.findElement(By.id("boton-login")).click();
		testUtil.waitChangeWeb();
		driver.findElements(By.id("sendMessage")).get(0).click();
	    driver.findElement(By.id("inputMessage")).click();
	    driver.findElement(By.id("inputMessage")).clear();
	    //abrimos una conversación y mandamos mensaje
	    driver.findElement(By.id("inputMessage")).sendKeys("Hola");
	    testUtil.waitChangeWeb();
	    driver.findElement(By.id("submitMessage")).click();
	    testUtil.waitChangeWeb();
	    driver.findElement(By.id("offersmanage")).click();
	    driver.findElement(By.id("conver")).click();
	    testUtil.waitChangeWeb();
	    testUtil.waitChangeWeb();
	    //vamos a las conversaciones, 
	    //intentamos mandar un mensaje en esa conversacion y comprobamos que esta el mensaje anterior
	    testUtil.searchText("user3@email.com", true);
	    driver.findElement(By.linkText("Enviar mensaje")).click();
	    testUtil.waitChangeWeb();
	    testUtil.searchText("Hola", true);
	}
	
	// PR35 Sobre el listado de conversaciones enviar un mensaje a una conversación ya abierta.
	// Comprobar que el mensaje aparece en el listado de mensajes.
	@Test
	public void PR35() {
		driver.get(URL + "/cliente.html");
		testUtil.waitChangeWeb();
		driver.findElement(By.name("email")).click();
		driver.findElement(By.name("email")).clear();
		driver.findElement(By.name("email")).sendKeys("user2@email.com");
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("user2");
		driver.findElement(By.id("boton-login")).click();
		testUtil.waitChangeWeb();
		driver.findElements(By.id("sendMessage")).get(0).click();
	    driver.findElement(By.id("inputMessage")).click();
	    driver.findElement(By.id("inputMessage")).clear();
	    //abrimos una conversación y mandamos mensaje
	    driver.findElement(By.id("inputMessage")).sendKeys("Que tal");
	    testUtil.waitChangeWeb();
	    driver.findElement(By.id("submitMessage")).click();
	    testUtil.waitChangeWeb();
	    driver.findElement(By.id("offersmanage")).click();
	    driver.findElement(By.id("conver")).click();
	    testUtil.waitChangeWeb();
	    testUtil.waitChangeWeb();
	    //vamos a las conversaciones, 
	    //intentamos mandar un mensaje en esa conversacion y comprobamos que esta el mensaje anterior
	    testUtil.searchText("user3@email.com", true);
	    driver.findElement(By.linkText("Enviar mensaje")).click();
	    testUtil.waitChangeWeb();
	    testUtil.searchText("Hola", true);
	    testUtil.searchText("Que tal", true);
	}
	
	
	// PR36 Mostrar el listado de conversaciones ya abiertas. Comprobar que el listado contiene las
	// conversaciones que deben ser.
	@Test
	public void PR36() {
	driver.get(URL + "/cliente.html");
	testUtil.waitChangeWeb();
    driver.findElement(By.id("email")).click();
    driver.findElement(By.id("email")).clear();
    driver.findElement(By.id("email")).sendKeys("user2@email.com");
    driver.findElement(By.id("password")).click();
    driver.findElement(By.id("password")).clear();
    driver.findElement(By.id("password")).sendKeys("user2");
    driver.findElement(By.id("boton-login")).click();
    testUtil.waitChangeWeb();
    //mandamos primer mensaje
    driver.findElements(By.id("sendMessage")).get(0).click();
    testUtil.waitChangeWeb();
    driver.findElement(By.id("inputMessage")).click();
    driver.findElement(By.id("inputMessage")).clear();
    driver.findElement(By.id("inputMessage")).sendKeys("Hola");
    driver.findElement(By.id("submitMessage")).click();
    testUtil.waitChangeWeb();
    driver.findElement(By.id("offersmanage")).click();
    driver.findElement(By.id("conver")).click();
    //mandamos mensaje a la oferta1 del usuario2
    driver.get(URL + "/cliente.html?w=ofertas");
    testUtil.waitChangeWeb();
    driver.findElements(By.id("sendMessage")).get(5).click();
    testUtil.waitChangeWeb();
    driver.findElement(By.id("inputMessage")).click();
    driver.findElement(By.id("inputMessage")).clear();
    driver.findElement(By.id("inputMessage")).sendKeys("Como estas");
    driver.findElement(By.id("submitMessage")).click();
    testUtil.waitChangeWeb();
    driver.findElement(By.id("offersmanage")).click();
    driver.findElement(By.id("conver")).click();
    testUtil.waitChangeWeb();
    driver.findElements(By.id("sendMessageConver")).get(0).click();
    //comprobar que esta el mensaje que mandamos al primer usuario
    testUtil.waitChangeWeb();
    testUtil.searchText("user3@email.com", true);
    testUtil.searchText("Hola", true);
    driver.findElement(By.id("offersmanage")).click();
    driver.findElement(By.id("conver")).click();
    testUtil.waitChangeWeb();
    driver.findElements(By.id("sendMessageConver")).get(2).click();
    //comprobar que esta el mensaje que mandamos al segundo usario
    testUtil.waitChangeWeb();
    testUtil.searchText("user5@email.com", true);
    testUtil.searchText("Hola", true);
    
	}
	
	// PR37. Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la primera y
	// comprobar que el listado se actualiza correctamente.
	
	@Test
	public void PR37() {

		driver.get(URL + "/cliente.html");
		testUtil.waitChangeWeb();
	    driver.findElement(By.id("email")).click();
	    driver.findElement(By.id("email")).clear();
	    driver.findElement(By.id("email")).sendKeys("user2@email.com");
	    driver.findElement(By.id("password")).click();
	    driver.findElement(By.id("password")).clear();
	    driver.findElement(By.id("password")).sendKeys("user2");
	    driver.findElement(By.id("boton-login")).click();
	    testUtil.waitChangeWeb();
	    //mandamos mensaje a la oferta1 del primer usuario1
	    driver.get(URL + "/cliente.html?w=conversaciones");
	    testUtil.waitChangeWeb();
	    
	    List<WebElement> elements = driver.findElements(By.id("deleteMessage"));
		int size = elements.size();
	    //comprobamos que este la conversacion con el primer usuario
		testUtil.waitChangeWeb();
	    testUtil.searchText("user3@email.com", true);
	    //debería de aparecer el boton de eliminar
	    testUtil.searchText("Eliminar conversación", true);	    
	    driver.findElements(By.id("deleteMessage")).get(0).click();
	    testUtil.waitChangeWeb();
	    testUtil.waitChangeWeb();
	    //una vez borramos, la lista de conversaciones aparecera vacia
	    elements = driver.findElements(By.id("deleteMessage"));
	    testUtil.waitChangeWeb();
	    testUtil.searchText("user3@email.com", false);
	     	    
	    assertTrue(size-1 == elements.size());
	}
	
	// PR38.  Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la última y
	// comprobar que el listado se actualiza correctamente.
		
		@Test
		public void PR38() {

			driver.get(URL + "/cliente.html");
			testUtil.waitChangeWeb();
		    driver.findElement(By.id("email")).click();
		    driver.findElement(By.id("email")).clear();
		    driver.findElement(By.id("email")).sendKeys("user2@email.com");
		    driver.findElement(By.id("password")).click();
		    driver.findElement(By.id("password")).clear();
		    driver.findElement(By.id("password")).sendKeys("user2");
		    driver.findElement(By.id("boton-login")).click();
		    testUtil.waitChangeWeb();
		    //mandamos mensaje a la oferta1 del primer usuario1
		    driver.get(URL + "/cliente.html?w=conversaciones");
		    List<WebElement> elements = driver.findElements(By.id("deleteMessage"));
			int size = elements.size();
		    //comprobamos que este la conversacion con el primer usuario
			testUtil.waitChangeWeb();
		    testUtil.searchText("user4@email.com", true);
		    //debería de aparecer el boton de eliminar
		    testUtil.searchText("Eliminar", true);	    
		    driver.findElements(By.id("deleteMessage")).get(0).click();
		    testUtil.waitChangeWeb();
		    testUtil.waitChangeWeb();
		    //una vez borramos, la lista de conversaciones aparecera vacia
		    elements = driver.findElements(By.id("deleteMessage"));
		    testUtil.waitChangeWeb();
		    testUtil.searchText("user4@email.com", false);
		     	    
		    assertTrue(size-1 == 1);
		}
}
