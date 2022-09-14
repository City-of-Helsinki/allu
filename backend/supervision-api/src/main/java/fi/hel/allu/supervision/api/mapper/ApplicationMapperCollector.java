package fi.hel.allu.supervision.api.mapper;

import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapperCollector {

    private final ApplicationMapper applicationMapper;
    private final CustomerMapper customerMapper;

    public ApplicationMapperCollector(ApplicationMapper applicationMapper, CustomerMapper customerMapper) {
        this.applicationMapper = applicationMapper;
        this.customerMapper = customerMapper;
    }

    public ApplicationMapper getApplicationMapper() {
        return applicationMapper;
    }

    public CustomerMapper getCustomerMapper() {
        return customerMapper;
    }
}
