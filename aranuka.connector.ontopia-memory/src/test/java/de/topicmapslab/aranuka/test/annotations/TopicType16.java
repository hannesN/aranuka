package de.topicmapslab.aranuka.test.annotations;

import java.util.Date;
import java.util.Set;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic
public class TopicType16 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;
		
	// subject locator
	@Id(type=IdType.SUBJECT_LOCATOR)
	private Set<String> setSubjectLocator;
		
	// item identifier
	@Id(type=IdType.ITEM_IDENTIFIER)
	private String[] arrayItemIdentifier;
		
	// name
	@Name
	private String stringName;
	@Name
	private Set<String> setNames;
	@Name
	private String[] arrayNames;
		
	// occurrence
	@Occurrence
	private String stringOccurrence;
	@Occurrence
	private Set<String> setSringOccurrence;
	@Occurrence
	private String[] arrayStringOccurrece;
	@Occurrence
	private int intOccurrence;
	@Occurrence
	private float floatOccurrence;
	@Occurrence
	private double doubleOccurrence;
	@Occurrence
	private boolean booleanOccurrence;
	@Occurrence
	private Date dateOccurrence;
	
	// associations
	@Association(played_role="test:topic_type_01_role", type="test:unnary_association")
	private boolean unaryAssociation;
	@Association(played_role="test:topic_type_01_role", other_role="test:counter_player_01_role", type="test:binary_association")
	private CounterPlayer01 binaryAssociation;
	@Association(played_role="test:topic_type_01_role", other_role="test:counter_player_01_role", type="test:set_binary_association")
	private Set<CounterPlayer01> setBinaryAssociation;
	@Association(played_role="test:topic_type_01_role", other_role="test:counter_player_01_role", type="test:array_binary_association")
	private CounterPlayer01[] arrayBinaryAssociation;
	@Association(played_role="test:topic_type_01_role", type="test:nnary_association")
	private AssociationContainer01 nnaryAssociation;
	@Association(played_role="test:topic_type_01_role", type="test:set_nnary_association")
	private Set<AssociationContainer01> setNnaryAssociation;
	@Association(played_role="test:topic_type_01_role", type="test:array_nnary_association")
	private AssociationContainer01[] arrayNnaryAssociation;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}
	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}
	public Set<String> getSetSubjectLocator() {
		return setSubjectLocator;
	}
	public void setSetSubjectLocator(Set<String> setSubjectLocator) {
		this.setSubjectLocator = setSubjectLocator;
	}
	public String[] getArrayItemIdentifier() {
		return arrayItemIdentifier;
	}
	public void setArrayItemIdentifier(String[] arrayItemIdentifier) {
		this.arrayItemIdentifier = arrayItemIdentifier;
	}
	public String getStringName() {
		return stringName;
	}
	public void setStringName(String stringName) {
		this.stringName = stringName;
	}
	public String getStringOccurrence() {
		return stringOccurrence;
	}
	public void setStringOccurrence(String stringOccurrence) {
		this.stringOccurrence = stringOccurrence;
	}
	public Set<String> getSetSringOccurrence() {
		return setSringOccurrence;
	}
	public void setSetSringOccurrence(Set<String> setSringOccurrence) {
		this.setSringOccurrence = setSringOccurrence;
	}
	public String[] getArrayStringOccurrece() {
		return arrayStringOccurrece;
	}
	public void setArrayStringOccurrece(String[] arrayStringOccurrece) {
		this.arrayStringOccurrece = arrayStringOccurrece;
	}
	public int getIntOccurrence() {
		return intOccurrence;
	}
	public void setIntOccurrence(int intOccurrence) {
		this.intOccurrence = intOccurrence;
	}
	public float getFloatOccurrence() {
		return floatOccurrence;
	}
	public void setFloatOccurrence(float floatOccurrence) {
		this.floatOccurrence = floatOccurrence;
	}
	public double getDoubleOccurrence() {
		return doubleOccurrence;
	}
	public void setDoubleOccurrence(double doubleOccurrence) {
		this.doubleOccurrence = doubleOccurrence;
	}
	public boolean isBooleanOccurrence() {
		return booleanOccurrence;
	}
	public void setBooleanOccurrence(boolean booleanOccurrence) {
		this.booleanOccurrence = booleanOccurrence;
	}
	public Date getDateOccurrence() {
		return dateOccurrence;
	}
	public void setDateOccurrence(Date dateOccurrence) {
		this.dateOccurrence = dateOccurrence;
	}
	public boolean isUnaryAssociation() {
		return unaryAssociation;
	}
	public void setUnaryAssociation(boolean unaryAssociation) {
		this.unaryAssociation = unaryAssociation;
	}
	public CounterPlayer01 getBinaryAssociation() {
		return binaryAssociation;
	}
	public void setBinaryAssociation(CounterPlayer01 binaryAssociation) {
		this.binaryAssociation = binaryAssociation;
	}
	public Set<CounterPlayer01> getSetBinaryAssociation() {
		return setBinaryAssociation;
	}
	public void setSetBinaryAssociation(Set<CounterPlayer01> setBinaryAssociation) {
		this.setBinaryAssociation = setBinaryAssociation;
	}
	public CounterPlayer01[] getArrayBinaryAssociation() {
		return arrayBinaryAssociation;
	}
	public void setArrayBinaryAssociation(CounterPlayer01[] arrayBinaryAssociation) {
		this.arrayBinaryAssociation = arrayBinaryAssociation;
	}
	public AssociationContainer01 getNnaryAssociation() {
		return nnaryAssociation;
	}
	public void setNnaryAssociation(AssociationContainer01 nnaryAssociation) {
		this.nnaryAssociation = nnaryAssociation;
	}
	public Set<AssociationContainer01> getSetNnaryAssociation() {
		return setNnaryAssociation;
	}
	public void setSetNnaryAssociation(
			Set<AssociationContainer01> setNnaryAssociation) {
		this.setNnaryAssociation = setNnaryAssociation;
	}
	public AssociationContainer01[] getArrayNnaryAssociation() {
		return arrayNnaryAssociation;
	}
	public void setArrayNnaryAssociation(
			AssociationContainer01[] arrayNnaryAssociation) {
		this.arrayNnaryAssociation = arrayNnaryAssociation;
	}
	public Set<String> getSetNames() {
		return setNames;
	}
	public void setSetNames(Set<String> setNames) {
		this.setNames = setNames;
	}
	public String[] getArrayNames() {
		return arrayNames;
	}
	public void setArrayNames(String[] arrayNames) {
		this.arrayNames = arrayNames;
	}
	
}
