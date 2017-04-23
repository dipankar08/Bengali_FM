package com.prgguru.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by ddutta on 4/23/2017.
 */
public class ChannelList {
    final static String[][] FM = {
            {"Internet","FNF FM", "http://94.23.36.117/proxy/arrahm00?mp=/stream"},
            {"Kolkata","AIR BENGALI", "http://airlive.nic.in/hls-live/livepkgr/_definst_/bengali/bengali.m3u8"},
            {"Mirchi","Mirchi top 20", "http://mt20live-lh.akamaihd.net/i/mt20live_1@346531/master.m3u8"},
            {"Mirchi","Mithe Mirchi", "http://meethimirchihdl-lh.akamaihd.net/i/MeethiMirchiHDLive_1_1@320572/master.m3u8"},
            {"Mirchi","Puraje Jeans", "http://puranijeanshdliv-lh.akamaihd.net/i/PuraniJeansHDLive_1_1@334555/master.m3u8"},
            {"Mirchi","Filmy Mirchi", "http://filmymirchihdliv-lh.akamaihd.net/i/FilmyMirchiHDLive_1_1@336266/master.m3u8"},
            {"Mirchi","Mirchi PhelaNeha", "http://pehlanashahdlive-lh.akamaihd.net/i/PehlaNashaHDLive_1_1@335229/master.m3u8"},
            {"Mirchi","Club Mirchi", "http://clubmirchihdlive-lh.akamaihd.net/i/ClubMirchiHDLive_1_1@336269/master.m3u8"},
            {"Mirchi","Mirchi Mehefil", "http://mirchimahfil-lh.akamaihd.net/i/MirchiMehfl_1@120798/master.m3u8"},
            {"Mirchi","Wakaao Mirchi", "http://wmirchi-lh.akamaihd.net/i/WMIRCHI_1@75780/master.m3u8"},
            {"Internet","Radio City", "http://prclive1.listenon.in:9960/"},
            {"Internet","BongNet","https://radio.bongonet.net:9000/;stream.mpeg"},
            {"Others","Vividh Bhrati","http://airlive.nic.in/hls-live/livepkgr/_definst_/vividhbharti.m3u8"},
            {"Others","Telegu","http://android.programmerguru.com/wp-content/uploads/2013/04/hosannatelugu.mp3"}
    };

    public static List<String> getAllCategories(){
        HashSet<String> ans = new HashSet<>();
        for(int i =0;i<FM.length;i++){
            ans.add(FM[i][0]);
        }
        return new ArrayList<String>(ans);
    }

    public static List<String> getAllChannelForCategories(String cat){
        HashSet<String> ans = new HashSet<>();
        for(int i =0;i<FM.length;i++){
            if(FM[i][0].equals(cat)) {
                ans.add(FM[i][1]);
            }
        }
        return new ArrayList<String>(ans);
    }

    public static String getChannelDetails(String name){
        HashSet<String> ans = new HashSet<>();
        for(int i =0;i<FM.length;i++){
            if(FM[i][1].equals(name)) {
                return FM[i][2];
            }
        }
        return null;
    }

}
