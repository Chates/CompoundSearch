/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.descriptor.ICompoundDescriptor;
import com.webtoad.diplomka.descriptor.result.IDescriptorResult;
import com.webtoad.diplomka.entities.Compound;
import com.webtoad.diplomka.resources.ListResource;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author Chates
 */
abstract class AbstractSimilarity implements ISimilarity {

    protected List<Compound> compoundsFromDatabase = new ArrayList<Compound>();
    protected List<Compound> similarCompounds = new ArrayList<Compound>();
    protected Compound requestCompound;

    @Override
    public List<Compound> findAllSimilar() throws CompoundSearchException {
	this.screen();
	
	for (Compound c : compoundsFromDatabase) {
	    if (isSimilar(c)) {
		similarCompounds.add(c);
	    }
	}

	return similarCompounds;
    }

    @Override
    public void screen() {
    }
    
    @Override
    public void setCompounds(List<Compound> lc) {
	this.compoundsFromDatabase = lc;
    }

}