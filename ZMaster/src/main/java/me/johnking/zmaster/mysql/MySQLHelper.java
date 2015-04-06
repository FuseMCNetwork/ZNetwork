package me.johnking.zmaster.mysql;

import me.johnking.zmaster.ZMaster;
import me.johnking.zmaster.server.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Marco on 15.08.2014.
 */
public class MySQLHelper {

    public static void stopServer(Server server){
        try (Connection conn = ZMaster.getNetworkConnection().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM runtime_servers WHERE name = ?;");
            ps.setString(1, server.getServerId());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void startServer(Server server){
        try (Connection conn = ZMaster.getNetworkConnection().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO runtime_servers" +
                    "(name, gamemode, address, port, max_players, master, dynamic) VALUES" +
                    "(?, ?, ?, ?, ?, ?, ?);");
            ps.setString(1, server.getServerId());
            ps.setString(2, server.getType());
            ps.setString(3, server.getAddress());
            ps.setInt(4, server.getPort());
            ps.setInt(5, server.getMaxUsers());
            ps.setString(6, ZMaster.getInstance().getName());
            ps.setBoolean(7, server.isDynamic());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadedServer(Server server){
        try (Connection conn = ZMaster.getNetworkConnection().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE runtime_servers SET online = 1 WHERE name = ?;");
            ps.setString(1, server.getServerId());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
