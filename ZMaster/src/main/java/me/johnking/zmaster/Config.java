package me.johnking.zmaster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Marco on 15.08.2014.
 */
public class Config {

    private String parameters;
    private int minPort;
    private int maxPort;
    private int maxHeap;
    private int queueSize;

    public Config () {
        loadConfig();
    }

    private void loadConfig(){
        try (Connection connection = ZMaster.getNetworkConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT parameters, min_port, max_port, max_heap, queue_size FROM master_config WHERE name = ?;");
            ps.setString(1, ZMaster.getInstance().getName());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                parameters = rs.getString(1);
                minPort = rs.getInt(2);
                maxPort = rs.getInt(3);
                maxHeap = rs.getInt(4);
                queueSize = rs.getInt(5);
            }
            ps.close();
            rs.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public String getParameters(){
        return parameters;
    }

    public int getMinPort(){
        return minPort;
    }

    public int getMaxPort(){
        return maxPort;
    }

    public int getQueueSize(){
        return queueSize;
    }

    public int getMaxHeap(){
        return maxHeap;
    }
}
