package me.johnking.zmaster;

/**
 * Created by Marco on 22.08.2014.
 */
public class ShutdownThread extends Thread{

    public void run(){
        try {
            ZMaster.getInstance().stop();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                ZMaster.getInstance().getReader().getTerminal().restore();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
