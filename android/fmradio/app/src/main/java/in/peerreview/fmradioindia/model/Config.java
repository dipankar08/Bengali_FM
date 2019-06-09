package in.peerreview.fmradioindia.model;

import java.util.ArrayList;
import java.util.List;

public class Config {
  private List<String> catList;
  private List<String> langList;

  private Config(Builder builder) {
    catList = builder.catList;
    langList = builder.langList;
  }

  public List<String> getCatList() {
    return catList;
  }

  public List<String> getLangList() {
    return langList;
  }

  public static class Builder {
    List<String> catList = new ArrayList<>();
    List<String> langList = new ArrayList<>();

    public Builder setCatList(List<String> catList) {
      this.catList = catList;
      return this;
    }

    public Builder setLangList(List<String> langList) {
      this.langList = langList;
      return this;
    }

    public Config build() {
      return new Config(this);
    }
  }
}
