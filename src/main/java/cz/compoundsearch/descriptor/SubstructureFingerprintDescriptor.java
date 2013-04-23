package cz.compoundsearch.descriptor;

import cz.compoundsearch.exceptions.CompoundSearchException;
import java.util.BitSet;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.SubstructureFingerprinter;

/**
 * Descriptor returning structural key fingerprint from the CDK library.
 * 
 * Fragment dictionary is defined in 
 * <a href="http://pele.farmbio.uu.se/nightly-1.2.3/cdk-javadoc-1.2.4/index.html">CDK documentation</a>
 * 
 * @author Martin Mates
 */
public class SubstructureFingerprintDescriptor implements ICompoundDescriptor {

    public SubstructureFingerprintDescriptor() {
    }

    /**
     * Creation of the structural fingerprint using CDK fingerprinter.
     * 
     * @param c Molecule from which the descriptor is calculated
     * @return BitSet Structural key fingerprint
     * @throws CompoundSearchException 
     */
    @Override
    public BitSet calculate(AtomContainer c) throws CompoundSearchException {
	SubstructureFingerprinter sf = new SubstructureFingerprinter();

	try {
	    return sf.getFingerprint(c);
	} catch (CDKException ex) {
	    throw new CompoundSearchException("Unable to create fingeprint of the Compound. CDK error.");
	}

    }
}
