package com.example.provabarcode;

import 	android.graphics.*;

public class Barcode
{
	public static enum Type { Code39 }; //set di barcode supportati
		
	private Type type;
	private String stringa;
	
	public Barcode(String stringa, Type type)
	{
		this.stringa = stringa;
		this.type = type;
	}

	//crea un bitmap delle dimensioni indicate del codice a barre: dimensioni non mandatorie
	//dalle dimensioni calcoliamo la divisione intera per il calcolo dell'unità di barra
	public Bitmap toBitmap(int width, int height)
	{
		switch(type)
		{
			case Code39:
			{
				BarcodeCode39 b = new BarcodeCode39(stringa);
				b.setNarrowPixelSize(1); //b viene usata per trovare la dimensione minima del codice
				b.setSpaceToNarrowRatio(2);
				b.setWideToNarrowRatio(3);
				if(width/b.toBitMap().length<1) throw new IllegalArgumentException("Dimensione schermo troppo bassa!");
				b.setNarrowPixelSize( width/b.toBitMap().length ); //reimposto la dimensione base con la divisione intera
				boolean[] bitmap = b.toBitMap();
				
				int[] colormap = new int[bitmap.length]; //array per i colori dei pixel
				for(int i=0; i< bitmap.length; i++) colormap[i] = bitmap[i] ? Color.WHITE : Color.BLACK;
				
				Bitmap image = Bitmap.createBitmap(colormap.length, height, Bitmap.Config.ARGB_8888);
				//image è un bitmap vuoto che rispetta le dimensioni naturali del codice-array
				
				for(int i=0; i<colormap.length; i++) 
					for(int k=0; k<height; k++) image.setPixel(i, k, colormap[i]);
				
				return image;
			}
		}
		throw new IllegalStateException("Illegal position reached! switch-case failed!");
	}
	
	//stavolta le dimensioni sono mandatorie e l'immagine viene scalata
	public Bitmap toScaledBitmap(int width, int height)
	{
		switch(type)
		{
			case Code39:
			{
				BarcodeCode39 b = new BarcodeCode39(stringa);
				b.setNarrowPixelSize(1); //impostiamo la dimensione base a 1 perchè il ridimensionamento penserà al resto
				b.setSpaceToNarrowRatio(2);
				b.setWideToNarrowRatio(3);
				boolean [] bitmap = b.toBitMap();
				
				int[] colormap = new int[bitmap.length]; //array per i colori dei pixel
				for(int i=0; i< bitmap.length; i++) colormap[i] = bitmap[i] ? Color.WHITE : Color.BLACK;
				
				return Bitmap.createScaledBitmap(Bitmap.createBitmap(colormap, bitmap.length, 1, Bitmap.Config.ARGB_8888), width, height, false);
			}
		}
		throw new IllegalStateException("Illegal position reached! switch-case failed!");
	}
}