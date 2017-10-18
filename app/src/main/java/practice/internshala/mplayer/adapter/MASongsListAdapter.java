package practice.internshala.mplayer.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import practice.internshala.mplayer.MainActivity;
import practice.internshala.mplayer.R;
import practice.internshala.mplayer.models.Song;

/**
 * Created by piyush on 18/10/17.
 */

public class MASongsListAdapter extends RecyclerView.Adapter<MASongsListAdapter.MASongsListHolder> {

    private ArrayList<Song> songArrayList;
    private Context context;
    private static Uri uri;
    private Activity activity;
    public MASongsListAdapter(ArrayList<Song> songArrayList, Context context,
                              Activity activity) {
        this.songArrayList = songArrayList;
        this.context = context;
        this.activity = activity;
        uri = Uri.parse("content://media/external/audio/albumart");
    }

    @Override
    public MASongsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = (View) layoutInflater.inflate(R.layout.song_list_item,parent,false);
        return new MASongsListHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MASongsListHolder holder, final int position) {
        holder.vhTvSongTitle.setText(songArrayList.get(position).getTitle());
        holder.vhTvSongSinger.setText(songArrayList.get(position).getArtist());
        holder.vhLlSongItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(position);
                if(activity instanceof MainActivity){
                    ((MainActivity) activity).songPicked(v);
                }
            }
        });
        Uri uri2 = ContentUris.withAppendedId(uri, Long.parseLong(songArrayList.get(position).getAlbumID()));
        ParcelFileDescriptor pfd = null;
        try {
            pfd = context.getContentResolver()
                    .openFileDescriptor(uri2, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
                holder.vhIvSongImage.setImageBitmap(bm);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

     class MASongsListHolder extends RecyclerView.ViewHolder{
        public TextView vhTvSongTitle,vhTvSongSinger;
        public ImageView vhIvSongImage;
         public LinearLayout vhLlSongItem;
        public MASongsListHolder(View itemView) {
            super(itemView);
            vhTvSongTitle = (TextView) itemView.findViewById(R.id.vh_tv_song_title);
            vhTvSongSinger = (TextView) itemView.findViewById(R.id.vh_tv_song_singer);
            vhIvSongImage = (ImageView) itemView.findViewById(R.id.vh_iv_song_image);
            vhLlSongItem = (LinearLayout) itemView.findViewById(R.id.vh_ll_song_item);
        }
    }
}
