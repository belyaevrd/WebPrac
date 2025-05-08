package ru.webprac.DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.webprac.classes.*;

import java.util.Collection;

@Repository
public class LessonDAO extends BaseDAO<Lesson, Long> {
    public LessonDAO() {
        super(Lesson.class);
    }
}