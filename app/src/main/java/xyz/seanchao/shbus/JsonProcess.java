package xyz.seanchao.shbus;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by SeanC on 2018/2/15.
 * #实现Nav一般车站的数据解析
 * 基本格式[{"name":"xx站","id";"123"},{...}]
 */

/**
 * 序列化一个对象成JSON字符串
 User user = new User();
 user.setName("校长");
 user.setAge(3);
 user.setSalary(new BigDecimal("123456789.0123"));
 String jsonString = JSON.toJSONString(user);
 System.out.println(jsonString);
 // 输出 {"age":3,"name":"校长","old":false,"salary":123456789.0123}

 反序列化一个JSON字符串成Java对象
 String jsonString = "{\"age\":3,\"birthdate\":1496738822842,\"name\":\"校长\",\"old\":true,\"salary\":123456789.0123}";
 User u = JSON.parseObject(jsonString ,User.class);
 System.out.println(u.getName());
 // 输出 校长

 String jsonStringArray = "[{\"age\":3,\"birthdate\":1496738822842,\"name\":\"校长\",\"old\":true,\"salary\":123456789.0123}]";
 List<User> userList = JSON.parseArray(jsonStringArray, User.class);
 System.out.println(userList.size());
 // 输出 1
 */

public class JsonProcess {

    //读取JSON
    public static BusStop[] getJson(String jsonString) {
        BusStop[] busStops = new BusStop[32];
        //init
        for (int i = 0; i < busStops.length; i++) {
            busStops[i] = new BusStop("", "");
        }
        List<BusStop> busStopList = JSON.parseArray(jsonString, BusStop.class);
        for (int i = 0; i < busStopList.size(); i++) {
            busStops[i] = busStopList.get(i);
            if (busStops[i].equals(null)) {
                busStops[i] = new BusStop("", "");
            }
        }
        return busStops;
    }

    public static String toJson(BusStop busStop, String currentData) {
        BusStop[] busStops = getJson(currentData);
        //得到有效的长度
        int num = 0;
        for (int i = 0; i < busStops.length; i++) {
            System.out.println(busStops[i].getName());
            if (!busStops[i].getName().equals("")) {
                num++;
            }
        }
        busStops[num] = busStop;
        String jsonString = JSON.toJSONString(busStops);
        System.out.println("得到的JsonString" + jsonString);
        return jsonString;
    }
}
