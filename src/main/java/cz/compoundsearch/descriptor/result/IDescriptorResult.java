/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor.result;


/**
 *
 * @author Chates
 */
public interface IDescriptorResult {
    
    public Integer length();
    
    @Override
    public String toString();
    
    public Object getValue();
    
}
