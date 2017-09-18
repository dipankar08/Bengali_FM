package in.peerreview.fmradioindia;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.peerreview.fmradioindia.External.SimpleSend;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
    private static final String TAG = "RVAdapter" ;
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

        PersonViewHolder(View itemView) {
            super(itemView);
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
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nodes, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }
    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.sl.setText((i+1)+"");
        personViewHolder.name.setText(nodes.get(i).getName());
        int per =0;
        if(nodes.get(i).getCount() > 0){
            per = (int)((float)nodes.get(i).getSuccess()/(nodes.get(i).getCount())*100);
        }
        String msg = nodes.get(i).getCount()+" plays  .  "+ per+"% working.";
        personViewHolder.count.setText(msg);
        Glide.with(mContext)
                .load(nodes.get(i).getImg())
                .override(70, 70)
                .into(personViewHolder.img);
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
}