package de.hm.ccwi.twitterDataExtraction.API;

/**
 * @author Marcel
 * @project twitterDataExtraction
 * @email mk@mkarrasch.de
 * @createdOn 26.11.2016
 * @package de.mk.twitterDataExtraction
 */

public class ResponseEntity {

    private String entity = "";
    private Double confidence = 0D;
    private boolean isHashtag;

    /**
     * @return Boolean if Entity is also a Hashtag
     */
    boolean getIsHashtag () {
        return isHashtag;
    }

    /**
     * @param entity Set Boolean if entity is also Hashtag
     */
    void setIsHashtag (boolean entity) {
        this.isHashtag = entity;
    }

    /**
     * @param entity Set Name of the Entity
     */
    void setEntity (String entity) {
        this.entity = entity;
    }

    /**
     * @param confidence Set Confidence of the Entity
     */
    void setConfidence (Double confidence) {
        this.confidence = confidence;
    }

    /**
     * @return Name of the Entity
     */
    public String getEntity () {
        return entity;
    }


    /**
     * @return Confidence of the Entity
     */
    public Double getConfidence () {
        return confidence;
    }

}
