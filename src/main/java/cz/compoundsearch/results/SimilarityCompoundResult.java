package cz.compoundsearch.results;

import cz.compoundsearch.entities.Compound;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML/JSON mapping of the similarity searching result containing MDL molfile 
 * and similarity of the molecule. 
 * 
 * This class is used in {@link cz.compoundsearch.resources.SimilarityResource} 
 * for sending actual result molecules to the client.
 * 
 * @author Martin Mates
 */
@XmlRootElement
public class SimilarityCompoundResult implements Comparable<SimilarityCompoundResult> {

    private Compound compound;
    private Double similarity;

    
    public SimilarityCompoundResult() {
	
    }
    
    public SimilarityCompoundResult(Compound c, Double similarity) {
	this.compound = c;
	this.similarity = similarity;
    }

    /**
     * Getter for molecule present in the result.
     * 
     * Returns instance of the Compound implementing ICompund interface. This is
     * possible thanks to XML/JSON mapping in class 
     * {@link cz.compoundsearch.entities.Compound}
     * 
     * @return Compound Molecule from the result
     */
    public Compound getCompound() {
	return compound;
    }

    /**
     * Setter for compound present in the result.
     * 
     * @param compound Instance of the Compound
     */
    public void setCompound(Compound compound) {
	this.compound = compound;
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
    public int compareTo(SimilarityCompoundResult sr) {
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
