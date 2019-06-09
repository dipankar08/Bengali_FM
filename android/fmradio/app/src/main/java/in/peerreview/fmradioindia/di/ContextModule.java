package in.peerreview.fmradioindia.di;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module
public class ContextModule {
  Context context;

  public ContextModule(Context context) {
    this.context = context;
  }

  @Provides
  @Named("ApplicationContext")
  Context getContext() {
    return context;
  }
}
