package edu.pjatk.postman.db.controller.request.model;

import edu.pjatk.postman.db.repository.model.User;
import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetRequestResponse {
    private Long id;
    private Long userId;
    private String url;
}
