package nl.uva.doubledatasourceaxon.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@ToString
@Entity
@Table
public class Integration {

    @Id
    private String sisCourseId;
    private String integrationId;

}
