package model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainPosition {
    public String TrainId;
    public int CarCount;
    public int DirectionNum;
    public int CircuitId;
    public String DestinationStationCode;
    public String LineCode;
    public int SecondsAtLocation;
    public String ServiceType;
}
