package com.sweatsunited.data.sql;

import java.sql.*;

public class AtomSQLRunnable implements Runnable
{
    private Connection connection;
    private AtomSQL sql;
    private SQLResult action;
    
    public AtomSQLRunnable(final Connection connection, final AtomSQL sql, final SQLResult action) {
        this.connection = connection;
        this.sql = sql;
        this.action = action;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public AtomSQL getSQL() {
        return this.sql;
    }
    
    @Override
    public void run() {
        try {
            final PreparedStatement statement = this.connection.prepareStatement(this.sql.getSQL());
            try {
                this.sql.applyObjects(statement);
                final ResultSet result = statement.executeQuery();
                try {
                    this.action.process(result);
                    if (result != null) {
                        result.close();
                    }
                }
                catch (Throwable t) {
                    if (result != null) {
                        try {
                            result.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (Throwable t2) {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException ex) {
            throw new AtomSQLException("",ex);
        }
    }
}
