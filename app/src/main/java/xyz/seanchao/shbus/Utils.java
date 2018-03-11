package xyz.seanchao.shbus;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.valueOf;

/**
 * Created by SeanC on 2018/2/17.
 */

public class Utils {

    public static String getDeltaTime(String targetTime) {
        if (targetTime.equals("zZ-zZ")) {
            return "暂无来车";
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");

            String nowStr = df.format(new Date());
            Date now = df.parse(nowStr);
            Date date = df.parse(targetTime);

            long l = -(now.getTime() - date.getTime());

            long day = l / (24 * 60 * 60 * 1000);

            long hour = (l / (60 * 60 * 1000) - day * 24);

            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);

            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

            System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");

            String hourStr = String.valueOf(hour);
            String minStr = String.valueOf(min);

            String deltaTime = "出错了…";
            if (min <= 1) {
                int i = (int) (Math.random() * 4);//1st switch case max +1
                int j = (int) (Math.random() * 6);//2nd switch case max +1 +if ->m
                switch (i) {
                    case 0:
                        deltaTime = "就要来咯，";
                        break;
                    case 1:
                        deltaTime = "就要来咯，";
                        break;
                    case 2:
                        deltaTime = "快到了，";
                        break;
                    case 3:
                        deltaTime = "马上就要到站了，";
                }
                switch (j) {
                    case 0:
                        deltaTime += "准备上车吧~";
                        break;
                    case 1:
                        deltaTime += "提前准备好公交卡或零钱吧";
                        break;
                    case 2:
                        deltaTime += "请文明排队，有序候车";
                        break;
                    case 3:
                        deltaTime += "祝路上不堵";
                        break;
                    case 4:
                        deltaTime += "上车后请给需要帮助的乘客让个座";
                        break;
                    case 5:
                        deltaTime += "上车后请配合往里走";
                        break;
                }
            } else if (hour == 0) {
                int i = (int) (Math.random() * 3);
                switch (i) {
                    case 0:
                        deltaTime = "还有大概" + minStr + "分钟到";
                        break;
                    case 1:
                        deltaTime = "还有大约" + minStr + "分钟到";
                        break;
                    case 2:
                        deltaTime = "理论上，" + minStr + "分钟后到";
                        break;
                }
            } else if (hour > 0) {
                deltaTime = "还有" + hourStr + "小时" + minStr + "分钟";
            }
            return deltaTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "出错了……";
    }
}
