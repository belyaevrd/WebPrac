package ru.webprac.classes;

import javax.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserCourse implements Serializable {
    private Long userId;

    private Long courseId;
}