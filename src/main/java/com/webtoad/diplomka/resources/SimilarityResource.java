/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.SimilarityRequestXML;
import com.webtoad.diplomka.entities.Compound;
import com.webtoad.diplomka.results.SimilarityCompoundResult;
import com.webtoad.diplomka.results.SimilarityInfoResult;
import com.webtoad.diplomka.results.SimilarityParameter;
import com.webtoad.diplomka.results.SimilarityResult;
import com.webtoad.diplomka.results.SimilarityStatsResult;
import com.webtoad.diplomka.similarity.AtomCountSimilarity;
import com.webtoad.diplomka.similarity.ISimilarity;
import com.webtoad.diplomka.similarity.SubstructureSimilarity;
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

//    @POST
//    @Path("/")
//    public List<SimilarityResult> universalSimilarity(JAXBElement<SimilarityRequestXML> sr) {
//	
//
//    }
    @POST
    @Path("/substructure/")
    public List<SimilarityResult> substructureSimilarity(JAXBElement<SimilarityRequestXML> sr) {
	try {
	    Compound requestCompound = new Compound(sr.getValue().getMolfile());
	    SubstructureSimilarity ss = new SubstructureSimilarity(requestCompound);

	    List<SimilarityResult> similarityResults = ss.findAllSimilar();

	    if (similarityResults.isEmpty()) {
		CompoundResponse cr = new CompoundResponse("None compound is similar.", 404);
		throw new WebApplicationException(cr.buildResponse());
	    }

	    return similarityResults;
	} catch (CompoundSearchException e) {
	    CompoundResponse cr = new CompoundResponse(500, e);
	    throw new WebApplicationException(cr.buildResponse());
	}

    }

    @POST
    @Path("/atom-count/")
    public SimilarityStatsResult atomCountSimilarity(JAXBElement<SimilarityRequestXML> sr) {

	// REFLECTION
//	Reflections reflections = new Reflections("com.webtoad.diplomka.similarity");
//	Set<Class<? extends ISimilarity>> subTypes = reflections.getSubTypesOf(ISimilarity.class);

	try {

	    Compound requestCompound = new Compound(sr.getValue().getMolfile());
	    AtomCountSimilarity acs = new AtomCountSimilarity(requestCompound);

	    Object[] parameters = new Object[2];
	    parameters[0] = 0.8;
	    parameters[1] = 10;
	    acs.setParameters(parameters);

	    List<SimilarityResult> similarityResults = acs.findAllSimilar();

	    if (similarityResults.isEmpty()) {
		CompoundResponse cr = new CompoundResponse("None compound is similar.", 404);
		throw new WebApplicationException(cr.buildResponse());
	    }

	    this.savedSimilarityResults = similarityResults;
	    return new SimilarityStatsResult(this.savedSimilarityResults.size());

	} catch (CompoundSearchException e) {
	    CompoundResponse cr = new CompoundResponse(500, e);
	    throw new WebApplicationException(cr.buildResponse());
	}
    }

    @GET
    @Path("/atom-count/{limit}/")
    public List<SimilarityCompoundResult> atomCountSimilarity(@PathParam("limit") Integer limit) {
	// Check wether there are some saved results 
	if (this.savedSimilarityResults.isEmpty()) {
	    CompoundResponse cr = new CompoundResponse("Make similarity request first.", 404);
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
	while (index < this.savedSimilarityResults.size() && index < limit) {
	    Compound c = idResource.getCompoundById(this.savedSimilarityResults.get(index).getId()).get(0);
	    Double s = this.savedSimilarityResults.get(index).getSimilarity();
	    returnResults.add(new SimilarityCompoundResult(c, s));
	    index++;
	}

	return returnResults;
    }

    private Set<ISimilarity> getSimilaritiesReflection() {
	// Get all available Similarities via reflection
	Reflections reflections = new Reflections("com.webtoad.diplomka.similarity");
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
