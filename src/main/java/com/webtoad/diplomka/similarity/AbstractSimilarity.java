/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.entities.Compound;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Chates
 */
abstract class AbstractSimilarity implements ISimilarity {

    protected List<SimilarityResult> similarCompounds = new ArrayList<SimilarityResult>();
    protected Compound requestCompound;
    protected Integer batchSize = 100;
    protected Double treshold = 0.8;
    protected Integer numberOfResults = 100;

    @Override
    public List<SimilarityResult> findAllSimilar() throws CompoundSearchException {
	// Run screening function first
	this.screen();

	// Postune brani sloucenin
	Integer start = 0;
	while (true) {
	    List<Compound> result;
	    result = getCompounds(start, this.batchSize);

	    // is result empty? If yes exit cycle
	    if (result.isEmpty()) {
		break;
	    }

	    // Run the similar function for all returned compounds
	    Double currentSimilarity;
	    for (Compound c : result) {
		currentSimilarity = calculateSimilarity(c);
		// Is similrity over the requested treshold?
		if (currentSimilarity >= this.treshold) {
		    similarCompounds.add(new SimilarityResult(c, currentSimilarity));
		}
	    }

	    // Sort results according to similarity
	    Collections.sort(similarCompounds);

	    // If similarity of last returned compound is higher than current
	    // treshold we set treshold higher since lower similarities wont
	    // fit to number of results.
	    if (similarCompounds.size() > numberOfResults) {
		Double lastReturnedSimilarity = similarCompounds.get(numberOfResults - 1).getSimilarity();
		if (lastReturnedSimilarity > this.treshold) {
		    this.treshold = lastReturnedSimilarity;
		}
		
		// Cut sorted list to requested size. Saving memory keeping only best results
		similarCompounds = similarCompounds.subList(0, this.numberOfResults);
	    }	    

	    start += this.batchSize;
	}	

	return similarCompounds;
    }

    @Override
    public void screen() throws CompoundSearchException {
    }

    public Integer getBatchSize() {
	return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
	this.batchSize = batchSize;
    }
}
