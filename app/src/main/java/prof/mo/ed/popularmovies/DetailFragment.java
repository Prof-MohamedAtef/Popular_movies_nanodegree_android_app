package prof.mo.ed.popularmovies;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prof-Mohamed Atef on 8/8/2018.
 */
public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    DBHelper myDB;

    public ArrayAdapter<MovieEntity> ReviewsAdapter;
    ArrayList<MovieEntity> ReviewsList = new ArrayList<MovieEntity>();

    public JSONObject moviesTrailers;
    public JSONArray moviesTrailersArray;
    public JSONObject oneTrailerData;
    public ArrayAdapter<MovieEntity> trailersAdapter;
    ArrayList<MovieEntity> trailersList = new ArrayList<MovieEntity>();
    public String main_List = "results";

    RatingBar ratingBar;

    ListView listView_trailers, listView_reviews;
    TextView text_author;

    ViewGroup header, footer;

    MovieEntity movieEntity = null;
    String trailersEndPoint;
    String reviewsEndPoint;
    String movieID = null;

    public void startFetchingReviews() {
        try {
            ReviewsAsync reviewsAsync = new ReviewsAsync();
            reviewsAsync.execute(reviewsEndPoint);
        } catch (Exception e) {
            Log.v(LOG_TAG, "didn't Execute Reviews");
        }
    }

    public void startFetchingTrailers() {
        try {
            FetchTrailers fetchTrailers = new FetchTrailers();
            fetchTrailers.execute(trailersEndPoint);
        } catch (Exception e) {
            Log.v(LOG_TAG, "didn't Execute Trailers");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ratingBar = (RatingBar) header.findViewById(R.id.ratingBar);
        TextView txtTitle = (TextView) header.findViewById(R.id.txt_title);
        TextView txtReleaseDate = (TextView) header.findViewById(R.id.txt_release_date);
        TextView txtVoteAverage = (TextView) header.findViewById(R.id.txt_VoteAverage);
        TextView txtOverView = (TextView) header.findViewById(R.id.txt_overview);
        ImageView imgPoster = (ImageView) header.findViewById(R.id.Image_Poster);

        text_author = (TextView) footer.findViewById(R.id.text_author);

        final Bundle bundle = getArguments();
        myDB = new DBHelper(getActivity());

        if (bundle != null) {
            movieEntity = (MovieEntity) bundle.getSerializable("twoPaneExtras");
            txtTitle.setText(movieEntity.getTITLE_STRING());
            txtReleaseDate.setText(movieEntity.getRELEASE_DATE_STRING());
            txtVoteAverage.setText(movieEntity.getVOTE_AVERAGE());
            txtOverView.setText(movieEntity.getOVERVIEW_STRING());
            Picasso.with(getActivity()).load(movieEntity.getPOSTER_PATH_STRING()).into(imgPoster);
            movieID = movieEntity.getVIDEO_ID_STRING();
            boolean IsFound = myDB.get_Movie_ID(movieID);

            if (IsFound == true) {
                int movieState = myDB.getIS__Favourite_DB(movieID);
                if (movieState == 0) {
                    ratingBar.setRating(0);
                } else if (movieState == 1) {
                    ratingBar.setRating(1);
                }
            } else if (IsFound == false) {
                ratingBar.setRating(0);
            }
            String DefaultUri = "http://api.themoviedb.org/3";
            trailersEndPoint = DefaultUri + "/movie/" + movieID + "/videos?api_key=868bebbf76a3831f11b3985c703cf959";
            reviewsEndPoint = DefaultUri + "/movie/" + movieID + "/reviews?api_key=868bebbf76a3831f11b3985c703cf959";

            try {
                startFetchingReviews();
            } catch (Exception e) {
                Log.v(LOG_TAG, "didn't Execute Reviews");
            }
            try {
                startFetchingTrailers();
            } catch (Exception e) {
                Log.v(LOG_TAG, "didn't Execute Trailers");
            }
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (ratingBar.getRating() == 1) {
                    movieEntity.setIs__Favourite(1);
                    boolean IsInserted = myDB.insertData(movieEntity.getVIDEO_ID_STRING(), movieEntity.getTITLE_STRING(), movieEntity.getOVERVIEW_STRING(), movieEntity.getRELEASE_DATE_STRING(), movieEntity.getPOPULARITY_STRING(), movieEntity.getVOTE_AVERAGE(), movieEntity.getIs__Favourite(), movieEntity.getPOSTER_PATH_STRING());
                    if (IsInserted == true) {
                        Toast.makeText(getActivity(), "Added to Favourites", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getActivity(), "Movie not Inserted", Toast.LENGTH_LONG).show();
                } else if (ratingBar.getRating() == 0) {
                    movieEntity.setIs__Favourite(0);
                    boolean IsUpdated = myDB.UpdateData(movieEntity.getVIDEO_ID_STRING(), movieEntity.getIs__Favourite());
                    Toast.makeText(getActivity(), "Removed from Favourites", Toast.LENGTH_LONG).show();
                }
            }
        });

        trailersAdapter = new TrailersAdapter(getActivity(), R.layout.trailer_list_item, new ArrayList<MovieEntity>());
        listView_trailers.addHeaderView(header, null, false);
        listView_trailers.addFooterView(footer, null, false);
        listView_trailers.setAdapter(trailersAdapter);
        listView_trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        header = (ViewGroup) inflater.inflate(R.layout.header, listView_trailers,
                false);

        footer = (ViewGroup) inflater.inflate(R.layout.footer, listView_reviews,
                false);

        listView_trailers = (ListView) rootView.findViewById(R.id.listview_trailers);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    class FetchTrailers extends AsyncTask<String, Void, ArrayList<MovieEntity>> {

        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

        private ArrayList<MovieEntity> getTrailersDataFromJson(String TrailersJsonStr)
                throws JSONException {

            moviesTrailers = new JSONObject(TrailersJsonStr);
            moviesTrailersArray = moviesTrailers.getJSONArray(main_List);

            for (int i = 0; i < moviesTrailersArray.length(); i++) {

                // Get the JSON object representing a Trailer per each loop

                String TRAILER_ID_STRING, TRAILER_KEY_STRING, TRAILER_NAME_STRING, TRAILER_SITE_STRING, TRAILER_SIZE_STRING;
                String TRAILER_ID = "id", TRAILER_KEY = "key", TRAILER_NAME = "name", TRAILER_SITE = "site", TRAILER_SIZE = "size";

                oneTrailerData = moviesTrailersArray.getJSONObject(i);

                TRAILER_ID_STRING = oneTrailerData.getString(TRAILER_ID);
                TRAILER_KEY_STRING = oneTrailerData.getString(TRAILER_KEY);
                TRAILER_NAME_STRING = oneTrailerData.getString(TRAILER_NAME);
                TRAILER_SITE_STRING = oneTrailerData.getString(TRAILER_SITE);
                TRAILER_SIZE_STRING = oneTrailerData.getString(TRAILER_SIZE);

                trailersAdapter = null;

                MovieEntity entity = new MovieEntity(TRAILER_ID_STRING, TRAILER_KEY_STRING, TRAILER_NAME_STRING, TRAILER_SITE_STRING, TRAILER_SIZE_STRING);
                trailersList.add(entity);
            }

            return trailersList;
        }

        @Override
        protected ArrayList<MovieEntity> doInBackground(String... params) {

            try {
                String myTrailersJsonSTR = null;

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                if (params.length == 0) {
                    return null;
                }

                try {

                    URL url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        myTrailersJsonSTR = null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }

                    myTrailersJsonSTR = buffer.toString();

                    Log.v(LOG_TAG, "Trailers JSON String: " + myTrailersJsonSTR);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error here Exactly ", e);

                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    return getTrailersDataFromJson(myTrailersJsonSTR);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "didn't get Movies Data from getTrailersDataFromJson method", e);
                    e.printStackTrace();
                }
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieEntity> result) {
            if (result != null && getActivity() != null) {
                trailersAdapter = new TrailersAdapter(getActivity(), R.layout.trailer_list_item, result);
                listView_trailers.setAdapter(trailersAdapter);
            }

        }
    }

    class ReviewsAsync extends AsyncTask<String, Void, ArrayList<MovieEntity>> {
        private final String LOG_TAG = ReviewsAsync.class.getSimpleName();
        public JSONObject moviesReviews;
        public JSONArray moviesReviewsArray;
        public JSONObject oneReviewsData;
        public String main_List = "results";

        private ArrayList<MovieEntity> getReviewsDataFromJson(String MoviesJsonStr)
                throws JSONException {

            moviesReviews = new JSONObject(MoviesJsonStr);
            moviesReviewsArray = moviesReviews.getJSONArray(main_List);
            ReviewsList.clear();
            Log.v("jsonArraySize", Integer.toString(moviesReviewsArray.length()));
            for (int i = 0; i < moviesReviewsArray.length(); i++) {
                String AUTHOR_STRING, CONTENT_STRING;
                String AUTHOR = "author", CONTENT = "content";
                // Get the JSON object representing a movie per each loop
                oneReviewsData = moviesReviewsArray.getJSONObject(i);
                AUTHOR_STRING = oneReviewsData.getString(AUTHOR);
                Log.v("reviewData", AUTHOR_STRING);
                CONTENT_STRING = oneReviewsData.getString(CONTENT);
                Log.v("reviewData", CONTENT_STRING);
                MovieEntity entity = new MovieEntity(AUTHOR_STRING, CONTENT_STRING);
                ReviewsList.add(entity);
            }
            Log.v("auther_name", ReviewsList.get(0).AUTHOR_STRING);
            Log.v("ArraySize", Integer.toString(ReviewsList.size()));
            return ReviewsList;
        }

        @Override
        protected ArrayList<MovieEntity> doInBackground(String... params) {

            try {
                String myReviewsJsonSTR = null;

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                if (params.length == 0) {
                    return null;
                }

                try {

                    URL url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        myReviewsJsonSTR = null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }

                    myReviewsJsonSTR = buffer.toString();

                    Log.v(LOG_TAG, "Reviews JSON String: " + myReviewsJsonSTR);
                    return getReviewsDataFromJson(myReviewsJsonSTR);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error here Exactly ", e);

                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieEntity> result) {
            if (result != null && result.size() > 0) {
                text_author.setText("");
                for (MovieEntity mv : result) {
                    text_author.append("Author Name : \n" + mv.getAUTHOR_STRING().toString() + "\n\n" + "Review : \n" + mv.getCONTENT_STRING().toString() + "\n\n\n**\n\n\n");
                }
            } else text_author.setText("No Reviews");
        }
    }
}