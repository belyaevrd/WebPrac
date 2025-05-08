package ru.webprac.util;

import org.hibernate.type.EnumType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.Types;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class EnumPSQL extends EnumType {

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if(value == null) {
            st.setNull(index, Types.OTHER);
        }
        else {
            st.setObject(index, value.toString(), Types.OTHER);
        }
    }
}