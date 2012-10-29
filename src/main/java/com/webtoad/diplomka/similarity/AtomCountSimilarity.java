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

    @Override
    public Boolean isSimilar(Compound c) throws CompoundSearchException {
	Integer requestAtoms = Integer.parseInt(this.acdRequestResult.toString());
	Integer compoundFromDBAtoms = Integer.parseInt(this.atomCountDescriptor.calculate(c).toString());
	if (requestAtoms == compoundFromDBAtoms) {
	    return true;
	}
	return false;
    }
}
