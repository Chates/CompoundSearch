package cz.compoundsearch.resources;

import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.SubstructureFingerprint;
import cz.compoundsearch.results.SimilarityStatsResult;
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
 * REST resource returning a compounds from database searched by different
 * criteria.
 *
 * Resource can produce both JSON and XML formats based on "Accept" header in
 * HTTP request.
 *
 * <p><strong>This resource is mapped to /list/ URL</strong></p>
 *
 * @author Martin Mates
 */
@Path("/list/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class ListResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    private EntityManager em;
    private Boolean calledFromApp = false;

    public ListResource() {
    }

    public ListResource(EntityManager em) {
	this.em = em;
    }

    /**
     * Return all compounds from database.
     *
     * If database contains no compounds response with status 404 (Not found) is
     * returned and header "Compound-search-error" is added to response
     * explaining the error.
     *
     * If parameter {@link #setCalledFromApp} is set to true, empty ArrayList is
     * returned instead of 404 response.
     *
     * <p><strong>Method is mapped to /list/ URL.</strong></p>
     *
     * @return	ArrayList All compounds from database
     */
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
    @Path("/count/")
    public SimilarityStatsResult getCompoundCount() {
	CriteriaBuilder builder = em.getCriteriaBuilder();
	CriteriaQuery<Long> cq = builder.createQuery(Long.class);
	cq.select(builder.count(cq.from(Compound.class)));
	em.createQuery(cq);
	Long count = em.createQuery(cq).getSingleResult();

	SimilarityStatsResult ssr = new SimilarityStatsResult(count);
	return ssr;
    }

    /**
     * Return specified number of compounds from database.
     *
     * If database contains no compounds response with status 404 (Not found) is
     * returned and header "Compound-search-error" is added to response
     * explaining the error.
     *
     * If parameter {@link #setCalledFromApp} is set to true, empty ArrayList is
     * returned instead of 404 response.
     *
     * <p><strong>Method is mapped to /list/{limit} URL.</strong></p>
     *
     * @return	ArrayList compounds from database
     */
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

    /**
     * Return specified number of compounds from database and start from
     * specified index.
     *
     * If database contains no compounds response with status 404 (Not found) is
     * returned and header "Compound-search-error" is added to response
     * explaining the error.
     *
     * If parameter {@link #setCalledFromApp} is set to true, empty ArrayList is
     * returned instead of 404 response.
     *
     * <p><strong>Method is mapped to /list/{limit}/{start} URL.</strong></p>
     *
     * @return	ArrayList compounds from database
     */
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

    /**
     * Used by {@link cz.compoundsearch.similarity.SubstructureSimilarity} and
     * {@link cz.compoundsearch.descriptor.SubstructureFingerprintDescriptor} to
     * retrieve compounds with SubstructureFingeprint descriptor value.
     *
     * @return	ArrayList compounds from database
     */
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

    /**
     * Helper function for creating database typed query for Compound entity
     *
     * @return TypedQuery Prepared typed query for Compound entity
     */
    private TypedQuery<Compound> getCompoundTQ() {
	CriteriaBuilder crtiteriaBuilder = em.getCriteriaBuilder();
	CriteriaQuery<Compound> query = crtiteriaBuilder.createQuery(Compound.class);
	Root<Compound> c = query.from(Compound.class);
	query.select(c);

	TypedQuery<Compound> tq = em.createQuery(query);
	return tq;
    }
}
