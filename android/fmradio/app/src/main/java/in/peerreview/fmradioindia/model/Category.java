package in.peerreview.fmradioindia.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
  private String name;
  private List<Channel> mList;
  private boolean shouldShow;

  public Category(String name, boolean shoudShow) {
    this.name = name;
    this.shouldShow = shoudShow;
    mList = new ArrayList<>();
  }

  public void addItem(Channel c) {
    mList.add(c);
  }

  public String getName() {
    return name;
  }

  public List<Channel> getList() {
    return mList;
  }

  public Category addList(List<Channel> list) {
    mList = list;
    return this;
  }

  public void setShouldShow(boolean shouldShow) {
    this.shouldShow = shouldShow;
  }

  public boolean isShouldShow() {
    return shouldShow;
  }
}
