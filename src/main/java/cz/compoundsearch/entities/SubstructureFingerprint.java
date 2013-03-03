/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.entities;

import java.util.BitSet;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@Entity
@XmlRootElement
@Table(name = "substructure_fingerprint")
@Access(AccessType.PROPERTY)
public class SubstructureFingerprint implements ICompound {

    private Long id;
    private BitSet fingerprint;
    private Compound compound;

//    @Column(name = "fingerprint")
//    public byte[] getFingerprintArray() {
//	byte[] bytes = new byte[fingerprint.length() / 8 + 1];
//
//	for (int i = 0; i < fingerprint.length(); i++) {
//	    if (fingerprint.get(i)) {
//		bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
//	    }
//	}
//
//	return bytes;
//    }
//
//    public void setFingerprintArray(byte[] bytes) {
//	BitSet bits = new BitSet();
//	for (int i = 0; i < bytes.length * 8; i++) {
//	    if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
//		bits.set(i);
//	    }
//	}
//	fingerprint = bits;
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Column(name = "fingerprint")
    public BitSet getFingerprint() {
	return fingerprint;
    }

    public void setFingerprint(BitSet fingerprint) {
	this.fingerprint = fingerprint;
    }

    @OneToOne
    @JoinColumn(name = "compound_id", unique = true)
    @Override
    public Compound getCompound() {
	return compound;
    }

    public void setCompound(Compound compound) {
	this.compound = compound;
    }
}
