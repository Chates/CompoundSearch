/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor;

import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.exceptions.CompoundSearchException;
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
    public Integer calculate(AtomContainer c) throws CompoundSearchException {
	DescriptorValue dValue;

	AtomContainer molecule = c;


	org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor acd = new org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor();
	dValue = acd.calculate(molecule);


	return Integer.parseInt(dValue.getValue().toString());

    }
}
