package de.topicmapslab.aranuka.test.preusker.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier = { "http://de.wikipedia.org/wiki/Brief" })
public class Brief {

	@Id(type = IdType.SUBJECT_IDENTIFIER)
	private String subjectIdentifier;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Datierung", datatype = "http://www.w3.org/2001/XMLSchema#string")
	private String datum_transkribiert;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Altersbestimmung", datatype = "http://www.w3.org/2001/XMLSchema#date")
	private java.util.Date datum_zugesichert;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Identifikator", datatype = "http://www.w3.org/2001/XMLSchema#string")
	private String id;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Paginierung", datatype = "http://www.w3.org/2001/XMLSchema#integer")
	private int seitenumfang;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Signatur", datatype = "http://www.w3.org/2001/XMLSchema#string")
	private String signatur;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Ortsbestimmung", datatype = "http://www.w3.org/2001/XMLSchema#string")
	private String standort;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Volltext", datatype = "http://www.w3.org/2001/XMLSchema#string")
	private String volltext;
	@de.topicmapslab.aranuka.annotations.Occurrence(type = "http://de.wikipedia.org/wiki/Sonstiges", datatype = "http://www.w3.org/2001/XMLSchema#string")
	private String weitereInformationen;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://de.wikipedia.org/wiki/Adressat", played_role = "http://psi.archaeologie.sachsen.de/preusker/empfangen_von", persistOnCascade = true)
	private Brief.Adressat adressat;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://psi.archaeologie.sachsen.de/preusker/briefdigitalisierung", played_role = "http://psi.archaeologie.sachsen.de/preusker/digitalisierter_brief", other_role = "http://psi.archaeologie.sachsen.de/preusker/digitale_aufnahme")
	private Digitalisat digitalisat;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://psi.archaeologie.sachsen.de/preusker/briefkategorie", played_role = "http://psi.archaeologie.sachsen.de/preusker/kategorisierter_brief", other_role = "http://psi.archaeologie.sachsen.de/preusker/kategorisierung")
	private Set<Kategorie> kategorieSet;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://psi.archaeologie.sachsen.de/preusker/briefschlagwort", played_role = "http://psi.archaeologie.sachsen.de/preusker/verschlagworteter_brief", other_role = "http://psi.archaeologie.sachsen.de/preusker/verschlagwortung")
	private Set<Schlagwort> schlagwortSet;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://psi.archaeologie.sachsen.de/preusker/entstehungsort", played_role = "http://psi.archaeologie.sachsen.de/preusker/entstand_in", other_role = "http://psi.archaeologie.sachsen.de/preusker/entstehungsort_von")
	private Ort ort;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://de.wikipedia.org/wiki/inaktiv", played_role = "http://psi.archaeologie.sachsen.de/preusker/inaktiver_brief")
	private boolean inaktiv;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://de.wikipedia.org/wiki/Thema", played_role = "http://psi.archaeologie.sachsen.de/preusker/thematisiert", other_role = "http://psi.archaeologie.sachsen.de/preusker/ist_thema_in")
	private Set<Person> personSet;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://de.wikipedia.org/wiki/Verfasser", played_role = "http://psi.archaeologie.sachsen.de/preusker/verfasst_von", persistOnCascade = true)
	private Brief.Verfasser verfasser;
	@de.topicmapslab.aranuka.annotations.Association(type = "http://psi.archaeologie.sachsen.de/preusker/verwendungsnachweis", played_role = "http://psi.archaeologie.sachsen.de/preusker/nachgewiesener_brief", other_role = "http://psi.archaeologie.sachsen.de/preusker/referenzierende_publikation")
	private Set<Publikation> publikationSet;

	public String getSubjectIdentifier() {
		return this.subjectIdentifier;
	}

	public void setSubjectIdentifier(String subjectIdentifier) {
		this.subjectIdentifier = subjectIdentifier;
	}

	public String getDatum_transkribiert() {
		return this.datum_transkribiert;
	}

	public void setDatum_transkribiert(String datum_transkribiert) {
		this.datum_transkribiert = datum_transkribiert;
	}

	public java.util.Date getDatum_zugesichert() {
		return this.datum_zugesichert;
	}

	public void setDatum_zugesichert(java.util.Date datum_zugesichert) {
		this.datum_zugesichert = datum_zugesichert;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
		setSubjectIdentifier("http://psi.archaeologie.sachsen.de/preusker/Brief/"
				+ id);
	}

	public int getSeitenumfang() {
		return this.seitenumfang;
	}

	public void setSeitenumfang(int seitenumfang) {
		this.seitenumfang = seitenumfang;
	}

	public String getSignatur() {
		return this.signatur;
	}

	public void setSignatur(String signatur) {
		this.signatur = signatur;
	}

	public String getStandort() {
		return this.standort;
	}

	public void setStandort(String standort) {
		this.standort = standort;
	}

	public String getVolltext() {
		return this.volltext;
	}

	public void setVolltext(String volltext) {
		this.volltext = volltext;
	}

	public String getWeitereInformationen() {
		return this.weitereInformationen;
	}

	public void setWeitereInformationen(String weitereInformationen) {
		this.weitereInformationen = weitereInformationen;
	}

	public Brief.Adressat getAdressat() {
		return this.adressat;
	}

	public void setAdressat(Brief.Adressat adressat) {
		this.adressat = adressat;
	}

	public Digitalisat getDigitalisat() {
		return this.digitalisat;
	}

	public void setDigitalisat(Digitalisat digitalisat) {
		this.digitalisat = digitalisat;
	}

	public Set<Kategorie> getKategorieSet() {
		if (this.kategorieSet == null) {
			return Collections.emptySet();
		} else {
			return this.kategorieSet;
		}
	}

	public void setKategorieSet(Set<Kategorie> kategorieSet) {
		this.kategorieSet = kategorieSet;
	}

	public void addKategorie(Kategorie kategorie) {
		if (this.kategorieSet == null) {
			this.kategorieSet = new HashSet<Kategorie>();
		}
		this.kategorieSet.add(kategorie);
	}

	public void removeKategorie(Kategorie kategorie) {
		if (this.kategorieSet != null) {
			this.kategorieSet.remove(kategorie);
		}
	}

	public Set<Schlagwort> getSchlagwortSet() {
		if (this.schlagwortSet == null) {
			return Collections.emptySet();
		} else {
			return this.schlagwortSet;
		}
	}

	public void setSchlagwortSet(Set<Schlagwort> schlagwortSet) {
		this.schlagwortSet = schlagwortSet;
	}

	public void addSchlagwort(Schlagwort schlagwort) {
		if (this.schlagwortSet == null) {
			this.schlagwortSet = new HashSet<Schlagwort>();
		}
		this.schlagwortSet.add(schlagwort);
	}

	public void removeSchlagwort(Schlagwort schlagwort) {
		if (this.schlagwortSet != null) {
			this.schlagwortSet.remove(schlagwort);
		}
	}

	public Ort getOrt() {
		return this.ort;
	}

	public void setOrt(Ort ort) {
		this.ort = ort;
	}

	public boolean isInaktiv() {
		return this.inaktiv;
	}

	public void setInaktiv(boolean inaktiv) {
		this.inaktiv = inaktiv;
	}

	public Set<Person> getPersonSet() {
		if (this.personSet == null) {
			return Collections.emptySet();
		} else {
			return this.personSet;
		}
	}

	public void setPersonSet(Set<Person> personSet) {
		this.personSet = personSet;
	}

	public void addPerson(Person person) {
		if (this.personSet == null) {
			this.personSet = new HashSet<Person>();
		}
		this.personSet.add(person);
	}

	public void removePerson(Person person) {
		if (this.personSet != null) {
			this.personSet.remove(person);
		}
	}

	public Brief.Verfasser getVerfasser() {
		return this.verfasser;
	}

	public void setVerfasser(Brief.Verfasser verfasser) {
		this.verfasser = verfasser;
	}

	public Set<Publikation> getPublikationSet() {
		if (this.publikationSet == null) {
			return Collections.emptySet();
		} else {
			return this.publikationSet;
		}
	}

	public void setPublikationSet(Set<Publikation> publikationSet) {
		this.publikationSet = publikationSet;
	}

	public void addPublikation(Publikation publikation) {
		if (this.publikationSet == null) {
			this.publikationSet = new HashSet<Publikation>();
		}
		this.publikationSet.add(publikation);
	}

	public void removePublikation(Publikation publikation) {
		if (this.publikationSet != null) {
			this.publikationSet.remove(publikation);
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.subjectIdentifier == null) ? 0
						: this.subjectIdentifier.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Brief other = ((Brief) obj);
		if (this.subjectIdentifier == null) {
			if (other.subjectIdentifier != null) {
				return false;
			}
		} else {
			if (!this.subjectIdentifier.equals(other.subjectIdentifier)) {
				return false;
			}
		}
		return true;
	}

	@de.topicmapslab.aranuka.annotations.AssociationContainer
	public static class Adressat {

		@de.topicmapslab.aranuka.annotations.Role(type = "http://psi.archaeologie.sachsen.de/preusker/hat_empfangen")
		private Entity entity;
		@de.topicmapslab.aranuka.annotations.Role(type = "http://psi.archaeologie.sachsen.de/preusker/funktion_adressat")
		private Funktion funktion;

		public Entity getEntity() {
			return this.entity;
		}

		public void setEntity(Entity entity) {
			this.entity = entity;
		}

		public Funktion getFunktion() {
			return this.funktion;
		}

		public void setFunktion(Funktion funktion) {
			this.funktion = funktion;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.entity == null) ? 0 : this.entity.hashCode());
			result = prime * result
					+ ((this.funktion == null) ? 0 : this.funktion.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			Brief.Adressat other = ((Brief.Adressat) obj);
			if (this.entity == null) {
				if (other.entity != null) {
					return false;
				}
			} else {
				if (!this.entity.equals(other.entity)) {
					return false;
				}
			}
			if (this.funktion == null) {
				if (other.funktion != null) {
					return false;
				}
			} else {
				if (!this.funktion.equals(other.funktion)) {
					return false;
				}
			}
			return true;
		}

	}

	@de.topicmapslab.aranuka.annotations.AssociationContainer
	public static class Verfasser {

		@de.topicmapslab.aranuka.annotations.Role(type = "http://psi.archaeologie.sachsen.de/preusker/funktion_verfasser")
		private Funktion funktion;
		@de.topicmapslab.aranuka.annotations.Role(type = "http://psi.archaeologie.sachsen.de/preusker/hat_verfasst")
		private Entity entity;

		public Funktion getFunktion() {
			return this.funktion;
		}

		public void setFunktion(Funktion funktion) {
			this.funktion = funktion;
		}

		public Entity getEntity() {
			return this.entity;
		}

		public void setEntity(Entity entity) {
			this.entity = entity;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.funktion == null) ? 0 : this.funktion.hashCode());
			result = prime * result
					+ ((this.entity == null) ? 0 : this.entity.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			Brief.Verfasser other = ((Brief.Verfasser) obj);
			if (this.funktion == null) {
				if (other.funktion != null) {
					return false;
				}
			} else {
				if (!this.funktion.equals(other.funktion)) {
					return false;
				}
			}
			if (this.entity == null) {
				if (other.entity != null) {
					return false;
				}
			} else {
				if (!this.entity.equals(other.entity)) {
					return false;
				}
			}
			return true;
		}

	}

}
