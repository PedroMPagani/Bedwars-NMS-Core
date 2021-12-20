package com.sweatsunited.data.sql;

import org.bukkit.command.*;
import org.bukkit.plugin.java.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import java.util.*;
import java.sql.*;

public class MySQL implements AtomSQLConnection
{
    public static final ConsoleCommandSender console;
    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private final ArrayList<String> tablesNames;
    private final int port;
    private Connection connection;
    private JavaPlugin plugin;

    public MySQL(final JavaPlugin plugin, final String host, final String user, final String password, final String database, final int port) {
        this.tablesNames = new ArrayList<String>();
        this.plugin = plugin;
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    public void runUpdate(final AtomSQLUpdateRunnable sql, final boolean asynchronously) {
        if (asynchronously) {
            new BukkitRunnable() {
                public void run() {
                    sql.run();
                }
            }.runTaskAsynchronously((Plugin)JavaPlugin.getPlugin((Class)this.plugin.getClass()));
        }
        else {
            sql.run();
        }
    }

    public void runSQL(final AtomSQLRunnable sql, final boolean asynchronously) {
        if (asynchronously) {
            new BukkitRunnable() {
                public void run() {
                    sql.run();
                }
            }.runTaskAsynchronously((Plugin)JavaPlugin.getPlugin((Class)this.plugin.getClass()));
        }
        else {
            sql.run();
        }
    }

    public void addTableStatement(final String tableName) {
        this.tablesNames.add(tableName);
    }

    @Override
    public void openConnection() {
        if (this.connection != null) {
            throw new AtomSQLException("[AtlasMySQL] N\u00e3o foi poss\u00edvel inicializar o MySQL pois esta conex\u00e3o atual j\u00e1 est\u00e1 aberta.");
        }
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.user, this.password);
            Bukkit.getConsoleSender().sendMessage("§a[AtlasMySQL] A conex\u00e3o com o MySQL foi efetuada com sucesso, criando tabelas...");
            this.createTable();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        if (this.connection == null) {
            throw new AtomSQLException("[AtlasMySQL] A conex\u00e3o remota com o servidor MySQL j\u00e1 foi encerrada.");
        }
        try {
            this.connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        if (this.connection == null) {
            throw new AtomSQLException("[AtlasMySQL] A conex\u00e3o com o servidor MySQL ainda n\u00e3o foi aberta por este motivo a tabela n\u00e3o foi criada.");
        }
        if (this.tablesNames.size() > 0) {
            for (final String tableStatement : this.tablesNames) {
                try (final Statement stmt = this.connection.createStatement()) {
                    stmt.executeUpdate(tableStatement);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        throw new AtomSQLException("[AtlasMySQL] Nenhuma tabela foi adicionada para ser carregada.");
    }

    static {
        console = Bukkit.getConsoleSender();
    }
}