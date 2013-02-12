package aku.web.tools.alexa;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import jodd.jerry.Jerry;

import org.apache.commons.io.FileUtils;

import aku.tools.Log;
import aku.tools.Utils;

import com.google.gson.Gson;

public class RankReader {
	private static final String SELECTOR_RANK = ".data-row1 .data:first";
	
	private static final String PROPERTIES_FILE = "config/reader.properties";
	
	private static final String PROPERTY_URL = "url.";
	private static final String PROPERTY_URL_MAX = "url.max";
	private static final String PROPERTY_PROCESS_FILE = "process.file";
	private static final String PROPERTY_PROCESS_TOSPREADSHEET = "process.spreadsheet";
	
	private static final String URL_MAX_DEFAULT = "100";

	private static final String EXT_JSON = ".json";

	public static void main(String[] args) throws Exception {
		Log.log("Ranked Reader");
		
		Properties properties = loadProperties(PROPERTIES_FILE);
		
		if(getBooleanProperty(properties, PROPERTY_PROCESS_TOSPREADSHEET)){
			Spreadsheet.process(properties, args[0], args[1]);
			Log.log("saved to spreadsheet");			
		}
		
		if(getBooleanProperty(properties, PROPERTY_PROCESS_FILE)){
			Map<String, String> ranks = read(properties);	
			
			String folderPath = properties.getProperty("folder");		
			String json = new Gson().toJson(ranks);
			saveToFile(folderPath, json);
			Log.log("saved to file");
		}
		
		Log.log("done");
	}

	private static Map<String, String> read(Properties properties)throws Exception {
		int max = Integer.parseInt(properties.getProperty(PROPERTY_URL_MAX, URL_MAX_DEFAULT));
		
		Map<String, String> ranks = new LinkedHashMap<String, String>();
		for(int i=1; i<max; i++){
			String url = properties.getProperty(PROPERTY_URL.concat(Integer.toString(i)));
			if(url==null)continue;
			String rank = readRank(url);
		
			ranks.put(url, rank);
		}
		return ranks;
	}

	private static boolean getBooleanProperty(Properties properties, String name){
		return Boolean.parseBoolean(properties.getProperty(name));
	}

	private static void saveToFile(String folder, String json) throws Exception {
		File source = new File(folder, Utils.getCurrentDateString().concat(EXT_JSON));		
		FileUtils.writeStringToFile(source, json, Utils.ENCODING);			
	}

	protected static String readRank(String url) throws Exception {
		Log.log("reading ", url, " ...");
		String source = Utils.getSource(url);			
		Jerry doc = Jerry.jerry(source);
		String rank = Utils.trim(doc.$(SELECTOR_RANK).text());		
		return rank;
	}

	private static Properties loadProperties(String name) throws Exception {
		Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(name);
        prop.load(fis);
        return prop;
	}

}
