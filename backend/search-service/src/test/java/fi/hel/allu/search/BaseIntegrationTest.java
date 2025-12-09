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
import java.util.*;

@Testcontainers
public abstract class BaseIntegrationTest {

    protected static final String CLUSTER_NAME = "allu-cluster";
    protected static final String NODE_NAME = "allu-node";
    protected static final String ELASTIC_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:6.8.23";

    protected static final String USERNAME = "someusername";

    @Container
    protected static ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE).withExposedPorts(9300, 9200)
            .withEnv("xpack.security.enabled", "false").withEnv("network.host", "_site_")
            .withEnv("network.publish_host", "_local_").withEnv("node.name", NODE_NAME)
            .withEnv("cluster.name", CLUSTER_NAME);

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

    public static ApplicationQueryParameters createRecurringQuery(ZonedDateTime begin, ZonedDateTime end) {
        QueryParameter recurringQP = new QueryParameter(QueryParameter.FIELD_NAME_RECURRING_APPLICATION, begin, end);
        ApplicationQueryParameters params = new ApplicationQueryParameters();
        params.setQueryParameters(Collections.singletonList(recurringQP));
        return params;
    }

    public static List<ContactES> createContacts() {
        return createContacts(Arrays.asList("kontakti ihminen", "toinen contact"));
    }

    public static List<ContactES> createContacts(Collection<String> contactNames) {
        ArrayList<ContactES> contacts = new ArrayList<>();
        Integer idCounter = 1;
        for (String contactName : contactNames) {
            contacts.add(new ContactES(idCounter, contactName, true));
            idCounter++;
        }
        return contacts;
    }

    public static ApplicationES createApplication(Integer id, Integer ownerid) {
        ApplicationES applicationES = createApplication(id);
        applicationES.setOwner(createUser(ownerid));
        return applicationES;
    }

    @Test
    void isRunning() {
        Assertions.assertTrue(container.isRunning());
    }

}
