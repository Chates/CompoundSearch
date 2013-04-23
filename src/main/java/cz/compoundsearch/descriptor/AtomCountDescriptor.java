package cz.compoundsearch.descriptor;

import cz.compoundsearch.exceptions.CompoundSearchException;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 * Descriptor returning number of atoms in a given molecule.
 * 
 * @author Martin Mates
 */
public class AtomCountDescriptor implements ICompoundDescriptor {

    public AtomCountDescriptor() {
    }

    /**
     * Calculation of the descriptor using CDK AtomCountDescriptor.
     * 
     * @param c Molecule from which the descriptor is calculated
     * @return Integer Number of atoms in molecule
     * @throws CompoundSearchException 
     */
    @Override
    public Integer calculate(AtomContainer c) throws CompoundSearchException {
	DescriptorValue dValue;

	org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor acd = new org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor();
	dValue = acd.calculate(c);
 
	return Integer.parseInt(dValue.getValue().toString());

    }
}
