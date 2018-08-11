package prof.mo.ed.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainFragment.MovieDataListener {

    boolean mTowPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout panelTwo = (FrameLayout) findViewById(R.id.panel_two);
        MainFragment mainFragment = new MainFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.panel_one, mainFragment)
                .commit();

        if (panelTwo == null) {
            mTowPanel = false;
        } else {
            mTowPanel = true;
            if (savedInstanceState == null) {
                DetailFragment detailFragment = new DetailFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.panel_two, detailFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onMovieFragmentSelected(MovieEntity movieEntity) {

        if (mTowPanel) {
            //Two Pane Ui
            DetailFragment detailFragment = new DetailFragment();
            Bundle twoPaneExtras = new Bundle();
            twoPaneExtras.putSerializable("twoPaneExtras", movieEntity);
            detailFragment.setArguments(twoPaneExtras);
            getFragmentManager().beginTransaction()
                    .replace(R.id.panel_two, detailFragment, "detail").commit();
        } else {
            // single pane Ui
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra("movieInfo", movieEntity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
    }
}