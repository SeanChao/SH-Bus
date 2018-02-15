package xyz.seanchao.shbus;

/**
 * Created by SeanC on 2018/2/15.
 */

public class BusStop {

    String name;
    String id;

    BusStop() {

    }

    BusStop(String name, String id) {
        this.setName(name);
        this.setId(id);
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
