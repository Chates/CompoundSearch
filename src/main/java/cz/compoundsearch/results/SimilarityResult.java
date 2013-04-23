package cz.compoundsearch.results;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML/JSON mapping for similarity result saved in memory during the session.
 * 
 * This object is saved in memory as the result of similarity searching. For 
 * scalability reasons it contains only compound ID and computed similarity.
 * 
 * This class is used in {@link cz.compoundsearch.resources.SimilarityResource}.
 * 
 * @author Martin Mates
 */
@XmlRootElement
public class SimilarityResult implements Comparable<SimilarityResult> {

    private Long id;
    private Double similarity;

    public SimilarityResult() {
	
    }
    
    public SimilarityResult(Long id, Double similarity) {
	this.id = id;
	this.similarity = similarity;
    }

    /**
     * Getter for compound ID in the database.
     * 
     * By this id molecule is retrieved from arbitrary repository.
     * 
     * @return Long Identification number of the compound
     */
    public Long getId() {
	return id;
    }

    /**
     * Getter for compound ID in the database.
     * 
     * By this id molecule is retrieved from arbitrary repository.
     * 
     * @param id Identification number of the compound
     */
    public void setID(Long id) {
	this.id = id;
    }

    /**
     * Getter for similarity present in the result.
     * 
     * @return Double Similarity of the molecule in the result
     */
    public Double getSimilarity() {
	return similarity;
    }

    /**
     * Setter for similarity present in the result.
     * 
     * @param similarity Similarity of the molecule in the result
     */
    public void setSimilarity(Double similarity) {
	this.similarity = similarity;
    }

    /**
     * Implementation of the method from Comparable interface.
     * 
     * Compound needs to be sorted by similarity in result set.
     * 
     * @param sr SimilarityCompoundResult
     * @return
     */
    @Override
    public int compareTo(SimilarityResult sr) {
	Integer compare = 0;

	if (this.similarity < sr.getSimilarity()) {
	    compare = 1;
	}

	if (this.similarity > sr.getSimilarity()) {
	    compare = -1;
	}

	return compare;
    }
}
