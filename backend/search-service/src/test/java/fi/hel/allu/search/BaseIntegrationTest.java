package fi.hel.allu.search;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.search.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Testcontainers
public abstract class BaseIntegrationTest {


	protected static final String CLUSTER_NAME = "allu-cluster";
	protected static final String NODE_NAME = "allu-node";
	protected static final String ELASTIC_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:6.8.0";

	protected static final String USERNAME = "someusername";

	@Container
	protected  ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE)
			.withExposedPorts(9300, 9200)
			.withEnv("xpack.security.enabled", "false")
			.withEnv("network.host", "_site_")
			.withEnv("network" + ".publish_host", "_local_")
			.withEnv("node.name", NODE_NAME)
			.withEnv("cluster.name", CLUSTER_NAME);


	@Test
	void isRunning() {
		Assertions.assertTrue(container.isRunning());
	}


	public static ApplicationES createApplication(Integer id) {
		ApplicationES applicationES = new ApplicationES();
		applicationES.setType(new ApplicationTypeES(ApplicationType.EVENT));
		applicationES.setId(id);
		applicationES.setApplicationId("TP000001");
		applicationES.setOwner(createUser(1));
		applicationES.setName(USERNAME + " " + id);
		applicationES.setStatus(new StatusTypeES(StatusType.PENDING));
		ZonedDateTime dateTime = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
		applicationES.setCreationTime(dateTime.toInstant().toEpochMilli());

		applicationES.setApplicationTypeData(createApplicationTypeData());
		return applicationES;
	}

	public static UserES createUser(Integer order) {
		return new UserES(USERNAME + " " + order, "real name");
	}

	public static List<ESFlatValue> createApplicationTypeData() {
		List<ESFlatValue> esFlatValues = new ArrayList<>();
		ZonedDateTime zonedDateTimeStart = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
		ZonedDateTime zonedDateTimeEnd = ZonedDateTime.parse("2016-07-06T06:23:04.000Z");

		esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "startTime",
										 zonedDateTimeStart.toInstant().toEpochMilli()));
		esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "endTime",
										 zonedDateTimeEnd.toInstant().toEpochMilli()));
		esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "attendees", 1000L));
		esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "description",
										 "Ulkoilmatapahtuman selitettä tässä."));
		return esFlatValues;
	}


}
