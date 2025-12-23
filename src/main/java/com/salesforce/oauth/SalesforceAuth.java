package com.salesforce.oauth; // Added package declaration

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Java client for Salesforce OAuth 2.0 Web Server Flow (SEAMLESS / AUTOMATED).
 * * REFACTOR UPDATE:
 * - Configurable via Constructor or Environment Variables.
 * - Methods now RETURN the result (String) instead of just printing, making it usable as a library.
 */
public class SalesforceAuth {

    // Instance variables for configuration
    private final String consumerKey;
    private final String consumerSecret;
    private final boolean isSandbox;

    // Endpoints
    private static final String PROD_LOGIN = "https://login.salesforce.com";
    private static final String SANDBOX_LOGIN = "https://test.salesforce.com";
    
    // Local Server Config
    private static final int LOCAL_PORT = 8080;
    private static final String CALLBACK_PATH = "/callback";
    private static final String REDIRECT_URI = "http://localhost:" + LOCAL_PORT + CALLBACK_PATH;
    private static final String SCOPE = "id api web refresh_token";

    /**
     * Constructor to configure the client dynamically.
     * @param consumerKey    Your Salesforce Connected App Consumer Key
     * @param consumerSecret Your Salesforce Connected App Consumer Secret
     * @param isSandbox      True if connecting to a Sandbox environment
     */
    public SalesforceAuth(String consumerKey, String consumerSecret, boolean isSandbox) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.isSandbox = isSandbox;
    }

    /**
     * Entry point for the application when run standalone.
     */
    public static void main(String[] args) {
        // 1. Try to load from Environment Variables
        String envKey = System.getenv("SALESFORCE_CONSUMER_KEY");
        String envSecret = System.getenv("SALESFORCE_CONSUMER_SECRET");
        
        // 2. Fallback to placeholders if env vars are missing
        String key = (envKey != null) ? envKey : "YOUR_CONSUMER_KEY";
        String secret = (envSecret != null) ? envSecret : "YOUR_CONSUMER_SECRET";
        boolean sandbox = false;

        // 3. Create instance and run
        SalesforceAuth authClient = new SalesforceAuth(key, secret, sandbox);
        String result = authClient.start();
        
        // 4. Output result (since we are in main)
        if (result != null) {
            System.out.println("\nSUCCESS! Magic Link generated:");
            System.out.println(result);
        } else {
            System.err.println("\nFAILURE: Could not generate magic link.");
        }
    }

    /**
     * Starts the authentication flow.
     * @return The Frontdoor URL (Magic Link) if successful, or null if failed.
     */
    public String start() {
        try {
            System.out.println("-------------------------------------------------------------");
            System.out.println("           SEAMLESS SALESFORCE AUTHENTICATION                ");
            System.out.println("-------------------------------------------------------------");
            
            if (consumerKey.equals("YOUR_CONSUMER_KEY") || consumerSecret.equals("YOUR_CONSUMER_SECRET")) {
                System.err.println("WARNING: Using placeholder credentials. Please set SALESFORCE_CONSUMER_KEY and SALESFORCE_CONSUMER_SECRET environment variables or edit the defaults in main().");
            }

            try (ServerSocket serverSocket = new ServerSocket(LOCAL_PORT)) {
                System.out.println("1. Listening on port " + LOCAL_PORT + " for Salesforce callback...");

                String authUrl = buildAuthorizationUrl();

                System.out.println("2. Launching system browser for login...");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(authUrl));
                } else {
                    System.out.println("   [!] Auto-open failed. Please open this URL manually:");
                    System.out.println("   " + authUrl);
                }

                System.out.println("3. Waiting for you to log in in the browser...");
                String authCode = waitForCallback(serverSocket);
                
                if (authCode != null) {
                    System.out.println("4. Authorization Code captured! Exchanging for token...");
                    return exchangeCodeForToken(authCode);
                } else {
                    System.err.println("Error: Failed to retrieve authorization code.");
                    return null;
                }
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String waitForCallback(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket client = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line = in.readLine(); 
            
            if (line != null && line.contains(CALLBACK_PATH)) {
                String code = extractCodeFromRequest(line);
                
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println("\r\n");
                out.println("<html><head><title>Login Successful</title></head>");
                out.println("<body style='font-family: sans-serif; text-align: center; margin-top: 50px;'>");
                out.println("<h1 style='color: #4CAF50;'>Login Successful!</h1>");
                out.println("<p>The authorization code has been captured.</p>");
                out.println("<p>You can close this tab and return to your application.</p>");
                out.println("<script>window.close();</script>"); 
                out.println("</body></html>");
                
                out.close();
                in.close();
                client.close();
                return code;
            }
            in.close();
            client.close();
        }
    }

    private String extractCodeFromRequest(String requestLine) {
        try {
            int codeStart = requestLine.indexOf("code=") + 5;
            int codeEnd = requestLine.indexOf(" ", codeStart); 
            if (codeEnd == -1) codeEnd = requestLine.length(); 
            
            String code = requestLine.substring(codeStart, codeEnd);
            if (code.contains("&")) {
                code = code.substring(0, code.indexOf("&"));
            }
            return URLDecoder.decode(code, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error parsing code from request line: " + requestLine);
            return null;
        }
    }

    private String buildAuthorizationUrl() throws Exception {
        String domain = isSandbox ? SANDBOX_LOGIN : PROD_LOGIN;
        String authEndpoint = domain + "/services/oauth2/authorize";

        Map<String, String> params = Map.of(
            "response_type", "code",
            "client_id", consumerKey, // Uses instance variable
            "redirect_uri", REDIRECT_URI,
            "scope", SCOPE
        );
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) queryString.append("&");
            queryString.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return authEndpoint + "?" + queryString.toString();
    }
    
    private String exchangeCodeForToken(String authCode) {
        try {
            String domain = isSandbox ? SANDBOX_LOGIN : PROD_LOGIN;
            String tokenEndpoint = domain + "/services/oauth2/token";
            
            String formData = buildTokenExchangeFormData(authCode);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenEndpoint))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Token Exchange Successful!");
                return parseTokenAndGenerateFrontdoor(response.body());
            } else {
                System.err.println("Token Exchange Failed! Status Code: " + response.statusCode());
                System.err.println(response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildTokenExchangeFormData(String authCode) {
        Map<String, String> data = Map.of(
            "grant_type", "authorization_code",
            "client_id", consumerKey,      // Uses instance variable
            "client_secret", consumerSecret, // Uses instance variable
            "redirect_uri", REDIRECT_URI,
            "code", authCode
        );
        StringBuilder formBody = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (formBody.length() > 0) formBody.append("&");
            formBody.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return formBody.toString();
    }
    
    private String parseTokenAndGenerateFrontdoor(String jsonResponse) {
        try {
            String tokenKey = "\"access_token\":\"";
            int start = jsonResponse.indexOf(tokenKey);
            if(start == -1) return null;
            start += tokenKey.length();
            int end = jsonResponse.indexOf("\"", start);
            String accessToken = jsonResponse.substring(start, end);
            
            String instanceKey = "\"instance_url\":\"";
            int instStart = jsonResponse.indexOf(instanceKey) + instanceKey.length();
            int instEnd = jsonResponse.indexOf("\"", instStart);
            String instanceUrl = jsonResponse.substring(instStart, instEnd);

            // Construct and RETURN the URL
            return instanceUrl + "/secur/frontdoor.jsp?sid=" 
                                + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

        } catch (Exception e) {
            System.err.println("Could not parse JSON response.");
            e.printStackTrace();
            return null;
        }
    }
}