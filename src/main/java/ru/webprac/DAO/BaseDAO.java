package ru.webprac.DAO;

import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.Collection;

@Repository
public abstract class BaseDAO<T, Id> {
    protected Class<T> entityClass;

    protected SessionFactory sessionFactory;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Autowired
    public void setSessionFactory(LocalSessionFactoryBean sessionFactory) {
        this.sessionFactory = sessionFactory.getObject();
    }

    public T getById(Serializable id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entityClass, id);
        }
    }

    public Collection<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(entityClass);
            criteria.from(entityClass);
            return session.createQuery(criteria).getResultList();
        }
    }

    public void save(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.save(entity);
            tr.commit();
        }
    }

    public void saveList(Collection<T> entities) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            for (T entity : entities) {
                session.save(entity);
            }
            tr.commit();
        }
    }

    public void update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.update(entity);
            tr.commit();
        }
    }

    public void delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            session.delete(entity);
            tr.commit();
        }
    }

    public void deleteById(Serializable id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            T entity = getById(id);
            session.delete(entity);
            tr.commit();
        }
    }

    public void deleteAllEntries() {
        String tableName = entityClass.getAnnotation(Table.class).name();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createNativeQuery(String.format("TRUNCATE TABLE %s RESTART IDENTITY CASCADE", tableName))
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }
}