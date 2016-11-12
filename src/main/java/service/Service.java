package service;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import model.Positions;
import model.TrainPosition;
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

/**
 * Created by henneberger on 11/12/16.
 */
public class Service {
    InfluxDB influxDB = null;

    public Service(InfluxDB influxDB) {
        this.influxDB = influxDB;
        influxDB.createDatabase("db2");
    }

    public Map<String, TrainMetaData> trainDiff = new HashMap<>();
    public void diffStations(Positions p) {
        List<TrainPosition> positionsList = p.TrainPositions.stream() //clean up
                .filter(s -> s.LineCode != null)
                .filter(s -> s.DestinationStationCode != null)
                .filter(s -> s.TrainId != null)
                .collect(Collectors.toList());

        if (trainDiff.size() == 0) {//first run, write all
            setNewTrainDiff(positionsList);

            return;
        }

        for (TrainPosition pos : positionsList) {
            if (trainDiff.get(pos.TrainId) != null && !trainDiff.get(pos.TrainId).DestinationStationCode.equals(pos.DestinationStationCode)){
                writeToInflux(pos.DestinationStationCode, pos.DirectionNum, System.currentTimeMillis() - trainDiff.get(pos.TrainId).time);
            }
        }

        setNewTrainDiff(positionsList);
    }

    public void setNewTrainDiff(List<TrainPosition> positions) {
        trainDiff = new HashMap<>();
        for (TrainPosition pos : positions) {
            trainDiff.put(pos.TrainId, new TrainMetaData(pos.DestinationStationCode));
        }
    }

    class TrainMetaData {
        String DestinationStationCode;
        long time = System.currentTimeMillis();

        public TrainMetaData(String destinationStationCode) {
            this.DestinationStationCode = destinationStationCode;
        }
    }

    public Positions getPositions(boolean live)  {
        String s = live ? makeCall() : "{\"TrainPositions\":[{\"TrainId\":\"033\",\"CarCount\":0,\"DirectionNum\":1,\"CircuitId\":2118,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":15,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"037\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":3150,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":2694,\"ServiceType\":\"NoPassengers\"},{\"TrainId\":\"068\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":1612,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":826,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"145\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":723,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"163\",\"CarCount\":0,\"DirectionNum\":1,\"CircuitId\":519,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"186\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":523,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"224\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":510,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"227\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":514,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"235\",\"CarCount\":0,\"DirectionNum\":1,\"CircuitId\":513,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"303\",\"CarCount\":0,\"DirectionNum\":1,\"CircuitId\":719,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"372\",\"CarCount\":0,\"DirectionNum\":1,\"CircuitId\":755,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":8100,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"375\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":2494,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"410\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":2580,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":6432,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"413\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":506,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"425\",\"CarCount\":0,\"DirectionNum\":1,\"CircuitId\":522,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"450\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":504,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"451\",\"CarCount\":0,\"DirectionNum\":1,\"CircuitId\":1386,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":13172,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"464\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":2678,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":11727,\"ServiceType\":\"NoPassengers\"},{\"TrainId\":\"480\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":756,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":21031,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"481\",\"CarCount\":0,\"DirectionNum\":2,\"CircuitId\":711,\"DestinationStationCode\":null,\"LineCode\":null,\"SecondsAtLocation\":30149,\"ServiceType\":\"Unknown\"},{\"TrainId\":\"417\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":976,\"DestinationStationCode\":\"G05\",\"LineCode\":\"BL\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"419\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":1117,\"DestinationStationCode\":\"G05\",\"LineCode\":\"BL\",\"SecondsAtLocation\":229,\"ServiceType\":\"Normal\"},{\"TrainId\":\"429\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1330,\"DestinationStationCode\":\"G05\",\"LineCode\":\"BL\",\"SecondsAtLocation\":58,\"ServiceType\":\"Normal\"},{\"TrainId\":\"490\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":2435,\"DestinationStationCode\":\"G05\",\"LineCode\":\"BL\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"294\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1615,\"DestinationStationCode\":\"J03\",\"LineCode\":\"BL\",\"SecondsAtLocation\":170,\"ServiceType\":\"Normal\"},{\"TrainId\":\"352\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1567,\"DestinationStationCode\":\"J03\",\"LineCode\":\"BL\",\"SecondsAtLocation\":575,\"ServiceType\":\"Normal\"},{\"TrainId\":\"483\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1162,\"DestinationStationCode\":\"J03\",\"LineCode\":\"BL\",\"SecondsAtLocation\":21,\"ServiceType\":\"Normal\"},{\"TrainId\":\"494\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":2574,\"DestinationStationCode\":\"J03\",\"LineCode\":\"BL\",\"SecondsAtLocation\":37,\"ServiceType\":\"Normal\"},{\"TrainId\":\"496\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1285,\"DestinationStationCode\":\"J03\",\"LineCode\":\"BL\",\"SecondsAtLocation\":53,\"ServiceType\":\"Normal\"},{\"TrainId\":\"013\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1824,\"DestinationStationCode\":\"E10\",\"LineCode\":\"GR\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"022\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1895,\"DestinationStationCode\":\"E10\",\"LineCode\":\"GR\",\"SecondsAtLocation\":1099,\"ServiceType\":\"Normal\"},{\"TrainId\":\"038\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":2208,\"DestinationStationCode\":\"E10\",\"LineCode\":\"GR\",\"SecondsAtLocation\":31,\"ServiceType\":\"Normal\"},{\"TrainId\":\"443\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":2134,\"DestinationStationCode\":\"E10\",\"LineCode\":\"GR\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"509\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1746,\"DestinationStationCode\":\"E10\",\"LineCode\":\"GR\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"016\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":2342,\"DestinationStationCode\":\"F11\",\"LineCode\":\"GR\",\"SecondsAtLocation\":85,\"ServiceType\":\"Normal\"},{\"TrainId\":\"067\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1967,\"DestinationStationCode\":\"F11\",\"LineCode\":\"GR\",\"SecondsAtLocation\":10,\"ServiceType\":\"Normal\"},{\"TrainId\":\"428\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":2009,\"DestinationStationCode\":\"F11\",\"LineCode\":\"GR\",\"SecondsAtLocation\":42,\"ServiceType\":\"Normal\"},{\"TrainId\":\"449\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":2055,\"DestinationStationCode\":\"F11\",\"LineCode\":\"GR\",\"SecondsAtLocation\":79,\"ServiceType\":\"Normal\"},{\"TrainId\":\"454\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":2299,\"DestinationStationCode\":\"F11\",\"LineCode\":\"GR\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"432\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":2855,\"DestinationStationCode\":\"D13\",\"LineCode\":\"OR\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"439\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":1500,\"DestinationStationCode\":\"D13\",\"LineCode\":\"OR\",\"SecondsAtLocation\":37,\"ServiceType\":\"Normal\"},{\"TrainId\":\"486\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1105,\"DestinationStationCode\":\"D13\",\"LineCode\":\"OR\",\"SecondsAtLocation\":85,\"ServiceType\":\"Normal\"},{\"TrainId\":\"497\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1389,\"DestinationStationCode\":\"D13\",\"LineCode\":\"OR\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"414\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":1624,\"DestinationStationCode\":\"K08\",\"LineCode\":\"OR\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"416\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1559,\"DestinationStationCode\":\"K08\",\"LineCode\":\"OR\",\"SecondsAtLocation\":629,\"ServiceType\":\"Normal\"},{\"TrainId\":\"470\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":3063,\"DestinationStationCode\":\"K08\",\"LineCode\":\"OR\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"508\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":2939,\"DestinationStationCode\":\"K08\",\"LineCode\":\"OR\",\"SecondsAtLocation\":111,\"ServiceType\":\"Normal\"},{\"TrainId\":\"018\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":237,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"027\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":306,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"055\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":6,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":293,\"ServiceType\":\"Normal\"},{\"TrainId\":\"069\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":362,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"404\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":263,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"411\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":681,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"457\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":227,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"475\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":336,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":21,\"ServiceType\":\"Normal\"},{\"TrainId\":\"489\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":389,\"DestinationStationCode\":\"A15\",\"LineCode\":\"RD\",\"SecondsAtLocation\":26,\"ServiceType\":\"Normal\"},{\"TrainId\":\"437\",\"CarCount\":8,\"DirectionNum\":2,\"CircuitId\":808,\"DestinationStationCode\":\"B06\",\"LineCode\":\"RD\",\"SecondsAtLocation\":26,\"ServiceType\":\"Normal\"},{\"TrainId\":\"488\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":869,\"DestinationStationCode\":\"B06\",\"LineCode\":\"RD\",\"SecondsAtLocation\":2428,\"ServiceType\":\"Normal\"},{\"TrainId\":\"412\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":566,\"DestinationStationCode\":\"B11\",\"LineCode\":\"RD\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"448\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":652,\"DestinationStationCode\":\"B11\",\"LineCode\":\"RD\",\"SecondsAtLocation\":90,\"ServiceType\":\"Normal\"},{\"TrainId\":\"006\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":109,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"045\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":62,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":15,\"ServiceType\":\"Normal\"},{\"TrainId\":\"048\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":699,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"052\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":24,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"422\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":467,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":47,\"ServiceType\":\"Normal\"},{\"TrainId\":\"430\",\"CarCount\":8,\"DirectionNum\":1,\"CircuitId\":133,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":53,\"ServiceType\":\"Normal\"},{\"TrainId\":\"442\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":90,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"505\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":164,\"DestinationStationCode\":\"B35\",\"LineCode\":\"RD\",\"SecondsAtLocation\":42,\"ServiceType\":\"Normal\"},{\"TrainId\":\"007\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":3156,\"DestinationStationCode\":\"K04\",\"LineCode\":\"SV\",\"SecondsAtLocation\":837,\"ServiceType\":\"Normal\"},{\"TrainId\":\"044\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":3251,\"DestinationStationCode\":\"K04\",\"LineCode\":\"SV\",\"SecondsAtLocation\":10,\"ServiceType\":\"Normal\"},{\"TrainId\":\"063\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":3312,\"DestinationStationCode\":\"N06\",\"LineCode\":\"SV\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"075\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":3417,\"DestinationStationCode\":\"N06\",\"LineCode\":\"SV\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"024\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1136,\"DestinationStationCode\":\"C15\",\"LineCode\":\"YL\",\"SecondsAtLocation\":1726,\"ServiceType\":\"Normal\"},{\"TrainId\":\"028\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":3135,\"DestinationStationCode\":\"C15\",\"LineCode\":\"YL\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"447\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1188,\"DestinationStationCode\":\"C15\",\"LineCode\":\"YL\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"462\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":945,\"DestinationStationCode\":\"C15\",\"LineCode\":\"YL\",\"SecondsAtLocation\":0,\"ServiceType\":\"Normal\"},{\"TrainId\":\"495\",\"CarCount\":6,\"DirectionNum\":2,\"CircuitId\":1947,\"DestinationStationCode\":\"C15\",\"LineCode\":\"YL\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"001\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1052,\"DestinationStationCode\":\"E06\",\"LineCode\":\"YL\",\"SecondsAtLocation\":26,\"ServiceType\":\"Normal\"},{\"TrainId\":\"424\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":1782,\"DestinationStationCode\":\"E06\",\"LineCode\":\"YL\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"},{\"TrainId\":\"444\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":2241,\"DestinationStationCode\":\"E06\",\"LineCode\":\"YL\",\"SecondsAtLocation\":31,\"ServiceType\":\"Normal\"},{\"TrainId\":\"458\",\"CarCount\":6,\"DirectionNum\":1,\"CircuitId\":988,\"DestinationStationCode\":\"E06\",\"LineCode\":\"YL\",\"SecondsAtLocation\":5,\"ServiceType\":\"Normal\"}]}\n";

        try {
            return new ObjectMapper().readValue(s, Positions.class);
        } catch (IOException e) {
            e.printStackTrace(); //blah
        }
        return null;
    }

    public String makeCall() {

        System.out.println("Making http call");
        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://api.wmata.com/TrainPositions/TrainPositions?contentType=json");


            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("api_key", "3a3ad8ee4030458387a9239bf0386f30");

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String s = EntityUtils.toString(entity);
                System.out.println(s);
                System.out.println("Http call returned.");
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeToInflux(String station, int directionNum, long time) {
        System.out.println("Writing to influx: " + station + " direction " + directionNum + " took " + time);

        BatchPoints batchPoints = BatchPoints
                .database("db1")
                .tag("async", "true")
                .retentionPolicy("autogen")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point2 = Point.measurement("station")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("station", station)
                .addField("direction", directionNum)
                .addField("t", time)
                .build();
        batchPoints.point(point2);
        influxDB.write(batchPoints);
    }
}
