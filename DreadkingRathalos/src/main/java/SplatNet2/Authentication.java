/*
 */
package SplatNet2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okio.ByteString;
/**
 * Nintendo has 3 POST requests that require iOS/Android auth and account auth,
 * most info is in JP so this'll be... fun Note - may need packet sniffer
 *
 * @author Jack L. Clements
 */
public class Authentication {
    // Note - Nintendo login token - 71b963c1b7b6d119 always the same
    //user ID & pass

    private static final String CLIENT_ID = "71b963c1b7b6d119";
    
    public static String COOKIE;
    
    public static void main(String[] args) {
        try {
            //login();
            //authorise("", "");
            

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
    
    public static String hmac(String username, String password, String csrfToken){
        String answer = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            
            
            //transform strings into bytes
            String msg = username + ":" + password + ":" + csrfToken;
            String key = csrfToken.substring(csrfToken.length()-9, csrfToken.length()-1);
            
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
            byte [] finished = mac.doFinal(msg.getBytes());
            answer = bytesToHex(finished);
        } catch (Exception ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;
    }
    
    public static String getAccessToken(String input){
        OkHttpClient client = new OkHttpClient();
        Headers.Builder head = new Headers.Builder();
        head.add("Accept", "application/json");
        head.add("User-Agent", "com.nintendo.znca/1.0.4 (Android/6.0.1)");
        
        JSONObject nPayload = new JSONObject();
        nPayload.put("client_id", CLIENT_ID);
        nPayload.put("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer-session-token");
        nPayload.put("session_token", input);
        
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), nPayload.toString());
        
        Request request = new Request.Builder().url("https://accounts.nintendo.com/connect/1.0.0/api/token").headers(head.build()).post(body).build();
        String response2 = "";
        try(Response response = client.newCall(request).execute()){
            response2 = response.body().string();
        } catch (IOException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject nResponse = new JSONObject(response2);
        String idToken = nResponse.getString("id_token");
        String authToken = nResponse.getString("access_token");
        

        JSONObject nInnerPayload = new JSONObject();
        
        nInnerPayload.put("naIdToken", idToken);
        nInnerPayload.put("naCountry", "null");
        nInnerPayload.put("naBirthday", "null");
        nInnerPayload.put("language", "null");
        
        JSONObject nOuterPayload = new JSONObject();
        nOuterPayload.put("parameter", nInnerPayload);
        
        Headers.Builder head2 = new Headers.Builder();
        head2.add("Accept", "application/json");
        //head2.add("Accept-Encoding", "gzip");
        head2.add("User-Agent", "com.nintendo.znca/1.0.4 (Android/6.0.1)");
        head2.add("Authorization", "Bearer " + authToken);
        
        RequestBody body2 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), nOuterPayload.toString());
        Request request2 = new Request.Builder().url("https://api-lp1.znc.srv.nintendo.net/v1/Account/GetToken").headers(head2.build()).post(body2).build();
        String response3 = "";
        try(Response response = client.newCall(request2).execute()){
            response3 = response.body().string();
        } catch (IOException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject nResponse2 = new JSONObject(response3);
        JSONObject result = new JSONObject(nResponse2.get("result").toString());
        
        JSONObject credentials = new JSONObject(result.get("webApiServerCredential").toString());
        String accessToken = credentials.get("accessToken").toString();
        
        Headers.Builder head3 = new Headers.Builder();
        head3.add("Accept", "application/json");
        head3.add("User-Agent", "com.nintendo.znca/1.0.4 (Android/6.0.1)");
        head3.add("Authorization", "Bearer " + accessToken);
        
        //body
        RequestBody body3 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        
        Request request3 = new Request.Builder().url("https://api-lp1.znc.srv.nintendo.net/v1/Game/ListWebServices").headers(head3.build()).post(body).build();
        String response4 = "";
        try(Response response = client.newCall(request3).execute()){
            response4 = response.body().string();
        } catch (IOException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        JSONObject nResponse3 = new JSONObject(response4);
        JSONArray services = new JSONArray(nResponse3.get("result").toString());
        //Just a reminder
        /*
        for(int i = 0; i < services.length(); i++){
            System.out.println(services.getJSONObject(i).get("name"));
        }*/
        //final selection
        JSONObject nSelection = new JSONObject();
        nSelection.put("id", services.getJSONObject(0).get("id"));
        
        JSONObject nSelector = new JSONObject();
        nSelector.put("parameter", nSelection);
        
        //body
        RequestBody body4 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), nSelector.toString());
        
        Request request4 = new Request.Builder().url("https://api-lp1.znc.srv.nintendo.net/v1/Game/GetWebServiceToken").headers(head3.build()).post(body4).build();
        String response5 = "";
        try(Response response = client.newCall(request4).execute()){
            response5 = response.body().string();
        } catch (IOException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject finalToken = new JSONObject(response5);
        
        return finalToken.toString();
    }

    public static void authorise(String username, String password) throws MalformedURLException, IOException, NoSuchAlgorithmException {

        //login to nintendo account
        OkHttpClient client = new OkHttpClient();
        Headers.Builder head = new Headers.Builder();
        head.add("Accept", "application/json");
        head.add("User-Agent", "com.nintendo.znca/1.0.4 (Android/6.0.1)");      
       
        String verifier = generateRandStr();
        
        HttpUrl url = new HttpUrl.Builder().scheme("https").host("accounts.nintendo.com").addPathSegment("connect").addPathSegment("1.0.0").addPathSegment("authorize")
                .addQueryParameter("client_id", CLIENT_ID).addQueryParameter("redirect_uri", "npf71b963c1b7b6d119://auth").addQueryParameter("response_type", "session_token_code")
                .addQueryParameter("scope", "openid user user.birthday user.mii user.screenName").addQueryParameter("session_token_code_challenge", hash(verifier))
                .addQueryParameter("session_token_code_challenge_method", "S256").addQueryParameter("state", generateRandStr()).addQueryParameter("theme", "login_form").build();
        
        Request request = new Request.Builder().url(url).headers(head.build()).build();
         
        Response response = client.newCall(request).execute();
        //System.out.println(response.body().string());
        
        String pattern = "eyJhbGciOiJIUzI1NiJ9\\.[a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*";
        Pattern pat = Pattern.compile(pattern);
        
        Matcher matt = pat.matcher(response.body().string());
        matt.find();
        String csrfToken = matt.group();
        
        //decrypt response key
        String splitKey = csrfToken.split("\\.")[1];
        
        JSONObject tokenFinder = new JSONObject(decryptBase64(splitKey));
        JSONObject ext = new JSONObject(tokenFinder.get("_ext").toString());
        JSONObject p = new JSONObject(ext.get("p").toString());
        
        String redirect = p.getString("post_login_redirect_uri");
        
        System.out.println(hmac(username, password, csrfToken));
        
        JSONObject nPayload = new JSONObject();
        nPayload.put("csrf_token", csrfToken);
        nPayload.put("display", "");
        nPayload.put("post_login_redirect_uri", redirect);
        nPayload.put("redirect_after", "5");
        nPayload.put("subject_id", username);
        nPayload.put("subject_id", password);
        nPayload.put("_h", hmac(username, password, csrfToken));
        
        RequestBody payload = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), nPayload.toString());
        Request login = new Request.Builder().url("https://accounts.nintendo.com/login").headers(head.build()).post(payload).build();
        
        String responseStr;
        try(Response response2 = client.newCall(login).execute()){
             responseStr = response2.body().string();
        }
        Matcher matt2 = pat.matcher(responseStr);
        matt.find();
        String tokenCode = matt.group();
        JSONObject nCookie = new JSONObject(decryptBase64(tokenCode.split("\\.")[1]));
        String session = nCookie.getString("typ");
        if(session.equals("csrf_token")){
            System.out.println("Ya fucked up");
        }
        if(session.equals("session_token_code")){
            System.out.println("You're in");
            getCookie(tokenCode, verifier, client);
            System.out.println(COOKIE);
        }
    }
    
    public static void getCookie(String token_code, String verifier, OkHttpClient client){
        Headers.Builder head = new Headers.Builder();
        head.add("Accept", "application/json");
        head.add("User-Agent", "com.nintendo.znca/1.0.4 (Android/6.0.1)");  
        
        JSONObject nPayload = new JSONObject();
        nPayload.put("client_id", CLIENT_ID);
        nPayload.put("session_token_code", token_code);
        nPayload.put("session_token_code_verifier", verifier);
        
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), nPayload.toString());
        Request rCookie = new Request.Builder().url("https://accounts.nintendo.com/connect/1.0.0/api/session_token").headers(head.build()).post(body).build();
        
        
        try(Response response = client.newCall(rCookie).execute()){
            JSONObject nResponse = new JSONObject(response.body().string());
            COOKIE = nResponse.getString("session_token");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
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
