/**
 * 
 */
package de.topicmapslab.aranuka.test.occurrencedatatype;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Occurrence;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * @author Hannes Niederhausen
 *
 */
@Topic(subject_identifier="http://test.de/city")
public class City {

	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String si;
	
	@Occurrence(type="http://test.de/coords")
	private String coords;
	
	@Occurrence(type="http://test.de/builddata")
	private Date buildDate;
	
	@Occurrence(type="http://test.de/lastupdate", datatype="xsd:dateTime")
	private Date lastUpdate;

	public String getSi() {
		return si;
	}

	public void setSi(String si) {
		this.si = si;
	}

	public String getCoords() {
		return coords;
	}

	public void setCoords(String coords) {
		this.coords = coords;
	}

	public Date getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((buildDate == null) ? 0 : buildDate.hashCode());
		result = prime * result + ((coords == null) ? 0 : coords.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((si == null) ? 0 : si.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		SimpleDateFormat tmp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (buildDate == null) {
			if (other.buildDate != null)
				return false;
		} else if (other.buildDate == null ) {
			return false;
		} else if (!tmp.format(getBuildDate()).equals(tmp.format(other.getBuildDate()))) {
				return false;
		}
			
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (other.lastUpdate == null ) {
			return false;
		} else if (!tmp.format(getLastUpdate()).equals(tmp.format(other.getLastUpdate()))) {
				return false;
		}
		if (si == null) {
			if (other.si != null)
				return false;
		} else if (!si.equals(other.si))
			return false;
		return true;
	}
	
	
}
