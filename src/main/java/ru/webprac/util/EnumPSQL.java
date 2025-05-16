package ru.webprac.util;

import lombok.EqualsAndHashCode;
import org.hibernate.type.EnumType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import ru.webprac.classes.UserRole;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Objects;

public class EnumPSQL implements UserType<UserRole> {

    @Override
    public boolean equals(UserRole x, UserRole y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(UserRole x) {
        return Objects.hashCode(x);
    }

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<UserRole> returnedClass() {
        return UserRole.class;
    }

    @Override
    public UserRole nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String value = rs.getString(position);
        return value == null ? null : UserRole.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, UserRole value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        st.setObject(index, value != null ? value.name() : null, Types.OTHER);
    }

    @Override
    public UserRole deepCopy(UserRole userRole) {
        return userRole;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(UserRole userRole) {
        return userRole;
    }

    @Override
    public UserRole assemble(Serializable serializable, Object o) {
        return (UserRole) serializable;
    }
}