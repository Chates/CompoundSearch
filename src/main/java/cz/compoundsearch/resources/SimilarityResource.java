package cz.compoundsearch.resources;

import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.requests.SimilarityRequest;
import cz.compoundsearch.results.SimilarityCompoundResult;
import cz.compoundsearch.results.SimilarityInfoResult;
import cz.compoundsearch.results.SimilarityParameter;
import cz.compoundsearch.results.SimilarityResult;
import cz.compoundsearch.results.SimilarityStatsResult;
import cz.compoundsearch.similarity.ISimilarity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import org.reflections.Reflections;

/**
 * Universal REST resource for all similarity requests.
 *
 * This class serves as an universal resource for all similarity requests. It 
 * provides information about available similarities and their parameters.
 * 
 * This resource is stateful which means that it will "live" in the session and 
 * is paired with the specific client with session ID. Session expires in 10 
 * minutes.
 * 
 * This class also keeps the search results and provide them to the client.
 * 
 * Resource can produce both JSON and XML formats based on "Accept" header in
 * HTTP request.
 *
 * <p><strong>This resource is mapped to /similarity/ URL</strong></p>
 *
 * @author Martin Mates
 */
@SessionScoped
@Path("/similarity/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateful
@StatefulTimeout(600000) // 10 minutes
public class SimilarityResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    private EntityManager em;
    @EJB
    private IdResource idResource;
    private List<SimilarityResult> savedSimilarityResults = new ArrayList<SimilarityResult>();
    private ISimilarity usedSimilarity = null;

    /**
     * Returns information about all available similarities and their parameters.
     * 
     * This method use reflection and looks for all classes implementing 
     * ISimilarity interface.
     * 
     * <p><strong>Method is mapped to /similarity/info/ URL.</strong></p>
     *
     * @return List<SimilarityInfoResult> XML/JSON mapped object with 
     * information about available similarities
     */
    @GET
    @Path("/info/")
    public List<SimilarityInfoResult> getAllSimilarities() {

	Set<ISimilarity> similarities = this.getSimilaritiesReflection();

	List<SimilarityInfoResult> result = new ArrayList<SimilarityInfoResult>();

	for (ISimilarity s : similarities) {
	    List<SimilarityParameter> parameters = new ArrayList<SimilarityParameter>();

	    // Get all parameters for similarity
	    String[] pNames = s.getParameterNames();
	    // For each parameter get its type
	    for (String pName : pNames) {
		parameters.add(new SimilarityParameter(pName, s.getParameterType(pName).getClass().getSimpleName()));
	    }
	    result.add(new SimilarityInfoResult(s.getClass().getSimpleName(), parameters));
	}

	return result;
    }

    /**
     * Method accepting similarity request.
     * 
     * This method serves all requests for similarity searching. It validates 
     * the request, set parameters, run the similarity and returns information
     * about searching result.
     * 
     * If required similarity is not found HTTP status 404 is returned with 
     * corresponding error message in custom header. If any other error occurs 
     * HTTP status 500 is returned.
     * 
     * If the searching is successful. Results are saved in private variable and
     * can be retrieved by the client later.
     * 
     * <p><strong>Method is mapped to /similarity/ URL.</strong></p>
     * 
     * @param sr Similarity request object
     * @return SimilarityStatsResult Information about the search result 
     * containing number of results.
     */
    @POST
    @Path("/")
    public SimilarityStatsResult universalSimilarity(JAXBElement<SimilarityRequest> sr) {
	try {

	    List<String> params = sr.getValue().getParameters();
	    String requestedSimilarity = sr.getValue().getSimilarity();
	    Compound requestCompound = new Compound(sr.getValue().getMolfile());

	    // Get all similarities vie reflection
	    Set<ISimilarity> similarities = this.getSimilaritiesReflection();

	    // Find requested similarity in set 
	    for (ISimilarity s : similarities) {
		if (s.getClass().getSimpleName().equals(requestedSimilarity)) {
		    this.usedSimilarity = s;
		}
	    }

	    // Requested similarity was not found?
	    if (this.usedSimilarity == null) {
		CompoundResponse cr = new CompoundResponse("Requested similarity is not available.", 404);
		throw new WebApplicationException(cr.buildResponse());
	    }

	    // Set similarity parameters
	    this.usedSimilarity.setRequestCompound(requestCompound);
	    this.usedSimilarity.setParameters(params);

	    // Get results
	    List<SimilarityResult> similarityResults = this.usedSimilarity.findAllSimilar();

	    // Is result empty?
	    if (similarityResults.isEmpty()) {
		CompoundResponse cr = new CompoundResponse("None compound is similar.", 404);
		throw new WebApplicationException(cr.buildResponse());
	    }

	    // If not save results
	    this.savedSimilarityResults = similarityResults;

	    return new SimilarityStatsResult(new Long(this.savedSimilarityResults.size()));
	} catch (CompoundSearchException e) {
	    CompoundResponse cr = new CompoundResponse(500, e);
	    throw new WebApplicationException(cr.buildResponse());
	}

    }
    
    /**
     * Returns number of results returned in last similarity search.
     * 
     * If there is no previous similarity or session has expired 404 HTTP code
     * is returned.
     * 
     * <p><strong>Method is mapped to /similarity/count/ URL.</strong></p>
     * 
     * @return SimilarityStatsResult Information about the search result 
     * containing number of results.
     */
    @GET
    @Path("/count/")
    public SimilarityStatsResult resultsCount() {
	// Check wether there are some saved results 
	if (this.savedSimilarityResults.isEmpty()) {
	    CompoundResponse cr = new CompoundResponse("There are no stored results. You have to make search request first or your session has expired.", 404);
	    throw new WebApplicationException(cr.buildResponse());
	}
	
	return new SimilarityStatsResult(new Long(this.savedSimilarityResults.size()));
    }

    /**
     * Returns actual results of the previous similarity searching.
     * 
     * In the same session user can query the results by calling this resource.
     * This method then retrieve compounds from database using current 
     * similarity that was used for searching.
     * 
     * If there are no results or session has expired 404 HTTP status is 
     * returned.
     * 
     * Maximum number of results to return is limited to 1000 due to HTTP 
     * transfer overload.
     * 
     * <p><strong>Method is mapped to /similarity/{limit} URL.</strong></p>
     * 
     * @param limit
     * @return 
     */
    @GET
    @Path("/{limit}/")
    public List<SimilarityCompoundResult> returnResults(@PathParam("limit") Integer limit) {
	// Check wether there are some saved results 
	if (this.savedSimilarityResults.isEmpty()) {
	    CompoundResponse cr = new CompoundResponse("There are no stored results. You have to make search request first or your session has expired.", 404);
	    throw new WebApplicationException(cr.buildResponse());
	}

	// Limit cannot be higher than 1000
	if (limit > 1000) {
	    CompoundResponse cr = new CompoundResponse("Maximum limit for similarity result request is 1000.", 404);
	    throw new WebApplicationException(cr.buildResponse());
	}

	int index = 0;
	List<SimilarityCompoundResult> returnResults = new ArrayList<SimilarityCompoundResult>();

	// Go through saved similarity results and get compounds from DB
	while (index < this.savedSimilarityResults.size() && returnResults.size() < limit) {	    
	    Compound c;
	    try {
		c = (Compound) this.usedSimilarity.getCompoundById(this.savedSimilarityResults.get(index).getId());
	    } catch (CompoundSearchException e) {
		CompoundResponse cr = new CompoundResponse(e.getMessage(), 404);
		throw new WebApplicationException(cr.buildResponse());
	    }
	    Double s = this.savedSimilarityResults.get(index).getSimilarity();
	    returnResults.add(new SimilarityCompoundResult(c, s));
	    index++;
	}

	return returnResults;
    }

    /**
     * Returns actual results of the previous similarity searching.
     * 
     * In the same session user can query the results by calling this resource.
     * This method then retrieve compounds from database using current 
     * similarity that was used for searching.
     * 
     * If there are no results or session has expired 404 HTTP status is 
     * returned.
     * 
     * This method also validates given parameters. Maximum number of results to 
     * return is limited to 1000 due to HTTP transfer overload.
     * 
     * This method is usually used when paginating the results. 
     * 
     * <p><strong>Method is mapped to /similarity/{limit}/{start} URL.</strong></p>
     * 
     * @param limit
     * @param start
     * @return 
     */
    @GET
    @Path("/{limit}/{start}")
    public List<SimilarityCompoundResult> returnResults(@PathParam("limit") Integer limit, @PathParam("start") Integer start) {
	// Check wether there are some saved results 
	if (this.savedSimilarityResults.isEmpty()) {
	    CompoundResponse cr = new CompoundResponse("There are no stored results. You have to make search result first or your session has expired.", 404);
	    throw new WebApplicationException(cr.buildResponse());
	}

	// Start is out of bounce
	if (start > this.savedSimilarityResults.size() - 1) {
	    CompoundResponse cr = new CompoundResponse("Start index for similarity result is out of bounce.", 404);
	    throw new WebApplicationException(cr.buildResponse());
	}

	// Limit cannot be higher than 1000
	if (limit > 1000) {
	    CompoundResponse cr = new CompoundResponse("Maximum limit for similarity result request is 1000.", 404);
	    throw new WebApplicationException(cr.buildResponse());
	}

	int index = start;
	List<SimilarityCompoundResult> returnResults = new ArrayList<SimilarityCompoundResult>();

	// Go through saved similarity results and get compounds from DB
	while (index < this.savedSimilarityResults.size() && returnResults.size() < limit) {
	    Compound c;
	    try {
		c = (Compound) this.usedSimilarity.getCompoundById(this.savedSimilarityResults.get(index).getId());
	    } catch (CompoundSearchException e) {
		CompoundResponse cr = new CompoundResponse(e.getMessage(), 404);
		throw new WebApplicationException(cr.buildResponse());
	    }
	    Double s = this.savedSimilarityResults.get(index).getSimilarity();
	    returnResults.add(new SimilarityCompoundResult(c, s));
	    index++;
	}

	return returnResults;
    }

    /**
     * Helper method for reflection searching for all classes implementing 
     * {@link cz.compoundsearch.similarity.ISimilarity} interface omitting 
     * {@link cz.compoundsearch.similarity.AbstractSimilarity}.
     * 
     * @return Set<ISimilarity> Instances of all available similarities
     */
    private Set<ISimilarity> getSimilaritiesReflection() {
	// Get all available Similarities via reflection
	Reflections reflections = new Reflections("cz.compoundsearch.similarity");
	Set<Class<? extends ISimilarity>> similarities = reflections.getSubTypesOf(ISimilarity.class);

	Set<ISimilarity> similaritiesToReturn = new HashSet<ISimilarity>();
	// We need to remove AbstractSimilarity from the set of similarities
	for (Class<? extends ISimilarity> s : similarities) {
	    if (!s.getSimpleName().equals("AbstractSimilarity")) {
		try {
		    similaritiesToReturn.add(s.newInstance());
		} catch (InstantiationException e) {
		    String message = " similarity cannot be inicialized. Probably doesnt have an empty constructor.";
		    CompoundResponse cr = new CompoundResponse(s.getSimpleName() + message, 500);
		    throw new WebApplicationException(cr.buildResponse());
		} catch (IllegalAccessException e) {
		    String message = " similarity cannot be inicialized.";
		    CompoundResponse cr = new CompoundResponse(s.getSimpleName() + message, 500);
		    throw new WebApplicationException(cr.buildResponse());
		}
	    }
	}

	return similaritiesToReturn;
    }
}
