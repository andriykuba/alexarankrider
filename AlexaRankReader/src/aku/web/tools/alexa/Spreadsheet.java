package aku.web.tools.alexa;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import aku.tools.Utils;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

public class Spreadsheet {
	private static final String PROPERTY_SPREADSHEET_SERVICE = "spreadsheet.service";
	private static final String PROPERTY_SPREADSHEET_TITLE = "spreadsheet.title";
	private static final String PROPERTY_WORKSHEET_TITLE = "worksheet.title";

	private static final String PROPERTY_WORKSHEET_INDEX_ROW = "worksheet.index.row";
	private static final String PROPERTY_WORKSHEET_INDEX_COL = "worksheet.index.col";

	private static final String PROPERTY_WORKSHEET_URL_ROW = "worksheet.url.row.start";
	private static final String PROPERTY_WORKSHEET_URL_COL = "worksheet.url.col";

	public static void process(Properties properties, String usr, String pwd) throws Exception{
	    SpreadsheetService service = new SpreadsheetService(properties.getProperty(PROPERTY_SPREADSHEET_SERVICE));
	    service.setUserCredentials(usr, pwd);
	    service.setProtocolVersion(SpreadsheetService.Versions.V3);

	    String spreadsheetTtitle = properties.getProperty(PROPERTY_SPREADSHEET_TITLE);
	    SpreadsheetEntry spreadsheet = getSpreadsheet(service, spreadsheetTtitle);

	    String worksheetTitle = properties.getProperty(PROPERTY_WORKSHEET_TITLE);
	    WorksheetEntry worksheet = getWorksheet(spreadsheet, worksheetTitle);
	 
	    
	    String indexRow = properties.getProperty(PROPERTY_WORKSHEET_INDEX_ROW);
	    String indexCol = properties.getProperty(PROPERTY_WORKSHEET_INDEX_COL);
	    int nextCellIndex = getNextCellIndex(worksheet, service, indexRow, indexCol);
	    
	    // Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = worksheet.getCellFeedUrl();
	    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
	    
	    // Insert current date
	    CellEntry cellEntry= new CellEntry (1, nextCellIndex, Utils.getCurrentDateString());
	    cellFeed.insert (cellEntry);
	    
	    processRanks(worksheet, service, properties, nextCellIndex);
	    
	    // Grow Index
	    nextCellIndex = nextCellIndex + 1;
	    CellEntry nextCellEntry= new CellEntry (Integer.parseInt(indexRow), Integer.parseInt(indexCol), Integer.toString(nextCellIndex));
	    cellFeed.insert (nextCellEntry);
	    
	}

	private static void processRanks(WorksheetEntry worksheet, SpreadsheetService service, Properties properties, int nextCellIndex) throws Exception{		
		String row = properties.getProperty(PROPERTY_WORKSHEET_URL_ROW);
		String col = properties.getProperty(PROPERTY_WORKSHEET_URL_COL);
		
	    URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString().concat("?min-row="+row+"&min-col="+col+"&max-col="+col)).toURL();
	    CellFeed feed = service.getFeed(cellFeedUrl, CellFeed.class);
	    for(CellEntry cell:feed.getEntries()){
	    	String url = cell.getCell().getValue();
	    	String rank = RankReader.readRank(url);
	    	
	    	String address = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
	    	String currentRow = address.substring(address.indexOf("R")+1, address.indexOf("C"));
	    	
	    	CellEntry entry = new CellEntry (Integer.parseInt(currentRow), nextCellIndex, rank);
	    	feed.insert(entry);
	    }
	}

	private static int getNextCellIndex(WorksheetEntry worksheet, SpreadsheetService service, String indexRow, String indexCol) throws Exception{
	    URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString().concat("?min-row="+indexRow+"&max-row="+indexRow+"&min-col="+indexCol+"&max-col="+indexCol)).toURL();
	    CellFeed positionFeed = service.getFeed(cellFeedUrl, CellFeed.class);
	    CellEntry positionEntry = positionFeed.getEntries().get(0);
	    int cellIndex = positionEntry.getCell().getNumericValue().intValue();
	    return cellIndex;
	}

	private static WorksheetEntry getWorksheet(SpreadsheetEntry spreadsheet, String worksheetTitle) throws Exception{
		List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();
		for (WorksheetEntry worksheet : worksheets) {
			String title = worksheet.getTitle().getPlainText();
	    	if(worksheetTitle.equals(title)){
	    		return worksheet;
	    	}
		}
		
		throw new Exception("Worksheet \"".concat(worksheetTitle).concat("\" coul not be found"));
	}
	
	private static SpreadsheetEntry getSpreadsheet(SpreadsheetService service, String spreadsheetTtitle) throws Exception{
	    // Define the URL to request.  This should never change.
	    URL feedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

	    // Make a request to the API and get all spreadsheets.
	    SpreadsheetFeed feed = service.getFeed(feedUrl, SpreadsheetFeed.class);
	    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
	    
	    for (SpreadsheetEntry entry : spreadsheets) {
	    	String title = entry.getTitle().getPlainText();
	    	if(spreadsheetTtitle.equals(title)){
	    		return entry;
	    	}
	    }
	    
	    throw new Exception("Spreadsheet \"".concat(spreadsheetTtitle).concat("\" could not be found"));
	}
	
}
