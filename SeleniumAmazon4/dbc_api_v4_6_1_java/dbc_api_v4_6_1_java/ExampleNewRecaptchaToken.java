import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;

import java.io.IOException;


class ExampleNewRecaptchaToken
{
    public static void main(String[] args)
        throws Exception
    {
        // EXAMPLE RECAPTCHA_TOKEN.
 
        // Put your DBC username & password here:
        //Client client = (Client)(new SocketClient(args[0], args[1]));
        String username = "username";
        String password = "password";
        Client client = (Client)(new HttpClient(username, password));
        client.isVerbose = true;

        try {
            try {
                System.out.println("Your balance is " + client.getBalance() + " US cents");
            } catch (IOException e) {
                System.out.println("Failed fetching balance: " + e.toString());
                return;
            }

            Captcha captcha = null;
            try {
                // Upload a reCAPTCHA and poll for its status with 120 seconds timeout.
                // Put your proxy, proxy type, page googlekey, page url and solving timeout (in seconds)
                // 0 or nothing for the default timeout value. 
                captcha = client.decode("http://user:password@127.0.0.1:1234","http","6Lc2fhwTAAAAAGatXTzFYfvlQMI2T7B6ji8UVV_f","http://google.com");
                
                //other method is to send a json with the parameters
                //
                // JSONObject json_params = new JSONObject();
                // json_params.put("proxy",proxy);
                // json_params.put("proxytype",proxytype);
                // json_params.put("googlekey",sitekey);
                // json_params.put("pageurl",pageurl);
                //captcha = client.decode(json_params);
            } catch (IOException e) {
                System.out.println("Failed uploading CAPTCHA");
                return;
            }
           
            if (null != captcha) {
                System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

                // Report incorrectly solved CAPTCHA if necessary.
                // Make sure you've checked if the CAPTCHA was in fact incorrectly
                // solved, or else you might get banned as abuser.
                /*try {
                    if (client.report(captcha)) {
                        System.out.println("Reported as incorrectly solved");
                    } else {
                        System.out.println("Failed reporting incorrectly solved CAPTCHA");
                    }
                } catch (IOException e) {
                    System.out.println("Failed reporting incorrectly solved CAPTCHA: " + e.toString());
                }*/
            } else {
                System.out.println("Failed solving CAPTCHA");
            }
        } catch (com.DeathByCaptcha.Exception e) {
            System.out.println(e);
        }

       
    }
}
