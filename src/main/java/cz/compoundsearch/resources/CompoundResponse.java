package cz.compoundsearch.resources;

import cz.compoundsearch.exceptions.CompoundSearchException;
import javax.ws.rs.core.Response;

/**
 * Helper for building a custom HTTP response for errors.
 * 
 * This response sets the status code of the response and adds a custom HTTP 
 * header explaining the error to the client.
 * 
 * @author Martin Mates
 */
public class CompoundResponse {

    private String message;
    private Integer statusCode;
    
    final static String CUSTOM_HEADER = "Compound-search-error";
    
    /**
     * Constructor accepting an error message and HTTP status code.
     * 
     * @param message Custom error message
     * @param statusCode HTTP status code that will be returned
     */
    public CompoundResponse(String message, Integer statusCode) {
	this.message = message;
	this.statusCode = statusCode;
    }

    /**
     * Constructor accepting HTTP status code and CompoundSearchException that 
     * contains information about the error. 
     * 
     * @param statusCode HTTP status code that will be returned
     * @param exception Exception containing information about the error
     */
    public CompoundResponse(Integer statusCode, CompoundSearchException exception) {
	this.message = exception.getMessage();
	this.statusCode = statusCode;
    }

    /**
     * Builds the HTTP response with appropriate status code and custom message
     * in the header.
     * 
     * @return Response HTTP response
     */
    public Response buildResponse() {
	return Response.status(this.statusCode).header(CUSTOM_HEADER, this.message).build();
    }

    /**
     * Getter for error message contained in the response
     * 
     * @return String Error message
     */
    public String getMessage() {
	return message;
    }

    /**
     * Setter for error message that will be contained in the response
     * 
     * @param message Error message
     */
    public void setMessage(String message) {
	this.message = message;
    }

    /**
     * Getter for HTTP status code
     * 
     * @return Integer HTTP status code 
     */
    public Integer getStatusCode() {
	return statusCode;
    }

    /**
     * Setter for HTTO status code
     * 
     * @param statusCode HTTP status code
     */
    public void setStatusCode(Integer statusCode) {
	this.statusCode = statusCode;
    }
    
    
}
