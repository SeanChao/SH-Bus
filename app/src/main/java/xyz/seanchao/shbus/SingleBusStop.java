package xyz.seanchao.shbus;

/**
 * Created by SeanC on 2018/2/16.
 */

public class SingleBusStop {
    String bus;
    String name;
    String id;

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
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

    public SingleBusStop(String bus, String name, String id) {
        this.bus = bus;
        this.name = name;
        this.id = id;
    }

    public SingleBusStop() {

    }

    public boolean equal(SingleBusStop single) {
        boolean b = false;
        if (this.getName().equals(single.getName()) && this.getBus().equals(single.getBus()) && this.getId().equals(single.getId())) {
            b = true;
        }
        return b;
    }
}
