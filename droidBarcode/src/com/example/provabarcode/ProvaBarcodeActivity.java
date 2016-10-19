package com.example.provabarcode;

//import codicefiscale.main.R;
import android.app.*;
import android.content.pm.ActivityInfo;
import android.graphics.*;
import android.os.*;
import android.view.*;

public class ProvaBarcodeActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        Barcode codice = new Barcode("SPDSFN87C14F258X", Barcode.Type.Code39);
        
        BarcodeView tv = new BarcodeView(this, codice, BarcodeView.SizeHint.PixelSafe);
        //BarcodeView tv = new BarcodeView(this, codice, BarcodeView.SizeHint.Bound);
        setContentView(tv);
    }
}