package cz.compoundsearch.entities;

import cz.compoundsearch.exceptions.CompoundSearchException;
import org.openscience.cdk.AtomContainer;

/**
 * Interface for chemical compound.
 * 
 * Implementation of this interface represents chemical compound through the 
 * whole framework. This is basically a wrapper for molecule from database, 
 * which all similarities, descriptors and algorithms are designed to work with. 
 * IAtomContainer from CDK library, which all compounds contain, achieves this. 
 * 
 * @author Martin Mates
 */
public interface ICompound {
    
    /**
     * Getter for AtomContainer.
     * 
     * @return AtomContainer Representation of the chemical molecule in CDK library
     * @throws CompoundSearchException 
     */
    public AtomContainer getAtomContainer() throws CompoundSearchException;
    
    /**
     * Getter for compound ID.
     * 
     * @return Long ID of the compound in specific repository or database 
     */
    public Long getId();
    
    /**
     * Setter for compound ID.
     * 
     * @param id ID of the compound in specific repository of database
     */
    public void setId(Long id);
    
}
