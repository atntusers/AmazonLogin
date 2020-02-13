package com.imagedownload;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

public class Download {

   public static void main(String[] args) throws Exception {
   
      try{
         String fileName = "digital_image_processing.jpg";
         String website = "https://opfcaptcha-prod.s3.amazonaws.com/8f46832211374cf487e8e7847d4810e4.jpg?AWSAccessKeyId=AKIA5WBBRBBBTXKHVYV7&Expires=1580985575&Signature=okMgQDGpZ2M2zRys5CsBksu6shI%3D";//+fileName;
         
         System.out.println("Downloading File From: " + website);
         
         URL url = new URL(website);
         InputStream inputStream = url.openStream();
         OutputStream outputStream = new FileOutputStream(fileName);
         byte[] buffer = new byte[2048];
         
         int length = 0;
         
         while ((length = inputStream.read(buffer)) != -1) {
            System.out.println("Buffer Read of length: " + length);
            outputStream.write(buffer, 0, length);
         }
         
         inputStream.close();
         outputStream.close();
         
      } catch(Exception e) {
         System.out.println("Exception: " + e.getMessage());
      }
   }
}