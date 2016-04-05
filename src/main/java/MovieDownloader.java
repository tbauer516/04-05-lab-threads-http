import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		try { // attempts to encode the movie name as UTF-8 in order to make it url compatible
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){ // failed the encode, but caught the exception to not crash code
			return null;
		}

		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		// initialize variables here
		String movies[] = null;

		try {
			// attempts to make a URL from the above string
			URL url = new URL(urlString);
			// attempts to initialize connection, set method to "get" and connect
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			// create an input stream to read data from the JSON from our URL
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null; // if input stream was not created
			}

			reader = new BufferedReader(new InputStreamReader(inputStream));
			// read lines returned from the web query
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}
			// if no results were returned from the search
			if (buffer.length() == 0) {
				return null;
			}
			// append results and format to be an array
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n");
		} 
		catch (IOException e) { // some exception occured and we caught it here
			return null;
		} 
		finally {
			if (urlConnection != null) {
				urlConnection.disconnect(); // close out connection to the URL
			}
			if (reader != null) {
				try {
					reader.close(); // close our connection to the reader
				} 
				catch (IOException e) { // if an exception was thrown, just ignore it
				}
			}
		}

		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().startsWith("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
