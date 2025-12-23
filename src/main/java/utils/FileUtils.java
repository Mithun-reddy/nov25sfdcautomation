package utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class FileUtils {
	
	
	public static String readPropertiesFile(String filePath, String key) {
		String value = null;
		System.out.println("Reading properties file from: " + filePath);
		Properties properties = new Properties();
		try {
			FileReader input = new FileReader(filePath);
			properties.load(input);
			value = properties.getProperty(key);
			System.out.println("Value for key '" + key + "': " + value);
		} catch (IOException e) {
			System.err.println("Error reading properties file: " + e.getMessage());
		}
		return value;
	}

}
