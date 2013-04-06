/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.results;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement(name = "availableSimilarities")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimilarityInfoResult {

    private String similarityName;
    private List<SimilarityParameter> parameters;

    // Because of the json/xml message writer
    public SimilarityInfoResult() {
    }

    public SimilarityInfoResult(String similarityName, List<SimilarityParameter> parameters) {
	this.similarityName = similarityName;
	this.parameters = parameters;	
    }

    public String getName() {
	return similarityName;
    }

    public void setName(String similarityName) {
	this.similarityName = similarityName;
    }

    public List<SimilarityParameter> getParameters() {
	return parameters;
    }

    public void setParameters(List<SimilarityParameter> parameters) {
	this.parameters = parameters;
    }
    
    
}
