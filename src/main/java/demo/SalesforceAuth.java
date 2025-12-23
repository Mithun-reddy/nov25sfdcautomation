package demo;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Java client for Salesforce OAuth 2.0 Username-Password Flow.
 * This class authenticates with Salesforce to obtain an access token
 * by sending a POST request to the token endpoint.
 * * NOTE: For production use, replace the simple String parsing with a robust
 * JSON library (like Jackson or Gson) and manage credentials securely 
 * (e.g., environment variables, secret manager).
 */
public class SalesforceAuth {

    // --- Configuration: REPLACE THESE PLACEHOLDERS ---
    private static final String CONSUMER_KEY = "3MVG9gTv.DiE8cKRQpLRPb_WDDm.8goZAv.mGXgVl0nMuc04RrtsVlSN8EKvFn_m9hgmH7XWTw7XjfMgBUD5v";
    private static final String CONSUMER_SECRET = "7FF4D19060EECA01241D292A744B6A07C39DEADA8ABA334E86FEC2CD899CBC2E";
    private static final String USERNAME = "jul22.mithun@ta.com";
    // NOTE: The password must be followed by the Security Token if you haven't 
    // whitelisted your IP address. If the security token is 'XYZ', the combined
    // string is 'your_passwordXYZ'.
    private static final String PASSWORD_WITH_TOKEN = "November@20252PleghLMQWprIVwbsZ7euvCK";
    
    private static final String AUTH_URL = "https://login.salesforce.com/services/oauth2/token";

    public static void main(String[] args) {
        try {
            // 1. Build the form data payload
            String formData = buildFormData();

            // 2. Configure the HTTP client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            // 3. Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Check status and process result
            if (response.statusCode() == 200) {
                System.out.println("Authentication Successful!");
                System.out.println("Raw Response:");
                System.out.println(response.body());
                
                // Simple parsing (for demonstration purposes only)
                parseAndDisplayToken(response.body());

            } else {
                System.err.println("Authentication Failed! Status Code: " + response.statusCode());
                System.err.println("Error Response:");
                System.err.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred during the API call: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Builds the URL-encoded string required for the POST request body.
     */
    private static String buildFormData() {
        Map<String, String> data = Map.of(
            "grant_type", "password",
            "client_id", CONSUMER_KEY,
            "client_secret", CONSUMER_SECRET,
            "username", USERNAME,
            "password", PASSWORD_WITH_TOKEN
        );

        StringBuilder formBody = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (formBody.length() > 0) {
                formBody.append("&");
            }
            // URL encode the keys and values
            formBody.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            formBody.append("=");
            formBody.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return formBody.toString();
    }
    
    /**
     * Extracts and displays the access token and instance URL from the JSON response.
     */
    private static void parseAndDisplayToken(String jsonResponse) {
        try {
            // Find access_token
            String tokenKey = "\"access_token\":\"";
            int start = jsonResponse.indexOf(tokenKey) + tokenKey.length();
            int end = jsonResponse.indexOf("\"", start);
            String accessToken = jsonResponse.substring(start, end);

            // Find instance_url
            String instanceKey = "\"instance_url\":\"";
            start = jsonResponse.indexOf(instanceKey) + instanceKey.length();
            end = jsonResponse.indexOf("\"", start);
            String instanceUrl = jsonResponse.substring(start, end);

            System.out.println("\nExtracted Credentials:");
            System.out.println("  Access Token: " + accessToken.substring(0, 10) + "... (truncated for display)");
            System.out.println("  Instance URL: " + instanceUrl);
            
            // This token is now used to authenticate subsequent REST API calls to the instance URL
            // Example: GET /services/data/v60.0/sobjects/Account
            
        } catch (Exception e) {
            System.err.println("Could not parse the JSON response for token and instance URL.");
        }
    }
}