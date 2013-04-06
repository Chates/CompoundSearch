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

/**
 *
 * @author Chates
 */
public abstract class AbstractSimilarity implements ISimilarity {

    protected List<SimilarityResult> similarCompounds = new ArrayList<SimilarityResult>();
    protected ICompound requestCompound;
    protected Integer batchSize = 1000;
    protected Double treshold = 0.8;
    protected Integer numberOfResults = 100000;

    @Override
    public List<SimilarityResult> findAllSimilar() throws CompoundSearchException {
	// Number of results cant be higher than 100 000
	if (this.numberOfResults > 100000) {
	    this.numberOfResults = 100000;
	}	

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
		start += this.batchSize;
		continue;
	    }

	    // Run the similarity function for all returned compounds
	    Double currentSimilarity;
	    for (ICompound c : result) {

		currentSimilarity = calculateSimilarity(c.getAtomContainer());
		// Is similrity over the requested treshold?
		if (currentSimilarity >= this.treshold) {
		    similarCompounds.add(new SimilarityResult(c.getId(), currentSimilarity));
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
    public ICompound getRequestCompound() {
	return requestCompound;
    }

    @Override
    public void setRequestCompound(ICompound c) throws CompoundSearchException {
	this.requestCompound = c;
    }
}
