package com.example.provabarcode;

import android.util.Log;
import android.view.*;
import android.content.*;
import android.graphics.*;

public class BarcodeView extends View
{
	public static enum SizeHint{ PixelSafe, Bound };
	private Barcode codice;
	private SizeHint sh;
	
	public BarcodeView(Context context,Barcode codice, SizeHint sh)
	{
		super(context);
		this.codice = codice;
		this.sh = sh;
	}
	
	public BarcodeView(Context context,Barcode codice)
	{
		this(context, codice, SizeHint.Bound);
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		canvas.drawColor(Color.WHITE);
		
		switch(sh)
		{
			case Bound:
			{
				//codice scalato: riempie per intero l'area dello schermo
				//canvas.drawBitmap(codice.toScaledBitmap(canvas.getWidth(), canvas.getHeight()/2), new Matrix(), null);
				
				Bitmap img = codice.toScaledBitmap(canvas.getWidth(), canvas.getHeight()*2/3);
				int x = (canvas.getWidth() - img.getWidth())/2;
				int y = (canvas.getHeight() - img.getHeight())/2;
				
				//Log.d("Dim e Off", "x=" + x + " y=" + y + " iw=" + img.getWidth() + " ih=" + img.getHeight() + " L=" + canvas.getWidth() + " H=" + canvas.getHeight());
				
				canvas.drawBitmap(img, x, y, null);
			} break;
			case PixelSafe:
			{
				//codice non scalato centrato
				Bitmap img = codice.toBitmap(canvas.getWidth(), canvas.getHeight()*2/3);
				int x = (canvas.getWidth() - img.getWidth())/2;
				int y = (canvas.getHeight() - img.getHeight())/2;
				
				//Log.d("Dim e Off", "x=" + x + " y=" + y + " iw=" + img.getWidth() + " ih=" + img.getHeight() + " L=" + canvas.getWidth() + " H=" + canvas.getHeight());
				
				canvas.drawBitmap(img, x, y, null);
			} break;
		}		
	}
}
