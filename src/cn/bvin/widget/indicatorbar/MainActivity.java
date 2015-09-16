package cn.bvin.widget.indicatorbar;

import cn.bvin.widget.indicatorbar.IndicatorBar.OnIndicatorChangeListener;
import cn.bvin.widget.seekbarindicator.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IndicatorBar ib = (IndicatorBar) findViewById(R.id.indicatorBar1);
        ib.setOnIndicatorChangeListener(new OnIndicatorChangeListener() {
            
            @Override
            public void onIndicatorChanged(IndicatorBar indicatorBar, int position, float xAtPosition) {
                Log.i("position"+position, "xAtPosition"+xAtPosition);
            }
        });
        ib.setHightlightIndicators(new int[]{3,4,5});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
