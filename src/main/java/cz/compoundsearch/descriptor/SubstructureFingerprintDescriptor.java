/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.descriptor;

import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import java.util.BitSet;
import org.openscience.cdk.AtomContainer;
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
    public BitSet calculate(AtomContainer c) throws CompoundSearchException {
	SubstructureFingerprinter sf = new SubstructureFingerprinter();

	try {
	    return sf.getFingerprint(c);
	} catch (CDKException ex) {
	    throw new CompoundSearchException("Unable to make fingeprint of the Compound. CDK error.");
	}

    }
}
