/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.descriptor.AtomCountDescriptor;
import com.webtoad.diplomka.descriptor.ICompoundDescriptor;
import com.webtoad.diplomka.descriptor.result.IDescriptorResult;
import com.webtoad.diplomka.entities.Compound;
import com.webtoad.diplomka.resources.ListResource;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author Chates
 */
public class AtomCountSimilarity extends AbstractSimilarity {

    private ICompoundDescriptor atomCountDescriptor;
    private IDescriptorResult acdRequestResult;
    private EntityManager entityManager;

    public AtomCountSimilarity(Compound requestCompound) throws CompoundSearchException {
	this.requestCompound = requestCompound;

	this.atomCountDescriptor = new AtomCountDescriptor();
	this.acdRequestResult = this.atomCountDescriptor.calculate(this.requestCompound);
    }

    @Override
    public Double calculateSimilarity(Compound c) throws CompoundSearchException {
	Integer requestAtoms = Integer.parseInt(this.acdRequestResult.toString());
	Integer compoundFromDBAtoms = Integer.parseInt(this.atomCountDescriptor.calculate(c).toString());

	Double similarity = (double) Math.min(requestAtoms, compoundFromDBAtoms) / Math.max(requestAtoms, compoundFromDBAtoms);

	return similarity;
    }

    @Override
    public void setParameters(Object[] parameters) throws CompoundSearchException {

	if (parameters.length != 2) {
	    throw new CompoundSearchException("AtomCountSimilarity requires 2 parameters");
	}

	if (!(parameters[0] instanceof Double)) {
	    throw new CompoundSearchException("AtomCountSimilarity treshold parameter must be of type Double");
	}

	if (!(parameters[1] instanceof Integer)) {
	    throw new CompoundSearchException("AtomCountSimilarity numberOfResults parameter must be of type Integer");
	}

	// Now everything is ok 	
	this.treshold = (Double) parameters[0];
	this.numberOfResults = (Integer) parameters[1];
    }

    @Override
    public Object[] getParameters() {
	Object[] parameters = new Object[2];
	parameters[0] = this.treshold;
	parameters[1] = this.numberOfResults;

	return parameters;
    }

    @Override
    public String[] getParameterNames() {
	String[] names = new String[2];
	names[0] = "treshold";
	names[1] = "numberOfResults";

	return names;
    }

    @Override
    public Object getParameterType(String name) {
	if (name.equals("treshold")) {
	    return 0.0;
	}

	if (name.equals("numberOfResults")) {
	    return 1;
	}

	return null;
    }

    @Override
    public List<Compound> getCompounds(Integer start, Integer limit) throws CompoundSearchException {
	// Is entity manager set yet?
	if (this.entityManager == null) {
	    throw new CompoundSearchException("AtomCount similarity doesnt have database connection.");
	}

	ListResource lr = new ListResource(this.entityManager);
	List<Compound> result = new ArrayList<Compound>();
	
	try {
	    result = lr.getCompounds(limit, start);
	} catch (WebApplicationException e) { // result is empty do nothing, empty result is returned
	   
	}
	return result;
    }

    public void setEntityManager(EntityManager em) {
	this.entityManager = em;
    }
}
