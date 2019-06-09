package in.peerreview.fmradioindia.applogic;

import android.os.Build;
import android.support.annotation.Nullable;
import com.google.common.collect.Lists;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.model.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChannelManager {
  private static final int MAX_LIMIT = 6;
  private static final String SUGGESTION_LIST_TAG = "Mostly Played";
  private static final String NEWLY_ADDED_TAG = "Newly Added";

  private HashMap<String, Channel> mIdToChannelMap;
  private HashMap<String, Integer> mIdToIndex;
  private List<Channel> mFullChannelList;
  private List<Channel> mRecentSearchChannelList;
  private List<Channel> mRecentPlayedChannelList;
  private List<Channel> mLikedChannelList;
  private LinkedHashMap<String, Category> mCategoriesMap;
  private List<Category> mSelectedCatList;
  List<Callback> mCallbacks;
  private LinkedHashMap<String, Boolean> mUserPref;
  List<Channel> mSuggested;
  private boolean mAlreadyBuild = false;
  private TelemetryManager mTelemetryManager;

  private DataFetcher mDataFetcher;
  private StorageManager mStorageManager;

  public void markLike(String id) {
    mTelemetryManager.dbIncrementLike(id, true);
  }

  public void markUnlike(String id) {
    mTelemetryManager.dbIncrementLike(id, false);
  }

  public interface Callback {
    void onLoadError(String err);

    void onLoadSuccess();

    void onCatListRefreshed();
    void onChangeRecentSerachList();

      void onChangeRecentPlayList();

    void onChangeFebList();
    void onPrefUpdated();
  }

  @Inject
  public ChannelManager(
      DataFetcher dataFetcher, StorageManager storageManager, TelemetryManager telemetryManager) {
    mDataFetcher = dataFetcher;
    mStorageManager = storageManager;
    mTelemetryManager = telemetryManager;
    mCallbacks = new ArrayList<>();
    mCategoriesMap = new LinkedHashMap<>();
    mSelectedCatList = new ArrayList<>();
    mIdToChannelMap = new HashMap<>();
    mIdToIndex = new HashMap<>();
    mRecentSearchChannelList = mStorageManager.getRecentSearch();
    mRecentPlayedChannelList = mStorageManager.getRecentPlayed();
    mLikedChannelList = mStorageManager.getLike();
    mFullChannelList = mStorageManager.getAll();
    mUserPref = mStorageManager.getPref();
    mSuggested = new ArrayList<>();
  }

  public void fetch() {
    if (mFullChannelList != null && mFullChannelList.size() > 0) {
      processChannel();
    }
    mDataFetcher.fetchData(
        new DataFetcher.Callback() {
          @Override
          public void onSuccess(final List<Channel> channels) {
            if (channels != null) {
              mFullChannelList = channels;
              processChannel();
            } else {
              for (Callback callback : mCallbacks) {
                callback.onLoadError("Not able to load channel");
              }
            }
          }

          @Override
          public void onSuccessConfig(Config config) {
            processConfig(config);
          }

          @Override
          public void onError(String msg) {
            for (Callback callback : mCallbacks) {
              callback.onLoadError(msg);
            }
          }
        });
  }

  public void addCallback(Callback callback) {
    mCallbacks.add(callback);
  }

  public void removeCallback(Callback callback) {
    mCallbacks.remove(callback);
  }

  private void processConfig(Config config) {
    // Might need to process the config
    boolean isSelected = mUserPref.size() <= 0;
    for (String s : Lists.reverse(config.getCatList())) {
      if (mUserPref.get(s.trim()) == null) {
        mUserPref.put(s.trim(), isSelected);
      }
    }
    for (String s : Lists.reverse(config.getLangList())) {
      if (mUserPref.get(s.trim()) == null) {
        mUserPref.put(s.trim(), isSelected);
      }
    }
    for(Callback callback:mCallbacks){
        callback.onPrefUpdated();
    }
    maybuildCatList();
  }

  private void processChannel() {
    maybuildCatList();
  }

  private void maybuildCatList() {

    if (mUserPref.size() == 0 || mFullChannelList.size() == 0) {
      DLog.d("Skip building the list as UserPref or mFullChannelList not avilable");
      return;
    }
    if (mAlreadyBuild) {
      mAlreadyBuild = true;
      DLog.d("Already build list -- so skiping");
    }

    // Initilize all data.
    mCategoriesMap = new LinkedHashMap<>();
    mSuggested = new ArrayList<>();
    mIdToIndex = new HashMap<>();
    mIdToChannelMap = new HashMap<>();

    // Building the Index map.
    for (int i = 0; i < mFullChannelList.size(); i++) {
      Channel channel = mFullChannelList.get(i);
      mIdToChannelMap.put(channel.getId(), channel);
      mIdToIndex.put(channel.getId(), i);
    }

    // Population extra index,
    mCategoriesMap.put(
        "Recent Play List".toLowerCase(),
        new Category("Recent Play List", true).addList(mRecentPlayedChannelList));
    mCategoriesMap.put(
        "Liked Play List".toLowerCase(),
        new Category("Liked Play List", true).addList(mLikedChannelList));

    mUserPref.put(SUGGESTION_LIST_TAG, true);
    mCategoriesMap.put(SUGGESTION_LIST_TAG.toLowerCase(), new Category(SUGGESTION_LIST_TAG, true));

    mUserPref.put(NEWLY_ADDED_TAG, true);
    mCategoriesMap.put(NEWLY_ADDED_TAG.toLowerCase(), new Category(SUGGESTION_LIST_TAG, true));

    // Create cat for each pref which is inorder as this is a linked hash map
    for (Map.Entry<String, Boolean> item : mUserPref.entrySet()) {
      mCategoriesMap.put(item.getKey().toLowerCase().trim(), new Category(item.getKey(), true));
    }


    // Traversal each channel and insert into map and also populate the UserPref if needed.
    for (Channel channel : mFullChannelList) {

      for(String cat :channel.getCategories()) {
          if (cat != null && cat.trim().length() > 0) {
              if (mUserPref.get(cat) == null) {
                  mUserPref.put(cat, true);
                  mCategoriesMap.put(cat.toLowerCase().trim(), new Category(cat, false));
              }
              mCategoriesMap.get(cat.toLowerCase().trim()).addItem(channel);
          }
      }

      if (channel.getTags() != null) {
        for (String c : mCategoriesMap.keySet()) {
          if (channel.getTags().toLowerCase().contains(c)) {
            mCategoriesMap.get(c).addItem(channel);
          }
        }
      }

      if(channel.isNew()){
          mCategoriesMap.get(NEWLY_ADDED_TAG.toLowerCase().trim()).addItem(channel);
      }
    }

    // adding to suggetion.
    if (mCategoriesMap.get(SUGGESTION_LIST_TAG.trim().toLowerCase()) != null) {
      mSuggested.addAll(mCategoriesMap.get(SUGGESTION_LIST_TAG.trim().toLowerCase()).getList());
    }


    // 3. Trip the perf which is empty.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      mUserPref
          .entrySet()
          .removeIf(e -> mCategoriesMap.get(e.getKey().toLowerCase()).getList().size() == 0);
    }

    //4. Notifcy callback.
    for (Callback callback : mCallbacks) {
      callback.onLoadSuccess();
    }
    reBuildCatList();
  }

  // cat_list: "Recent Play List, Liked Play List, Suggested Live, Kolkata Live, Bengali News TV,
  // Hindi News Tv, English News Tv, Mumbai Live, Delhi Live, Bangalore Live, Pune Live, Hyderabad
  // Live, All India Radio, Bangladesh Radio ",
  private void reBuildCatList() {
    mSelectedCatList = new ArrayList<>();
    for (Map.Entry<String, Boolean> entry : mUserPref.entrySet()) {
      if (entry.getValue()) {
        if (mCategoriesMap.get(entry.getKey().toLowerCase()) != null) {
          mSelectedCatList.add(mCategoriesMap.get(entry.getKey().toLowerCase()));
        }
      }
    }
    for (Callback callback : mCallbacks) {
      callback.onCatListRefreshed();
    }
  }

  public List<Channel> applySearch(String s) {
    List<Channel> mSearchResult = new ArrayList<>();
    if (s == null || s.length() == 0) {
      return mFullChannelList;
    } else {
      for (Channel c : mFullChannelList) {
        if (c.getName().toLowerCase().contains(s.toLowerCase())) {
          mSearchResult.add(c);
        }
      }
    }
    return mSearchResult;
  }

  public List<Channel> getAll() {
    return mFullChannelList;
  }

  public List<Channel> getRecentPlayed() {
    return mRecentPlayedChannelList;
  }

  public void markRecentPlayed(String id) {
    Channel channel = mIdToChannelMap.get(id);
    // remove older if exist.
    mRecentPlayedChannelList.remove(channel);
    if (channel != null) {
      mRecentPlayedChannelList.add(0, channel);
    }

    if (mRecentPlayedChannelList.size() > MAX_LIMIT) {
      mRecentPlayedChannelList.remove(MAX_LIMIT);
    }
    for (Callback callback : mCallbacks) {
      callback.onChangeRecentPlayList();
    }
  }

  public List<Channel> getRecentSearch() {
    return mRecentSearchChannelList;
  }

  public void markRecentSearch(String id) {
    Channel channel = mIdToChannelMap.get(id);
    // remove older if exist.
    mRecentSearchChannelList.remove(channel);
    if (channel != null) {
      mRecentSearchChannelList.add(0, channel);
    }
    if (mRecentSearchChannelList.size() > MAX_LIMIT) {
      mRecentSearchChannelList.remove(MAX_LIMIT);
    }
    for (Callback callback : mCallbacks) {
      callback.onChangeRecentSerachList();
    }
  }

  public List<Channel> getFev() {
    return mLikedChannelList;
  }

  public void toggleFev(String id) {
    Channel channel = mIdToChannelMap.get(id);
    if (channel == null) {
      return;
    }
    if (mLikedChannelList.contains(channel)) {
      mLikedChannelList.remove(channel);
    } else {
      mLikedChannelList.add(0, channel);
      if (mLikedChannelList.size() > MAX_LIMIT) {
        mLikedChannelList.remove(MAX_LIMIT);
      }
    }
    for (Callback callback : mCallbacks) {
      callback.onChangeFebList();
    }
  }

  public boolean isFev(String id) {
    Channel channel = mIdToChannelMap.get(id);
    if (channel == null) {
      return false;
    }
    return mLikedChannelList.contains(channel);
  }

  public Channel getChannelForId(String id) {
    return mIdToChannelMap.get(id);
  }

  public Integer getIndexForId(String id) {
    if (mIdToIndex.get(id) == null) {
      DLog.d("Some invalid entry");
      // This is important - we need to clear the storage data here.
      clearAll();
    }
    return mIdToIndex.get(id);
  }

  public List<Category> getCatMap() {
    return mSelectedCatList;
  }

  public LinkedHashMap<String, Boolean> getUserPref() {
      return (LinkedHashMap<String, Boolean>) mUserPref.clone();
  }

  public @Nullable List<Channel> getSuggestedList() {
    return mSuggested;
  }

  public void setFilterUserPref(Map<String, Boolean> val) {
    for (Map.Entry<String, Boolean> entry : val.entrySet()) {
      mUserPref.put(entry.getKey(), entry.getValue());
    }
    reBuildCatList();
  }

  public void saveList() {
    mStorageManager.saveAll(mFullChannelList);
    mStorageManager.saveRecentPlayed(mRecentPlayedChannelList);
    mStorageManager.saveRecentSearch(mRecentSearchChannelList);
    mStorageManager.saveLike(mLikedChannelList);
    mStorageManager.savePref(mUserPref);
  }

  public void clearAll() {
    mStorageManager.deleteAll();
    mStorageManager.deleteRecentPlayed();
    mStorageManager.deleteRecentSearch();
    mStorageManager.deleteLike();
    mStorageManager.deletePref();
  }
}
