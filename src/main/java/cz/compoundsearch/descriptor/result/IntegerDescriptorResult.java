/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor.result;

/**
 *
 * @author Chates
 */
public class IntegerDescriptorResult implements IDescriptorResult {

    private Integer value;
    
    public IntegerDescriptorResult(Integer value) {
	this.value = value;
    }
    
    public Integer intValue() {
	return this.value;
    }
    
    @Override
    public Integer length() {
	return 1;
    }
    
    @Override
    public String toString() {
	return Integer.toString(this.value);
    }


    
}
