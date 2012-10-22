/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.entities;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Chates
 */
@Provider
public class EntityNotFoundMapper implements ExceptionMapper<EntityNotFoundException> {

    @Override
    public Response toResponse(EntityNotFoundException exception) {
	return Response.status(404).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
    
    
    
}
