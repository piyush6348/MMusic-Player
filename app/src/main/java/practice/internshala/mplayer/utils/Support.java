package practice.internshala.mplayer.utils;

import android.content.Context;

import practice.internshala.mplayer.R;

/**
 * Created by piyush on 20/10/17.
 */

public class Support {

    public static final int SORT_BY_NAME=1;
    public static final int SORT_BY_DATE_MODIFIED=2;
    public static final int SPLASH_DISPLAY_LENGTH = 2000;
    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }

}
