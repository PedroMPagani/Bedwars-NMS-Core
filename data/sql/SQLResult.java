package com.sweatsunited.data.sql;

import java.sql.*;

public interface SQLResult
{
    void process(final ResultSet p0) throws SQLException;
}
