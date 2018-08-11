package prof.mo.ed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Prof-Mohamed on 8/7/2018.
 */

public class TrailersAdapter extends ArrayAdapter<MovieEntity> {

    private ArrayList<MovieEntity> feedTrailersList;
    public transient Context mContext;

    public TrailersAdapter(Context context, int Resource, ArrayList<MovieEntity> feedTrailersList) {
        super(context, Resource, feedTrailersList);

        this.feedTrailersList=feedTrailersList;
        this.mContext=context;
    }

    @Override
    public int getCount() {
        return feedTrailersList.size();
    }

    @Override
    public MovieEntity getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final MovieEntity feedItem=feedTrailersList.get(i);

        View view=convertView;
        if (view==null){
            view=LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item, parent,false);
        }
        TextView trailerName=(TextView)view.findViewById(R.id.trailer_name);
        trailerName.setText(feedItem.getTRAILER_NAME_STRING());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(feedItem.getTRAILER_KEY_STRING()));
                getContext().startActivity(intent);
            }
        });
        return view;
    }
}