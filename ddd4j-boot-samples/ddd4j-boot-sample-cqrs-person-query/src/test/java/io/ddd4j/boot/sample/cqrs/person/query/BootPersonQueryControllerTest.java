package io.ddd4j.boot.sample.cqrs.person.query;

import io.ddd4j.core.cqrs.projection.DefaultProjectionService;
import io.ddd4j.core.cqrs.projection.InMemoryProjectionPositionRepository;
import io.ddd4j.core.cqrs.projection.ProjectionRunner;
import io.ddd4j.sample.cqrs.person.application.PersonCommandService;
import io.ddd4j.sample.cqrs.person.domain.CreatePersonCommand;
import io.ddd4j.sample.cqrs.person.domain.DeletePersonCommand;
import io.ddd4j.sample.cqrs.person.domain.PersonEvent;
import io.ddd4j.sample.cqrs.person.infrastructure.InMemoryPersonEventStore;
import io.ddd4j.sample.cqrs.person.infrastructure.InMemoryPersonRepository;
import io.ddd4j.sample.cqrs.person.query.PersonListEntry;
import io.ddd4j.sample.cqrs.person.query.PersonListView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BootPersonQueryControllerTest {

    @Test
    void shouldProjectAndQueryPersonList() {
        InMemoryPersonEventStore eventStore = new InMemoryPersonEventStore();
        PersonCommandService commandService = new PersonCommandService(new InMemoryPersonRepository(eventStore));
        PersonListView view = new PersonListView();
        ProjectionRunner<PersonEvent> runner = new ProjectionRunner<>(
                new DefaultProjectionService(new InMemoryProjectionPositionRepository()),
                eventStore
        );
        BootPersonQueryController controller = new BootPersonQueryController(view, runner);

        commandService.create(CreatePersonCommand.builder()
                .personId("p-boot-200")
                .name("Boot Bob")
                .build());

        PersonListEntry entry = controller.get("p-boot-200").getData();
        assertEquals("Boot Bob", entry.getName());
        assertEquals(1, controller.all().getData().size());

        commandService.delete(DeletePersonCommand.builder()
                .personId("p-boot-200")
                .build());
        assertNull(controller.get("p-boot-200").getData());
    }
}
