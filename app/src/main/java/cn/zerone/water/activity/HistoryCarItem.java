package cn.zerone.water.activity;

public class HistoryCarItem {
    private String Address;
    private String Path;
    private String AddTime;
    private String DataType;
    private String EngineeringId;
    private String StationName;
    private String CarInfoId;

    public HistoryCarItem(String address, String path, String addTime, String dataType, String engineeringId, String stationName, String carInfoId) {
        this.Address = address;
        this.Path = path;
        this.AddTime = addTime;
        this.DataType = dataType;
        this.CarInfoId = carInfoId;
        this.EngineeringId = engineeringId;
        this.StationName = stationName;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public void setpath(String path) { this.Path = path; }

    public void setAddTime(String addTime) { this.AddTime = addTime; }

    public void setDataType(String dataType) { this.DataType = dataType; }

    public void setCarInfoId(String carInfoId) { this.CarInfoId = carInfoId; }

    public void setEngineeringId(String engineeringId) { this.EngineeringId = engineeringId; }

    public void setStationName(String stationName) { this.StationName = stationName; }

    public String getStationName() { return StationName; }

    public String getEngineeringId() { return EngineeringId; }

    public String getCarInfoId() { return CarInfoId; }

    public String getDataType() { return DataType; }

    public String getAddTime() {
        return AddTime;
    }

    public String getAddress() {
        return Address;
    }

    public String getpath() {
        return Path;
    }
}
