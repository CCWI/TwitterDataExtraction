package de.hm.ccwi.twitterDataExtraction.API;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcel
 * @project twitterDataExtraction
 * @email mk@mkarrasch.de
 * @createdOn 26.11.2016
 * @package de.mk.twitterDataExtraction.API
 */

public interface InterfaceAPI {

    /**
     * Create a HTTP POST which consits of two or more parameters. Number of parameters depends on the used API.
     *
     * @param tweet Twitter Tweet which should be posted
     * @throws UnsupportedEncodingException if text is not in Unicode
     */
    public void createPOST (String tweet) throws UnsupportedEncodingException;

    /**
     * Extract all Entities and the confidence Level from the received JSON file.
     * Save all Enties in a seperate responseEntity. All entities will be saved in the ArrayList entityList.
     * entityList is ordered from A to Z
     *
     * @throws IOException    IO Error
     * @throws ParseException Parse Error
     */
    public void receiveGET () throws IOException, ParseException;
    
    /**
     * 
     * @return
     */
    public int getNumberEntities();
    
    /**
     * 
     * @return
     */
	public List<ResponseEntity> getEntityList();
    
    /**
     * 
     * @throws IOException
     */
	public void executePOST() throws IOException;

	/**
	 * 
	 * @param twitterHastags
	 * @return
	 */
	public double calculateRating(ArrayList<String> twitterHastags);
}
