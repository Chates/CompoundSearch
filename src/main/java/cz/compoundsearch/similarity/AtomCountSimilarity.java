/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.similarity;

import cz.compoundsearch.descriptor.AtomCountDescriptor;
import cz.compoundsearch.descriptor.ICompoundDescriptor;
import cz.compoundsearch.descriptor.result.IDescriptorResult;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.resources.ListResource;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Chates
 */
public class AtomCountSimilarity extends AbstractSimilarity {

    private ICompoundDescriptor atomCountDescriptor;
    private IDescriptorResult acdRequestResult;

    public AtomCountSimilarity(Compound requestCompound) throws CompoundSearchException {
	this.requestCompound = requestCompound;

	this.atomCountDescriptor = new AtomCountDescriptor();
	this.acdRequestResult = this.atomCountDescriptor.calculate(this.requestCompound);
    }

    public AtomCountSimilarity() {
	this.atomCountDescriptor = new AtomCountDescriptor();
    }

    @Override
    public void setRequestCompound(Compound c) throws CompoundSearchException {
	this.requestCompound = c;
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
    public void setParameters(List<String> parameters) throws CompoundSearchException {

	if (parameters.size() != 2) {
	    throw new CompoundSearchException("AtomCountSimilarity requires 2 parameters");
	}

	try {
	    this.treshold = Double.parseDouble(parameters.get(0));
	    if (this.treshold <= 0) {
		throw new CompoundSearchException("AtomCountSimilarity treshold parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	    throw new CompoundSearchException("AtomCountSimilarity treshold parameter must be of type Double");
	}
	
	try {
	    this.numberOfResults = Integer.parseInt(parameters.get(1));	
	    if (this.numberOfResults <= 0) {
		throw new CompoundSearchException("AtomCountSimilarity numberOfResults parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	     throw new CompoundSearchException("AtomCountSimilarity numberOfResults parameter must be of type Integer");
	}

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
	ListResource lr;
	List<Compound> result;
	try {
	    Context context = new InitialContext();
	    lr = (ListResource) context.lookup("java:module/ListResource");
	    // Must be set. 404 WebApplicationException is invoked and app stopped when empty result otherwise.
	    lr.setCalledFromApp(true);
	} catch (NamingException e) {
	    throw new CompoundSearchException("Database error in AtomCountSimilarity. Cannot obtain REST resources.");
	}

	result = lr.getCompounds(limit, start);

	return result;
    }
}
