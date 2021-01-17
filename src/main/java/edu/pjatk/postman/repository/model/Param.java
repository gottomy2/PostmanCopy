package edu.pjatk.postman.repository.model;

import lombok.*;
import javax.persistence.*;

/**
 * @author gottomy2
 * Creates the 'params' table on the database
 * ID - auto generated id of param.
 * request_id - id of request table entity for which was the param provided.
 * name - name of the param | example: "username"
 * value - value of the param | example : "admin"
 */

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "params")
public class Param {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long requestId;

    private String name,value;
}