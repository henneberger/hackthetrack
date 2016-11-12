import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import model.Positions;
import service.Service;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

public class App {
    public static boolean live = false;

    public static void main(String[] args) {

        final Service s = new Service();

        s.connectToInflux();

        Runnable runnabledelayedTask = new Runnable() {
            public void run() {
                System.out.println("Getting locations");
                Positions p = s.getPositions(live);

                if(p == null) {
                    System.out.println("Something weird happened");
                    return;
                }

                s.diffStations(p);
                System.out.println("Found " + p.TrainPositions.size() + " trains.");

                System.out.println();

            }
        };

        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

        scheduledPool.scheduleAtFixedRate(runnabledelayedTask, 1, 10, TimeUnit.SECONDS);

    }
}
