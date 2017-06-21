package de.hm.ccwi.twitterDataExtraction.Util;

import java.util.Comparator;

import de.hm.ccwi.twitterDataExtraction.API.ResponseEntity;

/**
 * @author Marcel
 * @project extractTwitterData
 * @email mk@mkarrasch.de
 * @createdOn 27.11.2016
 * @package de.mk.extractTwitterData.Util
 */

public class SortResponseEntity implements Comparator<ResponseEntity> {

    /**
     * Sort all Entities alphabetical (A->Z) by comparing to entities
     *
     * @param e1 First Entity
     * @param e2 Second Entity
     * @return Return result of sorting
     */
    @Override
    public int compare (ResponseEntity e1, ResponseEntity e2) {
        return e1.getEntity().compareTo(e2.getEntity());
    }


}
