package backend;

/**
 * Created by Darshan on 12/7/2014.
 */
public class Timer extends Thread {
    @Override
    public void run() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
