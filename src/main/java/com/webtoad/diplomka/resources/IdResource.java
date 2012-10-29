package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.entities.Compound;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * REST resource returning a compound from database searched by given ID.
 *
 * Resource can produce both JSON and XML formats based on "Accept" header in
 * HTTP request.
 * 
 * This resource is mapped to /id/ URL
 *
 * @author Martin Mates
 */
@Path("/id/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class IdResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    private EntityManager em;

    /**
     * Search database for compound with a given ID.
     *
     * If compound is not found response with status 404 (Not found) is
     * returned and header "Compound-search-error" is added to response 
     * explaining the error.
     * 
     * Method is mapped to /id/{id} URL.
     *
     * @param	id ID of compound to look up in database
     * @return	ArrayList containing one compound
     */
    @GET
    @Path("/{id}/")
    public List<Compound> getCompoundById(@PathParam("id") Long id) {
	Compound c = em.find(Compound.class, id);

	if (c == null) {
	    CompoundResponse crf = new CompoundResponse("Compound with a given ID was not found.", 404);
	    throw new WebApplicationException(crf.buildResponse());
	}

	// ArrayList because of data consistency in response mapped to JSON {"compound":[{}]} or {"compound":{"id"}}
	List<Compound> returnValue = new ArrayList<Compound>();
	returnValue.add(c);

	return returnValue;
    }
}
