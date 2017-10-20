package practice.internshala.mplayer;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by piyush on 20/10/17.
 */

public class MPlayerApplication extends Application {
    public Realm realm;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    }
}
