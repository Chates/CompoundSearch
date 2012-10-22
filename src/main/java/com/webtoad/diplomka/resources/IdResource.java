/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.entities.Compound;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Chates
 */
@Path("/id/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class IdResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    protected EntityManager em;

    @GET
    @Path("/{id}/")
    public Compound getCompoundById(@PathParam("id") Long id) {
	Compound c = em.find(Compound.class, id);

	if (c == null) {
	    throw new WebApplicationException(Response.status(404).entity("Compound with a given ID was not found.").build());
	}

	return c;
    }
}
