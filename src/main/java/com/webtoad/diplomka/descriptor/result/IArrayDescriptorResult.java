/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.descriptor.result;

/**
 *
 * @author Chates
 */
public interface IArrayDescriptorResult<N extends Number> extends IDescriptorResult {
    
    public void add(N value);
    
    public N get(Integer index);
    
}
