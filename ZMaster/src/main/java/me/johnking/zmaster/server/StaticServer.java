package me.johnking.zmaster.server;

import me.johnking.zmaster.ZMaster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Marco on 15.08.2014.
 */
public class StaticServer extends Server {

    private StaticServer(String gamemode, String serverId, String address, int port, int heap, int maxUsers){
        super(gamemode, serverId, address, port, heap, maxUsers);
    }

    public static void loadServers(){
        try (Connection connection = ZMaster.getNetworkConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT name, address, port, gamemode, max_players, heap FROM static_servers WHERE master=?");
            ps.setString(1, ZMaster.getInstance().getName());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String serverId = rs.getString(1);
                String address = rs.getString(2);
                int port = rs.getInt(3);
                String gamemode = rs.getString(4);
                int maxUsers = rs.getInt(5);
                int heap = rs.getInt(6);
                new StaticServer(gamemode, serverId, address, port, heap, maxUsers);
            }
            ps.close();
            rs.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
