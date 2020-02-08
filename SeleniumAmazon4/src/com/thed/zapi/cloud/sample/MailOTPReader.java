package com.thed.zapi.cloud.sample;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiMessage;
import javax.mail.*;
public class MailOTPReader {

	String hostName = "imap.gmail.com";
	String username = "admin@fgtsoft.site";
	String password = "Ram@12345";
	int messageCount;
	int unreadMsgCount;
	String emailSubject;
	Message emailMessage;
	String OTP;
	
	public String getOTP() {

		Properties props = new Properties();
		props.setProperty("http.proxySet", "true");
		//props.setProperty("http.proxyHost", "proxyHost");
		//props.setProperty("http.proxyPort", "proxyPort");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.store.protocol", "imaps");
	    //sysProps.setProperty("mail.store.protocol", "imaps");

	    try {
	        Session session = Session.getInstance(props, null);
	        Store store = session.getStore("imaps");
	        store.connect(hostName, username, password);
	        Folder emailInbox = store.getFolder("INBOX");
	        emailInbox.open(Folder.READ_WRITE);
	        messageCount = emailInbox.getMessageCount();
	        System.out.println("Total Message Count: " + messageCount);
	        unreadMsgCount = emailInbox.getNewMessageCount();
	        System.out.println("Unread Emails count:" + unreadMsgCount);
	        emailMessage = emailInbox.getMessage(messageCount);
	        emailSubject = emailMessage.getSubject();

	        Pattern linkPattern = Pattern.compile("href=\"(.*)\" target"); // here you need to define regex as per you need
	        Matcher pageMatcher =
	                linkPattern.matcher(emailMessage.getContent().toString());

	        while (pageMatcher.find()) {
	            System.out.println("Found OTP " + pageMatcher.group(1));
	            OTP = pageMatcher.group(1);
	        }
	        emailMessage.setFlag(Flags.Flag.SEEN, true);
	        emailInbox.close(true);
	        store.close();

	    } catch (Exception mex) {
	        mex.printStackTrace();
	    }
	
		return OTP;
	}

	public void setOPT(String oTP) {
		OTP = oTP;
	}

}

