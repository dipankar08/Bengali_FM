package in.peerreview.fmradioindia.ui.rowlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Channel;
import java.util.List;
import javax.annotation.Nullable;

public class RowListAdapter extends RecyclerView.Adapter<RowListAdapter.MyViewHolder> {

  @Nullable private List<Channel> channelList;
  private Context mContext;
  private int mLayoutId;

  public RowListAdapter(Context context, int layoutId) {
    mContext = context;
    mLayoutId = layoutId;
  }

  public void setItem(List<Channel> list) {
    channelList = list;
    notifyDataSetChanged();
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView count;
    public TextView title;
    public TextView subtitle;
    public TextView live;
    public TextView neww;

    public MyViewHolder(View view) {
      super(view);
      image = view.findViewById(R.id.row_image);
      title = view.findViewById(R.id.row_title);
      neww = view.findViewById(R.id.row_new);
      subtitle = view.findViewById(R.id.row_subtitle);
      count = view.findViewById(R.id.row_count);
      live = view.findViewById(R.id.row_live);
    }
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (mLayoutId == -1) {
      mLayoutId = R.layout.item_roew;
    }
    View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
    return new RowListAdapter.MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(RowListAdapter.MyViewHolder holder, int position) {
    Channel c = channelList.get(position);
    if (holder.count != null) {
      holder.count.setText(position + 1 + "");
    }
    if (holder.title != null) {
      holder.title.setText(c.getName());
    }
    if (holder.subtitle != null) {
      holder.subtitle.setText(Html.fromHtml(c.getSubTitle()));
    }
    if (holder.image != null) {
      if (c.getImg() != null && c.getImg().length() != 0) {
        Glide.with(mContext).load(c.getImg()).into(holder.image);
      } else {
        holder.image.setImageResource(R.drawable.ic_music);
      }
    }
    if (holder.live != null) {
      if (c.isOnline()) {
        holder.live.setText("Live");
        holder.live.setBackgroundResource(R.drawable.rouned_button_red_full);
      } else {
        holder.live.setText("Offline");
        holder.live.setBackgroundResource(R.drawable.rounded_black_full);
      }
    }
    if (holder.neww != null) {
      if (c.isNew()) {
        holder.neww.setVisibility(View.VISIBLE);
      } else {
        holder.neww.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public int getItemCount() {
    return channelList != null ? channelList.size() : 0;
  }
}
