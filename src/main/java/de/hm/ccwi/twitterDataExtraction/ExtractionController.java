package de.hm.ccwi.twitterDataExtraction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import de.hm.ccwi.twitterDataExtraction.API.AlchemyEntityAPI;
import de.hm.ccwi.twitterDataExtraction.API.AlchemyKeywordAPI;
import de.hm.ccwi.twitterDataExtraction.API.DandelionAPI;
import de.hm.ccwi.twitterDataExtraction.API.InterfaceAPI;
import de.hm.ccwi.twitterDataExtraction.API.MeaningCloudAPI;
import de.hm.ccwi.twitterDataExtraction.API.ResponseEntity;
import de.hm.ccwi.twitterDataExtraction.ReadFile.ReadFile;
import de.hm.ccwi.twitterDataExtraction.ReadFile.ReadTwitterJSON;
import de.hm.ccwi.twitterDataExtraction.Util.CSVWriter;
import de.hm.ccwi.twitterDataExtraction.Util.EntityKeywordLog;

/**
 * @author Marcel
 * @project twitterDataExtraction
 * @email mk@mkarrasch.de
 * @createdOn 26.11.2016
 * @package de.mk.twitterDataExtraction
 */
public class ExtractionController extends ExtractionConfiguration {

	private static final Logger LOG = LogManager.getLogger("twitterExtraction");

	private static Integer numberRetweets = 0;
	private static Integer numberTweets = 0;
	private static Integer skippedTweets = 0;
	private static Double[] ratingArray = new Double[] { 0.0, 0.0, 0.0, 0.0 };
	private static Double[] rttArray = new Double[] { 0.0, 0.0, 0.0, 0.0 };
	private static Integer numberOfAPICalls = 0;

	private static List<EntityKeywordLog> entityKeywordLogList = new ArrayList<EntityKeywordLog>();

	private static long timeStart;
	private static long timeEnd;

	public static void main(String[] args) {

		try {
			// Start reading inputfile
			ReadFile inputFile = new ReadFile(DATASET);
			String inputLine = inputFile.nextLine();

			for (int i = 0; i <= DATASET_ROWNUMBER_START; i++) {
				inputFile.nextLine();
			}

			while (inputLine != null) {
				if (inputLine.startsWith("{\"contributors\":")) {
					numberTweets += 1;

					ReadTwitterJSON twitterJSON = new ReadTwitterJSON(inputLine);

					// Only call API if it is not a ReTweet
					if (!twitterJSON.isRT()) {

						String tweet = twitterJSON.getTweet();

						LOG.debug("----------- Start -------------------");
						LOG.debug(tweet);

						if (TEST_VERSION == 2) {
							twitterJSON.removeUrl();
						}
						if (TEST_VERSION == 3) {
							twitterJSON.removeUrl();
							twitterJSON.removeHastags();
						}

						tweet = twitterJSON.getTweet();

						// Extract all Hashtags
						ArrayList<String> twitterHastags = twitterJSON.extractTweetHastags();

						// Sort the Array List Entities from A to Z
						if (twitterHastags.size() > 0) {
							LOG.debug("------ Hashtags (" + twitterJSON.numberOfHastags() + ") ------");

							int i = 1;
							for (String s : twitterHastags) {
								LOG.debug("Hashtag " + i + " is " + s);
								i++;
							}

							LOG.debug("=> Number: " + twitterJSON.numberOfHastags());

							DandelionAPI dandelionAPI = null;
							AlchemyEntityAPI newAlchemyAPI = null;
							MeaningCloudAPI meaningCloudAPI = null;
							AlchemyKeywordAPI alchemyKeywordAPI = null;

							if (USE_DANDELION) {
								LOG.debug("----------- Dandelion API -----------");
								dandelionAPI = new DandelionAPI();
								callProcessingAPI(dandelionAPI, 0, tweet, twitterHastags);
							}

							if (USE_ALCHEMYENTITYRECOGNITION) {
								LOG.debug("----------- Alchemy API -------------");
								newAlchemyAPI = new AlchemyEntityAPI();
								callProcessingAPI(newAlchemyAPI, 1, tweet, twitterHastags);
							}

							if (USE_MEANINGCLOUD) {
								LOG.debug("----------- meaningCloud API -------------");
								meaningCloudAPI = new MeaningCloudAPI();
								callProcessingAPI(meaningCloudAPI, 2, tweet, twitterHastags);
							}

							if (USE_ALCHEMYKEYWORDEXTRACTION) {
								LOG.debug("----------- Alchemy API -------------");
								alchemyKeywordAPI = new AlchemyKeywordAPI();
								callProcessingAPI(alchemyKeywordAPI, 3, tweet, twitterHastags);
							}

							if (OUTPUT_ENTITIES_AND_KEYWORDS_FOR_TWEET && USE_ALCHEMYENTITYRECOGNITION
									&& USE_ALCHEMYKEYWORDEXTRACTION) {
								logExtractedEntityKeywords(newAlchemyAPI, alchemyKeywordAPI, tweet);
							}

							LOG.debug("----------- END ---------------------");
							LOG.debug("");

							numberOfAPICalls += 1;
							if (numberOfAPICalls == 1000) {
								LOG.warn("Daily API Limit (1000 Requests) reached");
								break;
							}
						} else {
							LOG.warn("No Hashtags - Skip Record: " + tweet);
							skippedTweets += 1;
						}

					} else {
						numberRetweets += 1;
					}
				} else {
					LOG.warn("Skip " + inputLine);
				}

				inputLine = inputFile.nextLine();
			}

			printTestResult(numberRetweets, numberTweets, skippedTweets, ratingArray, rttArray, numberOfAPICalls);
			if(OUTPUT_ENTITIES_AND_KEYWORDS_FOR_TWEET && USE_ALCHEMYENTITYRECOGNITION
					&& USE_ALCHEMYKEYWORDEXTRACTION) {
				exportEntityKeywordListToCSV();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void exportEntityKeywordListToCSV() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(CSV_FILE_FOR_EXPORT);
			
			for(EntityKeywordLog log : entityKeywordLogList) {
				String entityString = "";
				for(String entity : log.getEntityList()) {
					entityString = entityString + entity + "|";
				}
				String keywordString = "";
				for(String keyword : log.getKeywordList()) {
					keywordString = keywordString + keyword + "|";
				}
				CSVWriter.writeLine(writer, Arrays.asList(log.getText(), entityString, keywordString));
				writer.flush();				
			}
			writer.close();
		} catch (IOException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	private static void callProcessingAPI(InterfaceAPI api, Integer arraySlotNumber, String tweet,
			ArrayList<String> twitterHastags) {
		timeStart = System.nanoTime();

		try {
			api.createPOST(tweet);
			api.executePOST();
			api.receiveGET();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		timeEnd = System.nanoTime();
		double rating = api.calculateRating(twitterHastags);
		ratingArray[arraySlotNumber] = ratingArray[arraySlotNumber] + rating;
		rttArray[arraySlotNumber] = rttArray[arraySlotNumber] + (timeEnd - timeStart) / 1000000;
		LOG.debug("=> Number: " + api.getNumberEntities());
		LOG.debug("=> Percentage: " + String.format("%.2f", rating) + "%");
	}

	private static void logExtractedEntityKeywords(InterfaceAPI entityAPI, InterfaceAPI keywordAPI, String tweet) {

		List<String> entityList = new ArrayList<String>();
		List<String> keywordList = new ArrayList<String>();

		for (ResponseEntity entity : entityAPI.getEntityList()) {
			entityList.add(entity.getEntity());
		}

		for (ResponseEntity keyword : keywordAPI.getEntityList()) {
			keywordList.add(keyword.getEntity());
		}

		EntityKeywordLog log = new EntityKeywordLog(tweet, entityList, keywordList);
		entityKeywordLogList.add(log);
	}

	private static void printTestResult(int numberRetweets, int numberTweets, int skippedTweets, Double[] ratingArray,
			Double[] rttArray, int numberOfAPICalls) {
		LOG.info("----------- Rating ---------------------");
		LOG.info("Number of Provided tweets  " + numberTweets);
		LOG.info("Number of Re-Tweets        " + numberRetweets);
		LOG.info("Number of API Calls        " + numberOfAPICalls);
		LOG.info("Number of Skipped Records  " + skippedTweets);

		if (USE_ALCHEMYENTITYRECOGNITION) {
			LOG.info("Rating Alchemy (Entity)            " + String.format("%.2f", ratingArray[1] / numberOfAPICalls)
					+ "%");
			LOG.info("RTT Alchemy (Entity)               " + String.format("%.2f", rttArray[1] / numberOfAPICalls)
					+ " Millisekunden");
		}

		if (USE_DANDELION) {
			LOG.info("Rating Dandelion " + String.format("%.2f", ratingArray[0] / numberOfAPICalls) + "%");
			LOG.info("RTT Dandelion " + String.format("%.2f", rttArray[0] / numberOfAPICalls) + " Millisekunden");
		}

		if (USE_MEANINGCLOUD) {
			LOG.info("Rating meaningCloud " + String.format("%.2f", ratingArray[2] / numberOfAPICalls) + "%");
			LOG.info("RTT meaningCloud " + String.format("%.2f", rttArray[2] / numberOfAPICalls) + " Millisekunden");
		}

		if (USE_ALCHEMYKEYWORDEXTRACTION) {
			LOG.info("Rating Alchemy (Keyword) " + String.format("%.2f", ratingArray[3] / numberOfAPICalls) + "%");
			LOG.info("RTT alchemy (Keyword) " + String.format("%.2f", rttArray[3] / numberOfAPICalls)
					+ " Millisekunden");
		}
	}
}