package in.peerreview.fmradioindia.applogic;

import android.content.Context;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.INetwork;
import in.co.dipankar.quickandorid.utils.Network;
import in.co.dipankar.quickandorid.utils.TelemetryUtils;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class TelemetryManager {

    public static final String TELEMETRY_CLICK_BTN_OPTION = "click_option_btn";
    public static final String TELEMETRY_CLICK_BTN_REPORT = "click_report_btn";
    public static final String TELEMETRY_CLICK_BTN_SAHRE = "click_share_btn";
    public static final String TELEMETRY_CLICK_BTN_FOLLOW = "click_follow_btn";
    public static final String TELEMETRY_CLICK_BTN_RATE = "click_rate_btn";
    public static final String TELEMETRY_CLICK_BTN_CREDIT = "click_credit_btn";
    public static String DB_ENDPOINT = "http://simplestore.dipankar.co.in/api/nodel_bengalifm1";
  public static String TELEMETRY_ENDPOINT = "http://simplestore.dipankar.co.in/api/stat_bengalifm1";

  // modidy DB entry
  public static String DB_RANK_UP_URL = DB_ENDPOINT + "?_cmd=rankup&_payload=rank&id=";
  public static String DB_RANK_DOWN_URL = DB_ENDPOINT + "?_cmd=rankdown&_payload=rank&id=";
  public static String DB_COUNT_CLICK = DB_ENDPOINT + "?_cmd=increment&_payload=count_click&id=";
  public static String DB_COUNT_ERROR = DB_ENDPOINT + "?_cmd=increment&_payload=count_error&id=";
  public static String DB_COUNT_SUCCESS =
      DB_ENDPOINT + "?_cmd=increment&_payload=count_success&id=";
  public static String DB_COUNT_LIKE = DB_ENDPOINT + "?_cmd=increment&_payload=like&id=";
  public static String DB_COUNT_UNLIKE = DB_ENDPOINT + "?_cmd=increment&_payload=unlike&id=";
  // Adding telemetry
  public static final String TELEMETRY_PLAYER_TRY_PLAYING =
      "player_click"; // indicate we are really tring.
  public static final String TELEMETRY_PLAYER_SUCCESS = "player_success"; // indicate succss
  public static final String TELEMETRY_PLAYER_ERROR = "player_error"; // indicate error

  public static final String TELEMETRY_CLICK_PREV_BUTTON = "click_prev_btn";
  public static final String TELEMETRY_CLICK_NEXT_BUTTON = "click_next_btn";
  public static final String TELEMETRY_CLICK_MAIN_LIST_ITEM = "click_main_list_item";
  public static final String TELEMETRY_CLICK_SUGGESTION_LIST_ITEM = "click_suggestion_list_item";
  public static final String TELEMETRY_CLICK_RATING_BAR = "click_rating_bar";

  public static final String TELEMETRY_CLICK_QUICK_LIST_ITEM = "click_quick_list_item";
  public static final String TELEMETRY_CLICK_QUICK_LIST_ITEM_LONG = "click_quick_item_list_long";
  public static final String TELEMETRY_CLICK_PLAY_BUTTON = "click_play_btn";
  public static final String TELEMETRY_CLICK_RECORD_BUTTON = "click_record_btn";
  public static final String TELEMETRY_CLICK_LOCK_BUTTON = "click_lock_btn";
  public static final String TELEMETRY_CLICK_FEV_BUTTON = "click_fev_btn";
  public static final String TELEMETRY_CLICK_MAIN_LIST_ITEM_LONG = "click_main_list_item_long";
  public static final String TELEMETRY_CLICK_SEARCH_BAR = "click_search_bar";
  public static final String TELEMETRY_CLICK_QSB_HINDI = "click_qsb_hindi";
  public static final String TELEMETRY_CLICK_QSB_KOLKATA = "click_qsb_kolkata";
  public static final String TELEMETRY_CLICK_QSB_BANGALADESH = "click_qsb_bangadesh";
  public static final String TELEMETRY_CLICK_QSB_BENGALI = "click_qsb_bengali";
  public static final String TELEMETRY_CLICK_BUTTONSHEET_TUT = "click_buttom_sheet_tut";
  public static final String TELEMETRY_CLICK_BUTTONSHEET_FEB = "click_buttom_sheet_feb";
  public static final String TELEMETRY_CLICK_BUTTONSHEET_AUTOSTOP = "click_buttom_sheet_autostop";
  public static final String TELEMETRY_CLICK_BUTTONSHEET_AUTOSTART = "click_buttom_sheet_autostart";
  public static final String TELEMETRY_CLICK_BUTTONSHEET_CHANGE_THEME =
      "click_buttom_sheet_changetheme";
  public static final String TELEMETRY_PERMISSION_DENY_WRITE_STOARGE = "permission_deny_storage";
  public static final String TELEMETRY_CLICK_MENU_RATE = "click_menu_rate";
  public static final String TELEMETRY_CLICK_MENU_ABOUT = "click_menu_about";
  public static final String TELEMETRY_CLICK_MENU_REPORT = "click_menu_report";
  public static final String TELEMETRY_CLICK_MENU_UPGRADE = "click_menu_upgrade";
  public static final String TELEMETRY_SHOW_MENU_RATE_AUTO = "click_menu_rate_auto";
  public static final String TELEMETRY_SHOW_MENU_UPGRADE_AUTO = "click_menu_upgrade_auto";

  private TelemetryUtils mTelemetryUtils;
  private INetwork mNetwork;

  @Inject
  @Named("ApplicationContext")
  Context mContext;

  @Inject
  public TelemetryManager() {
    if (mTelemetryUtils == null) {
      mTelemetryUtils =
          new TelemetryUtils(
              mContext,
              TELEMETRY_ENDPOINT,
              true,
              new TelemetryUtils.Callback() {
                @Override
                public void onSuccess() {}

                @Override
                public void onFail() {}
              });
    }

    if (mNetwork == null) {
      mNetwork = new Network(mContext, true);
      DLog.d("Netwrok is created");
    }
  }

  public void rankUp(String id) {
    mNetwork.retrive(DB_RANK_UP_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);
  }

  public void rankDown(String id) {
    mNetwork.retrive(DB_RANK_DOWN_URL + id, Network.CacheControl.GET_LIVE_ONLY, null);
  }

  public void dbIncrementCount(String id) {
    mNetwork.retrive(DB_COUNT_CLICK + id, Network.CacheControl.GET_LIVE_ONLY, null);
  }

  public void dbIncrementError(String id) {
    mNetwork.retrive(DB_COUNT_ERROR + id, Network.CacheControl.GET_LIVE_ONLY, null);
  }

  public void dbIncrementSuccess(String id) {
    mNetwork.retrive(DB_COUNT_SUCCESS + id, Network.CacheControl.GET_LIVE_ONLY, null);
  }

  public void markHit(String telemetry) {
    mTelemetryUtils.markHit(telemetry);
  }

  public void dbIncrementLike(String id, boolean inc) {
    if (inc) {
      mNetwork.retrive(DB_COUNT_LIKE + id, Network.CacheControl.GET_LIVE_ONLY, null);
    } else {
      mNetwork.retrive(DB_COUNT_UNLIKE + id, Network.CacheControl.GET_LIVE_ONLY, null);
    }
  }
}
