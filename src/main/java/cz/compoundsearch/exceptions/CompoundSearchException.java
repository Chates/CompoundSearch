package cz.compoundsearch.exceptions;

/**
 * Exception indicating an error during similarity searching or working with 
 * chemicals.
 * 
 * This exception is usually mapped to 500 o 404 HTTP response.
 * 
 * @author Martin Mates
 */
public class CompoundSearchException extends Exception {

    public CompoundSearchException(String message) {
	super(message);
    }
}
