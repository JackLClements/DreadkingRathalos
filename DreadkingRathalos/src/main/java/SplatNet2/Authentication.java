/*
 */
package SplatNet2;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.Cookie;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Nintendo has 3 POST requests that require iOS/Android auth and account auth,
 * most info is in JP so this'll be... fun Note - may need packet sniffer
 *
 * @author Jack L. Clements
 */
public class Authentication {
    // Note - Nintendo login token - 71b963c1b7b6d119 always the same
    //user ID & pass

    private static final String CLIENT_ID = "template";

    public static void main(String[] args) {
        try {
            //login();
            //authorise("", "");
            System.out.println(hash("NovkIZFaudAJKoKQIffpuDocHlFmVFphrvOCrEaDQkkUqogQHx"));
            //authorise("","");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String generateRandStr() {
        StringBuilder sb = new StringBuilder();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 50; i++) {
            Random rand = new Random(); //close enough for now
            sb.append(alphabet.charAt(rand.nextInt(52)));
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static String hash(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        //transfer string to bytes, hash
        byte[] textByte = text.getBytes("UTF-8");
        byte[] hash = digest.digest(textByte);
        //readable
        //transfer to base 64
        byte[] base64 = java.util.Base64.getUrlEncoder().encode(hash);
        
        //
        
        
        String decoded = new String(base64, "UTF-8"); //equivalent of bytes.decode in python
        if(decoded.contains("=")){
            decoded = decoded.substring(0, decoded.length()-1);
        }
        return decoded;
    }
    
    public static String decryptBase64(String string) throws UnsupportedEncodingException{
        byte [] base64 = java.util.Base64.getUrlDecoder().decode(string);
        return new String(base64, "UTF-8");
    }

    public static void authorise(String username, String password) throws MalformedURLException, IOException, NoSuchAlgorithmException {

        //login to nintendo account
        URL url = new URL("https://accounts.nintendo.com/connect/1.0.0/authorize");
        HttpURLConnection session = (HttpURLConnection) url.openConnection();
        session.setRequestMethod("GET");
        session.connect();
        String post_login = session.getHeaderField(0);
        System.out.println(post_login);  
        
        /*
        
        
        

        JSONObject nHead = new JSONObject();
        nHead.put("Accept-Encoding", "gzip");
        nHead.put("User-Agent", "OnlineLounge/1.0.4 NASDKAPI Android");
        
        String challengeString = generateRandStr(); //aparrently we need to demonstrate we can use 256-SHA hash because... reasons?
        /*
        JSONObject nPayload = new JSONObject();
        nPayload.put("client_id", CLIENT_ID);
        nPayload.put("redirect_uri", "npf71b963c1b7b6d119://auth"); //TODO - work this out, uses pyformat but for what end?
        nPayload.put("response_type", "session_token_code");
        nPayload.put("scope", "openid user user.birthday user.mii user.screenName");
        nPayload.put("session_token_code_challenge", hash(challengeString)); //needs hashing 
        nPayload.put("session_token_code_challenge_method", "S256");
        nPayload.put("state", generateRandStr());
        nPayload.put("theme", "login_form");
        session.setRequestMethod("GET");
        
        session.setRequestProperty("Content-Type", "application/json");
        session.setRequestProperty("Accept-Encoding", "gzip");
        session.setRequestProperty("User-Agent", "OnlineLounge/1.0.4 NASDKAPI Android");
        
       
        session.setRequestProperty("client_id", CLIENT_ID);
        session.setRequestProperty("redirect_uri", "npf71b963c1b7b6d119://auth"); //TODO - work this out, uses pyformat but for what end?
        session.setRequestProperty("response_type", "session_token_code");
        session.setRequestProperty("scope", "openid user user.birthday user.mii user.screenName");
        session.setRequestProperty("session_token_code_challenge", hash(challengeString)); //needs hashing 
        session.setRequestProperty("session_token_code_challenge_method", "S256");
        session.setRequestProperty("state", generateRandStr());
        session.setRequestProperty("theme", "login_form");
        
        session.connect();
        */ 
    }

    public static void login() throws MalformedURLException {
        //based on work of TODO PUT URLS
        URL url = new URL("https://api-lp1.znc.srv.nintendo.net/v1/Account/GetToken");
        //JSON request for Nintendo Online Services login
        //Needs a session token, which is generated by a login, so I guess we're going from there
        JSONObject nHead = new JSONObject();
        nHead.put("Host", "accounts.nintendo.com");
        nHead.put("Accept-Encoding", "gzip, deflate");
        nHead.put("Content-Type", "application/json;charset=utf-8");
        nHead.put("Accept-Language", "en-US");
        nHead.put("Content-Length", new Integer(437));
        nHead.put("Accept", "application/json");
        nHead.put("Connection", "keep-alive");
        nHead.put("User-Agent", "OnlineLounge/1.0.4 NASDKAPI Android");
        //seems fine
        //System.out.println("Output - " + nHead.toString());

        JSONObject nBody = new JSONObject();
        nBody.put("client_id", "71b963c1b7b6d119");
        nBody.put("session_token", "session_token");
        nBody.put("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer-session-token");

    }
}
