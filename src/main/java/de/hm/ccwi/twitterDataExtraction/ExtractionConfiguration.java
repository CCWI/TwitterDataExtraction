package de.hm.ccwi.twitterDataExtraction;

public class ExtractionConfiguration {

	/**
	 * Json-Files, which are e. g. placed in the resource folder. bspw.
	 * twitter_iot_complete,
	 */
	public static final String DATASET = "./src/main/resources/twitter_iot_complete.txt";

	/**
	 * Amount of rows to skip before test start. Typically e. g. 0, 10000,
	 * 20000, 30000, 40000, 50000. (Because a multiple amount of tweets are
	 * needed to find 1000 appropriate tweets for request.)
	 */
	public static final Integer DATASET_ROWNUMBER_START = 0;

	/**
	 * Version 1: With URLs and Hashtags. Version 2: Without URLs. Version 3:
	 * Without URLs and Hashtags.
	 */
	public static final Integer TEST_VERSION = 2;

	public static final Boolean USE_MEANINGCLOUD = false;
	public static final Boolean USE_DANDELION = false;
	public static final Boolean USE_ALCHEMYENTITYRECOGNITION = true;
	public static final Boolean USE_ALCHEMYKEYWORDEXTRACTION = true;

	/**
	 * Only available for Arlchemy-API. Extraction of Entiies and Keywords need
	 * to be set to true!
	 */
	public static final Boolean OUTPUT_ENTITIES_AND_KEYWORDS_FOR_TWEET = true;
	public static final String CSV_FILE_FOR_EXPORT = "./src/main/resources/entityKeywordExport.csv";

}
