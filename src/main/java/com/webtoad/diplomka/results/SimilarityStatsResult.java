/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.results;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
public class SimilarityStatsResult {

    private int numberOfResults;

    // Because of the json/xml message writer
    public SimilarityStatsResult() {
	
    }
    
    public SimilarityStatsResult(int resultCount) {
	this.numberOfResults = resultCount;
    }

    public int getResultCount() {
	return numberOfResults;
    }

    public void setResultCount(int resultCount) {
	this.numberOfResults = resultCount;
    }

   
}
