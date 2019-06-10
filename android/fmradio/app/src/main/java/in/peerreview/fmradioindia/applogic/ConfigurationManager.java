package in.peerreview.fmradioindia.applogic;

import android.support.annotation.NonNull;
import in.peerreview.fmradioindia.BuildConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConfigurationManager {

  public enum Config {
    ENABLE_TV("enable_tv"),
    RELOAD_DATA("reload_data"),
    ADD_FREE("add_free"),
    TEST_ADD("test_add"),
    VIDEO_Add("video_add");
    private final String mValue;

    Config(String value) {
      mValue = value;
    }
  }

  public interface ConfigChangeListener {
    void onChange(Config config, Object newvalue);
  }

  private List<ConfigChangeListener> mConfigChangeListeners;

  private Map<Config, Object> mStoredConfig;

  @Inject
  public ConfigurationManager() {
    mConfigChangeListeners = new ArrayList<>();
    mStoredConfig = new HashMap<>();
    init();
  }

  private void init() {
    mStoredConfig.put(Config.TEST_ADD, isDebug());
    mStoredConfig.put(Config.VIDEO_Add, true);
    mStoredConfig.put(Config.ADD_FREE, false);
  }

  public void addConfigChangeListener(ConfigChangeListener configChangeListener) {
    mConfigChangeListeners.add(configChangeListener);
  }

  public void removeConfigChangeListener(ConfigChangeListener configChangeListener) {
    mConfigChangeListeners.remove(configChangeListener);
  }

  public void updateConfig(Config config, Object object) {
    if (mStoredConfig.get(config) == object) {
      return;
    }
    mStoredConfig.put(config, object);
    for (ConfigChangeListener listener : mConfigChangeListeners) {
      listener.onChange(config, object);
    }
  }

  public @NonNull Object getConfig(Config c) {
    if (mStoredConfig.get(c) != null) {
      return mStoredConfig.get(c);
    } else {
      return new Object();
    }
  }

  private boolean isDebug() {
    return BuildConfig.DEBUG;
  }
}
