/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.entities.Compound;
import java.util.List;
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

    @Override
    public Double calculateSimilarity(Compound c) throws CompoundSearchException {
	try {
	    if (UniversalIsomorphismTester.isSubgraph(c.getAtomContainer(), this.requestCompound.getAtomContainer()) == true) {
		return 1.0;
	    } else {
		return 0.0;
	    }
	} catch (CDKException e) {
	    throw new CompoundSearchException("Unable to test substructure presence CDK error.");
	}
    }

    @Override
    public List<Compound> getCompounds(Integer start, Integer limit) throws CompoundSearchException {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParameters(Object[] parameters) throws CompoundSearchException {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] getParameters() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getParameterNames() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getParameterType(String name) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

}