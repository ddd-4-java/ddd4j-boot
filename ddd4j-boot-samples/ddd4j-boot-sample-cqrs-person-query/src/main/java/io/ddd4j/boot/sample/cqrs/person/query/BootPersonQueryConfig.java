package io.ddd4j.boot.sample.cqrs.person.query;

import io.ddd4j.core.cqrs.projection.DefaultProjectionService;
import io.ddd4j.core.cqrs.projection.InMemoryProjectionPositionRepository;
import io.ddd4j.core.cqrs.projection.ProjectionRunner;
import io.ddd4j.sample.cqrs.person.domain.PersonEvent;
import io.ddd4j.sample.cqrs.person.infrastructure.InMemoryPersonEventStore;
import io.ddd4j.sample.cqrs.person.query.PersonListView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BootPersonQueryConfig {

    @Bean
    public InMemoryPersonEventStore personEventStore() {
        return new InMemoryPersonEventStore();
    }

    @Bean
    public PersonListView personListView() {
        return new PersonListView();
    }

    @Bean
    public ProjectionRunner<PersonEvent> personProjectionRunner(InMemoryPersonEventStore eventStore) {
        return new ProjectionRunner<>(
                new DefaultProjectionService(new InMemoryProjectionPositionRepository()),
                eventStore
        );
    }
}
