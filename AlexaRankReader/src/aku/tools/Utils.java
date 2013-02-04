package aku.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.exception.UncheckedException;


public class Utils {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11";
	public static final String ENCODING = "utf-8";
	private static final Pattern CHARSET = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
	private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static String getCurrentDateString(){
		return dateFormat.format(new Date());
	}

	public static String getCurrentTimeString(){
		return timeFormat.format(new Date());
	}

	public static  URLConnection openConnection(String stringUrl) throws Exception {
		URL url = new URL(stringUrl);	
		URLConnection con = url.openConnection();
		con.addRequestProperty("User-Agent", USER_AGENT);
		return con;
	}

	public static String getSource(String stringUrl) throws Exception{
		URLConnection con = Utils.openConnection(stringUrl);

		String contentType = con.getContentType();
		Matcher m = CHARSET.matcher(contentType);
		String charset = m.matches() ? m.group(1) : ENCODING;
		
		Reader r = new InputStreamReader(con.getInputStream(), charset);
		StringBuilder buf = new StringBuilder();
		
		while (true) {
		  int ch = r.read();
		  if (ch < 0) break;
		  buf.append((char) ch);
		}
		
		return buf.toString();	
	}
	
	public static String trim(String string) {
		try{
			BufferedReader reader = new BufferedReader(new StringReader(string));
			StringBuffer out = new StringBuffer();
			String subString;
			while((subString = reader.readLine())!=null){
				if(subString.trim().length()>0) out.append(subString);
			}
			return out.toString().trim();
		}catch(Exception e){
			throw new UncheckedException(e);
		}
	}

}