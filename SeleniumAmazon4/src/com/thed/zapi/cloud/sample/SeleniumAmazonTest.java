package com.thed.zapi.cloud.sample;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.Exception;
import com.DeathByCaptcha.SocketClient;
import com.opencsv.CSVWriter;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class SeleniumAmazonTest {
	public static void main(String[] args) throws InterruptedException, Exception, IOException {
		String gUserID="admin@fgtsoft.site";
		String gPass="Ram@12345";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		Date date = new Date();  
		System.out.println("********************* Start Time: *************************"+formatter.format(date));

		WebDriver driver2 = new ChromeDriver();
		driver2.manage().deleteAllCookies();
		driver2.manage().window().maximize();
		driver2.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver2.get("https://accounts.google.com/signin/v2/sl/pwd?service=mail&continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&flowName=GlifWebSignIn&flowEntry=AddSession&cid=0&navigationDirection=forward");
		driver2.findElement(By.id("identifierId")).sendKeys(gUserID); //userid

		driver2.findElement(By.xpath(".//span[@class='RveJvd snByac']")).click();
		Thread.sleep(5000);
		driver2.findElement(By.name("password")).sendKeys(gPass); //password
		driver2.findElement(By.xpath(".//span[@class='RveJvd snByac']")).click();
		Thread.sleep(5000);
		FileWriter outputfile = new FileWriter(System.getProperty("user.dir") + "/testResult.csv"); 
		CSVWriter writer = new CSVWriter(outputfile); 
		try {
			
			String[] header = { "UserName", "emailID","Result" }; 
			writer.writeNext(header); 
			BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/test.csv"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String str[] = line.split(",");
				if(!((line.contains("Pass"))||(line.contains("Skip")))) {
					System.out.println("Testing for :: "+str[0] + "," + str[1] + "," + str[2]);
					String name=str[0];
					String emailAddress= str[1];
					String pass= str[2];
					String mobile=str[3];
					String pin=str[4];
					String house= str[5];
					String street=str[6];
					String city= str[7];
					String state=str[8];
					String country=str[9];

					String finalRes= seleniumUI(driver2,gUserID, gPass,name, emailAddress, pass, mobile, pin, house, street, city, state,country);
					if(finalRes.contains("Pass")) {
						String[] data1 = { name, emailAddress, "Pass" }; 
						writer.writeNext(data1);
					}else {
						String[] data1 = { name, emailAddress, finalRes}; 
						writer.writeNext(data1);
					}
				}else {
					System.out.println(str[0] + "," + str[1] + "," + str[2] +"skip this details");
				}
			}
			formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			Date dateend = new Date();  
			System.out.println("*********************** End Time ******************* : "+formatter.format(dateend));
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			writer.close();
			driver2.close();
		}

	}

	public static String seleniumUI(WebDriver driver2,String gName, String gPass,String name, String emailID, String password, String mobile, String pin, String house,
			String street, String city, String state, String country) throws InterruptedException, Exception, IOException {
		String finalRes=null;
		String captchaURL=null;
		Captcha captchaVal=null;
		String otp=null;
		int counter1=0;
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		try {
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.get("https://www.amazon.com/ap/register?showRememberMe=true&openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.com%2F%3Fref_%3Dnav_ya_signin%26_encoding%3DUTF8&prevRID=2T13VTJ5NJ3JEB8RXX4E&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=usflex&openid.mode=checkid_setup&prepopulatedLoginId=&failedSignInCount=0&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&pageId=usflex&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0");

		System.out.println("Amazon First Registration page loaded");
		Thread.sleep(500);
		driver.findElement(By.id("ap_customer_name")).sendKeys(name);
		driver.findElement(By.id("ap_email")).sendKeys(emailID);
		driver.findElement(By.id("ap_password")).sendKeys(password);
		driver.findElement(By.id("ap_password_check")).sendKeys(password);
		driver.findElement(By.id("continue")).click();
		Thread.sleep(500);

		String errText = driver.findElement(By.xpath(".//body")).getAttribute("innerHTML");
		
		if (errText.contains("captcha")) {
			do {
				System.out.println("Inside capcha screen");
				if(driver.findElement(By.xpath(".//body")).getAttribute("innerHTML").contains("Hear the challenge")) {
					captchaURL=driver.findElement(By.xpath(".//img[contains(@src,'captcha')]")).getAttribute("src");
					captchaVal= ExampleNewRecaptchaToken(captchaURL);
					if(captchaVal!=null) {
					driver.findElement(By.id("auth-captcha-guess")).sendKeys(captchaVal.toString());
					}else {
						break;
					}
				}
				driver.findElement(By.id("ap_password")).sendKeys(password);
				driver.findElement(By.id("ap_password_check")).sendKeys(password);
				driver.findElement(By.id("continue")).click();
				Thread.sleep(500);
				errText = driver.findElement(By.xpath(".//body")).getAttribute("innerHTML");
				counter1++;
				if(counter1>5) {
					break;
				}
			}while(errText.contains("There was a problem"));
			int counter2=0;
			errText = driver.findElement(By.xpath(".//body")).getAttribute("innerHTML");
			if (errText.contains("Anti-Automation Challenge")) {
				do {
					System.out.println("inside page Anti-Automation Challenge ");
					captchaURL=driver.findElement(By.xpath(".//img[contains(@src,'captcha')]")).getAttribute("src");
					captchaVal= ExampleNewRecaptchaToken(captchaURL);
					if(captchaVal!=null) {
						driver.findElement(By.xpath(".//input[@name='cvf_captcha_input']")).sendKeys(captchaVal.toString());
						}else {
							break;
						}
					
					driver.findElement(By.xpath(".//input[@name='cvf_captcha_captcha_action']")).click();
					Thread.sleep(500);
					errText = driver.findElement(By.xpath(".//body")).getAttribute("innerHTML");
					counter2++;
					if(counter2>5) {
						break;
					}
				}while(errText.contains("Anti-Automation Challenge"));
			}

		}
		//verify OPT steps
		errText = driver.findElement(By.xpath(".//body")).getAttribute("innerHTML");
		Boolean otpSuccess=false;
		if (errText.contains("Verify email address")) {
			System.out.println("inside OTP verify screen");
			String OPTpagevalidation= driver.findElement(By.xpath(".//body")).getAttribute("innerHTML");
			if(!(OPTpagevalidation.contains("Add mobile number"))) {
				do {
					//check if any error msg in OTP page
					if(driver.findElement(By.xpath(".//body")).getAttribute("innerHTML").contains("Invalid OTP. Please check your code and try again."))	{
						Thread.sleep(1000);
						driver.findElement(By.xpath(".//a[contains(text(),'Resend OTP')]")).click();
						Thread.sleep(2000);
					}
					//moving to gmail account ro get the OTP
					driver2.findElement(By.xpath(".//a[@title='Inbox']")).click();
					Thread.sleep(5000);
					List <WebElement> inbox= driver2.findElements(By.xpath("(//table[@class='F cf zt'])[2]//tr//td[6]//span[@class='y2']"));

					for(int i=0;i<inbox.size();i++) {
						if(inbox.get(i).getAttribute("innerText").contains("OTP")) {
							String[] arrOfStr = inbox.get(i).getAttribute("innerText").split(":",7); 
							otp= arrOfStr[1].trim();
							break;
						}
					}
					//MailOTPReader mailReader = new MailOTPReader();
					//String opt = mailReader.getOTP();
					driver.findElement(By.xpath(".//input[@name='code']")).sendKeys(otp);
					driver.findElement(By.xpath("(//input[@type='submit'])[1]")).click();
					Thread.sleep(6000);
					OPTpagevalidation= driver.findElement(By.xpath(".//body")).getAttribute("innerHTML");
				}while(OPTpagevalidation.contains("Invalid OTP. Please check your code and try again."));
				otpSuccess=true;
			}else {
				//if add mobile number page will appear then skip the user
				finalRes="Skip";
				otpSuccess=false;
			}
		}
		if(otpSuccess) {	
			if(driver.getCurrentUrl().contains("nav_ya_signin")) {
				System.out.println("Login successful username: " + name); 
				Thread.sleep(10000);
				driver.get("https://www.amazon.com/a/addresses?ref_=ya_d_l_addr");
				driver.findElement(By.id("ya-myab-plus-address-icon")).click();
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-countryCode")).click();
				Thread.sleep(1000);
				driver.findElement(By.xpath(".//ul[@class='a-nostyle a-list-link']/li/a[text()='"+country+"']")).click();
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-enterAddressFullName")).sendKeys(name);
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-enterAddressPhoneNumber")).sendKeys(mobile);
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-enterAddressPostalCode")).sendKeys(pin);
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-enterAddressLine1")).sendKeys(house);
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-enterAddressLine2")).sendKeys(street);
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-enterAddressCity")).sendKeys(city);
				Thread.sleep(1000);
				driver.findElement(By.id("address-ui-widgets-enterAddressStateOrRegion")).click();
				Thread.sleep(1000);
				driver.findElement(By.xpath(".//a[contains(text(),'"+state+"')]")).click();
				Thread.sleep(1000);
				driver.findElement(By.className("a-button-input")).click();
				Thread.sleep(5000);
				//driver.findElement(By.xpath(".//span[contains(text(),'Save Address')]")).click();
				//Thread.sleep(1000);
				if(driver.getCurrentUrl().contains("yaab-enterAddressSucceed")) {
					driver.findElement(By.xpath(".//span[@id='kyc_xborder_skip_section_label']")).click();
					Thread.sleep(1000);
					driver.findElement(By.xpath(".//span[@id='kyc-xborder-continue-button']")).click();
					Thread.sleep(1000);
					driver.findElement(By.id("ya-myab-set-default-shipping-btn-0")).click();
					Thread.sleep(1000);
					if (driver.getCurrentUrl().contains("setDefaultAddressSuccess")) {

						driver.findElement(By.xpath(".//i[@class='hm-icon nav-sprite']")).click();
						Thread.sleep(4000);
						driver.findElement(By.xpath(".//div[contains(text(),'Sign Out')]")).click();
						Thread.sleep(6000);
						if(driver.getCurrentUrl().contains("signin")){
							finalRes="Pass";
						}else {
							finalRes="Fail";
						}
					} else {
						System.out.println("IDFailed");
						finalRes="Fail";
					}
				}else {
					System.out.println("given address is wrong");
					finalRes="Fail";
				}
			}else {
				// check the reason of signin failure
				finalRes="Fail";
			}
		}else {
			finalRes="Skip";
		}
		}catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			driver.close();
		}
		
		return finalRes;
	}

	public static Captcha ExampleNewRecaptchaToken(String captchaURL) throws IOException, Exception, InterruptedException {
		/*
		 * Put your DeathByCaptcha account username and password here. Use HttpClient
		 * for HTTP API.
		 */

		Client client = (Client) (new SocketClient("allinonerc@gmail.com", "Abi2015@"));
		double balance = client.getBalance();
		String fileName=null;
		Captcha captcha = null;
		fileName = "digital_image_processing.jpg";
		System.out.println("Downloading File From: " + captchaURL);

		URL url = new URL(captchaURL);
		InputStream inputStream = url.openStream();
		OutputStream outputStream = new FileOutputStream(fileName);
		byte[] buffer = new byte[2048];

		int length = 0;
		int counter=0;

		while ((length = inputStream.read(buffer)) != -1) {
			System.out.println("Buffer Read of length: " + length);
			outputStream.write(buffer, 0, length);
			counter++;
			if(counter>5) {
				break;
			}
		}

		inputStream.close();
		outputStream.close();
		if(counter<=5) {
			captcha = client.decode(System.getProperty("user.dir") + "/"+fileName, 2, 0);
			if (null != captcha) {
				/*
				 * The CAPTCHA was solved; captcha.id property holds its numeric ID, and
				 * captcha.text holds its text.
				 */
				System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

				if (client.report(captcha)) {
					System.out.println("Reported as incorrectly solved");
				} else {
					System.out.println("Failed reporting incorrectly solved CAPTCHA");
				}
			}
		}else {
			captcha=null;
		}

		
		return captcha;

	}
}