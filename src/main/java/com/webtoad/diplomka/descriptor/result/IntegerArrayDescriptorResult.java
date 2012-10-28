/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.descriptor.result;

import java.util.ArrayList;

/**
 *
 * @author Chates
 */
public class IntegerArrayDescriptorResult implements IArrayDescriptorResult<Integer> {

    private ArrayList<Integer> array;

    public IntegerArrayDescriptorResult() {
	this.array = new ArrayList<Integer>();
    }

    public IntegerArrayDescriptorResult(Integer size) {
	this.array = new ArrayList<Integer>(size);
    }

    @Override
    public void add(Integer value) {
	this.array.add(value);
    }

    @Override
    public Integer get(Integer index) {
	if (index >= this.array.size()) {
	    return 0;
	}
	return this.array.get(index);
    }

    @Override
    public Integer length() {
	return array.size();
    }

    @Override
    public String toString() {
	StringBuilder stringBuilder = new StringBuilder();
	for (int i = 0; i < length(); i++) {
	    stringBuilder.append(get(i));
	    if (i + 1 < length()) {
		stringBuilder.append(",");
	    }
	}
	return stringBuilder.toString();
    }



}
