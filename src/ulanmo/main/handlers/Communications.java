package ulanmo.main.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import ulanmo.main.util.DateUtility;
import ulanmo.main.util.Utility;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class Communications {
	private final String DEBUG_TAG = "Communications";
	private Activity parentActivity;
	private String token;
	private HashMap<String, String> serverURLs;
	private boolean demo;

	public Communications(Activity parentActivity) {
		this.parentActivity = parentActivity;
		demo = false;
		token = "THETOKEN";
		serverURLs = new HashMap<String, String>();
		serverURLs.put("getAllStations", "http://penoy.admu.edu.ph:8180/ulanmo/getAllStations");
		serverURLs.put("getAllBarangays", "http://penoy.admu.edu.ph:8180/ulanmo/getAllBarangays");
		serverURLs.put("getPastHour", "http://penoy.admu.edu.ph:8180/ulanmo/getPastHour");
		serverURLs.put("getPast24Hours", "http://penoy.admu.edu.ph:8180/ulanmo/getPast24Hours");
		serverURLs.put("getPast5Days", "http://penoy.admu.edu.ph:8180/ulanmo/getPast5Days");
		serverURLs.put("geocoding", "http://maps.googleapis.com/maps/api/geocode/json");
	}
	
	public boolean isDemo() {
		return demo;
	}
	
	public void setDemo(boolean demo) {
		this.demo = demo;
	}

	public boolean requestAllBarangays(AbstractHandler handler) {
		Log.d(DEBUG_TAG, "Request all barangays");
		
		return readBarangaysFromFile(handler);
//		Log.d(DEBUG_TAG, "RequestingAllBarangays");
//		
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("token", "THETOKEN");
//		
//		if (checkConnection()) {
//			new WebDataRequest(handler).execute(Utility.getURL(serverURLs.get("getAllBarangays"), params));
//			return true;
//		} else {
//			return false;
//		}
	}
	
	private boolean readBarangaysFromFile(AbstractHandler handler) {
		Log.d(DEBUG_TAG, "Reading all barangays");
		String json = null;
	    try {
	        InputStream is = parentActivity.getAssets().open("barangays.json");
	        int size = is.available();
	        byte[] buffer = new byte[size];
	        is.read(buffer);
	        is.close();
	        json = new String(buffer, "UTF-8");
	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return false;
	    }
	    handler.handle(json);
		return true;
	}

	public boolean requestAllStations(AbstractHandler handler) {
		Log.d(DEBUG_TAG, "Requesting all stations");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		
		if (checkConnection()) {
			new WebDataRequest(handler)
					.execute(Utility.getURL(serverURLs.get("getAllStations"), params));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean requestLocation(AbstractHandler handler, double latitude, double longitude) {
		Log.d(DEBUG_TAG, String.format("Geocoding: [lat, %f] [long,  %f]", latitude, longitude));
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("sensor", "true");
		params.put("latlng", latitude+","+longitude);
		params.put("locale", Locale.getDefault().getLanguage());
		
		if (checkConnection()) {
			new WebDataRequest(handler)
					.execute(Utility.getURL(serverURLs.get("geocoding"), params));
			return true;
		} else
			return false;
	}
	
	public boolean requestPast(AbstractHandler handler, double latitude, double longitude, String label) {
		Log.d(DEBUG_TAG, String.format("Requesting: " + label +": [lat, %f] [long,  %f]", latitude, longitude));
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("lat", Double.toString(latitude));
		params.put("lng", Double.toString(longitude));
		params.put("date", DateUtility.getDate());
		params.put("hour", DateUtility.getHour());
		Log.d("Communications", "Connectivity: " + checkConnection());
		
		if(demo) {
			handler.handle("[{\"lat\":14.02,\"lng\":127.2,\"rainfall\":3.7594922185904727,\"date\":\"2013-08-18 09:00:00\"}]");
			return true;
		}
		
		if (checkConnection()) {
			new WebDataRequest(handler)
					.execute(Utility.getURL(serverURLs.get(label), params));
			return true;
		} else
			return false;
	}

	public boolean requestPastHour(AbstractHandler handler, double latitude, double longitude) {
		return requestPast(handler, latitude, longitude, "getPastHour");
	}
	
	public boolean requestPast24Hours(AbstractHandler handler, double latitude, double longitude) {
		return requestPast(handler, latitude, longitude, "getPast24Hours");
	}
	
	public boolean requestPast5Days(AbstractHandler handler, double latitude, double longitude) {
		return requestPast(handler, latitude, longitude, "getPast5Days");
	}
	
	private boolean checkConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		Log.d(DEBUG_TAG, "Checking Connection [NetworkInfo!NULL, " + (networkInfo != null) + "]");
		return (networkInfo != null && networkInfo.isConnected());
	}

//	private String downloadUrl(String myurl) throws IOException {
//		InputStream is = null;
//		int len = 10000;
//
//		try {
//			Log.d(DEBUG_TAG, "download URL: " + myurl);
//			URL url = new URL(myurl);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setReadTimeout(10000);
//			conn.setConnectTimeout(15000);
//			conn.setRequestMethod("GET");
//			conn.setDoInput(true);
//
//			conn.connect();
//			int response = conn.getResponseCode();
//			Log.d(DEBUG_TAG, "The response is: " + response);
//			is = conn.getInputStream();
//
//			String contentAsString = readIt(is, len);
//			Log.d(DEBUG_TAG, "Decoded: " + contentAsString.substring(0, Math.min(45, contentAsString.length())));
//			return contentAsString;
//
//		}
//		catch(FileNotFoundException e) {
//			return "[{\"rainfall\":-2}]";
//		}
//		catch(Exception e) {
//			Log.d(DEBUG_TAG, "Exception: ", e);
//			return "";
//		}
//		finally {
//			if (is != null) {
//				is.close();
//			}
//		}
//	}
//
//	private String readIt(InputStream stream, int len) throws IOException,
//			UnsupportedEncodingException {
//		Reader reader = null;
//		reader = new InputStreamReader(stream, "UTF-8");
//		char[] buffer = new char[len];
//		int length = 0;
//		while (true) {
//		    int ret = reader.read(buffer, length, buffer.length - length);
//		    if (ret == -1 || length == buffer.length) break;
//		    length += ret;
//		}
//		return new String(buffer);
//	}

	private class WebDataRequest extends AsyncTask<String, Void, String> {
		private AbstractHandler handler;

		public WebDataRequest(AbstractHandler handler) {
			this.handler = handler;
		}

		@Override
		protected String doInBackground(String... urls) {
			int responseCode = -1;
		    StringBuilder builder = new StringBuilder();
		    HttpClient client = new DefaultHttpClient();
		    HttpGet httpget = new HttpGet(urls[0]);

		    try {
		        HttpResponse response = client.execute(httpget);
		        StatusLine statusLine = response.getStatusLine();
		        responseCode = statusLine.getStatusCode();

		        if (responseCode == HttpURLConnection.HTTP_OK) {
		            HttpEntity entity = response.getEntity();
		            InputStream content = entity.getContent();
		            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		            String line;
		            while((line = reader.readLine()) != null){
		                builder.append(line);
		            }
		            return builder.toString();
		        }
		        else {
		           Log.d(DEBUG_TAG, String.format("Unsuccessful HTTP response code: %d", responseCode));
		        }
		    }
		    catch (Exception e) {
		        Log.d(DEBUG_TAG, "HTTP error: ", e);
		    }           

		    return "Unable to retrieve web page. URL may be invalid.";
		}

		@Override
		protected void onPostExecute(String result) {
			// testText.setText(result);
			handler.handle(result);
		}
	}
}
