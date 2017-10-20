package practice.internshala.mplayer.database;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import practice.internshala.mplayer.MPlayerApplication;

/**
 * Created by piyush on 20/10/17.
 */

public class RealmHelper {
    public static void addToDatabase(String id){
        id = id.trim();

        SongDbPOJO temp = new SongDbPOJO(new Integer(id),new Boolean(true));
        temp.setFavourite(new Boolean(true));
        temp.setId(new Integer(id));

        Log.e("addToDatabase:",id );
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        realm.copyToRealm(temp);

        realm.commitTransaction();
    }
    public static boolean isMarkedFavourite(String songID){
        songID = songID.trim();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<SongDbPOJO> realmResults = realm.where(SongDbPOJO.class)
                .equalTo("id",Integer.parseInt(songID)).findAll();

        return realmResults.first().isFavourite().booleanValue();
    }
    public static void flipFavouriteMark(String songID){
        songID = songID.trim();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<SongDbPOJO> realmResults = realm.where(SongDbPOJO.class)
                .equalTo("id",Integer.parseInt(songID)).findAll();

        realm.beginTransaction();
        for(SongDbPOJO songDbPOJO: realmResults){
            songDbPOJO.setFavourite(new Boolean(!songDbPOJO.isFavourite().booleanValue()));
        }
        realm.commitTransaction();
    }
    public static boolean checkPresence(String songID){
        songID = songID.trim();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<SongDbPOJO> realmResults = realm.where(SongDbPOJO.class)
                .equalTo("id",Integer.parseInt(songID)).findAll();
        if(realmResults.size()==0)
            return false;
        return true;
    }
    public static RealmResults<SongDbPOJO> fetchDB(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SongDbPOJO> realmResults = realm.where(SongDbPOJO.class).findAll();
        return realmResults;
    }
}
