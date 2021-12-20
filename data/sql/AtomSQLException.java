package com.sweatsunited.data.sql;

public class AtomSQLException extends RuntimeException
{
    private static final long serialVersionUID = -1746878370430655863L;
    
    public AtomSQLException() {
    }
    
    public AtomSQLException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    public AtomSQLException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public AtomSQLException(final String message) {
        super(message);
    }
    
    public AtomSQLException(final Throwable cause) {
        super(cause);
    }
}
