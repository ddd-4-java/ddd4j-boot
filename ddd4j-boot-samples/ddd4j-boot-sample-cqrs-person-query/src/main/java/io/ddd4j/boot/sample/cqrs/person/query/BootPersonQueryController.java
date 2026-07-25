package io.ddd4j.boot.sample.cqrs.person.query;

import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.core.cqrs.readmodel.ProjectionRunner;
import io.ddd4j.sample.cqrs.person.domain.PersonEvent;
import io.ddd4j.sample.cqrs.person.query.PersonListEntry;
import io.ddd4j.sample.cqrs.person.query.PersonListView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class BootPersonQueryController {

    private final PersonListView view;

    private final ProjectionRunner<PersonEvent> runner;

    public BootPersonQueryController(PersonListView view, ProjectionRunner<PersonEvent> runner) {
        this.view = view;
        this.runner = runner;
    }

    @GetMapping
    public ApiRestResponse<List<PersonListEntry>> all() {
        runner.runOnce(view);
        return ApiRestResponse.success(view.findAll());
    }

    @GetMapping("/{personId}")
    public ApiRestResponse<PersonListEntry> get(@PathVariable String personId) {
        runner.runOnce(view);
        return ApiRestResponse.success(view.findById(personId).orElse(null));
    }
}
