package practice.internshala.mplayer.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import practice.internshala.mplayer.MainActivity;
import practice.internshala.mplayer.R;
import practice.internshala.mplayer.adapter.MASongsListAdapter;
import practice.internshala.mplayer.models.Song;
import practice.internshala.mplayer.service.MusicService;
import practice.internshala.mplayer.utils.Support;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1 = 1;
    private RecyclerView rvSongs;

    private MainActivity mainActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity)activity;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(int param1) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        rvSongs = (RecyclerView) rootView.findViewById(R.id.rv_songs);
        setUpAdapter(getSongsList(getActivity(),mParam1),getActivity());
        return rootView;
    }


    private void setUpAdapter(ArrayList<Song> songsList, Context context) {

        MASongsListAdapter maSongsListAdapter = new MASongsListAdapter(
                songsList,context,getActivity()
        );
        rvSongs.setLayoutManager(new LinearLayoutManager(context));
        rvSongs.setAdapter(maSongsListAdapter);

        MusicService musicService =mainActivity.getMusicService();
        if(musicService!=null)
            musicService.setList(songsList);
    }

    public static ArrayList<Song> getSongsList(Context context,int sortType){

        ArrayList<Song> songArrayList = new ArrayList<>();

        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = "";
        if(sortType == Support.SORT_BY_NAME)
            sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        else if(sortType == Support.SORT_BY_DATE_MODIFIED)
            sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " ASC";

        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String albumID = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    String id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));

                    Song song = new Song(title,album,artist,duration,path,albumID,id);
                    // Add code to get more column here
                    songArrayList.add(song);
                    // Save to your list here
                }

            }
        }

        cur.close();
        return songArrayList;
    }

}
