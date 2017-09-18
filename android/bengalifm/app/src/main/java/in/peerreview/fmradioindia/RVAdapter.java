package in.peerreview.fmradioindia;

import android.content.Context;
import android.icu.text.DisplayContext;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.peerreview.fmradioindia.External.AndroidUtils;
import in.peerreview.fmradioindia.External.SimpleSend;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
    private static final String TAG = "RVAdapter" ;
    private static final String ADMOB_ID_PROD = "ca-app-pub-6413024436378029/2754825666";
    private static final String ADMOB_ID_DEBUG = "ca-app-pub-3940256099942544/6300978111";
    static List<Nodes> nodes = new ArrayList<>();
    Context mContext;

    RVAdapter(List<Nodes> persons,Context c){
        if (persons != null){
            this.nodes = persons;
        }
        mContext = c;
    }
    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView sl;
        TextView name;
        TextView count;
        ImageView img;
        int type;

        PersonViewHolder(View itemView,int type) {
            super(itemView);
            this.type = type;
            cv = (CardView)itemView.findViewById(R.id.cv);
            sl = (TextView)itemView.findViewById(R.id.sl);
            name = (TextView)itemView.findViewById(R.id.name);
            count = (TextView)itemView.findViewById(R.id.count);
            img = (ImageView)itemView.findViewById(R.id.img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    final int pos = getAdapterPosition();
                    //Toast.makeText(MainActivity.Get(), nodes.get(pos).getName(), Toast.LENGTH_SHORT).show();
                    MainActivity.Get().play(nodes.get(pos));
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return nodes.size();
    }
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == 1) //add
        {
            AdView adView = new AdView(MainActivity.Get());
            adView.setAdSize(AdSize.BANNER);
            if(AndroidUtils.isDebug()){
                adView.setAdUnitId(ADMOB_ID_DEBUG);
            } else{
                adView.setAdUnitId(ADMOB_ID_PROD);
            }
            float density = MainActivity.Get().getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,height);
            adView.setLayoutParams(params);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            v = (View)adView;
        } else{
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nodes, viewGroup, false);
        }

        PersonViewHolder pvh = new PersonViewHolder(v,viewType);
        return pvh;
    }
    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        if(personViewHolder.type ==1){
            // this is an add view..
        } else{
            personViewHolder.sl.setText((i+1)+"");
            personViewHolder.name.setText(nodes.get(i).getName());
            int per =0;
            if((nodes.get(i).getSuccess()+nodes.get(i).getError()) > 0){
                per = (int)((float)nodes.get(i).getSuccess()/(nodes.get(i).getSuccess()+nodes.get(i).getError())*100);
            }
            String msg = nodes.get(i).getCount()+" plays  .  "+ per+"% working.";
            personViewHolder.count.setText(msg);
            Glide.with(mContext)
                    .load(nodes.get(i).getImg())
                    .override(70, 70)
                    .into(personViewHolder.img);
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public void update(List<Nodes> datas){
        if(datas == null)
            return;
        if (nodes != null && nodes.size()>=0){
            nodes.clear();
        }
        nodes.addAll(datas);
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position)
    {
        if (position % 5 == 0)
            return 1;
        return 2;
    }
}