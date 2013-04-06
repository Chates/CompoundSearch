/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.similarity;

import cz.compoundsearch.descriptor.SubstructureFingerprintDescriptor;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.SubstructureFingerprint;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.exceptions.NoMoreCompoundsException;
import cz.compoundsearch.resources.IdResource;
import cz.compoundsearch.resources.ListResource;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;

/**
 *
 * @author Chates
 */
public class SubstructureSimilarity extends AbstractSimilarity {

    public SubstructureSimilarity(Compound requestCompound) {
	this.requestCompound = requestCompound;
	this.treshold = 1.0;
    }

    public SubstructureSimilarity() {
    }

    @Override
    public Double calculateSimilarity(AtomContainer c) throws CompoundSearchException {
	try {
	    if (UniversalIsomorphismTester.isSubgraph(c, this.requestCompound.getAtomContainer()) == true) {
		return 1.0;
	    } else {
		return 0.0;
	    }
	} catch (CDKException e) {
	    throw new CompoundSearchException("Unable to test substructure presence CDK error.");
	}
    }

    @Override
    public List<Compound> screen(Integer start, Integer limit) throws CompoundSearchException, NoMoreCompoundsException {
	// Result of the screening
	List<Compound> screeningResult = new ArrayList<Compound>();
	
	// Selected fingeprints from DB
	List<SubstructureFingerprint> sfResult;
	sfResult = this.getCompounds(start, limit);
	
	// Result empty. No more compounds in database throw exception
	if (sfResult.isEmpty()) {
	    throw new NoMoreCompoundsException();
	}

	// Fingerprint of requested compound
	SubstructureFingerprintDescriptor sfd = new SubstructureFingerprintDescriptor();
	BitSet reqCompFingerprint = (BitSet) sfd.calculate(this.requestCompound.getAtomContainer());
	
	// For each Compound perform screening
	for (SubstructureFingerprint sf : sfResult) {
	    BitSet curCompFingerprint = sf.getFingerprint();
	    BitSet reqCompFingerprintClone = (BitSet) reqCompFingerprint.clone();
	    
	    // Perform logical AND to currentCompound
	    reqCompFingerprintClone.and(curCompFingerprint);
	    
	    // All bits in requested compound has to be in current compound
	    if (reqCompFingerprint.equals(reqCompFingerprintClone)) {
		screeningResult.add(sf.getCompound()); // May be substructure
	    }	    
	}
	
	return screeningResult;
	
    }

    @Override
    public void setParameters(List<String> parameters) throws CompoundSearchException {
	if (parameters.size() != 1) {
	    throw new CompoundSearchException("SubStructureSimilarity requires 1 parameter");
	}

	try {
	    this.numberOfResults = Integer.parseInt(parameters.get(0));
	    if (this.numberOfResults <= 0) {
		throw new CompoundSearchException("SubStructureSimilarity numberOfResults parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	    throw new CompoundSearchException("SubStructureSimilarity numberOfResults parameter must be of type Integer");
	}
    }

    @Override
    public Object[] getParameters() {
	Object[] parameters = new Object[1];
	parameters[0] = this.numberOfResults;

	return parameters;
    }

    @Override
    public String[] getParameterNames() {
	String[] names = new String[1];
	names[0] = "numberOfResults";

	return names;
    }

    @Override
    public Object getParameterType(String name) {
	if (name.equals("numberOfResults")) {
	    return 1;
	}

	return null;
    }

    @Override
    public List<SubstructureFingerprint> getCompounds(Integer start, Integer limit) throws CompoundSearchException {
	ListResource lr;
	// Selected fingeprints from DB
	List<SubstructureFingerprint> sfResult;
	
	try {
	    Context context = new InitialContext();
	    lr = (ListResource) context.lookup("java:module/ListResource");
	    // Must be set. 404 WebApplicationException is invoked and app stopped when empty result otherwise.
	    lr.setCalledFromApp(true);
	} catch (NamingException e) {
	    throw new CompoundSearchException("Database error in SubstructureSimilarity. Cannot obtain REST resources.");
	}

	sfResult = lr.getSubstructureFingerprint(limit, start);
	
	return sfResult;
    }
    
    
    @Override
    public Compound getCompoundById(Long id) throws CompoundSearchException {
	IdResource ir;		
	List<Compound> irResult;
	
	try {
	    Context context = new InitialContext();
	    ir = (IdResource) context.lookup("java:module/IdResource");
	    ir.setCalledFromApp(true);
	} catch (NamingException e) {
	    throw new CompoundSearchException("Database error in SubstructureSimilarity. Cannot obtain REST resources.");
	}	
	
	irResult = ir.getCompoundById(id);
	
	if (irResult.isEmpty()) {
	    throw new CompoundSearchException("Database error in SubstructureSimilarity. Cannot obtain compound with an ID " + id + ".");
	}
	
	return irResult.get(0);
    }
}