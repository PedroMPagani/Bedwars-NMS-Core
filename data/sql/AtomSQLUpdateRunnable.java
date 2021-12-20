package com.sweatsunited.data.sql;

import java.sql.*;

public class AtomSQLUpdateRunnable implements Runnable
{
    private Connection connection;
    private AtomSQL sql;
    
    public AtomSQLUpdateRunnable(final Connection connection, final AtomSQL sql) {
        this.connection = connection;
        this.sql = sql;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    @Override
    public void run() {
        try {
            final PreparedStatement statement = this.connection.prepareStatement(this.sql.getSQL());
            try {
                this.sql.applyObjects(statement);
                statement.executeUpdate();
                if (statement != null) {
                    statement.close();
                }
            }
            catch (Throwable t) {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (SQLException ex) {
            throw new AtomSQLException("",ex);
        }
    }
}
