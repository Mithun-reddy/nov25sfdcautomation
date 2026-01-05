package utils;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GmailUtil {

    @Deprecated
    public static synchronized String getOTP() {
    	String otp = null;
        String host = "imap.gmail.com";
        String username = "mithun.r@tekarch.com";
        String password = "rajl wtor cxuv dnjz";

        try {
            // 1. Setup Properties for SSL IMAP
            Properties props = new Properties();
            props.put("mail.store.protocol", "imap");
            props.put("mail.imap.host", host);
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");

            // 2. Create Session and Connect
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imap");
            store.connect(host, username, password);

            // 3. Open Inbox
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY); // Use READ_WRITE if you want to mark emails as read later

            // 4. Search for Unread Messages
            // FlagTerm filters for messages where Flags.Flag.SEEN is false (Unread)
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            System.out.println("Total Unread Messages: " + messages.length);

            // 5. Process the latest message (Last in the array is usually the latest)
            if (messages.length > 0) {
                Message latestMessage = messages[messages.length - 1]; 
                System.out.println("Subject: " + latestMessage.getSubject());
                // Finish resetting your Salesforce password
                // Extract Body
                String body = getTextFromMessage(latestMessage);
                
                // Extract OTP
                otp = extractOTP(body);
                System.out.println("Found OTP: " + otp);
            } else {
                System.out.println("No unread messages found.");
            }

            // 6. Close connection
            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return otp;
    }

    public static synchronized String getOTP(String subject, String from) {
    	String otp = null;
        String host = "imap.gmail.com";
        String username = "mithun.r@tekarch.com";
        String password = "rajl wtor cxuv dnjz";

        try {
            // 1. Setup Properties for SSL IMAP
            Properties props = new Properties();
            props.put("mail.store.protocol", "imap");
            props.put("mail.imap.host", host);
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");

            // 2. Create Session and Connect
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imap");
            store.connect(host, username, password);

            // 3. Open Inbox
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE); // Use READ_WRITE if you want to mark emails as read later

            // 4. Search for Unread Messages from a specific sender with a specific subject
            SearchTerm searchTerm = new AndTerm(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false),
                new FromTerm(new javax.mail.internet.InternetAddress(from))
            );
            Message[] messages = inbox.search(searchTerm);

            System.out.println("Total Unread Messages: " + messages.length);

            // 5. Process the latest message (Last in the array is usually the latest)
            if (messages.length > 0) {
                Message latestMessage = messages[messages.length - 1]; 
                System.out.println("Subject: " + latestMessage.getSubject());
                // Finish resetting your Salesforce password
                // Extract Body
                String body = getTextFromMessage(latestMessage);
                
                // Extract OTP
                otp = extractOTP(body);
                System.out.println("Found OTP: " + otp);
                latestMessage.setFlag(Flags.Flag.SEEN, true);
            } else {
                System.out.println("No unread messages found.");
            }

            // 6. Close connection
            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return otp;
    }

    /**
     * Helper method to handle "Multipart" emails (Emails with attachments/HTML)
     */
    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                // You might want to skip HTML or parse it with Jsoup if the OTP is hidden in tags
                String html = (String) bodyPart.getContent();
                result.append(html); 
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private static String extractOTP(String text) {
        // Regex to find 4 to 6 digits (adjust length as needed)
        Pattern pattern = Pattern.compile("\\b\\d{4,6}\\b");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }
}
