package demo;
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
 * * Flow:
 * 1. Starts a local server on port 8080.
 * 2. Opens the system browser to the Salesforce Login page.
 * 3. Listens for the callback from Salesforce to capture the authorization code automatically.
 * 4. Exchanges the code for a token and generates the Frontdoor URL.
 */
public class SalesforceAuth2 {

    // --- Configuration: REPLACE THESE PLACEHOLDERS ---
	private static final String CONSUMER_KEY = "3MVG9gTv.DiE8cKRQpLRPb_WDDm.8goZAv.mGXgVl0nMuc04RrtsVlSN8EKvFn_m9hgmH7XWTw7XjfMgBUD5v";
    private static final String CONSUMER_SECRET = "7FF4D19060EECA01241D292A744B6A07C39DEADA8ABA334E86FEC2CD899CBC2E";    
    // TOGGLE FOR SANDBOX
    private static final boolean IS_SANDBOX = false;

    // Endpoints
    private static final String LOGIN_DOMAIN = IS_SANDBOX ? "https://test.salesforce.com" : "https://login.salesforce.com";
    private static final String AUTHORIZE_ENDPOINT = LOGIN_DOMAIN + "/services/oauth2/authorize";
    private static final String TOKEN_ENDPOINT = LOGIN_DOMAIN + "/services/oauth2/token";
    
    // --- LOCAL SERVER CONFIGURATION ---
    // IMPORTANT: You MUST add "http://localhost:8080/callback" to your 
    // Connected App's "Callback URL" list in Salesforce Setup.
    private static final int LOCAL_PORT = 8080;
    private static final String CALLBACK_PATH = "/callback";
    private static final String REDIRECT_URI = "http://localhost:" + LOCAL_PORT + CALLBACK_PATH;
    
    private static final String SCOPE = "id api web refresh_token";

    public static void main(String[] args) {
        try {
            System.out.println("-------------------------------------------------------------");
            System.out.println("           SEAMLESS SALESFORCE AUTHENTICATION                ");
            System.out.println("-------------------------------------------------------------");
            
            // 1. Start Local Server to listen for the redirect
            try (ServerSocket serverSocket = new ServerSocket(LOCAL_PORT)) {
                System.out.println("1. Listening on port " + LOCAL_PORT + " for Salesforce callback...");

                // 2. Build Auth URL
                String authUrl = buildAuthorizationUrl();

                // 3. Open Browser Automatically
                System.out.println("2. Launching system browser for login...");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(authUrl));
                } else {
                    System.out.println("   [!] Auto-open failed. Please open this URL manually:");
                    System.out.println("   " + authUrl);
                }

                // 4. Wait for the browser to redirect back to us
                System.out.println("3. Waiting for you to log in in the browser...");
                String authCode = waitForCallback(serverSocket);
                
                // 5. Exchange Code for Token
                if (authCode != null) {
                    System.out.println("4. Authorization Code captured! Exchanging for token...");
                    exchangeCodeForToken(authCode);
                } else {
                    System.err.println("Error: Failed to retrieve authorization code.");
                }
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Listens for a single incoming HTTP request on the server socket, 
     * extracts the 'code' parameter, and sends a success response to the browser.
     */
    private static String waitForCallback(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket client = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            // Read the HTTP Request Line (e.g., "GET /callback?code=XYZ... HTTP/1.1")
            String line = in.readLine(); 
            
            if (line != null && line.contains(CALLBACK_PATH)) {
                // We found the callback request!
                String code = extractCodeFromRequest(line);
                
                // Send a nice HTML response to the browser
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println("\r\n");
                out.println("<html><head><title>Login Successful</title></head>");
                out.println("<body style='font-family: sans-serif; text-align: center; margin-top: 50px;'>");
                out.println("<h1 style='color: #4CAF50;'>Login Successful!</h1>");
                out.println("<p>The authorization code has been captured.</p>");
                out.println("<p>You can close this tab and return to your application.</p>");
                out.println("<script>window.close();</script>"); // Attempt to close tab
                out.println("</body></html>");
                
                out.close();
                in.close();
                client.close();
                return code;
            }
            
            // If it's not the callback (e.g. favicon request), just close and keep listening
            in.close();
            client.close();
        }
    }

    private static String extractCodeFromRequest(String requestLine) {
        try {
            // Logic to parse "code=..." from the URL
            int codeStart = requestLine.indexOf("code=") + 5;
            int codeEnd = requestLine.indexOf(" ", codeStart); // End of URL in HTTP request
            if (codeEnd == -1) codeEnd = requestLine.length(); 
            
            String code = requestLine.substring(codeStart, codeEnd);
            
            // Strip any other parameters that might follow (like &state=...)
            if (code.contains("&")) {
                code = code.substring(0, code.indexOf("&"));
            }
            
            return URLDecoder.decode(code, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error parsing code from request line: " + requestLine);
            return null;
        }
    }

    private static String buildAuthorizationUrl() throws Exception {
        Map<String, String> params = Map.of(
            "response_type", "code",
            "client_id", CONSUMER_KEY,
            "redirect_uri", REDIRECT_URI,
            "scope", SCOPE
        );
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) queryString.append("&");
            queryString.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return AUTHORIZE_ENDPOINT + "?" + queryString.toString();
    }
    
    private static void exchangeCodeForToken(String authCode) {
        try {
            String formData = buildTokenExchangeFormData(authCode);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKEN_ENDPOINT))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Token Exchange Successful!");
                parseTokenAndGenerateFrontdoor(response.body());
            } else {
                System.err.println("Token Exchange Failed! Status Code: " + response.statusCode());
                System.err.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String buildTokenExchangeFormData(String authCode) {
        Map<String, String> data = Map.of(
            "grant_type", "authorization_code",
            "client_id", CONSUMER_KEY,
            "client_secret", CONSUMER_SECRET,
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
    
    private static void parseTokenAndGenerateFrontdoor(String jsonResponse) {
        try {
            String tokenKey = "\"access_token\":\"";
            int start = jsonResponse.indexOf(tokenKey);
            if(start == -1) return;
            start += tokenKey.length();
            int end = jsonResponse.indexOf("\"", start);
            String accessToken = jsonResponse.substring(start, end);
            
            String instanceKey = "\"instance_url\":\"";
            int instStart = jsonResponse.indexOf(instanceKey) + instanceKey.length();
            int instEnd = jsonResponse.indexOf("\"", instStart);
            String instanceUrl = jsonResponse.substring(instStart, instEnd);

            String frontdoorUrl = instanceUrl + "/secur/frontdoor.jsp?sid=" 
                                + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

            System.out.println("\n-------------------------------------------------------------");
            System.out.println("                   BROWSER LOGIN (Frontdoor)                 ");
            System.out.println("-------------------------------------------------------------");
            System.out.println("Here is your magic link. Paste it into your browser:");
            System.out.println("\n" + frontdoorUrl + "\n");
            System.out.println("-------------------------------------------------------------");

        } catch (Exception e) {
            System.err.println("Could not parse JSON response.");
            e.printStackTrace();
        }
    }
}