/*
 */
package SplatNet2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import org.json.JSONObject;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okio.Buffer;
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

    private static HashSet<String> COOKIES = new HashSet<>();

    public static String COOKIE; //ought to change, is session token

    public static void addCookies(HashSet<String> set) {
        for (String cookie : set) {
            COOKIES.add(cookie);
        }
    }

    public static HashSet<String> getCookies() {
        return COOKIES;
    }
    /*
    public static void main(String[] args) {
        try {

            //login();
            //authorise("", "");
            //System.out.println(hash("NovkIZFaudAJKoKQIffpuDocHlFmVFphrvOCrEaDQkkUqogQHx"));
            //System.out.println(getAccessToken("eyJhbGciOiJIUzI1NiJ9.eyJzdDpzY3AiOlswLDgsOSwxNywyM10sImp0aSI6IjEzOTI5MDUzNCIsInR5cCI6InNlc3Npb25fdG9rZW4iLCJhdWQiOiI3MWI5NjNjMWI3YjZkMTE5IiwiaWF0IjoxNTAzNzgzODE2LCJzdWIiOiJkOThhZTEyOWYzYzVlYmI1IiwiaXNzIjoiaHR0cHM6Ly9hY2NvdW50cy5uaW50ZW5kby5jb20iLCJleHAiOjE1NjY4NTU4MTZ9.vDLp8Aq261TMSETyzLez2YxWIrByNFsu4OAFeligy70"));
            //login("1", "2");
            /*
            Note - I have *some* idea why it rejects the token
            1. If the token you send back is explicitly incorrect it displays the error
            2. If the username/password is incorrect it displays the other error
            3. If the hmac is incorrect even with csrf token correct it displays the other error
            4. Cogito ergo sum -
            
            Need to further check the token against the sending method. 
            May be pertinent to remake the entire thing.
            

            //System.out.println(hmac("jackclements95@gmail.com", "Iamkira95", "eyJhbGciOiJIUzI1NiJ9.eyJfZXh0Ijp7InQiOiIzNTU3NTA4NDY0IiwicCI6eyJwb3N0X2xvZ2luX3JlZGlyZWN0X3VyaSI6Imh0dHBzOi8vYWNjb3VudHMubmludGVuZG8uY29tL2Nvbm5lY3QvMS4wLjAvYXV0aG9yaXplP2NsaWVudF9pZD03MWI5NjNjMWI3YjZkMTE5JnJlZGlyZWN0X3VyaT1ucGY3MWI5NjNjMWI3YjZkMTE5JTNBJTJGJTJGYXV0aCZyZXNwb25zZV90eXBlPXNlc3Npb25fdG9rZW5fY29kZSZzY29wZT1vcGVuaWQrdXNlcit1c2VyLmJpcnRoZGF5K3VzZXIubWlpK3VzZXIuc2NyZWVuTmFtZSZzZXNzaW9uX3Rva2VuX2NvZGVfY2hhbGxlbmdlPVQ3TGlzMF9YYXlNanRHTGNKalk3bUM3c094RUVXTGpzM2pTazBWaDA2X1Umc2Vzc2lvbl90b2tlbl9jb2RlX2NoYWxsZW5nZV9tZXRob2Q9UzI1NiZzdGF0ZT1BVGxYdkdJZ1F1U2JYa3dBS1ZGQUtDTEJSWVpEUnhmT1h4S2dLYU5yd0xRa2lpVmRZaSZ0aGVtZT1sb2dpbl9mb3JtIn0sImEiOiJhY2NvdW50c19sb2dpbl9mb3JtIn0sImV4cCI6MTUwMzc2OTM2MCwic3ViIjoiMCIsImlzcyI6Imh0dHBzOi8vYWNjb3VudHMubmludGVuZG8uY29tIiwiaWF0IjoxNTAzNzY1NzYwLCJqdGkiOiJhZTc1ZTNjNy01OTVhLTQxYjEtODgzNi0xNGVjMDc3ZjFkYzMiLCJ0eXAiOiJjc3JmX3Rva2VuIn0.nVXIsOHFk0fwCOw8XXRYOvH693wVzDR3YoIJJ0cTO7c"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

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
        if (decoded.contains("=")) {
            decoded = decoded.substring(0, decoded.length() - 1);
        }
        return decoded;
    }

    public static String decryptBase64(String string) throws UnsupportedEncodingException {
        byte[] base64 = java.util.Base64.getUrlDecoder().decode(string);
        return new String(base64, "UTF-8");
    }

    public static String unpack(String token) throws UnsupportedEncodingException {
        String data = token.split("\\.")[1];
        /*
        if(data.length()%4 != 0){
            System.out.println("Too short");
            for(int i = 0; i < (4-(data.length()%4)); i++){
                data = data.concat("=");
            }
        }*/
        String dataBase = decryptBase64(data);
        return dataBase;
    }

    public static String hmac(String username, String password, String csrfToken) {
        String answer = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            //transform strings into bytes
            String msg = username + ":" + password + ":" + csrfToken;
            String key = csrfToken.substring(csrfToken.length() - 8, csrfToken.length());
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
            byte[] finished = mac.doFinal(msg.getBytes());
            answer = bytesToHex(finished);
        } catch (Exception ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;
    }

    public static String getAccessToken(String input) {
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
        System.out.println("Request 1 - " + request.toString());
        System.out.println(request.url());
        String response2 = "";
        try (Response response = client.newCall(request).execute()) {
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
        System.out.println("Request 2 - " + request2.toString());
        String response3 = "";
        try (Response response = client.newCall(request2).execute()) {
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
        try (Response response = client.newCall(request3).execute()) {
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
        try (Response response = client.newCall(request4).execute()) {
            response5 = response.body().string();
        } catch (IOException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }

        JSONObject finalToken = new JSONObject(response5);

        return finalToken.toString();
    }
    

    public static void authorise(String username, String password) throws MalformedURLException, IOException, NoSuchAlgorithmException {

        //login to nintendo account
        //finally set the cookie handler on client
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new AddCookiesInterceptor()).addInterceptor(new ReceivedCookiesInterceptor()).build();

        //ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache());
        Headers.Builder head = new Headers.Builder();
        head.add("Accept", "application/json");
        head.add("User-Agent", "OnlineLounge/1.0.4 NASDKAPI Android");
        //gzip encoding/decoding done as standard
        String verifier = generateRandStr();

        HttpUrl url = new HttpUrl.Builder().scheme("https").host("accounts.nintendo.com").addPathSegment("connect").addPathSegment("1.0.0").addPathSegment("authorize")
                .addQueryParameter("client_id", CLIENT_ID).addEncodedQueryParameter("redirect_uri", "npf71b963c1b7b6d119%3A%2F%2Fauth").addQueryParameter("response_type", "session_token_code")
                .addEncodedQueryParameter("scope", "openid+user+user.birthday+user.mii+user.screenName").addQueryParameter("session_token_code_challenge", hash(verifier))
                .addQueryParameter("session_token_code_challenge_method", "S256").addQueryParameter("state", generateRandStr()).addQueryParameter("theme", "login_form").build();

        Request request = new Request.Builder().url(url).headers(head.build()).build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
        /*
        String pattern = "(eyJhbGciOiJIUzI1NiJ9\\.[a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*)";
        Pattern pat = Pattern.compile(pattern);
        Matcher matt = pat.matcher(response.body().string());
        matt.find();
        String csrfToken = matt.group();*/

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter token code");
        String csrfToken = scan.nextLine();

        //decrypt response key
        //params is for GET-style URL parameters, data is for POST-style body information
        String tokenKey = unpack(csrfToken);
        JSONObject tokenFinder = new JSONObject(tokenKey);
        JSONObject ext = new JSONObject(tokenFinder.get("_ext").toString());
        JSONObject p = new JSONObject(ext.get("p").toString());

        String redirect = p.getString("post_login_redirect_uri");

        JSONObject nPayload = new JSONObject();
        nPayload.put("csrf_token", csrfToken);
        nPayload.put("display", "");
        nPayload.put("post_login_redirect_uri", redirect);
        nPayload.put("redirect_after", 5);
        nPayload.put("subject_id", username);
        nPayload.put("subject_password", password);
        nPayload.put("_h", hmac(username, password, csrfToken));
        //build request

        RequestBody payload = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), nPayload.toString());

        Request login = new Request.Builder().url("https://accounts.nintendo.com/login").headers(head.build()).post(payload).build();
        //System.out.println("Request - " + login.headers().toString());
        Response response2 = client.newCall(login).execute();
        String responseStr = response2.body().string();

        /*
            Matcher matt2 = pat.matcher(responseStr);
            
            matt2.find();
            String tokenCode = matt2.group();
            JSONObject nCookie = new JSONObject(decryptBase64(tokenCode.split("\\.")[1]));
            System.out.println(nCookie.toString());
            String session = nCookie.getString("typ");
            System.out.println(session);
            if(session.equals("csrf_token")){
            }
            if(session.equals("session_token_code")){
            System.out.println("You're in");
            getCookie(tokenCode, verifier, client);
            System.out.println(COOKIE);
            }*/
    }

    public static void getCookie(String token_code, String verifier, OkHttpClient client) {
        Headers.Builder head = new Headers.Builder();
        head.add("Accept", "application/json");
        head.add("User-Agent", "com.nintendo.znca/1.0.4 (Android/6.0.1)");

        JSONObject nPayload = new JSONObject();
        nPayload.put("client_id", CLIENT_ID);
        nPayload.put("session_token_code", token_code);
        nPayload.put("session_token_code_verifier", verifier);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), nPayload.toString());
        Request rCookie = new Request.Builder().url("https://accounts.nintendo.com/connect/1.0.0/api/session_token").headers(head.build()).post(body).build();

        try (Response response = client.newCall(rCookie).execute()) {
            JSONObject nResponse = new JSONObject(response.body().string());
            COOKIE = nResponse.getString("session_token");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
