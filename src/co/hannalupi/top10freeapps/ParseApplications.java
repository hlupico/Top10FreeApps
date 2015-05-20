package co.hannalupi.top10freeapps;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class ParseApplications {
	
	private String data;
	//create an array of the values within the application class
	private ArrayList<Application> applications;
	
	//create ParseApplications constructor
	//initializes data, it does not start the process
	public ParseApplications(String xmlData){
		data = xmlData;
		
		//Initalize the array, create an empty array
		applications = new ArrayList<Application>();
	}

	//This will be the main process that MainActivity calls to get the final list of applications
	public ArrayList<Application> getApplications() {
		return applications;
	}
	
	//Create main process of the application
	public boolean process(){
		
		boolean operationStatus = true;
		Application currentRecord = null;
		boolean inEntry = false;
		String textValue = "";
		
		try{
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			//Pass text file through StringReader and then bring it through the XmlPull Parser to process
			//StringReader formats the information so that XmlPullParse can access it
			xpp.setInput(new StringReader(this.data));
			int eventType = xpp.getEventType();
			
			//continue processing until end of the file is reached
			while(eventType != XmlPullParser.END_DOCUMENT){
				String tagName = xpp.getName();
				if(eventType == XmlPullParser.START_TAG){
					if(tagName.equalsIgnoreCase("entry")){
						inEntry = true;
						currentRecord = new Application();
					}
				}else if(eventType == XmlPullParser.TEXT){
					textValue = xpp.getText();
				}else if(eventType == XmlPullParser.END_TAG){
					if(inEntry){
						if(tagName.equalsIgnoreCase("entry")){
							applications.add(currentRecord);
							inEntry = false;
						}
						if(tagName.equalsIgnoreCase("name")){
							currentRecord.setName(textValue);
						}else if (tagName.equalsIgnoreCase("artist")){
							currentRecord.setArtist(textValue);
						}else if (tagName.equalsIgnoreCase("releasedate")){
							currentRecord.setReleaseDate(textValue);
						}
					}
				}
				//Push parser to the next tag
				eventType = xpp.next();
				
			}
		}catch (Exception e){
			e.printStackTrace();
			operationStatus = false;
		}
		
		//this for loop will cycle through all application objects saved in Application class
		//and will temporarily save them as the main class, you will then have access to the 
		//setter and getter methods of each object in that class
		for(Application app : applications ){
			Log.d("LOG", "********");
			Log.d("LOG", app.getName());
			Log.d("LOG", app.getArtist());
			Log.d("LOG", app.getReleaseDate());
			
		}
		
		return operationStatus;
		
	}
	
}
