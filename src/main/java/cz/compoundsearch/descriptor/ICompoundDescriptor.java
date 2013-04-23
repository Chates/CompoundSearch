package cz.compoundsearch.descriptor;

import cz.compoundsearch.exceptions.CompoundSearchException;
import org.openscience.cdk.AtomContainer;

/**
 * Interface for all further descriptors.
 * 
 * @author Martin Mates
 */
public interface ICompoundDescriptor {
    
    /**
     * This method is used for descriptor calculations and may return any type.
     * 
     * @param c Molecule from which the descriptor is calculated
     * @return Object Computed value of the descriptor
     * @throws CompoundSearchException 
     */
    public Object calculate(AtomContainer c) throws CompoundSearchException ;
     
    
}