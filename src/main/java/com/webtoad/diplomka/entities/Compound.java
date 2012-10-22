package com.webtoad.diplomka.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Martin Mates
 */
@Entity
@XmlRootElement
@Table(name = "Compounds")
public class Compound {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "molecular_formula", length = 255, nullable = false)
    private String molecularFormula;
    @Column(name = "smiles", length = 255, nullable = false)
    private String smiles;   
    @Column(name = "molfile", length = 10000, nullable = false)
    private String molfile;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getMolecularFormula() {
	return molecularFormula;
    }

    public void setMolecularFormula(String molecularFormula) {
	this.molecularFormula = molecularFormula;
    }

    public String getSmiles() {
	return smiles;
    }

    public void setSmiles(String smiles) {
	this.smiles = smiles;
    }

    public String getMolfile() {
	return molfile;
    }

    public void setMolfile(String molfile) {
	this.molfile = molfile;
    }


       
}
