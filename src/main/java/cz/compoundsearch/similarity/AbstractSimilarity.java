package cz.compoundsearch.similarity;

import cz.compoundsearch.entities.ICompound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.exceptions.NoMoreCompoundsException;
import cz.compoundsearch.results.SimilarityResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class defines default functionality for other similarities and 
 * implements algorithm for finding similar structures in database. 
 * 
 * @author Martin Mates
 */
public abstract class AbstractSimilarity implements ISimilarity {

    protected List<SimilarityResult> similarCompounds = new ArrayList<SimilarityResult>();
    protected ICompound requestCompound;
    protected Integer batchSize = 1000;
    protected Double threshold = 0.8;
    protected Integer numberOfResults = 100000;

    /**
     * Implementation of the default algorithm for similarity searching.
     * 
     * @return List<SimilarityResult> List of similarity results sorted by 
     * similarity
     * @throws CompoundSearchException 
     */
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
		if (currentSimilarity >= this.threshold) {
		    similarCompounds.add(new SimilarityResult(c.getCompoundId(), currentSimilarity));
		}
	    }
	    result.clear();

	    // Sort results by similarity
	    Collections.sort(similarCompounds);

	    // If similarity of last returned compound is higher than current
	    // treshold we set treshold higher since lower similarities wont
	    // fit to number of results.
	    if (similarCompounds.size() > numberOfResults) {
		Double lastReturnedSimilarity = similarCompounds.get(numberOfResults - 1).getSimilarity();
		if (lastReturnedSimilarity > this.threshold) {
		    this.threshold = lastReturnedSimilarity;
		}

		// Cut sorted list to requested size. Saving memory keeping only best results
		//similarCompounds = similarCompounds.subList(0, this.numberOfResults);
		similarCompounds = new ArrayList<SimilarityResult>(similarCompounds.subList(0, this.numberOfResults));
	    }

	    start += this.batchSize;
	}

	return similarCompounds;
    }

    /**
     * Implementation of the default screening.
     * 
     * This screening does nothing only retrieve compounds from database and 
     * returns them all.
     * 
     * Implemented in AbstractSimilarity so the programmer can omit 
     * implementation of screening in their similarity.
     * 
     * @param start
     * @param limit
     * @return List<? extends ICompound> List of compounds that passed the screening
     * procedure
     * @throws CompoundSearchException
     * @throws NoMoreCompoundsException 
     */
    @Override
    public List<? extends ICompound> screen(Integer start, Integer limit) throws CompoundSearchException, NoMoreCompoundsException {
	List<? extends ICompound> result = this.getCompounds(start, limit);

	if (result.isEmpty()) {
	    throw new NoMoreCompoundsException();
	}
	
	return result;
    }

    /**
     * Getter for batch size.
     * 
     * For memory consumption reasons compounds are retrieved from database in 
     * batches.
     * 
     * @return 
     */
    public Integer getBatchSize() {
	return batchSize;
    }

    /**
     * Setter for batch size.
     * 
     * For memory consumption reasons compounds are retrieved from database in 
     * batches.
     * 
     * @param batchSize Size of the batch for compound retrieval
     */
    public void setBatchSize(Integer batchSize) {
	this.batchSize = batchSize;
    }

    /**
     * Getter for query compound.
     * 
     * @return ICompound Query compound implementing ICompound interface
     */
    @Override
    public ICompound getRequestCompound() {
	return requestCompound;
    }

    /**
     * Setter for query compound.
     * 
     * @param c Query compound implementing ICompound interface
     * @throws CompoundSearchException 
     */
    @Override
    public void setRequestCompound(ICompound c) throws CompoundSearchException {
	this.requestCompound = c;
    }
}
