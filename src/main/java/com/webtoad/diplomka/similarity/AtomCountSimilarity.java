/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.descriptor.AtomCountDescriptor;
import com.webtoad.diplomka.entities.Compound;
import java.util.List;

/**
 *
 * @author Chates
 */
public class AtomCountSimilarity extends AbstractSimilarity {

    public AtomCountSimilarity(Compound requestCompound) throws CompoundSearchException {
	this.selectAllCompoundsFromDB();
	this.requestCompound = requestCompound;
	this.descriptor = new AtomCountDescriptor();
	this.requestCompoundDescriptorResult = this.descriptor.calculate(requestCompound);
    }


    @Override
    public Boolean isSimilar(Compound c) throws CompoundSearchException {
	if (this.requestCompoundDescriptorResult == this.descriptor.calculate(c)) {
	    return true;
	}
	return false;
    }
}
