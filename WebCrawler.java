import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

public class WebCrawler {
	
	/**
	 * @author jflahive
	 * @param presidentName - name of a president to search for
	 * @return String - link to a page filtering for a specific president's speeches
	 */
	public static String getPresidentLink(String presidentName) {
		try {
			Document millerCenter = getDOMFromURL("https://millercenter.org/the-presidency/presidential-speeches");
			Element president = millerCenter.selectFirst("label:contains("+presidentName+")");
			
			String append = president.attr("for");
			append = append.split("-")[append.split("-").length-1];
			
			return "https://millercenter.org/the-presidency/presidential-speeches?field_president_target_id["+append+"]="+append;
		} catch (Exception e) {
			System.out.println("President link retrieval failed.");
			
			return null;
		}
	}
	
	/**
	 * @author jflahive
	 * @param presidentLink - link to a page filtering for a specific president's speeches
	 * @return ArrayList<String> - links to speech pages on millercenter.org
	 */
	public static ArrayList<String> getPresidentSpeechLinks(String presidentLink) {
		try {
			ArrayList<String> out = new ArrayList<String>();
			Document millerCenter = getDOMFromURL(presidentLink);
			Elements speeches = millerCenter.select("[hreflang]");
			
			for (Element speech : speeches) {
				out.add("https://millercenter.org"+speech.attr("href"));
			}
			
			return out;
		} catch (Exception e) {
			System.out.println("Speech link retrieval failed.");
			
			return null;
		}
	}
	
	/**
	 * @author jflahive
	 * @param speechLink - link to a speech page on millercenter.org
	 * @return String - html text of speech
	 */
	public static String getSpeechFromSpeechLink(String speechLink) {
		try {
			Document millerCenter = getDOMFromURL(speechLink);
			Element transcript = millerCenter.selectFirst("div.view-transcript");
			String text = transcript.html();
			
			text = text.replaceAll("()<.*?>", ""); // Remove HTML tags
			
			text = text.replaceAll("View Transcript",""); // Remove leading descriptor
			text = text.replaceAll("Transcript", ""); // Remove leading descriptor
			text = text.replaceAll("&nbsp;", " "); // Remove non-breaking spaces

			return text;
		} catch (Exception e) {
			System.out.println("Speech text retrieval failed.");
			
			return null;
		}
	}
	
	/**
	 * @author jflahive
	 * @param speechText - html text of speech
	 * @return void 
	 */
	public static void exportSpeech(String speechText, int index) {
		try {
			PrintWriter out = new PrintWriter("speech"+index+".txt");
			out.write(speechText.trim());
			out.close();
			
		} catch (Exception e) {
			System.out.println("Speech export failed.");
		}
	}
	
	/**
	 * Method to get a Document from a String URL
	 * @author andrewallace
	 * @param u - url to parse
	 * @return Document - to parse
	 * @throws Exception
	 */
	public static Document getDOMFromURL(String u) throws Exception {
		URL url = new URL(u);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuilder sb = new StringBuilder();
		String curr = in.readLine();
		while(curr != null) {
			sb.append(curr);
			curr = in.readLine();
		}
		return Jsoup.parse(sb.toString());
	}

}
