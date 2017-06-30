package in.peerreview.fmradioindia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ddutta on 6/25/2017.
 */
// ******************************* Fragments************************************************
@SuppressLint("validFragment")
public class BaseFragment extends Fragment {
    protected String m_fragmentTag;
    protected View m_myview;
    protected int m_id;
    protected CustomListAdapter m_adapter;
    protected List<Nodes> m_NodeList = new ArrayList<>();
    private List<BaseFragment> s_allFrags = new ArrayList<>();
    public BaseFragment(int id) {
        m_id = id;
        s_allFrags.add(this);
    }
    public BaseFragment get(int id) {
        return s_allFrags.get(id);
    }

    public void setFragmentTag(String tag)
    {
        this.m_fragmentTag = tag;
    }

    public String getFragmentTag()
    {
        return this.m_fragmentTag;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_myview = inflater.inflate(m_id, container, false);
        if(m_NodeList.size() != 0){
            showLList();
        } else{
            showLoading();
        }
        m_adapter = renderList();
        return m_myview;
    }
    public View getView(){
        return m_myview;
    }

    public CustomListAdapter getListAdapter(){
        return m_adapter;
    }

    public List<Nodes> getNodes(){
        return m_NodeList;
    }
    public void setNodes(List<Nodes> list ){
        if (list != null){
            m_NodeList.clear();
            m_NodeList.addAll(list);
            if(m_adapter != null) {
                m_adapter.notifyDataSetChanged();
            }
        }
    }

    public void addNodes(Nodes n ){
        if (n != null){
            m_NodeList.add(0,n);
            if(m_adapter != null) {
                m_adapter.notifyDataSetChanged();
            }
        }
    }
    private  CustomListAdapter renderList( ) {
        //list = (ListView) findViewById(R.id.list);
        Activity c = MainActivity.Get();
        CustomListAdapter adapter = new CustomListAdapter(MainActivity.Get(), m_NodeList);
        ListView listview= (ListView)m_myview.findViewById(R.id.list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.Get(), "You clicked " + m_NodeList.get(position).getName(), Toast.LENGTH_SHORT).show();
                MainActivity.Get().m_curFragment = MainActivity.Get().viewadapter.getVisibleFragment();
                MainActivity.Get().m_curPlayList = MainActivity.Get().m_curFragment.getNodes();
                if(MainActivity.Get().m_curPlayList != null && position <MainActivity.Get().m_curPlayList.size() ) {
                    MainActivity.Get().m_curPlayingNode = MainActivity.Get().m_curPlayList.get(position);
                    MainActivity.Get().play();
                } else{
                    MainActivity.Get().showToast("Click returns invalid entry!");
                }
            }
        });
        return adapter;
    }

    public void clearAnimation(){
        /*
        ListView listview= (ListView)m_myview.findViewById(R.id.list);
        if(listview == null) return;
        for (int i=0 ;i <listview.getChildCount();i++){
// todo                    listview.getChildAt(i).findViewById(R.id.play_anim).setVisibility(View.GONE);

        }
        */
    }
    public void showInProgressAnimation(){
        /*
        ListView listview= (ListView)m_myview.findViewById(R.id.list);
        if(listview == null) return;
        for (int i=0 ;i <listview.getChildCount();i++){
            if(m_NodeList.get(i).getUrl().equals(MainActivity.Get().m_curPlayingNode.getUrl())){
                //setImage(listview.getChildAt(i).findViewById(R.id.play_anim),R.drawable.loading2);
                //listview.getChildAt(i).findViewById(R.id.play_anim).setVisibility(View.VISIBLE);
            }
        }
        */
    }
    public void showPlayingAnimation(){
        /*
        ListView listview= (ListView)m_myview.findViewById(R.id.list);
        if(listview == null) return;
        for (int i=0 ;i <listview.getChildCount();i++){
            if(m_NodeList.get(i).getUrl().equals(MainActivity.Get().m_curPlayingNode.getUrl())){
                //setImage(listview.getChildAt(i).findViewById(R.id.play_anim),R.drawable.play_anim);
                //listview.getChildAt(i).findViewById(R.id.play_anim).setVisibility(View.VISIBLE);
            }
        }
        */
    }

    public void showLoading() {
        if(m_myview != null){
            m_myview.findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
            m_myview.findViewById(R.id.list).setVisibility(View.GONE);
        }
    }
    public void showLList() {
        if(m_myview != null){
            m_myview.findViewById(R.id.search_loading).setVisibility(View.GONE);
            m_myview.findViewById(R.id.list).setVisibility(View.VISIBLE);
        }
    }

    public void showLoadingEmpty() {
        if(m_myview != null){
            m_myview.findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
            m_myview.findViewById(R.id.loading_icon).setVisibility(View.GONE);
            ((TextView)m_myview.findViewById(R.id.loading_msg)).setText("Sorry,Currently We don not have any FM channel for this type.");
            m_myview.findViewById(R.id.list).setVisibility(View.GONE);
        }
    }
    public void showLoadingError() {
        if(m_myview != null){
            m_myview.findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
            m_myview.findViewById(R.id.loading_icon).setVisibility(View.GONE);
            ((TextView)m_myview.findViewById(R.id.loading_msg)).setText("Ooops, Network error happens while loading the list. Please check your internet connection and retry.");
            m_myview.findViewById(R.id.list).setVisibility(View.GONE);
        }
    }
    public void showLoadingTry() {
        if(m_myview != null){
            m_myview.findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
            m_myview.findViewById(R.id.loading_icon).setVisibility(View.VISIBLE);
            ((TextView)m_myview.findViewById(R.id.loading_msg)).setText("Please wait for a min, We are retriving FM list from server.");
            m_myview.findViewById(R.id.list).setVisibility(View.GONE);
        }
    }
    class CustomListAdapter extends ArrayAdapter<Nodes> {

        private Activity context;
        private List<Nodes> web;

        public CustomListAdapter(Activity context, List<Nodes> web) {
            super(context, R.layout.list, web);
            this.context = context;
            this.web = web;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView;
            if (convertView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView= inflater.inflate(R.layout.list, null, true);
            } else{
                rowView= convertView;
            }
            Log.d("Dipankar",position+"");
            LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.item);
            TextView sl = (TextView) rowView.findViewById(R.id.sl);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            final ImageView fev = (ImageView) rowView.findViewById(R.id.isfev);
            TextView excl = (TextView) rowView.findViewById(R.id.excl);

            sl.setText((position+1)+".");
            final Nodes cur = web.get(position);
            if(cur == null){
                // todo
            }
            txtTitle.setText(cur.getName());
            MainActivity.Get().setImage(imageView,MainActivity.Get().getChannelIcon(cur));

            if((MainActivity.Get().m_curPlayingNode != null) && MainActivity.Get().m_curPlayingNode.getUrl() == web.get(position).getUrl() ){
                // ((GifTextView) rowView.findViewById(R.id.play_anim)).setVisibility(View.VISIBLE);
            }
            //exclusive item
            if(web.get(position).getType() != null  && web.get(position).getType().indexOf("Exclusive") != -1){
                layout.setBackgroundColor(Color.parseColor("#ebf442"));
                MainActivity.Get().setImage(imageView,R.drawable.exclusive);
                excl.setVisibility(View.VISIBLE);
            } else{
                if(layout != null){
                    //TODO  layout.setBackgroundColor(Color.parseColor("#000"));
                }
                excl.setVisibility(View.GONE);
            }
            MainActivity.Get().setImage(fev,R.drawable.fevblack);

            fev.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    int res = MainActivity.Get().toggleFev(cur);
                    if(res == 1){ //added
                        MainActivity.Get().setImage(fev,R.drawable.tickfev);
                    } else if(res == -1){ //removed
                        MainActivity.Get().setImage(fev,R.drawable.fevblack);
                    }
                }
            });
            return rowView;
        }
        public void clearData() {
            // clear the data
            web.clear();
            //this.setNotifyOnChange();
        }
    }
}

