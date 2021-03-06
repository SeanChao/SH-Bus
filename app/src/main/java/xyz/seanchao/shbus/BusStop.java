package xyz.seanchao.shbus;

/**
 * Created by SeanC on 2018/2/15.
 */

public class BusStop {

    String name;
    String id;
    String address;

    public String getDistrictAlias() {
        return districtAlias;
    }

    public void setDistrictAlias(String districtAlias) {
        this.districtAlias = districtAlias;
    }

    private String districtAlias;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    BusStop() {

    }

    BusStop(String name, String id) {
        this.setName(name);
        this.setId(id);
    }

    BusStop(String id, String name, String address) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
    }

    BusStop(String id, String name, String address, String districtAlias) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
        this.setDistrictAlias(districtAlias);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
