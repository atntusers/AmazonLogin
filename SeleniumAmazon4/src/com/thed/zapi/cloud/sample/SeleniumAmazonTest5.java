package com.thed.zapi.cloud.sample;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.Exception;
import com.DeathByCaptcha.SocketClient;

import jxl.read.biff.BiffException;

public class SeleniumAmazonTest5 {
	
	@Parameters({ "gmailuserID" ,"gmailPass","SampleFile"})
	@Test(retryAnalyzer=Retry.class)
	public void SeleniumAmazonUserTest(String GMAILUSERID,String GMAILPASS,  String SAMPLEUSERFILE) throws InterruptedException, Exception, BiffException, IOException {
		System.out.println("*********GMAILUSERID************" +GMAILUSERID+": **************SAMPLEUSERFILE***********"+SAMPLEUSERFILE);
		String gUserID=GMAILUSERID;
		String gPass=GMAILPASS;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		Date date = new Date();  
		System.out.println("********************* Start Time: *************************"+formatter.format(date));
		ExcelManager excelManager = new ExcelManager();

		WebDriver driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get("https://accounts.google.com/signin/v2/sl/pwd?service=mail&continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&flowName=GlifWebSignIn&flowEntry=AddSession&cid=0&navigationDirection=forward");
		driver.findElement(By.id("identifierId")).sendKeys(gUserID); //userid
		driver.findElement(By.xpath(".//span[@class='RveJvd snByac']")).click();
		Thread.sleep(8000);
		driver.findElement(By.name("password")).sendKeys(gPass); //password
		driver.findElement(By.xpath(".//span[@class='RveJvd snByac']")).click();
		Thread.sleep(5000);

		String[][] UserList = excelManager.readExcelTwoDimension(SAMPLEUSERFILE.trim(), "test-sample");

		try {

			for(int i=0;i<UserList.length;i++) {
				if(UserList[i][10]!=null) {
					if((!UserList[i][10].contains("Pass"))) {
						System.out.println("*********userRowNo:: " + i +" userName "+UserList[i][0]+" EmailAddress "+UserList[i][1]);
						String finalRes= seleniumUI(driver,gUserID, gPass,UserList[i][0], UserList[i][1], UserList[i][2], UserList[i][3], UserList[i][4], UserList[i][5], UserList[i][6], UserList[i][7], UserList[i][8],UserList[i][9]);
						excelManager.UpdateExcel(SAMPLEUSERFILE.trim(), "test-sample", "Name",i, 10, finalRes);
						System.out.println("*********Executed userRowNo:: " + i +" userName "+UserList[i][0]+" EmailAddress "+UserList[i][1]);
					}else {
						System.out.println("****User already registered********");
						//excelManager.UpdateExcel(SAMPLEUSERFILE.trim(), "test-sample", "Name",i+1, 10, UserList[i][10]);
					}
				}
			}

			formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			Date dateend = new Date();  
			System.out.println("*********************** End Time ******************* : "+formatter.format(dateend));


		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			driver.close();
		}

	}

	public static String seleniumUI(WebDriver driver,String gName, String gPass,String name, String emailID, String password, String mobile, String pin, String house,
			String street, String city, String state, String country) throws InterruptedException, Exception, IOException {
		String finalRes=null;
		String captchaURL=null;
		Captcha captchaVal=null;
		String otp=null;
		int counter1=0;
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/chromedriver.exe");
		WebDriver driver2 = new ChromeDriver();
		try {
			driver2.manage().deleteAllCookies();
			driver2.manage().window().maximize();
			driver2.get("https://www.amazon.com/ap/register?showRememberMe=true&openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.com%2F%3Fref_%3Dnav_ya_signin%26_encoding%3DUTF8&prevRID=2T13VTJ5NJ3JEB8RXX4E&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=usflex&openid.mode=checkid_setup&prepopulatedLoginId=&failedSignInCount=0&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&pageId=usflex&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0");

			System.out.println("Amazon First Registration page loaded");
			Thread.sleep(500);
			driver2.findElement(By.id("ap_customer_name")).sendKeys(name);
			driver2.findElement(By.id("ap_email")).sendKeys(emailID);
			driver2.findElement(By.id("ap_password")).sendKeys(password);
			driver2.findElement(By.id("ap_password_check")).sendKeys(password);
			driver2.findElement(By.id("continue")).click();
			Thread.sleep(500);
			
			String flag="Skip";
			String errText = driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML");
			if (errText.contains("Email address already in use")) {
				//user already registered, no action required
				flag="Pass";
				finalRes="Pass: Email address already in use";
			}else {
			if (errText.contains("captcha")) {
				do {
					System.out.println("Inside 1st capcha screen");
					if(driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML").contains("Hear the challenge")) {
						captchaURL=driver2.findElement(By.xpath(".//img[contains(@src,'captcha')]")).getAttribute("src");
						captchaVal= ExampleNewRecaptchaToken(captchaURL);
						if(captchaVal!=null) {
							driver2.findElement(By.id("auth-captcha-guess")).sendKeys(captchaVal.toString());
						}else {
							finalRes="Skipped: Captcha value is appearing as .gif or wrong";
							flag="Skip";
							break;
						}
					}
					driver2.findElement(By.id("ap_password")).sendKeys(password);
					driver2.findElement(By.id("ap_password_check")).sendKeys(password);
					driver2.findElement(By.id("continue")).click();
					Thread.sleep(500);
					errText = driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML");
					counter1++;
					if(counter1>5) {
						flag="Skip";
						finalRes="Skipped:: 1st Captcha screen validation failed";
						break;
					}
				}while(errText.contains("There was a problem"));
				int counter2=0;
				errText = driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML");
				if (errText.contains("Anti-Automation Challenge")) {
					do {
						System.out.println("inside page Anti-Automation Challenge ");
						captchaURL=driver2.findElement(By.xpath(".//img[contains(@src,'captcha')]")).getAttribute("src");
						captchaVal= ExampleNewRecaptchaToken(captchaURL);
						if(captchaVal!=null) {
							driver2.findElement(By.xpath(".//input[@name='cvf_captcha_input']")).sendKeys(captchaVal.toString());
						}else {
							break;
						}

						driver2.findElement(By.xpath(".//input[@name='cvf_captcha_captcha_action']")).click();
						Thread.sleep(500);
						errText = driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML");
						counter2++;
						if(counter2>5) {
							flag="Skip";
							finalRes="Skipped:: Anti-Automation Challenge Captcha screen validation failed";
							break;
						}
					}while(errText.contains("Anti-Automation Challenge"));
				}

			}
			//verify OPT steps
			errText = driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML");
			
			if (errText.contains("Verify email address")) {
				System.out.println("inside OTP verify screen");
				String OPTpagevalidation= driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML");
				if(!(OPTpagevalidation.contains("Add mobile number"))) {
					do {
						//check if any error msg in OTP page
						if(driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML").contains("Invalid OTP. Please check your code and try again."))	{
							Thread.sleep(1000);
							driver2.findElement(By.xpath(".//a[contains(text(),'Resend OTP')]")).click();
							System.out.println("invalid OTP, resend OTP again");
							Thread.sleep(2000);
						}
						//moving to gmail account to get the OTP
						driver.findElement(By.xpath(".//a[@title='Inbox']")).click();
						System.out.println("moving to gmail account for OTP");
						Thread.sleep(15000);
						List <WebElement> inbox= driver.findElements(By.xpath("(//table[@class='F cf zt'])[2]//tr//td[6]//span[@class='y2']"));
						for(int i=0;i<inbox.size();i++) {
							if(inbox.get(i).getAttribute("innerText").contains("OTP")) {
								String[] arrOfStr = inbox.get(i).getAttribute("innerText").split(":",7); 
								otp= arrOfStr[1].trim();
								break;
							}
						}
						if(driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML").contains("OTP")) {
							driver2.findElement(By.xpath(".//input[@name='code']")).sendKeys(otp);// i will check later
							driver2.findElement(By.xpath("(//input[@type='submit'])[1]")).click();
							Thread.sleep(6000);
							OPTpagevalidation= driver2.findElement(By.xpath(".//body")).getAttribute("innerHTML");
							flag="Pass";
						}else {
							finalRes="Fail to validate OTP";
							flag="Fail";
							break;
						}
					}while(OPTpagevalidation.contains("Invalid OTP. Please check your code and try again."));

				}else {
					//if add mobile number page will appear then skip the user
					System.out.println("Add Mobile number page appears");
					finalRes="Skipped: Add Mobile number page appears";
					flag="Skip";
				}
			}
			
			
			if(flag.contains("Pass")) {	
				if(driver2.getCurrentUrl().contains("nav_ya_signin")) {
					System.out.println("Login successful username: " + name); 
					Thread.sleep(10000);
					driver2.get("https://www.amazon.com/a/addresses?ref_=ya_d_l_addr");
					driver2.findElement(By.id("ya-myab-plus-address-icon")).click();
					Thread.sleep(1000);
					driver2.findElement(By.id("address-ui-widgets-countryCode")).click();
					Thread.sleep(1000);
					driver2.findElement(By.xpath(".//ul[@class='a-nostyle a-list-link']/li/a[text()='"+country+"']")).click();
					Thread.sleep(2000);
					driver2.findElement(By.id("address-ui-widgets-enterAddressFullName")).sendKeys(name);
					Thread.sleep(1000);
					driver2.findElement(By.id("address-ui-widgets-enterAddressPhoneNumber")).sendKeys(mobile);
					Thread.sleep(1000);
					driver2.findElement(By.id("address-ui-widgets-enterAddressPostalCode")).sendKeys(pin);
					Thread.sleep(1000);
					driver2.findElement(By.id("address-ui-widgets-enterAddressLine1")).sendKeys(house);
					Thread.sleep(1000);
					driver2.findElement(By.id("address-ui-widgets-enterAddressLine2")).sendKeys(street);
					Thread.sleep(1000);
					driver2.findElement(By.id("address-ui-widgets-enterAddressCity")).sendKeys(city);
					Thread.sleep(2000);
					driver2.findElement(By.id("address-ui-widgets-enterAddressStateOrRegion")).click();
					Thread.sleep(2000);
					driver2.findElement(By.xpath(".//a[contains(text(),'"+state+"')]")).click();
					Thread.sleep(3000);
					driver2.findElement(By.className("a-button-input")).click();
					Thread.sleep(5000);
					if(driver2.findElements(By.id("address-ui-widgets-enterAddressPostalCode")).size() != 0){
						driver2.findElement(By.className("a-button-input")).click(); }
					//driver2.findElement(By.xpath(".//span[contains(text(),'Save Address')]")).click();
					//Thread.sleep(1000);
					if(driver2.getCurrentUrl().contains("yaab-enterAddressSucceed")) {
						driver2.findElement(By.xpath(".//span[@id='kyc_xborder_skip_section_label']")).click();
						Thread.sleep(1000);
						driver2.findElement(By.xpath(".//span[@id='kyc-xborder-continue-button']")).click();
						Thread.sleep(1000);
						driver2.findElement(By.id("ya-myab-set-default-shipping-btn-0")).click();
						Thread.sleep(1000);
						if (driver2.getCurrentUrl().contains("setDefaultAddressSuccess")) {
							//signout
							/*driver2.findElement(By.xpath(".//i[@class='hm-icon nav-sprite']")).click();
						Thread.sleep(4000);
						driver2.findElement(By.xpath(".//div[contains(text(),'Sign Out')]")).click();
						Thread.sleep(6000);*/
							//if(driver2.getCurrentUrl().contains("signin")){
							finalRes="Pass";
							System.out.println("IDPassed");
							/*}else {
							finalRes="Fail";
						}*/
						} else {
							System.out.println("setDefaultAddressSuccess not visible");
							finalRes="Success page not appearing after set Default Address";
						}
					}else {
						System.out.println("given address is wrong");
						finalRes="Fail:Given Address is wrong";
					}
				}else {
					// check the reason of signin failure
					finalRes="Fail to Register user";
				}
			}else if(flag.contains("Fail")){
				finalRes=finalRes;
			}
			else if(flag.contains("Skip")){
				finalRes=finalRes;
			}
		}
		}catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(finalRes==null) {
				finalRes="Skipped user";
			}
			driver2.close();
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