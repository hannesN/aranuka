package de.topicmapslab.aranuka.codegen.core.layer;

import de.topicmapslab.aranuka.codegen.core.AranukaCodeGenTestCase;
import de.topicmapslab.aranuka.codegen.core.cached.generated.City;
import de.topicmapslab.aranuka.codegen.core.cached.generated.Country;
import de.topicmapslab.aranuka.codegen.core.cached.generated.GrandDuchy;
import de.topicmapslab.aranuka.codegen.core.cached.generated.Kingdom;
import de.topicmapslab.aranuka.codegen.core.cached.generated.Language;
import de.topicmapslab.aranuka.codegen.core.cached.generated.Republic;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.core.layer.dao.CityTopicDAO;
import de.topicmapslab.aranuka.codegen.core.layer.dao.CountryTopicDAO;
import de.topicmapslab.aranuka.codegen.core.layer.dao.GrandDuchyTopicDAO;
import de.topicmapslab.aranuka.codegen.core.layer.dao.KingdomTopicDAO;
import de.topicmapslab.aranuka.codegen.core.layer.dao.LanguageTopicDAO;
import de.topicmapslab.aranuka.codegen.core.layer.dao.RepublicTopicDAO;
import de.topicmapslab.aranuka.codegen.layer.base.TopicMapDAOFactory;
import de.topicmapslab.aranuka.codegen.properties.PropertyLoader;

/**
 * 
 * @author Sven Krosse
 *
 */
public class LazyLayerTestCase extends AranukaCodeGenTestCase {

	private TopicMapDAOFactory factory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		PropertyLoader.initialize(getTopicMapSystem(), getTopicMap());

		factory = TopicMapDAOFactory.getInstance(getTopicMap());
		factory.registerTopicMapDAO(City.class, CityTopicDAO.class);
		factory.registerTopicMapDAO(Country.class, CountryTopicDAO.class);
		factory.registerTopicMapDAO(GrandDuchy.class, GrandDuchyTopicDAO.class);
		factory.registerTopicMapDAO(Kingdom.class, KingdomTopicDAO.class);
		factory.registerTopicMapDAO(Language.class, LanguageTopicDAO.class);
		factory.registerTopicMapDAO(Republic.class, RepublicTopicDAO.class);
	}

	public void testLazyLayerRetrieve() throws TopicMap2JavaMapperException {
		CountryTopicDAO dao = (CountryTopicDAO) factory
				.getTopicMapDAO(Country.class);
		for (Country country : dao.retrieve()) {
			System.out.println(country.getSubjectIdentifiers() + ": "
					+ country.getTopicname());
		}
	}

}
