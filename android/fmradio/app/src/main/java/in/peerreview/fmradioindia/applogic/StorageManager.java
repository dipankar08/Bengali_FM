package in.peerreview.fmradioindia.applogic;

import android.content.Context;
import in.peerreview.fmradioindia.model.Channel;
import io.paperdb.Paper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class StorageManager {

  @Inject
  public StorageManager(@Named("ApplicationContext") Context context) {
    Paper.init(context);
  }

  public void saveRecentSearch(List<Channel> recent) {
    Paper.book().write("recentSearch", recent);
  }

  public List<Channel> getRecentSearch() {
    return Paper.book().read("recentSearch", new ArrayList<>());
  }

  public void saveRecentPlayed(List<Channel> recent) {
    Paper.book().write("recentPlayed", recent);
  }

  public List<Channel> getRecentPlayed() {
    return Paper.book().read("recentPlayed", new ArrayList<>());
  }

  public void saveLike(List<Channel> recent) {
    Paper.book().write("like", recent);
  }

  public List<Channel> getLike() {
    return Paper.book().read("like", new ArrayList<>());
  }

  public void saveAll(List<Channel> recent) {
    Paper.book().write("all", recent);
  }

  public List<Channel> getAll() {
    return Paper.book().read("all", new ArrayList<>());
  }

  public void deleteAll() {
    Paper.book().delete("all");
  }

  public void deleteRecentPlayed() {
    Paper.book().delete("recentPlayed");
  }

  public void deleteRecentSearch() {
    Paper.book().delete("recentSearch");
  }

  public void deleteLike() {
    Paper.book().delete("like");
  }

  public void savePref(LinkedHashMap<String, Boolean> val) {
    Paper.book().write("Pref", val);
  }

  public LinkedHashMap<String, Boolean> getPref() {
    return Paper.book().read("pref", new LinkedHashMap<String, Boolean>());
  }

  public void deletePref() {
    Paper.book().delete("pref");
  }
}
