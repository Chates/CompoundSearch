/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.results;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
public class SimilarityStatsResult {

    private Long numberOfResults;

    // Because of the json/xml message writer
    public SimilarityStatsResult() {
	
    }
    
    public SimilarityStatsResult(Long resultCount) {
	this.numberOfResults = resultCount;
    }

    public Long getResultCount() {
	return numberOfResults;
    }

    public void setResultCount(Long resultCount) {
	this.numberOfResults = resultCount;
    }

   
}
