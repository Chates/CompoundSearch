/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor;

import cz.compoundsearch.descriptor.result.FingerprintDescriptorResult;
import cz.compoundsearch.descriptor.result.IDescriptorResult;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import java.util.BitSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.SubstructureFingerprinter;

/**
 *
 * @author Chates
 */
public class SubstructureFingerprintDescriptor implements ICompoundDescriptor {

    public SubstructureFingerprintDescriptor() {
    }

    @Override
    public IDescriptorResult calculate(Compound c) throws CompoundSearchException {
	SubstructureFingerprinter sf = new SubstructureFingerprinter();

	try {
	    BitSet fingerprint = sf.getFingerprint(c.getAtomContainer());
	    return new FingerprintDescriptorResult(fingerprint);
	} catch (CDKException ex) {
	    throw new CompoundSearchException("Unable to make fingeprint of the Compound. CDK error.");
	}

    }
}
