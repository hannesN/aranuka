package de.topicmapslab.aranuka.test.read;

import java.util.Date;
import java.util.Set;

import org.junit.Ignore;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Scope;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Ignore
@Topic
public class TestTopicType2 {

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
	@Scope(themes={"test:name_theme1", "test:name_theme2"})
	private String stringName;
	
	@Name
	private Set<String> setNames;
	
	@Name
	private String[] arrayNames;
		
	// occurrence
	@Occurrence
	@Scope(themes={"test:occurrence_theme1"})
	private String stringOccurrence;
	
	private Set<String> setSringOccurrence;
	private String[] arrayStringOccurrece;
	private int intOccurrence;
	private float floatOccurrence;
	private double doubleOccurrence;
	private boolean booleanOccurrence;
	private Date dateOccurrence;
	
	// associations
	@Association(played_role="test:topic_type_01_role", type="test:unnary_association")
	@Scope(themes={"test:association_theme1", "test:association_theme2", "test:association_theme3"})
	private boolean unaryAssociation;
	
	@Association(played_role="test:topic_type_01_role", other_role="test:counter_player_01_role", type="test:binary_association")
	private TestCounterPlayer binaryAssociation;
	
	@Association(played_role="test:topic_type_01_role", other_role="test:counter_player_01_role", type="test:set_binary_association")
	private Set<TestCounterPlayer> setBinaryAssociation;
	
	@Association(played_role="test:topic_type_01_role", other_role="test:counter_player_01_role", type="test:array_binary_association")
	private TestCounterPlayer[] arrayBinaryAssociation;
	
	@Association(played_role="test:topic_type_01_role", type="test:nnary_association")
	private TestAssociationContainer nnaryAssociation;
	
	@Association(played_role="test:topic_type_01_role", type="test:set_nnary_association")
	private Set<TestAssociationContainer> setNnaryAssociation;
	
	@Association(played_role="test:topic_type_01_role", type="test:array_nnary_association")
	private TestAssociationContainer[] arrayNnaryAssociation;

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
	public TestCounterPlayer getBinaryAssociation() {
		return binaryAssociation;
	}
	public void setBinaryAssociation(TestCounterPlayer binaryAssociation) {
		this.binaryAssociation = binaryAssociation;
	}
	public Set<TestCounterPlayer> getSetBinaryAssociation() {
		return setBinaryAssociation;
	}
	public void setSetBinaryAssociation(Set<TestCounterPlayer> setBinaryAssociation) {
		this.setBinaryAssociation = setBinaryAssociation;
	}
	public TestCounterPlayer[] getArrayBinaryAssociation() {
		return arrayBinaryAssociation;
	}
	public void setArrayBinaryAssociation(TestCounterPlayer[] arrayBinaryAssociation) {
		this.arrayBinaryAssociation = arrayBinaryAssociation;
	}
	public TestAssociationContainer getNnaryAssociation() {
		return nnaryAssociation;
	}
	public void setNnaryAssociation(TestAssociationContainer nnaryAssociation) {
		this.nnaryAssociation = nnaryAssociation;
	}
	public Set<TestAssociationContainer> getSetNnaryAssociation() {
		return setNnaryAssociation;
	}
	public void setSetNnaryAssociation(
			Set<TestAssociationContainer> setNnaryAssociation) {
		this.setNnaryAssociation = setNnaryAssociation;
	}
	public TestAssociationContainer[] getArrayNnaryAssociation() {
		return arrayNnaryAssociation;
	}
	public void setArrayNnaryAssociation(
			TestAssociationContainer[] arrayNnaryAssociation) {
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
