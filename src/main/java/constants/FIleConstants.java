package constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FIleConstants {
	
	static String dateStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
	public static final String  ROOT_PATH = System.getProperty("user.dir");
	public static final String TEST_DATA_FILE_PATH = ROOT_PATH+"/src/main/java/testdata/sfdctestdata.properties";
	public static final String TEST_DATA_FILE_TO_UPLOAD = ROOT_PATH+"/explaination.txt";
	public static final String PHOTO_UPLOAD_PATH = ROOT_PATH+"/src/main/resources/screenshots/1765477149228.png";
	public static final String REPORT_PATH = ROOT_PATH+"/src/main/resources/reports/"+dateStamp+".html";
}
