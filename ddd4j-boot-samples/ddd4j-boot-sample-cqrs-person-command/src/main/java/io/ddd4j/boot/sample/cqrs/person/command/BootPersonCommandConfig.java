package io.ddd4j.boot.sample.cqrs.person.command;

import io.ddd4j.sample.cqrs.person.application.PersonCommandService;
import io.ddd4j.sample.cqrs.person.infrastructure.InMemoryPersonEventStore;
import io.ddd4j.sample.cqrs.person.infrastructure.InMemoryPersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BootPersonCommandConfig {

    @Bean
    public InMemoryPersonEventStore personEventStore() {
        return new InMemoryPersonEventStore();
    }

    @Bean
    public InMemoryPersonRepository personRepository(InMemoryPersonEventStore eventStore) {
        return new InMemoryPersonRepository(eventStore);
    }

    @Bean
    public PersonCommandService personCommandService(InMemoryPersonRepository repository) {
        return new PersonCommandService(repository);
    }
}
