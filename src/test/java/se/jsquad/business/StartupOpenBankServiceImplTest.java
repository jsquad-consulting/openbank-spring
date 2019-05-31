package se.jsquad.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.jsquad.entity.SystemProperty;
import se.jsquad.producer.OpenBankPersistenceUnitProducer;
import se.jsquad.property.AppPropertyConfiguration;
import se.jsquad.repository.SystemPropertyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"classpath:META-INF/applicationContext.xml"})
public class StartupOpenBankServiceImplTest {
	@Autowired
	StartupOpenBankService startupOpenBankService;

	@Autowired
	SystemPropertyRepository systemPropertyRepository;

	@Autowired
	AppPropertyConfiguration appPropertyConfiguration;

	@Autowired
	OpenBankPersistenceUnitProducer openBankPersistenceUnitProducer;

	@Test
	public void testSystemPropertyCacheIsPopulated() {
		// When
		List<SystemProperty> systemPropertyList = systemPropertyRepository.findAllUniqueSystemProperties();

		// Then
		assertEquals(1, systemPropertyList.size());

		SystemProperty systemProperty = systemPropertyList.iterator().next();

		assertTrue(openBankPersistenceUnitProducer.getEntityManager().getEntityManagerFactory().getCache()
				.contains(SystemProperty.class, systemProperty.getId()));

		assertEquals(appPropertyConfiguration.getName(), systemProperty.getName());
		assertEquals(appPropertyConfiguration.getVersion(), systemProperty.getValue());
	}
}
