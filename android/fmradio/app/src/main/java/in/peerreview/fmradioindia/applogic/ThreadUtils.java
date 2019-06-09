package in.peerreview.fmradioindia.applogic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ThreadUtils {
  static final int DEFAULT_THREAD_POOL_SIZE = 4;
  ExecutorService executorService;

  @Inject
  public ThreadUtils() {
    executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
  }

  public void execute(Runnable runnable) {
    executorService.execute(runnable);
  }
}
