package practice.internshala.mplayer.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import io.realm.RealmResults;
import practice.internshala.mplayer.MainActivity;
import practice.internshala.mplayer.R;
import practice.internshala.mplayer.adapter.MASongsListAdapter;
import practice.internshala.mplayer.database.RealmHelper;
import practice.internshala.mplayer.database.SongDbPOJO;
import practice.internshala.mplayer.models.Song;
import practice.internshala.mplayer.service.MusicService;

public class FavouritesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MusicService musicSrv;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    private RecyclerView rvFavouriteSongs;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance(String param1, String param2) {
        FavouritesFragment fragment = new FavouritesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    MainActivity mainActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        rvFavouriteSongs = (RecyclerView) rootView.findViewById(R.id.rv_favourite_songs);

        ArrayList<Song> songArrayList = getListOfFavouriteSongs();

        mainActivity.getMusicService().setList(songArrayList);
        MASongsListAdapter adapter = new MASongsListAdapter(songArrayList,
                getActivity(),getActivity());
        rvFavouriteSongs.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavouriteSongs.setAdapter(adapter);

       // getActivity().unbindService(mainActivity.getServiceConnection());


    }


    private ArrayList<Song> getListOfFavouriteSongs() {
        RealmResults<SongDbPOJO> db = RealmHelper.fetchDB();

        HashSet<Integer> fav = new HashSet<>();

        for(SongDbPOJO songDbPOJO: db){
            fav.add(songDbPOJO.getId());
        }

        return getSongsList(getActivity(),fav);
    }

    public static ArrayList<Song> getSongsList(Context context, HashSet<Integer> fav ){

        ArrayList<Song> songArrayList = new ArrayList<>();

        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));

                    Integer integer = new Integer(id);

                    if(fav.contains(integer)){
                        String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String albumID = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));


                        Song song = new Song(title,album,artist,duration,path,albumID,id);
                        // Add code to get more column here
                        songArrayList.add(song);
                        // Save to your list here
                    }
                }

            }
        }

        cur.close();
        return songArrayList;
    }

}
