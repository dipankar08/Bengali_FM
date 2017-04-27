package in.peerreview.bengalifm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ddutta on 4/23/2017.
 */
class Channel {
    String url;
    String category;
    String name;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {

        return url;
    }

    String type;
    public Channel(String category, String name,String url, String type){
        this.url =url;
        this.name = name;
        this.type = type;
        this.category = category;
    }
}


public class ChannelList {
    static List<Channel> FM = new ArrayList<>();
    static{
        FM.add(new Channel("Internet","FNF FM", "http://94.23.36.117/proxy/arrahm00?mp=/stream",null));
        FM.add(new Channel("Kolkata","AIR BENGALI", "http://airlive.nic.in/hls-live/livepkgr/_definst_/bengali/bengali.m3u8",null));
        FM.add(new Channel("Mirchi","Mirchi top 20", "http://mt20live-lh.akamaihd.net/i/mt20live_1@346531/master.m3u8",null));
        FM.add(new Channel("Mirchi","Mithe Mirchi", "http://meethimirchihdl-lh.akamaihd.net/i/MeethiMirchiHDLive_1_1@320572/master.m3u8",null));
        FM.add(new Channel("Mirchi","Puraje Jeans", "http://puranijeanshdliv-lh.akamaihd.net/i/PuraniJeansHDLive_1_1@334555/master.m3u8",null));
        FM.add(new Channel("Mirchi","Filmy Mirchi", "http://filmymirchihdliv-lh.akamaihd.net/i/FilmyMirchiHDLive_1_1@336266/master.m3u8",null));
        FM.add(new Channel("Mirchi","Mirchi PhelaNeha", "http://pehlanashahdlive-lh.akamaihd.net/i/PehlaNashaHDLive_1_1@335229/master.m3u8",null));
        FM.add(new Channel("Mirchi","Club Mirchi", "http://clubmirchihdlive-lh.akamaihd.net/i/ClubMirchiHDLive_1_1@336269/master.m3u8",null));
        FM.add(new Channel("Mirchi","Mirchi Mehefil", "http://mirchimahfil-lh.akamaihd.net/i/MirchiMehfl_1@120798/master.m3u8",null));
        FM.add(new Channel("Mirchi","Wakaao Mirchi", "http://wmirchi-lh.akamaihd.net/i/WMIRCHI_1@75780/master.m3u8",null));
        FM.add(new Channel("Internet","Radio City", "http://prclive1.listenon.in:9960/",null));
        FM.add(new Channel("Internet","BongNet","https://radio.bongonet.net:9000/;stream.mpeg",null));
        FM.add(new Channel("Others","Vividh Bhrati","http://airlive.nic.in/hls-live/livepkgr/_definst_/vividhbharti.m3u8",null));
        FM.add(new Channel("Others","Telegu","http://android.programmerguru.com/wp-content/uploads/2013/04/hosannatelugu.mp3",null));
    }

    public static void setList(List<Channel> list ){
        FM = list;
    }

    public static List<String> getAllCategories(){
        HashSet<String> ans = new HashSet<>();
        for(int i =0;i<FM.size();i++){
            ans.add((FM.get(i).getCategory()));
        }
        return new ArrayList<String>(ans);
    }

    public static List<Channel> getAllChannelForCategories(String cat){
        HashSet<Channel> ans = new HashSet<>();
        for(int i =0;i<FM.size();i++){
            if(FM.get(i).getCategory().equals(cat)) {
                ans.add(FM.get(i));
            }
        }
        return new ArrayList<Channel>(ans);
    }

    public static Channel getChannelDetails(String name){
        HashSet<String> ans = new HashSet<>();
        for(int i =0;i<FM.size();i++){
            if(FM.get(i).getName().equals(name)) {
                return FM.get(i);
            }
        }
        return null;
    }
}
