package cz.compoundsearch.descriptor;

import cz.compoundsearch.exceptions.CompoundSearchException;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;

/**
 * Descriptor returning molecular weight
 * 
 * @author Martin Mates
 */
public class MolWeightDescriptor implements ICompoundDescriptor {
    
    public MolWeightDescriptor() {
	
    }

    /**
     * Calculation of the descriptor using CDK WeightDescriptor.
     * 
     * @param c Molecule from which the descriptor is calculated
     * @return Double Molecular weight of the molecule
     * @throws CompoundSearchException 
     */
    @Override
    public Double calculate(AtomContainer c) throws CompoundSearchException {
	
	// Inicialization of CDK molecular weight descriptor
	WeightDescriptor wd = new WeightDescriptor();	
	
	// Returns result converted to Double
	return Double.parseDouble(wd.calculate(c).getValue().toString());
	
    } 
}
