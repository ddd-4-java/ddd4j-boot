package io.ddd4j.boot.sample.cqrs.person.command;

import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.sample.cqrs.person.application.PersonCommandService;
import io.ddd4j.sample.cqrs.person.domain.CreatePersonCommand;
import io.ddd4j.sample.cqrs.person.infrastructure.InMemoryPersonEventStore;
import io.ddd4j.sample.cqrs.person.infrastructure.InMemoryPersonRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BootPersonCommandControllerTest {

    @Test
    void shouldCreateAndDeletePerson() {
        InMemoryPersonEventStore eventStore = new InMemoryPersonEventStore();
        PersonCommandService commandService = new PersonCommandService(new InMemoryPersonRepository(eventStore));
        BootPersonCommandController controller = new BootPersonCommandController(commandService);

        ApiRestResponse<String> created = controller.create(CreatePersonCommand.builder()
                .personId("p-boot-100")
                .name("Boot Alice")
                .build());

        assertEquals("p-boot-100", created.getData());
        assertDoesNotThrow(() -> controller.delete("p-boot-100"));
    }
}
