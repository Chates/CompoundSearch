/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor.result;

import java.util.BitSet;

/**
 *
 * @author Chates
 */
public class FingerprintDescriptorResult implements IDescriptorResult {

    private BitSet value;

    public FingerprintDescriptorResult(BitSet value) {
	this.value = value;
    }

    @Override
    public Integer length() {
	return value.size();
    }

    @Override
    public String toString() {
	return value.toString();
    }
    
    @Override
    public BitSet getValue() {
	return value;
    }
}
