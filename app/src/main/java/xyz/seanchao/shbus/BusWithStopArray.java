package xyz.seanchao.shbus;

/**
 * Created by SeanC on 2018/2/17.
 */

public class BusWithStopArray {
    String bus;
    //原始数据;优化数据
    String[] stop = new String[64];
    String[] id = new String[64];
    int index = 0;
    int usefulMaxIndex = 0;

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String[] getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop[index] = stop;
    }

    public String[] getId() {
        return id;
    }

    public void setId(String id) {
        System.out.println("setId()is called: index:" + index + " id:" + id);
        this.id[index] = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    BusWithStopArray() {
        index = 0;
    }

    BusWithStopArray(String bus) {
        this.setBus(bus);
        index = 0;
    }

    public void add(String stop, String id) {
        this.setId(id);
        this.setStop(stop);
        index++;
    }

    public static BusWithStopArray findBusByName(String name, BusWithStopArray[] buses) {
        for (int i = 0; i < buses.length; i++) {
            if (buses[i].getBus().equals(name)) {
                return buses[i];
            }
        }
        return null;
    }
}

class OptimizedBus {
    String bus;
    String[] stop;
    String[] id;

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String[] getStop() {
        return stop;
    }

    public void setStop(String[] stop) {
        this.stop = stop;
    }

    public String[] getId() {
        return id;
    }

    public void setId(String[] id) {
        this.id = id;
    }

    OptimizedBus() {

    }

    OptimizedBus(String bus, String[] stop, String[] id) {

    }

}

