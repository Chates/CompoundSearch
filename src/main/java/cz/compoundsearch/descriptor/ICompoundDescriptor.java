/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor;

import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.descriptor.result.IDescriptorResult;
import cz.compoundsearch.entities.Compound;

/**
 *
 * @author Chates
 */
public interface ICompoundDescriptor {
    

    public IDescriptorResult calculate(Compound c) throws CompoundSearchException ;
     
    
}