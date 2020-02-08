package com.thed.zapi.cloud.sample;
import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.Exception;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;

import java.io.IOException;


class ExampleNewRecaptchaToken
{
    /* Put your DeathByCaptcha account username and password here.
       Use HttpClient for HTTP API. */
	public static void main(String[] args) throws IOException, Exception, InterruptedException
	{
	
		Client client = (Client)(new SocketClient("allinonerc@gmail.com", "Abi2015@"));
	    try {
	        double balance = client.getBalance();
	
	        /* Put your CAPTCHA file name, or file object, or arbitrary input stream,
	           or an array of bytes, and optional solving timeout (in seconds) here: */
	        Captcha captcha = null;
            try {
                // Upload a CAPTCHA and poll for its status with 120 seconds timeout.
                // Put you CAPTCHA image file name, file object, input stream, or
                // vector of bytes, and solving timeout (in seconds) if 0 the default value take place.
                // please note we are specifying type=2 in the second argument 
                captcha = client.decode(System.getProperty("user.dir")+"/sampleimg.jpg", 2, 0);
            } catch (IOException e) {
                System.out.println("Failed uploading CAPTCHA");
                return;
            }
	        //Captcha captcha = client.decode("https://opfcaptcha-prod.s3.amazonaws.com/d7f2253859f346359a45d8318a1c8bb9.jpg?AWSAccessKeyId=AKIA5WBBRBBBTXKHVYV7&Expires=1580929480&Signature=rZWFnTAYHU%2F4n4Qxto2pJHWu1Us%3D", 2, 0);
	        //Captcha captcha = client.decode("https://opfcaptcha-prod.s3.amazonaws.com/ee5cf09962a44da9843dda8c2e751459.jpg?AWSAccessKeyId=AKIA5WBBRBBBTXKHVYV7&Expires=1580928837&Signature=T7%2FMisojFku2bN1EXyexFbus%2FKs%3D",1580928171);
	        if (null != captcha) {
	            /* The CAPTCHA was solved; captcha.id property holds its numeric ID,
	               and captcha.text holds its text. */
	            System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);
	
	            if (client.report(captcha)) {
                    System.out.println("Reported as incorrectly solved");
                } else {
                    System.out.println("Failed reporting incorrectly solved CAPTCHA");
                }
	        }
	    } catch (AccessDeniedException e) {
	        /* Access to DBC API denied, check your credentials and/or balance */
	    }
	}
}