package com.sweatsunited.data.sql;

import java.sql.*;

public interface AtomSQLConnection
{
    Connection getConnection();
    
    void openConnection();
    
    void closeConnection();
}
