package practice.internshala.mplayer.fragments;


import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import practice.internshala.mplayer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NowPlaying#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NowPlaying extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String albumID;
    private String albumTitle;
    private String songTitle;

    private TextView tvSongArtist,tvSongTitle;
    private Context context;
    public NowPlaying() {
        // Required empty public constructor
    }

    private ImageView albumArt;
    private static Uri uri = Uri.parse("content://media/external/audio/albumart");
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NowPlaying.
     */
    // TODO: Rename and change types and number of parameters
    public static NowPlaying newInstance(String id, String albumName, String songTitle) {
        NowPlaying fragment = new NowPlaying();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, albumName);
        args.putString(ARG_PARAM3, songTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getString(ARG_PARAM1);
            albumTitle = getArguments().getString(ARG_PARAM2);
            songTitle = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        context = getActivity();
        initializeViews(rootView);
        fetchImage();

        return rootView;
    }

    private void fetchImage() {
        Uri uri2 = ContentUris.withAppendedId(uri, Long.parseLong(albumID));
        ParcelFileDescriptor pfd = null;
        try {
            pfd = context.getContentResolver()
                    .openFileDescriptor(uri2, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
                albumArt.setImageBitmap(bm);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeViews(View rootView) {
        tvSongTitle = (TextView) rootView.findViewById(R.id.song_title);
        tvSongArtist = (TextView) rootView.findViewById(R.id.song_artist);
        albumArt = (ImageView) rootView.findViewById(R.id.album_art);
        tvSongTitle.setText(songTitle);
        tvSongArtist.setText(albumTitle);
    }

}
