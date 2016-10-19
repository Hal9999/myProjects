package com.example.provabarcode;

import java.util.*;

public class BarcodeCode39
{
	private static HashMap<Character, String> code;
	static
	{
		code = new HashMap<Character, String>();
		code.put('0', "bwbWBwBwb");
		code.put('1', "BwbWbwbwB");
		code.put('2', "bwBWbwbwB");
		code.put('3', "BwBWbwbwb");
		code.put('4', "bwbWBwbwB");
		code.put('5', "BwbWBwbwb");
		code.put('6', "bwBWBwbwb");
		code.put('7', "bwbWbwBwB");
		code.put('8', "BwbWbwBwb");
		code.put('9', "bwBWbwBwb");
		code.put('A', "BwbwbWbwB");
		code.put('B', "bwBwbWbwB");
		code.put('C', "BwBwbWbwb");
		code.put('D', "bwbwBWbwB");
		code.put('E', "BwbwBWbwb");
		code.put('F', "bwBwBWbwb");
		code.put('G', "bwbwbWBwB");
		code.put('H', "BwbwbWBwb");
		code.put('I', "bwBwbWBwb");
		code.put('J', "bwbwBWBwb");
		code.put('K', "BwbwbwbWB");
		code.put('L', "bwBwbwbWB");
		code.put('M', "BwBwbwbWb");
		code.put('N', "bwbwBwbWB");
		code.put('O', "BwbwBwbWb");
		code.put('P', "bwBwBwbWb");
		code.put('Q', "bwbwbwBWB");
		code.put('R', "BwbwbwBWb");
		code.put('S', "bwBwbwBWb");
		code.put('T', "bwbwBwBWb");
		code.put('U', "BWbwbwbwB");
		code.put('V', "bWBwbwbwB");
		code.put('W', "BWBwbwbwb");
		code.put('X', "bWbwBwbwB");
		code.put('Y', "BWbwBwbwb");
		code.put('Z', "bWBwBwbwb");
		code.put('-', "bWbwbwBwB");
		code.put('.', "BWbwbwBwb");
		code.put(' ', "bWBwbwBwb");
		code.put('$', "bWbWbWbwb");
		code.put('/', "bWbWbwbWb");
		code.put('+', "bWbwbWbWb");
		code.put('%', "bwbWbWbWb");
		code.put('*', "bWbwBwBwb");
	}
	
	private String text;
	private int wideToNarrowRatio, spaceToNarrowRatio, narrowPixelSize; 
	
	public BarcodeCode39(String text)
	{
		this.text='*'+text+'*';
		
		//default WideToNarrow and SpaceToNarrow ratios
		this.wideToNarrowRatio = 3;
		this.spaceToNarrowRatio = 2;
		this.narrowPixelSize = 1;
	}
	
	public void setWideToNarrowRatio(int r)
	{
		this.wideToNarrowRatio=r;
	}
	
	public void setSpaceToNarrowRatio(int r)
	{
		this.spaceToNarrowRatio=r;
	}
	
	public void setNarrowPixelSize(int s)
	{
		this.narrowPixelSize=s;
	}
	
	public boolean[] toBitMap() throws IllegalArgumentException
	{
		//postcode contiene '|' per indicare spazi intersimbolo e i codici BbWw
		StringBuffer postcode = new StringBuffer();
		postcode.append("|");
		for(char c: text.toCharArray()) postcode.append(code.get(c)).append("|");

		//true bianco, false nero
		ArrayList<Boolean> coded = new ArrayList<Boolean>();
		for(char c: postcode.toString().toCharArray())
		{
			switch(c)
			{
				case 'W': { for(int i=0; i<narrowPixelSize*wideToNarrowRatio; i++) coded.add(true); } break;
				case 'B': { for(int i=0; i<narrowPixelSize*wideToNarrowRatio; i++) coded.add(false); } break;
				case 'w': { for(int i=0; i<narrowPixelSize; i++) coded.add(true); } break;
				case 'b': { for(int i=0; i<narrowPixelSize; i++) coded.add(false); } break;
				case '|': { for(int i=0; i<narrowPixelSize*spaceToNarrowRatio; i++) coded.add(true); } break;
				default:  { throw new IllegalStateException("Illegal position reached! switch-case failed! => " + c); }
			}
		}

		boolean[] bitmap = new boolean[coded.size()];
		int i=0;
		for(boolean b : coded) bitmap[i++]=b;
		return bitmap;
	}
}