import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import model.Positions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import service.Service;

public class App {
    public static boolean live = true;

    public static void main(String[] args) {

        final Service s = new Service(connectToInflux());

        Runnable runnabledelayedTask = () -> {
            System.out.println("Getting locations");
            Positions p = s.getPositions(live);

            if(p == null) {
                System.out.println("Something weird happened");
                return;
            }

            s.diffStations(p);

            System.out.println("Found " + p.TrainPositions.size() + " trains.");

        };

        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

        scheduledPool.scheduleAtFixedRate(runnabledelayedTask, 1, 10, TimeUnit.SECONDS);

    }
    public static InfluxDB connectToInflux() {
        System.out.println("Connecting to influx");
        return InfluxDBFactory.connect("http://192.168.99.100:8086", "root", "somepassword");
    }

}
