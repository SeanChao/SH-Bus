package seanchao.xyz.shbus;

/**
 * Created by SeanC on 2018/2/13.
 */

import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BusInfoProcess {

    public static void main(String[] args) throws IOException {
        String url = "http://webapp.shbustong.com:56008/MobileWeb/ForecastChange.aspx?stopid=bsq626";
        Document doc = Jsoup.connect(url).get();
        //String title = doc.title();
        Elements li = doc.select("li");
        String busInfo = li.text()+" ";
        System.out.println(busInfo);
        //listAllBus();

        //尝试使用遍历字符串的形式对得到的信息进行匹配
        infoExtract(busInfoFormat(busInfo));

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
            //String title = doc.title();
            Elements li = doc.select("li");
            String busInfo = li.text();
            if (!busInfo.equals("")) {
                counter ++ ;
                System.out.println("bsq"+i+" "+"有效"+counter+" "+li.text());
            }
        }
    }

    public static String[] busInfoFormat (String busInfo) {
        int spaceCounter = 0;
        String singleBusInfo = "";
        String busInfoArray [] = new String [32];
        //初始化，避免空指针
        for(int i = 0 ; i < busInfoArray.length ; i ++ ) {
            busInfoArray[i]="";
        }
        int busNum = 0;
        for (int i = 0 ; i < busInfo.length() ; i++ ) {
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
                System.out.println(busInfoArray[busNum-1]);
            }
        }
        return busInfoArray;
    }

    public static void infoExtract( String [] busInfoArray) {
        int busNum = busInfoArray.length;
        Bus[] busArray = new Bus[32];

        for (int i = 0 ; i<busNum ; i++ ) {
            String name = "";
            String destination = "";
            String timeTable = "";
            boolean isRunning = false ;
            String arrivalTime = "" ;

            int spaceCounter = 0;//空格计数器
            if ( !busInfoArray[i].equals(null)&& busInfoArray[i].length() != 0) {
                for (int j = 0 ; j < busInfoArray[i].length() ; j++ ) {
                    //TODO 抽取单个bus的上述信息
                    String singleBusInfo = busInfoArray[i];
                    if (String.valueOf(singleBusInfo.charAt(j)).equals(" ")) {
                        spaceCounter ++;
                    }
                    if(spaceCounter == 0 ) {
                        name += String.valueOf(singleBusInfo.charAt(j));
                    }else if (spaceCounter == 1 ) {
                        destination += String.valueOf(singleBusInfo.charAt(j)).trim();
                    }else if (spaceCounter == 2 ) {
                        timeTable += String.valueOf(singleBusInfo.charAt(j)).trim();
                    }else if (spaceCounter == 3 ) {
                        arrivalTime += String.valueOf(singleBusInfo.charAt(j)).trim();
                        if (arrivalTime.equals("暂无来车，请稍候查询！")){
                            isRunning = false;
                        }else {
                            String pattern = "[^\\d+\\:\\d+]";
                            arrivalTime = arrivalTime.replaceAll(pattern, "");
                        }
                    }
                }
                //check
                System.out.println(name);
                System.out.println(destination);
                System.out.println(timeTable);
                System.out.println(arrivalTime);
            }

            //深生成Bus对象

            Bus bus = new Bus(name,destination,timeTable,isRunning,arrivalTime);
            busArray[i] = bus ;
        }
    }
}
