/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor;

import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import org.openscience.cdk.AtomContainer;

/**
 *
 * @author Chates
 */
public interface ICompoundDescriptor {
    

    public Object calculate(AtomContainer c) throws CompoundSearchException ;
     
    
}