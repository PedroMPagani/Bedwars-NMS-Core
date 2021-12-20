package com.sweatsunited.data.sql;

import org.apache.commons.lang.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AtomSQL {

    private String sql;
    private Object[] objects;

    public AtomSQL(String sql, Object... objects) {
        this.sql = sql;
        this.objects = objects;
    }

    public String getSQL() {
        return sql;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void append(AtomSQL sql) {
        append(sql.getSQL(), sql.getObjects());
    }

    public void append(String sql, Object... objects) {
        this.sql += sql;
        this.objects = ArrayUtils.addAll(this.objects, objects);
    }

    public void applyObjects(PreparedStatement statement) throws SQLException {
        if (objects.length > 0) {
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
        }
    }

}
