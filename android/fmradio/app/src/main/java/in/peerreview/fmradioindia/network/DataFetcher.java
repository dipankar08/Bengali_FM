package in.peerreview.fmradioindia.network;

import android.content.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.Network;
import in.peerreview.fmradioindia.applogic.Utils;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.model.Config;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.json.JSONException;
import org.json.JSONObject;

@Singleton
public class DataFetcher {
  public interface Callback {
    void onSuccess(List<Channel> mChannelList);

    void onSuccessConfig(Config config);

    void onError(String msg);
  }

  private static String URL = Constant.DB_ENDPOINT + "find";
  private static String APP_CONFIG_URL = Constant.CONFIG_ENDPOINT + "find";
  private List<Channel> mChannelList;
  private Network mNetwork;
  private List<Callback> mCallback;
  private Context mContext;

  @Inject Utils mUtils;

  @Inject
  public DataFetcher(@Named("ApplicationContext") Context context) {
    mContext = context;
    mChannelList = new ArrayList<>();
    mNetwork = new Network(context, true);
  }

  public void fetchData(final Callback callback) {

    long startTime = System.nanoTime();
    mNetwork.retrive(
        APP_CONFIG_URL,
        Network.CacheControl.GET_LIVE_ONLY,
        new Network.Callback() {
          @Override
          public void onSuccess(JSONObject jsonObject) {
            List<String> langListStr = new ArrayList<>();
            List<String> catListStr = new ArrayList<>();
            try {
              if ("success".equals(jsonObject.getString("status"))) {
                JSONObject config = jsonObject.getJSONArray("out").getJSONObject(0);
                langListStr = Arrays.asList(config.getString("lang_list").split(","));
                catListStr = Arrays.asList(config.getString("cat_list").split(","));
              }
            } catch (JSONException e) {
              DLog.e("Not able to retrieve Config list");
              e.printStackTrace();
            }
            callback.onSuccessConfig(
                new Config.Builder().setCatList(catListStr).setLangList(langListStr).build());
            long endTime = System.nanoTime();
            mUtils.printExecutionTime(startTime, endTime);
          }

          @Override
          public void onError(String s) {
            callback.onError("Not able to retrived data");
          }
        });

    mNetwork.retrive(
        URL,
        Network.CacheControl.GET_LIVE_ONLY,
        new Network.Callback() {

          @Override
          public void onSuccess(JSONObject jsonObject) {
            callback.onSuccess(parse(jsonObject));
            long endTime = System.nanoTime();
            mUtils.printExecutionTime(startTime, endTime);
          }

          @Override
          public void onError(String s) {
            callback.onError("Not able to retrived data");
          }
        });
  }

  private List<Channel> parse(JSONObject jsonObject) {
    ObjectMapper mMapper = new ObjectMapper();
    try {
      if (jsonObject.getString("status").equals("success")) {
        String jsonList = jsonObject.getString("out");
        String stringVal = jsonList;
        mChannelList = mMapper.readValue(stringVal, new TypeReference<List<Channel>>() {});
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Collections.sort(
        mChannelList,
        new Comparator<Channel>() {
          @Override
          public int compare(Channel o1, Channel o2) {
            return o2.getCount_click() - o1.getCount_click();
          }
        });
    return mChannelList;
  }
}
