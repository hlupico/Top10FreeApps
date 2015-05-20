package co.hannalupi.top10freeapps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	Button btnParse;
	ListView listApps;
	String xmlData;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnParse = (Button) findViewById(R.id.btnParse);
		listApps = (ListView) findViewById(R.id.listApps);
		
		btnParse.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Instantiate the ParseApplications class
				ParseApplications parse = new ParseApplications(xmlData); //xmlData is assigned in OnPostExecute process
				//Execute the code
				boolean operationStatus = parse.process();
				if (operationStatus){
					ArrayList<Application> allApps = parse.getApplications();
					
					//These three lines take all of the information from the Application class and 
					//organize it into a list which will appear in the listview
					ArrayAdapter<Application> adapter = new ArrayAdapter<Application>(MainActivity.this, R.layout.list_item, allApps);
					listApps.setVisibility(listApps.VISIBLE);
					listApps.setAdapter(adapter);
							
					
				}else {
					Log.d("ActivityMain", "Error parsing file");
				}
				
			}
		});
		
		new DownloadData().execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//create a class that will run asynchronously 
	private class DownloadData extends AsyncTask<String, Void, String>{
		
		String myXMLData; //Store xml data from Apple rss feed here
		//protected class is one level more private than public, (String...urls) will allow the function 
		//gather 0 to multiple urls.
		protected String doInBackground(String... urls){
			//create try block to test and catch any possible errors
			try{
				//will process first url passed to downloadXML function
				myXMLData = downloadXML(urls[0]);
			} catch(IOException e){
				return "Unable to download XML file";
			}
			
			return "";
		}
		
		protected void onPostExecute (String result){
			Log.d("On Post Execute", myXMLData);
			//Save the xml file after it has been succesfully downloaded
			xmlData = myXMLData;
		}
		
		//define downloadXML
		//theURL is the website that the error will be thrown to
		//adding the throw method will throw the error back to calling method, in this case the doInBackground method,
		//if an error is contained in this method
		private String downloadXML(String theURL) throws IOException {
			int BUFFER_SIZE = 20000;
			InputStream is = null;
			
			String xmlContents = "";
			
			try{
				//Open URL and create connection
				URL url = new URL(theURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				//Get a response to see what happened
				int response = conn.getResponseCode();
				Log.d("DownloadXML", "The response returned is: " + response);
				is=conn.getInputStream();
				
				//Create InputStreamReader and pass the InputStream, or connection, to it to be read
				InputStreamReader isr = new InputStreamReader(is);
				int charRead;
				char[] inputBuffer = new char[BUFFER_SIZE];
				
				try{
					//while we still have data, continue reading
					//charRead is the number of characters that have been read
					while((charRead = isr.read(inputBuffer))>0)
					{
						String readString = String.copyValueOf(inputBuffer, 0, charRead);
						//continually add what was read to the xmlContent string
						xmlContents += readString;
						//Clear out inputBuffer, initalize and empty
						inputBuffer = new char[BUFFER_SIZE];	
					}
					return xmlContents;
					
				}catch (IOException e){
					e.printStackTrace();
					return null;
				}
	
			}finally{ //finally will close InputStream not matter what, even if there are errors. 
				if(is!=null){
					is.close();
				}
			}
		}
	}
}
