package xyz.seanchao.shbus;

/**
 * Created by SeanC on 2018/2/13.
 */

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BusInfoProcess {
    static int postCounter = 0;

    public static void updateNetworkStat() {
        postCounter++;
        String log = "已发送" + postCounter + "次网络请求";
        Log.d("STAT", log);
    }

    public static void main(String[] args) throws IOException {
        String url = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq022";
        String url2 = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq626";
        String url3 = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq937";
        String urln1 = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq1";
        //Document doc = Jsoup.connect(url).get();
        //String title = doc.title();
        //Elements li = doc.select("li");
        //String busInfo = li.text()+" ";
        //System.out.println(busInfo);
        //listAllBus();

        //尝试使用遍历字符串的形式对得到的信息进行匹配
        //infoExtract(busInfoFormat(busInfo));
        //getBusByUrl(url);
        System.out.print("a");
        System.out.print(getBusStopName(urln1));
        System.out.print("a");
    }

    public static Bus[] getBusByUrl (String url){
        //获取网页信息
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            updateNetworkStat();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            //TODO
        }
        //提取<li>标签中的公交信息并格式化
        Elements li = doc.select("li");
        String busInfo = li.text()+" ";
        //System.out.println(busInfo);
        //尝试使用遍历字符串的形式对得到的信息进行匹配
        return infoExtract(busInfoFormat(busInfo));
    }

    public static void listAllBus() throws IOException {
        int counter = 0;
        String urlbase = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq";
        for (int i = 0 ; i < 2000 ; i ++) {
            String url = null;
            if ( i < 10) {
                url = urlbase + "00" + i;
            }else if( i < 100 ) {
                url = urlbase + "0" + i ;
            }else {
                url = urlbase + i ;
            }
            Document doc = Jsoup.connect(url).get();
            updateNetworkStat();
            //String title = doc.title();
            Elements li = doc.select("li");
            String busInfo = li.text();
            if (!busInfo.equals("")) {
                counter ++ ;
                //System.out.println("bsq"+i+" "+"有效"+counter+" "+li.text());
            }
        }
    }

    //生成{公交1,公交2...}的数组
    public static String[] busInfoFormat (String busInfo) {
        int spaceCounter = 0;
        String singleBusInfo = "";
        String busInfoArray [] = new String [32];
        //初始化，避免空指针
        for(int i = 0; i < busInfoArray.length ; i ++ ) {
            busInfoArray[i]="";
        }
        int busNum = 0;
        for (int i = 0; i < busInfo.length() ; i++ ) {
            String singleWord = String.valueOf(busInfo.charAt(i));
            if (singleWord.equals(" ") ) {
                spaceCounter ++;
            }
            singleBusInfo += singleWord;
            if (spaceCounter == 4 ) {
                busInfoArray[busNum] = singleBusInfo;
                busNum ++ ;
                spaceCounter = 0;
                singleBusInfo = "";
            }

        }
        //Debug
        for (int i = 0; i < busInfoArray.length; i++) {
            System.out.println("busInfoArray" + "[" + (i) + "]=" + busInfoArray[i]);
        }
        return busInfoArray;
    }

    public static Bus[] infoExtract( String [] busInfoArray) {
        int busNum = busInfoArray.length;
        Bus[] busArray = new Bus[32];

        for (int i = 0 ; i<busNum ; i++ ) {
            String name = "";
            String destination = "";
            String timeTable = "";
            boolean isRunning = false ;
            String arrivalTime = "" ;

            int spaceCounter = 0;//空格计数器
            int relative = 0;
            if (!busInfoArray[i].equals("") && busInfoArray[i].length() != 0) {
                for (int j = 0 ; j < busInfoArray[i].length() ; j++ ) {
                    //抽取单个bus的上述信息
                    String singleBusInfo = busInfoArray[i];
                    if (String.valueOf(singleBusInfo.charAt(j)).equals(" ")) {
                        spaceCounter ++;
                    }
                    if(spaceCounter == 0 ) {
                        name += String.valueOf(singleBusInfo.charAt(j));
                    }else if (spaceCounter == 1 ) {
                        relative++;
                        if (relative > 3) {
                            destination += String.valueOf(singleBusInfo.charAt(j)).trim();
                        }
                    }else if (spaceCounter == 2 ) {
                        timeTable += String.valueOf(singleBusInfo.charAt(j)).trim();
                    }else if (spaceCounter == 3 ) {
                        arrivalTime += String.valueOf(singleBusInfo.charAt(j)).trim();
                        if (arrivalTime.equals("暂无来车，请稍候查询！")){
                            isRunning = false;
                        }else {
                            String pattern = "[^\\d+:\\d+]";
                            arrivalTime = arrivalTime.replaceAll(pattern, "");
                            if (arrivalTime.equals("")) {
                                System.out.println("到达时间为:" + arrivalTime + arrivalTime.equals(""));
                                arrivalTime = "zZ-zZ";
                            }
                        }
                    }
                }
                //check
                /*System.out.println(name);
                System.out.println(destination);
                System.out.println(timeTable);
                System.out.println(arrivalTime);*/
            }

            //生成Bus对象
            busArray[i] = new Bus(name, destination, timeTable, arrivalTime);
            System.out.println("busArray[" + i + "].toString:" + busArray[i].toString());
        }
        return busArray;
    }

    public static String getBusStopName(String url) {
        //获取网页信息
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            updateNetworkStat();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //提取<li>标签中的公交信息并格式化
        Elements li = doc.select("#lbStationName");
        String busStopName = li.text();
        //TEST
        System.out.println(busStopName);
        return busStopName;
    }

    public static SingleBus getSingleBusByUrl(String url, String busName) {
        Bus[] buses = getBusByUrl(url);
        for (int i = 0; i < buses.length; i++) {
            Bus bus = buses[i];
            if (bus.getName().equals(busName)) {
                return new SingleBus(getBusStopName(url), bus.getName(), bus.getDestination(), bus.getTimeTable(), bus.getArrivalTime());
            }
        }
        return new SingleBus("", "", "", "", "");
    }

    public static String getAllBusInfo() throws IOException {
        String jsonData = "[";
        int counter = 0;
        String urlbase = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq";
        String url = "";
        for (int i = 0; i < 2000; i++) {
            String id = "";
            if (i < 10) {
                id = "00" + i;
            } else if (i < 100) {
                id = "0" + i;
            } else {
                id = "" + i;
            }
            url = urlbase + id;
            Document doc = Jsoup.connect(url).get();
            //String title = doc.title();
            Elements stopName = doc.select("#lbStationName");
            String busStopName = stopName.text();
            Elements stopAddress = doc.select("#lbStationAdress");
            String busStopAddress = stopAddress.text();
            if (busStopName.equals("")) {
                continue;
            }
            System.out.println(busStopName + " " + busStopAddress);
            jsonData += "{\"id\":\"" + id + "\",\"name\":\"" + busStopName + "\",\"address\":\"" + busStopAddress + "\"},";
            if (i > 10) {
                break;
            }
        }
        String jsonData2 = "";
        for (int i = 0; i < jsonData.length(); i++) {
            if (i == jsonData.length() - 1) {
                jsonData2 += "]";
                break;
            }
            jsonData2 += jsonData.charAt(i);
        }

        return jsonData2;
    }

    public static void listAllBustoGetBusArray() throws IOException {
        BusWithStopArray[] buses = new BusWithStopArray[4096];
        for (int f = 0; f < buses.length; f++) {
            buses[f] = new BusWithStopArray("");
        }
        int counter = 0;
        String urlbase = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq";
        String url = "";
        String id = "";
        String stop = "";
        for (int i = 0; i < 1600; i++) {
            if (i < 10) {
                id = "00" + i;
            } else if (i < 100) {
                id = "0" + i;
            } else {
                id = "" + i;
            }
            System.out.println("Generate id:" + id);
            url = urlbase + id;
            Document doc = Jsoup.connect(url).get();
            Elements busInfoElement = doc.select("li");
            stop = doc.select("#lbStationName").text();
            System.out.println(stop);
            String busInfo = busInfoElement.text();
            if (busInfo.equals("")) {
                continue;
            }
            Bus[] originBuses = infoExtract(busInfoFormat(busInfo));
            System.out.println("生成原始Bus数组 长度：" + originBuses.length);
            for (int ii = 0; ii < originBuses.length; ii++) {
                Bus buss = originBuses[ii];
                if (buss.name.equals("") || buss.name == null) {
                    continue;
                }
                System.out.println("生成原始Bus[" + ii + "] name:" + buss.name);
                BusWithStopArray bus; //= new BusWithStopArray(buss.name);
                bus = BusWithStopArray.findBusByName(buss.name, buses);
                System.out.println("有相同对象" + !(bus == null));
                if (bus == null) {
                    buses[counter] = new BusWithStopArray(buss.name);
                    buses[counter].add(stop, id);
                    System.out.println("不存在同名对象 add方法: buses[" + counter + "] name:" + buses[counter].getBus() + " stop:" + stop + " id:" + id);
                } else {
                    bus.add(stop, id);
                    System.out.println("存在 add方法: buses[" + counter + "] name:" + buses[counter].getBus() + " stop:" + stop + " id:" + id);
                }
                counter++;
                System.out.println("检测存在与否循环每次结束counter:" + counter);
            }
        }

        //此时输出的JSON中含有大量null，进一步处理：
        ArrayList<OptimizedBus> busList = new ArrayList<OptimizedBus>();
        for (int i = 0; i < buses.length; i++) {//length:4096
            BusWithStopArray bus = buses[i];
            if (bus.getBus().equals("")) {
                continue;
            }
            for (int j = 0; j < 64; j++) {
                //取得有效的长度 去除Null和""
                if (bus.getId()[j] != null && bus.getId()[j] != "") {
                    bus.usefulMaxIndex = j;
                }
            }
            int length = bus.usefulMaxIndex + 1;
            System.out.println("length:" + length);
            OptimizedBus oBus = new OptimizedBus();
            oBus.stop = new String[length];
            oBus.id = new String[length];
            for (int j = 0; j < length; j++) {
                oBus.stop[j] = bus.stop[j];
                oBus.id[j] = bus.id[j];
                oBus.bus = bus.bus;
            }
            busList.add(oBus);
        }
        int length = busList.size();
        OptimizedBus[] oBuses = new OptimizedBus[length];
        for (int i = 0; i < length; i++) {
            oBuses[i] = busList.get(i);
        }
        String jsonString = JSON.toJSONString(oBuses);
        System.out.println(jsonString);
    }


}

