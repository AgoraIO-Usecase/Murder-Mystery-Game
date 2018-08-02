package io.agora.murder.mystery;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import io.agora.murder.point.User;

/**
 * 用户列表适配器
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {
    private Context context;

    private List<User> mUserList = new ArrayList<>();
    private Animation mAnimation;

    public RecycleViewAdapter(Context context, List<User> mUserList) {
        this.context = context;
        this.mUserList = mUserList;
        this.mAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_anim_light);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_user, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = mUserList.get(position);
        if (user != null) {
            holder.imgHeader.setBackgroundResource(user.getImageSource());
            holder.imgMute.setVisibility(user.isAudioMute() ? View.VISIBLE : View.GONE);
            holder.textViewName.setText(user.getName());
            if (user.isSpeaking()) {
                holder.imgHeaderBg.setAlpha(1);
                holder.imgHeaderBg.startAnimation(mAnimation);
                mUserList.get(position).setSpeaking(false);
            } else {
                holder.imgHeaderBg.setAlpha(0);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgHeader;
        View imgHeaderBg;
        ImageView imgMute;
        TextView textViewName;

        public MyViewHolder(View convertView) {
            super(convertView);
            imgHeader = (ImageView) convertView.findViewById(R.id.item_header);
            imgHeaderBg = convertView.findViewById(R.id.item_user_bg);
            imgMute = (ImageView) convertView.findViewById(R.id.item_mute);
            textViewName = (TextView) convertView.findViewById(R.id.item_name);
        }
    }
}
