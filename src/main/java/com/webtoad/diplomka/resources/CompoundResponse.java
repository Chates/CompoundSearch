/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.CompoundSearchException;
import javax.ws.rs.core.Response;

/**
 *
 * @author Chates
 */
public class CompoundResponse {

    private String message;
    private Integer statusCode;
    
    final static String CUSTOM_HEADER = "Compound-search-error";
    
    public CompoundResponse(String message, Integer statusCode) {
	this.message = message;
	this.statusCode = statusCode;
    }

    public CompoundResponse(Integer statusCode, CompoundSearchException exception) {
	this.message = exception.getMessage();
	this.statusCode = statusCode;
    }

    public Response buildResponse() {
	return Response.status(this.statusCode).header(CUSTOM_HEADER, this.message).build();
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public Integer getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
	this.statusCode = statusCode;
    }
    
    
}
