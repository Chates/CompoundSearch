/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.entities.Compound;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
public class SimilarityResult implements Comparable<SimilarityResult> {

    private Compound compound;
    private Double similarity;

    // Because of the json/xml message writer
    public SimilarityResult() {
	
    }
    
    public SimilarityResult(Compound c, Double similarity) {
	this.compound = c;
	this.similarity = similarity;
    }

    public Compound getCompound() {
	return compound;
    }

    public void setCompound(Compound compound) {
	this.compound = compound;
    }

    public Double getSimilarity() {
	return similarity;
    }

    public void setSimilarity(Double similarity) {
	this.similarity = similarity;
    }

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
