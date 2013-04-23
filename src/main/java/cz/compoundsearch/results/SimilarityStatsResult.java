package cz.compoundsearch.results;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML/JSON mapping of the HTTP response body containing information about 
 * results of the similarity request.
 * 
 * Contains only number of results and current session ID is sent automatically
 * with this response.
 * 
 * @author Martin Mates
 */
@XmlRootElement
public class SimilarityStatsResult {

    private Long numberOfResults;

    public SimilarityStatsResult() {
	
    }
    
    public SimilarityStatsResult(Long resultCount) {
	this.numberOfResults = resultCount;
    }

    /**
     * Getter for number of results.
     * 
     * @return Long Number of results 
     */
    public Long getResultCount() {
	return numberOfResults;
    }

    /**
     * Setter for namuber of result.
     * 
     * @param resultCount Number of results
     */
    public void setResultCount(Long resultCount) {
	this.numberOfResults = resultCount;
    }

   
}
