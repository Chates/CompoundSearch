/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.entities;

import cz.compoundsearch.exceptions.CompoundSearchException;
import org.openscience.cdk.AtomContainer;

/**
 *
 * @author Chates
 */
public interface ICompound {
    
    public AtomContainer getAtomContainer() throws CompoundSearchException;
    public Long getId();
    
}
