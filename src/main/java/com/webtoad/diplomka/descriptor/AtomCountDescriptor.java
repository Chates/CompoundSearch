/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.descriptor;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.descriptor.result.IDescriptorResult;
import com.webtoad.diplomka.descriptor.result.IntegerDescriptorResult;
import com.webtoad.diplomka.entities.Compound;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 *
 * @author Chates
 */
public class AtomCountDescriptor implements ICompoundDescriptor {

    public AtomCountDescriptor() {
    }

    @Override
    public IDescriptorResult calculate(Compound c) throws CompoundSearchException {
	DescriptorValue dValue = null;

	AtomContainer molecule = c.getAtomContainer();

	org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor acd = new org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor();
	dValue = acd.calculate(molecule);


	return new IntegerDescriptorResult(Integer.parseInt(dValue.getValue().toString()));

    }
}
