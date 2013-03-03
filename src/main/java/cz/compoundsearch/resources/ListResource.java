/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.resources;

import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.SubstructureFingerprint;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
@Path("/list/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class ListResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    private EntityManager em;
    
    // Parameter to determine whether this resource is called from within 
    // an application or from HTTP request. Will react differently for empty 
    // results from database.
    private Boolean calledFromApp = false;
    
    public ListResource() {
	
    }    
    
    public ListResource(EntityManager em) {
	this.em = em;
    }

    private TypedQuery<Compound> getCompoundTQ() {
	CriteriaBuilder crtiteriaBuilder = em.getCriteriaBuilder();
	CriteriaQuery<Compound> query = crtiteriaBuilder.createQuery(Compound.class);
	Root<Compound> c = query.from(Compound.class);
	query.select(c);

	TypedQuery<Compound> tq = em.createQuery(query);
	return tq;
    }

    @GET
    @Path("/")
    public List<Compound> getCompounds() {
	TypedQuery<Compound> tq = this.getCompoundTQ();
	List<Compound> list = tq.getResultList();

	// Return 404 response when called from client. Return empty list otherwise.
	if (list.isEmpty() && this.calledFromApp == false) {
	    throw new WebApplicationException(Response.status(404).entity("There are no compounds in database.").build());
	}

	return list;
    }

    @GET
    @Path("/{limit}/")
    public List<Compound> getCompounds(@PathParam("limit") Integer limit) {
	TypedQuery<Compound> tq = this.getCompoundTQ();

	tq.setMaxResults(limit);

	List<Compound> list = tq.getResultList();

	// Return 404 response when called from client. Return empty list otherwise.
	if (list.isEmpty() && this.calledFromApp == false) {
	    throw new WebApplicationException(Response.status(404).entity("There are no compounds in database.").build());
	}

	return list;
    }

    @GET
    @Path("/{limit}/{start}/")
    public List<Compound> getCompounds(@PathParam("limit") Integer limit, @PathParam("start") Integer start) {
	TypedQuery<Compound> tq = this.getCompoundTQ();

	tq.setMaxResults(limit);
	tq.setFirstResult(start);

	List<Compound> list = tq.getResultList();

	// Return 404 response when called from client. Return empty list otherwise.
	if (list.isEmpty() && this.calledFromApp == false) {
	    throw new WebApplicationException(Response.status(404).entity("No compounds matching given request.").build());
	}

	return list;
    }
    
    
    public List<SubstructureFingerprint> getSubstructureFingerprint(Integer limit, Integer start) {
	// Create query
	CriteriaBuilder crtiteriaBuilder = em.getCriteriaBuilder();
	CriteriaQuery<SubstructureFingerprint> query = crtiteriaBuilder.createQuery(SubstructureFingerprint.class);
	Root<SubstructureFingerprint> sf = query.from(SubstructureFingerprint.class);
	query.select(sf);
	TypedQuery<SubstructureFingerprint> tq = em.createQuery(query);
	
	// Set limits
	tq.setMaxResults(limit);
	tq.setFirstResult(start);

	// Query database
	List<SubstructureFingerprint> list = tq.getResultList();
	
	return list;
    }
    
    
    public void setCalledFromApp(Boolean b) {
	this.calledFromApp = b;
    }
}
