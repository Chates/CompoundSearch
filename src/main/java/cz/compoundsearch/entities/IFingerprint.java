package cz.compoundsearch.entities;

import java.util.BitSet;

/**
 * Interface for fingerprint.
 *
 * Implementation of this interface represents fingerprint through the whole
 * framework. This is basically a wrapper for fingerprint from database, which
 * some similarities, descriptors and algorithms are designed to work with.
 *
 * @author Martin Mates
 */
public interface IFingerprint extends ICompound {

    /**
     * Getter for fingerprint
     *
     * @return BitSet Fingerprint
     */
    public BitSet getFingerprint();

    /**
     * Setter for fingerprint
     * 
     * @param bs Fingerprint
     */
    public void setFingerprint(BitSet bs);

    /**
     * Getter for compound associated with this fingerprint
     * 
     * @return Compound Compound entity from Database 
     */
    public Compound getCompound();
}
