package practice.internshala.mplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import practice.internshala.mplayer.R;
import practice.internshala.mplayer.models.Song;

/**
 * Created by piyush on 18/10/17.
 */

public class MASongsListAdapter extends RecyclerView.Adapter<MASongsListAdapter.MASongsListHolder> {

    private ArrayList<Song> songArrayList;
    private Context context;

    public MASongsListAdapter(ArrayList<Song> songArrayList, Context context) {
        this.songArrayList = songArrayList;
        this.context = context;
    }

    @Override
    public MASongsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = (View) layoutInflater.inflate(R.layout.song_list_item,parent,false);
        return new MASongsListHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MASongsListHolder holder, int position) {
        holder.vhTvSongTitle.setText(songArrayList.get(position).getTitle());
        holder.vhTvSongSinger.setText(songArrayList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

     class MASongsListHolder extends RecyclerView.ViewHolder{
        public TextView vhTvSongTitle,vhTvSongSinger;
        public ImageView vhIvSongImage;
        public MASongsListHolder(View itemView) {
            super(itemView);
            vhTvSongTitle = (TextView) itemView.findViewById(R.id.vh_tv_song_title);
            vhTvSongSinger = (TextView) itemView.findViewById(R.id.vh_tv_song_singer);
            vhIvSongImage = (ImageView) itemView.findViewById(R.id.vh_iv_song_image);
        }
    }
}
