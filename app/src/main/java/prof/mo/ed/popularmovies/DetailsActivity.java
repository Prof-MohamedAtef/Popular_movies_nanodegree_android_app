package prof.mo.ed.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
       if (savedInstanceState == null) {
            DetailFragment detailFragment = new DetailFragment();
            Bundle twoPaneExtras = new Bundle();
            twoPaneExtras.putSerializable("twoPaneExtras", getIntent().getSerializableExtra("movieInfo"));
            detailFragment.setArguments(twoPaneExtras);
            getFragmentManager().beginTransaction()
                    .replace(R.id.panel_two, detailFragment, "detail").commit();
        }
    }
}