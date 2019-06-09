package in.peerreview.fmradioindia.ui.player;

import in.co.dipankar.quickandorid.arch.BaseViewState;
import in.peerreview.fmradioindia.model.Channel;

public class PlayerState extends BaseViewState {
  public enum State {
    TRY_PLAYING,
    PLAYING,
    STOP,
    ERROR,
    PAUSE,
    RESUME
  }

  public enum VisibilityType {
    NONE,
    HIDE_ALL,
    SHOW_ALL,
    SHOW_MINI,
    SHOW_FULL
  }

  private Channel channel;
  private State state;
  private boolean fev;
  private VisibilityType visibilityType;

  public boolean isFev() {
    return fev;
  }

  public Channel getChannel() {
    return channel;
  }

  public State getState() {
    return state;
  }

  public VisibilityType getVisibilityType() {
    return visibilityType;
  }

  protected PlayerState(Builder builder) {
    super(builder);
    state = builder.state;
    channel = builder.channel;
    fev = builder.fev;
    visibilityType = builder.visibilityType;
  }

  public static class Builder extends BaseViewState.Builder<PlayerState.Builder> {
    private Channel channel = null;
    private State state = null;
    private boolean fev = false;
    private VisibilityType visibilityType = VisibilityType.NONE;

    public Builder() {}

    public Builder setChannel(Channel channel) {
      this.channel = channel;
      return this;
    }

    public Builder setState(State state) {
      this.state = state;
      return this;
    }

    public Builder setFev(boolean state) {
      this.fev = state;
      return this;
    }

    public Builder setVisibilityType(VisibilityType state) {
      this.visibilityType = state;
      return this;
    }

    public PlayerState build() {
      return new PlayerState(this);
    }
  }
}
