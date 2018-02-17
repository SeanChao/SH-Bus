package xyz.seanchao.shbus;

/**
 * Created by SeanC on 2018/2/13.
 */

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder>{

    private static final String TAG = "BusAdapter";

    private Context mContext;

    private List<Bus> mBusList;

    /**定义一个内部类ViewHolder，继承自...，然后其构造参数中传入一个View参数，通常是RecyclerView子项的最外层布局
     * 然后就可以通过findViewById()的方法获取到布局中的ImageView&TextView的实例了
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        TextView busName;
        TextView busDestinationName;
        TextView busArrivalTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            busName = (TextView) view.findViewById(R.id.bus_line_name);
            busDestinationName = (TextView) view.findViewById(R.id.bus_destination_name);
            busArrivalTime = (TextView) view.findViewById(R.id.bus_arrival_time);
        }
    }

    //用于传入要展示的数据源，并赋值给全局变量mBusList，后续的操作在这个数据源的基础上进行
    public BusAdapter(List<Bus> busList) {
        mBusList = busList;
    }

    //重写以下3个方法

    /**
    *创建ViewHolder实例。加载bus_item布局，随后创建一个ViewHolder实例，并把加载出来的布局传入到构造函数当中
    *最后将ViewHolder实例返回
    */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.bus_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Bus bus = mBusList.get(position);
                /*Intent intent = new Intent(mContext, BusActivity.class);
                intent.putExtra(BusActivity.BUS_NAME, bus.getName());
                intent.putExtra(BusActivity.BUS_IMAGE_ID, bus.getImageId());
                mContext.startActivity(intent);*/
                String deltaTime = Utils.getDeltaTime(bus.getArrivalTime());
                if (bus.getArrivalTime().equals("zZ-zZ")) {
                    deltaTime = "暂无来车";
                }
                Snackbar.make(v, deltaTime, Snackbar.LENGTH_LONG).show();
            }
        });
        holder.cardView.setLongClickable(true);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        return holder;
    }

    //用于对ViewHolder子项的数据进行赋值，在每个子项被滚动到屏幕内时被执行，通过position参数得到当前项的Bus实例
    //然后将数据设置到ViewHolder的ImageView和TextView中
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bus bus = mBusList.get(position);
        holder.busName.setText(bus.getName());
        holder.busDestinationName.setText(bus.getDestination());
        holder.busArrivalTime.setText(bus.getArrivalTime());
        //Glide.with(mContext).load(bus.getImageId()).into(holder.busImage);
    }

    //告诉RecyclerView共有多少子项，返回数据源长度
    @Override
    public int getItemCount() {
        return mBusList.size();
    }

}
