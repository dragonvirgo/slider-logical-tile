package shima.android;

import android.app.Activity;
import android.os.Bundle;

public class TileTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        Tiles tiles = new Tiles(4,4);
        tiles.print();
        tiles.shuffle();
    }
}