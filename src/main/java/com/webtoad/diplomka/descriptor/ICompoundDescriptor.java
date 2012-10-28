/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.descriptor;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.descriptor.result.IDescriptorResult;
import com.webtoad.diplomka.entities.Compound;

/**
 *
 * @author Chates
 */
public interface ICompoundDescriptor {
    

    public IDescriptorResult calculate(Compound c) throws CompoundSearchException ;
     
    
}