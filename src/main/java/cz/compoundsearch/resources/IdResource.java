package cz.compoundsearch.resources;

import cz.compoundsearch.entities.Compound;
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
 * <p><strong>This resource is mapped to /id/ URL</strong></p>
 *
 * @author Martin Mates
 */
@Path("/id/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class IdResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    private EntityManager em;
    private Boolean calledFromApp = false;

    /**
     * Search database for compound with a given ID.
     *
     * If compound is not found response with status 404 (Not found) is returned
     * and header "Compound-search-error" is added to response explaining the
     * error.
     *
     * <p><strong>This resource is mapped to /id/{id} URL</strong></p>
     *
     * @param	id ID of compound to look up in database
     * @return	ArrayList containing one compound
     */
    @GET
    @Path("/{id}/")
    public List<Compound> getCompoundById(@PathParam("id") Long id) {
	Compound c = em.find(Compound.class, id);

	if (c == null && this.calledFromApp == false) {
	    CompoundResponse crf = new CompoundResponse("Compound with a given ID was not found.", 404);
	    throw new WebApplicationException(crf.buildResponse());
	}

	// ArrayList because of data consistency in response mapped to JSON {"compound":[{}]} or {"compound":{"id"}}
	List<Compound> returnValue = new ArrayList<Compound>();

	// When result is empty return empty list
	if (c == null) {
	    return returnValue;
	} else {
	    returnValue.add(c);
	    return returnValue;
	}


    }

    /**
     * Parameter to determine whether this resource is called from within an
     * application or from HTTP request.
     *
     * Defaults to false (called from client as web service)
     *
     * Resource react differently for empty results from database.
     */
    public void setCalledFromApp(Boolean b) {
	this.calledFromApp = b;
    }
}
