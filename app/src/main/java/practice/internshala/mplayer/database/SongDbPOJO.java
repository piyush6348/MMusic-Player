package practice.internshala.mplayer.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by piyush on 20/10/17.
 */

public class SongDbPOJO extends RealmObject {
    public SongDbPOJO() {
    }

    public SongDbPOJO(Integer id, Boolean favourite) {
        this.id = id;
        this.favourite = favourite;
    }

    @PrimaryKey
    private Integer id;
    private Boolean favourite;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}
