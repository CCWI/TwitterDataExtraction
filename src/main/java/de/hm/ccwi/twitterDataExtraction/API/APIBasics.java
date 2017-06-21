package de.hm.ccwi.twitterDataExtraction.API;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Marcel
 * @project twitterDataExtraction
 * @email mk@mkarrasch.de
 * @createdOn 10.12.2016
 * @package de.mk.twitterDataExtraction.API
 */
public class APIBasics {

	HttpClient httpclient;
	HttpPost httppost;
	HttpEntity httpEntity;
	List<ResponseEntity> entityList;
	Properties properties;

	String APIKey;
	String outputMode;
	String topictypes;
	String lang;

	final String _ALCHEMY = "Alchemy";
	final String _DANDELION = "Dandelion";
	final String _MEANINGCLOUD = "meaningCloud";

	/**
	 * Read the connection.properties file. This file includes all API Keys
	 */
	APIBasics() {
		properties = new Properties();
		File f = new File("resources/connection.properties");

		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
			properties.load(bis);

			if (properties.getProperty("dandelionAPIKey").isEmpty() || properties.getProperty("alchemyAPIKey").isEmpty()
					|| properties.getProperty("meaningCloudAPIKey").isEmpty()) {
				System.out.println("One or more API Keys are not set. Program stops now");
				System.exit(0);
			}
		} catch (Exception ex) {
			System.out.println("connection.properties not set");
		}
	}

	/**
	 * Execute the created HTTP Post Element and receive the respone
	 *
	 * @throws IOException
	 *             General Error
	 */
	public void executePOST() throws IOException {
		try {
			HttpResponse response = httpclient.execute(httppost);
			httpEntity = response.getEntity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extract the JSON Array annotations which contains all Entites
	 *
	 * @param API
	 *            Name of API. Output is different for each API
	 * @param JSONZeile
	 *            Received response from Dandelion API
	 * @return JSONArray annotations which includes all Entites
	 * @throws ParseException
	 *             Parse Error
	 */
	JSONArray readResponseJSON(String API, String JSONZeile, String jsonObject) {
		JSONParser parser = new JSONParser();
		JSONObject responeJSON = null;
		try {
			responeJSON = (JSONObject) parser.parse(JSONZeile);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONArray returnValue = new JSONArray();

		if (responeJSON != null) {
			// if (responeJSON.get("statusInfo") == "invalid-api-key")
			// returnValue = null;

			returnValue = (JSONArray) responeJSON.get(jsonObject);
		}

		return returnValue;

	}

	/**
	 * In case APi return relevance as a String. Convert into double
	 *
	 * @param relevance
	 *            as String
	 * @return relevance as Double
	 */
	double convertRelevance(String relevance) {
		return Double.parseDouble(relevance.replace("\"", ""));
	}

	/**
	 * Number of entities saved in the array entityList.
	 *
	 * @return Number of entites.
	 */
	public int getNumberEntities() {
		return entityList.size();
	}

	/**
	 * Calculate the rating of an API call Basis is always the list of hashtags.
	 * If an hashtag is also an entity than the APi gets 0. if the API delivers
	 * another (new) entity which is not an hashtag the API also gets +0,5. If
	 * an entity is missing, than -1.
	 * <p>
	 * For Example: Tweet has 4 Hashtags (A, B; C; D) API return the entities A,
	 * B, E
	 * <p>
	 * Entity A = Hashtag A => 0 Entity B = Hashtag B => 0 Enity C missing => -1
	 * Enity D missing => -1 Entity D = New => +-0,5 Rating is 100 + (100 / 4) *
	 * (-2 + 0,5) = 62,5 %
	 *
	 * @param twitterHastags
	 *            ArrayList of all Entites
	 * @return Rating of this API Call
	 */
	public double calculateRating(ArrayList<String> twitterHastags) {
		int hits = 0;
		int added = 0;

		// Check if Hashtag has been identified as an entity from API. If so add
		// 1 to hits.
		for (String s : twitterHastags) {
			for (ResponseEntity entityList : entityList) {
				if (s.equals(entityList.getEntity())) {
					// Mark this entity that it is also a Hashtag
					entityList.setIsHashtag(true);
					hits++;
				}
			}
		}

		// Count all Entities that are not an Hastag
		for (ResponseEntity entityList : entityList) {
			if (!entityList.getIsHashtag())
				added++;
		}

		// e.g. 5 hashtags => 100% / 5 = 20%
		// 3 hitted entites and 1 added entites => rating is 20% * (3 + 1) = 80%
		return 100.0 / twitterHastags.size() * (hits + added * 0.5);
	}

	/**
	 * Check if entity should be added to the entity list. First check if entity
	 * is already in the entity list. Second check if entity is an URL or has
	 * more than 2 spaces
	 *
	 * @param s
	 *            Entity from API
	 * @return String of entity. If null than it will not be added
	 */
	String addEntity(String s) {

		// Replace # with nothing in case Entiy has a # sign
		s = s.replace("#", "");
		// Capitalize first letter
		if (s.length() > 2) {
			s = s.substring(0, 1).toUpperCase() + s.substring(1);
		}
		boolean addEntity = true;

		// Check first if Entity has been already added to the entity List
		for (ResponseEntity e : entityList)
			if (s.equals(e.getEntity())) {
				addEntity = false;
				s = null;
				break;
			}

		// Check if Entity is an URL. If so do not add to Entity List.
		// Check if entity has more than 2 spaces. If so do not add to Entity
		// List because the entity is a sentance.
		if (s != null && addEntity) {
			if (s.contains("@") || s.contains("http") || s.contains("Http")
					|| s.length() - s.replace(" ", "").length() > 1) {

				return null;
			}
		}
		return s;
	}

	public List<ResponseEntity> getEntityList() {
		return entityList;
	}

}
