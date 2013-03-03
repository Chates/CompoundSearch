/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.similarity;

import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.ICompound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.exceptions.NoMoreCompoundsException;
import cz.compoundsearch.results.SimilarityResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chates
 */
public abstract class AbstractSimilarity implements ISimilarity {

    protected List<SimilarityResult> similarCompounds = new ArrayList<SimilarityResult>();
    protected Compound requestCompound;
    protected Integer batchSize = 100;
    protected Double treshold = 0.8;
    protected Integer numberOfResults = 1000;

    @Override
    public List<SimilarityResult> findAllSimilar() throws CompoundSearchException {


	// Postune brani sloucenin
	Integer start = 0;
	while (true) {
	    List<? extends ICompound> result;
	    try {
		result = screen(start, this.batchSize);
	    } catch (NoMoreCompoundsException ex) {
		// No more compounds in database. Exit cycle
		break;
	    }
	    
	    if (result.isEmpty()) {
		// All compounds in this iteration were screened, continue to next iteration
		continue;
	    }

	    // Run the similarity function for all returned compounds
	    Double currentSimilarity;
	    for (ICompound c : result) {

		currentSimilarity = calculateSimilarity(c.getCompound());
		// Is similrity over the requested treshold?
		if (currentSimilarity >= this.treshold) {
		    similarCompounds.add(new SimilarityResult(c.getCompound().getId(), currentSimilarity));
		}
	    }

	    // Sort results by similarity
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
    public List<? extends ICompound> screen(Integer start, Integer limit) throws CompoundSearchException, NoMoreCompoundsException {
	List<? extends ICompound> result = this.getCompounds(start, limit);

	if (result.isEmpty()) {
	    throw new NoMoreCompoundsException();
	}
	
	return result;
    }

    public Integer getBatchSize() {
	return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
	this.batchSize = batchSize;
    }

    @Override
    public Compound getRequestCompound() {
	return requestCompound;
    }

    @Override
    public void setRequestCompound(Compound c) throws CompoundSearchException {
	this.requestCompound = c;
    }
}
