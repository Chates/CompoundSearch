package cz.compoundsearch.exceptions;

/**
 * Exception indicating the end of similarity searching algorithm.
 * 
 * End of default algorithm implemented in {@link cz.compoundsearch.similarity.AbstractSimilarity} happens when every 
 * compound in database is inspected and there are none left.
 * 
 * @author Martin Mates
 */
public class NoMoreCompoundsException extends Exception {

    public NoMoreCompoundsException() {
	
    }
}
