package io.ddd4j.boot.sample.cqrs.person.command;

import io.ddd4j.core.ApiCode;
import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.sample.cqrs.person.application.PersonCommandService;
import io.ddd4j.sample.cqrs.person.domain.CreatePersonCommand;
import io.ddd4j.sample.cqrs.person.domain.DeletePersonCommand;
import io.ddd4j.sample.cqrs.person.domain.PersonId;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class BootPersonCommandController {

    private final PersonCommandService commandService;

    public BootPersonCommandController(PersonCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/create")
    public ApiRestResponse<String> create(@Valid @RequestBody CreatePersonCommand command) {
        PersonId personId = commandService.create(command);
        return ApiRestResponse.of(ApiCode.SC_SUCCESS, personId.getValue());
    }

    @DeleteMapping("/{personId}")
    public ApiRestResponse<Void> delete(@PathVariable String personId) {
        commandService.delete(DeletePersonCommand.builder()
                .personId(personId)
                .build());
        return ApiRestResponse.success((Void) null);
    }
}
