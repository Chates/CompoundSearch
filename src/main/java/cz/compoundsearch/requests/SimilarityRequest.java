/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.requests;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
public class SimilarityRequest {
    
    private List<String> parameters;
    private String molfile;
    private String similarity;

    public List<String> getParameters() {
	return parameters;
    }

    public void setParameters(List<String> parameters) {
	this.parameters = parameters;
    }

    public String getMolfile() {
	return molfile;
    }

    public void setMolfile(String molfile) {
	this.molfile = molfile;
    }

    public String getSimilarity() {
	return similarity;
    }

    public void setSimilarity(String similarity) {
	this.similarity = similarity;
    }
    
    
    
}
