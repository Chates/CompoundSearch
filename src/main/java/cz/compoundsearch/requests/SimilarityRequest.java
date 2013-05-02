package cz.compoundsearch.requests;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML/JSON mapping for similarity searching HTTP request.
 * 
 * Request is expecting MDL molfile as String, appropriate parameters and 
 * name of the similarity.
 * 
 * @author Martin Mates
 */
@XmlRootElement
public class SimilarityRequest {
    
    private List<String> parameters;
    private String molfile;
    private String similarity;

    /**
     * Getter for similarity request parameters. 
     * 
     * @return List<String> Similarity parameters  
     */
    public List<String> getParameters() {
	return parameters;
    }

    /**
     * Setter for similarity parameters.
     * 
     * @param parameters List of parameters of type String
     */
    public void setParameters(List<String> parameters) {
	this.parameters = parameters;
    }

    /**
     * Getter for MDL molfile format of the query molecule.
     * 
     * @return String MDL molfile 
     */
    public String getMolfile() {
	return molfile;
    }

    /**
     * Setter for MDL molfile format of the query molecule.
     * 
     * @param molfile String MDL molfile 
     */
    public void setMolfile(String molfile) {
	this.molfile = molfile;
    }

    /**
     * Getter for the name of requested similarity.
     * 
     * @return String Name of the similarity to use
     */
    public String getSimilarity() {
	return similarity;
    }

    /**
     * Setter for the name of requested similarity
     * 
     * @param similarity Name of the similarity to use
     */
    public void setSimilarity(String similarity) {
	this.similarity = similarity;
    }
      
    
}
