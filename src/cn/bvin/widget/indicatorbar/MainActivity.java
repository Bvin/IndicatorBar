package cn.bvin.widget.indicatorbar;

import cn.bvin.widget.indicatorbar.IndicatorBar.OnIndicatorChangeListener;
import cn.bvin.widget.seekbarindicator.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final IndicatorBar ib = (IndicatorBar) findViewById(R.id.indicatorBar1);
        ib.setOnIndicatorChangeListener(new OnIndicatorChangeListener() {
            
            @Override
            public void onIndicatorChanged(IndicatorBar indicatorBar, int position, float xAtPosition) {
                Log.i("position"+position, "xAtPosition"+xAtPosition);
            }
        });
        ib.setHightlightIndicators(new int[]{3,4,5});
        SeekBar sb = (SeekBar) findViewById(R.id.seekBar1);
        sb.setMax(20);
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                ib.setMaxIndicator(progress);
                ib.invalidate();
            }
        });
        ToggleButton bt = (ToggleButton) findViewById(R.id.toggleButton1);
        bt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ib.setShowTicks(isChecked);
                ib.invalidate();
            }
        });
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
