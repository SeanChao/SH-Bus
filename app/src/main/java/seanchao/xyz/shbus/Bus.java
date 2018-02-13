package seanchao.xyz.shbus;

/**
 * Created by SeanC on 2018/2/12.
 */

public class Bus {
    //TODO: 对busInfo进行格式化
    /**初步想法：新建一个Bus类，添加属性：name destination time(首末班车)
     * Attributes:
     * String name
     * String destination
     * String timeTable
     * boolean isRunning
     * String arrivalTime
     *
     * 对获取到的站点全部班车信息进行正则表达式匹配，
     * 随后根据相关的信息生成一个Bus对象
     * 实际查询信息时，使用name对Bus对象进行匹配
     * 返回对应的Bus对象并输出
     *
     * arrivalTime变量的类型后期可能需要根据实际情况进行调整
     */
    String name;
    String destination;
    String timeTable;
    boolean isRunning;
    String arrivalTime;

    public Bus () {

    }

    public Bus(String name, String destination, String timeTable, boolean isRunning, String arrivalTime) {
        super();
        this.name = name;
        this.destination = destination;
        this.timeTable = timeTable;
        this.isRunning = isRunning;
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        System.out.print("输出公交信息["+"线路名："+this.name+" 方向："+this.destination+" 时间表："+this.timeTable+" ");
        String runningState = "ERROR";
        if(this.isRunning){
            runningState = "在运";
            System.out.println("下一班："+this.arrivalTime);
        }else{
            runningState = "停运";
            System.out.println();
        }
        return super.toString();
    }
}
