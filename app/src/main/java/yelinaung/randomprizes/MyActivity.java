package yelinaung.randomprizes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MyActivity extends Activity {

  @InjectView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
  @InjectView(R.id.people) ListView peopleList;
  @InjectView(R.id.progress) ProgressBar progressBar;

  private String[] peopleArray;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my);

    ButterKnife.inject(this);

    peopleArray = getResources().getStringArray(R.array.people_array);
    List<String> mPeople = Arrays.asList(peopleArray);
    ArrayAdapter<String> mPeopleAdapter =
        new ArrayAdapter<String>(this, R.layout.list_item, mPeople);
    peopleList.setAdapter(mPeopleAdapter);

    peopleList.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        int topRowVerticalPosition = (peopleList == null || peopleList.getChildCount() == 0) ? 0
            : peopleList.getChildAt(0).getTop();
        swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
      }
    });

    swipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_color1,
        R.color.swipe_refresh_color2, R.color.swipe_refresh_color3, R.color.swipe_refresh_color4);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        new RandomAsync().execute();
      }
    });
  }

  public class RandomAsync extends AsyncTask<Void, Void, String> {

    private void doFakeWork() {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
      swipeRefreshLayout.setRefreshing(true);
      progressBar.setVisibility(View.VISIBLE);
      peopleList.setVisibility(View.GONE);
    }

    @Override protected String doInBackground(Void... params) {

      doFakeWork();
      int idx = new Random().nextInt(peopleArray.length);
      String random = (peopleArray[idx]);
      swipeRefreshLayout.setRefreshing(true);
      Log.i("random", random);
      return random;
    }

    @Override protected void onPostExecute(String s) {
      super.onPostExecute(s);
      Log.i("s", s);
      progressBar.setVisibility(View.GONE);
      peopleList.setVisibility(View.VISIBLE);
      AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
      builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
        }
      }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          // User cancelled the dialog
          dialog.dismiss();
        }
      });
      builder.setMessage("The lucky one is : " + s);
      AlertDialog dialog = builder.create();
      dialog.show();
      swipeRefreshLayout.setRefreshing(false);
    }
  }
}
