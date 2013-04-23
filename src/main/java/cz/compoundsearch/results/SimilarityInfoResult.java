package cz.compoundsearch.results;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML/JSON mapping of the HTTP response body returning information about 
 * available similarities and corresponding parameters.
 * 
 * This class is used in {@link cz.compoundsearch.resources.SimilarityResource}.
 * 
 * @author Martin Mates
 */
@XmlRootElement(name = "availableSimilarities")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimilarityInfoResult {

    private String similarityName;
    private List<SimilarityParameter> parameters;

    public SimilarityInfoResult() {
    }

    public SimilarityInfoResult(String similarityName, List<SimilarityParameter> parameters) {
	this.similarityName = similarityName;
	this.parameters = parameters;	
    }

    /**
     * Getter for the name of the similarity.
     * 
     * @return String name of the similarity
     */
    public String getName() {
	return similarityName;
    }

    /**
     * Setter for the name of the similarity
     * 
     * @param similarityName Name of the similarity
     */
    public void setName(String similarityName) {
	this.similarityName = similarityName;
    }

    /**
     * Getter for list of the parameters that current similarity has.
     * 
     * @return List<SimilarityParameter> List of parameters with names and 
     * descriptions
     */
    public List<SimilarityParameter> getParameters() {
	return parameters;
    }

    /**
     * Setter for the parameters parameters that current similarity has.
     * @param parameters List of parameters with names and 
     * descriptions
     */
    public void setParameters(List<SimilarityParameter> parameters) {
	this.parameters = parameters;
    }
    
}
