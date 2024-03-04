package nl.uva.doubledatasourceaxon.aggregates;

import lombok.NoArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@NoArgsConstructor
@Aggregate
public class Department {

    @AggregateIdentifier
    private String id;

    public Department(String id) {
        this.id = id;
    }
}
