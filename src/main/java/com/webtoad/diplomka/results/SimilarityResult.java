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
public class SimilarityResult implements Comparable<SimilarityResult> {

    private Long id;
    private Double similarity;

    // Because of the json/xml message writer
    public SimilarityResult() {
	
    }
    
    public SimilarityResult(Long id, Double similarity) {
	this.id = id;
	this.similarity = similarity;
    }

    public Long getId() {
	return id;
    }

    public void setID(Long id) {
	this.id = id;
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
