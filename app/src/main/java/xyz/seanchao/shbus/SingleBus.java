package xyz.seanchao.shbus;

/**
 * Created by SeanC on 2018/2/16.
 */

public class SingleBus {
    String bus;
    String name;
    String id;
    String destination;
    String arrivalTime;
    String stop;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

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


    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public SingleBus(String stop, String name, String destination, String timeTable, String arrivalTime) {
        this.setName(name);
        this.setArrivalTime(arrivalTime);
        this.setDestination(destination);
        this.setStop(stop);
    }

    @Override
    public String toString() {
        return "stop:" + stop + ",name:" + name;
    }

    @Override
    public boolean equals(Object obj) {
        SingleBus single = (SingleBus) obj;
        boolean b = false;
        if (this.getName().equals(single.getName()) && this.getBus().equals(((SingleBus) obj).getName()) && this.getId().equals(((SingleBus) obj).getId())) {
            b = true;
        }
        return b;
    }
}
