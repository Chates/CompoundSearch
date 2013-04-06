/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
 *
 * @author Chates
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
     * Returns all available similarities and their parameters
     *
     * @return
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
		c = this.usedSimilarity.getCompoundById(this.savedSimilarityResults.get(index).getId());
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
		c = this.usedSimilarity.getCompoundById(this.savedSimilarityResults.get(index).getId());
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
