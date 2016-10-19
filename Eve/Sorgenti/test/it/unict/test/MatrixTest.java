package it.unict.test;

import it.unict.eve.Matrix;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class MatrixTest
{
	private double[][] array = new double[][]
	{ {    2,  4,  5,   7,  3},
	  {    3,  2,  4,   5,  2},
	  {  5.0, 21, 65, 908, 56},
	  {58.98, 78,  6,   3,  5} };
	private Matrix m;
	
	@Before
	public void setUp() throws Exception
	{
		m = new Matrix(array);
	}
	
	@Test
	public void testMatrixFile() throws IOException
	{
		
		LinkedList<File> fileList = new LinkedList<File>();
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matrix.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_treated.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_UNtreated.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DATA1_medie.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DataLZ.txt") );
		
		for(File file : fileList)
		{
			long time = System.currentTimeMillis();
			Matrix matrix = new Matrix( file );
			double elapsed = (System.currentTimeMillis() - time)/(float)1000;

			//System.out.println( matrix );
			System.out.println("Matrix from file: " + file.getName());
			System.out.println("rows = " + matrix.nRows() + " columns = " + matrix.nCols());
			System.out.println("took " + elapsed + " s to load");
			
			time = System.currentTimeMillis();
			matrix = matrix.transpose();
			elapsed = (System.currentTimeMillis() - time)/(float)1000;
			System.out.println("and " + elapsed + " s to transpose");
			System.out.println();
		}
	}
	
	@Test
	public void testToFile() throws IOException
	{
		
		LinkedList<File> fileList = new LinkedList<File>();
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matrix.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_treated.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_UNtreated.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DATA1_medie.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DataLZ.txt") );
		
		for(File file : fileList)
		{
			long time = System.currentTimeMillis();
			Matrix matrix = new Matrix( file );
			double elapsed1 = (System.currentTimeMillis() - time)/(float)1000;
			
			time = System.currentTimeMillis();
			matrix = matrix.transpose();
			double elapsed2 = (System.currentTimeMillis() - time)/(float)1000;

			matrix = matrix.transpose();
			time = System.currentTimeMillis();
			matrix.toFile(new File(file.getAbsolutePath() + "2.txt"));
			double elapsed3 = (System.currentTimeMillis() - time)/(float)1000;

			//System.out.println( matrix );
			System.out.println("Matrix from file: " + file.getName());
			System.out.println("rows = " + matrix.nRows() + " columns = " + matrix.nCols());
			System.out.println("took " + elapsed1 + " s to load");
			System.out.println("took " + elapsed2 + " s to transpose");
			System.out.println("took " + elapsed3 + " s to save");
			System.out.println();
		}
	}
	
	@Test
	public void testToString()
	{
		System.out.println("---------- testToString >>>>>>>>>>");

		String s = m.toString();
		String expected = new String("2.0\t4.0\t5.0\t7.0\t3.0\n3.0\t2.0\t4.0\t5.0\t2.0\n5.0\t21.0\t65.0\t908.0\t56.0\n58.98\t78.0\t6.0\t3.0\t5.0");
		
		Assert.assertEquals(expected, s);
		
		System.out.println("\"" + s + "\"");

		System.out.println("<<<<<<<<<< testToString ----------");
	}

	@Test
	public void testTranspose()
	{
		System.out.println("---------- testTranspose >>>>>>>>>>");

		Matrix expected = new Matrix( new double[][] { {2, 3, 5.0, 58.98},
													   {4, 2, 21, 78},
													   {5, 4, 65, 6},
													   {7, 5, 908, 3},
													   {3, 2, 56, 5} } );
		Assert.assertTrue( expected.equals(m.transpose()) );
		System.out.println( m.transpose() );
		
		System.out.println("<<<<<<<<<< testTranspose ----------");
	}

	@Test
	public void testMatrixDoubleArrayArray()
	{
		Matrix ma = new Matrix(array);
		Assert.assertEquals(array.length, ma.nRows());
		Assert.assertEquals(array[0].length, ma.nCols());
		
		for(int i=0; i<ma.nRows(); i++)
			for(int j=0; j<ma.nCols(); j++)
				Assert.assertEquals(array[i][j], ma.get(i, j));
		
		try
		{
			double[][] array2 = new double[][]
				{ {    2,  4,  5,   7,  3},
				  {    3,  2,  4,   2},
				  {  5.0, 21, 65, 908, 56},
				  {58.98, 78,  6,   3,  5} };
			new Matrix(array2);
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e) {}
	}

	@Test
	public void testNewFromMatrixArray()
	{
		System.out.println("---------- testNewFromMatrixArray >>>>>>>>>>");
		
		double[][] arr1 = new double[][] { { 1,  2,  3},
										   { 2,  3,  4} };
		double[][] arr2 = new double[][] { { 1,  2,  8} };
		double[][] arr3 = new double[][] { { 1,  7,  3},
										   { 2,  3, 12},  
										   {15, 18, 91} };
		
		Matrix m1 = new Matrix(arr1);
		Matrix m2 = new Matrix(arr2);
		Matrix m3 = new Matrix(arr3);
		Matrix[] mA = new Matrix[] {m1, m2, m3};
		
		Matrix cM = Matrix.newFromMatrixArray(mA, Matrix.AppendMode.RowMode);
		Matrix cMv = new Matrix( new double[][]
									{ {1, 2, 3},
				  					  {2, 3, 4},
				  					  {1, 2, 8},
				  					  {1, 7, 3},
					  				  {2, 3, 12},  
					  				  {15, 18 ,91} } );
		Assert.assertEquals(cMv.nCols(), cM.nCols());
		Assert.assertEquals(cMv.nRows(), cM.nRows());
		Assert.assertTrue(cMv.isIsoDimensional(cM));
		Assert.assertTrue(cMv.equals(cM));
		
		Matrix rM = Matrix.newFromMatrixArray(mA, Matrix.AppendMode.ColumnMode);
		Assert.assertEquals(cMv.nRows(), rM.nCols());
		Assert.assertEquals(cMv.nCols(), rM.nRows());
		Assert.assertTrue(cMv.transpose().equals(rM));
		
		System.out.println("C\n" + cM + "\n");
		System.out.println("R\n" + rM + "\n");
		
		System.out.println("<<<<<<<<<< testNewFromMatrixArray ----------");
	}

	@Test
	public void testMatrixDoubleArrayMatrixType()
	{
		System.out.println("---------- testMatrixDoubleArrayMatrixType >>>>>>>>>>");

		double[] a = new double[] { 1, 2, 7, 89, 5, 78, 1.0, 5, 9.62 };
		Matrix r = new Matrix(a, Matrix.MatrixType.ColumnMatrix);
		Matrix c = new Matrix(a, Matrix.MatrixType.RowMatrix);
		
		Assert.assertTrue( r.transpose().equals(c) );
		Assert.assertTrue(new Matrix(new double[][]{{ 1, 2, 7, 89, 5, 78, 1.0, 5, 9.62 }}).equals(c));
		Assert.assertTrue(new Matrix(new double[][]{{ 1, 2, 7, 89, 5, 78, 1.0, 5, 9.62 }}).transpose().equals(r));
		
		System.out.println("C\n" + c);
		System.out.println("R\n" + r);

		System.out.println("<<<<<<<<<< testMatrixDoubleArrayMatrixType ----------");
	}

	@Test
	public void testMatrixIntInt()
	{
		System.out.println("---------- testMatrixIntInt >>>>>>>>>>");

		Matrix m = new Matrix(8, 9);
		for (int i = 0; i < m.nRows(); i++)
			for (int j = 0; j < m.nCols(); j++)
				m.set(i, j, 1);
		Assert.assertEquals(8, m.getBackingArray().length);
		Assert.assertEquals(9, m.getBackingArray()[0].length);
		
		System.out.println(m);

		System.out.println("<<<<<<<<<< testMatrixIntInt ----------");
	}

	@Test
	public void testMatrixInt()
	{
		System.out.println("---------- testMatrixInt >>>>>>>>>>");

		Matrix m = new Matrix(5);
		for (int i = 0; i < m.nRows(); i++)
			for (int j = 0; j < m.nCols(); j++)
				m.set(i, j, 1);
		Assert.assertEquals(5, m.getBackingArray().length);
		Assert.assertEquals(5, m.getBackingArray()[0].length);
		System.out.println(m);

		System.out.println("<<<<<<<<<< testMatrixInt ----------");
	}

	@Test
	public void testNewLikeThis()
	{
		System.out.println("---------- testNewLikeThis >>>>>>>>>>");

		Matrix m2 = m.newLikeThis();
		
		Assert.assertEquals(m.nCols(), m2.nCols());
		Assert.assertEquals(m.nRows(), m2.nRows());
		System.out.println("M\n" + m);
		System.out.println("M2\n" + m2);

		System.out.println("<<<<<<<<<< testNewLikeThis ----------");
	}

	@Test
	public void testFillDouble()
	{
		System.out.println("---------- testFillDouble >>>>>>>>>>");

		Matrix filledM = m.newLikeThis().fill(8);
		
		double[][] array8 = new double[][]
				{ {8, 8, 8, 8, 8},
				  {8, 8, 8, 8, 8},
				  {8, 8, 8, 8, 8},
				  {8, 8, 8, 8, 8} };
		 
		Assert.assertTrue(new Matrix(array8).equals(filledM));
		System.out.println("m\n" + m);
		System.out.println("Fm\n" + filledM);

		System.out.println("<<<<<<<<<< testFillDouble ----------");
	}

	@Test
	public void testFillRandom()
	{
		System.out.println("---------- testFillRandom >>>>>>>>>>");
		
		Matrix m8 = m.newLikeThis().fill(1.0);
		Matrix filledRM = m.newLikeThis().fill(new Random());
		
		System.out.println("M8\n" + m8);
		System.out.println("MR\n" + filledRM);
		
		for(int i=0; i<filledRM.nRows(); i++) //sfruttiamo il fatto che l'oggetto Random genera numeri [-1, 1[
			for(int j=0; j<filledRM.nCols(); j++)
			{
				Assert.assertTrue(filledRM.get(i, j) < m8.get(i, j));
				Assert.assertTrue(filledRM.get(i, j) >= -1);
			}
		
		System.out.println("<<<<<<<<<< testFillRandom ----------");
	}

	@Test
	public void testNRowsNCols()
	{
		System.out.println("---------- testNRows >>>>>>>>>>");

		Assert.assertEquals(array.length, m.nRows());
		Assert.assertEquals(array[0].length, m.nCols());

		System.out.println("<<<<<<<<<< testNRows ----------");
	}

	@Test
	public void testGet()
	{
		System.out.println("---------- testGet >>>>>>>>>>");

		try
		{
			m.get(m.nRows(), 0);
			Assert.fail("Exception not thrown");
		} catch (ArrayIndexOutOfBoundsException e){}
		try
		{
			m.get(0, m.nCols());
			Assert.fail("Exception not thrown");
		} catch (ArrayIndexOutOfBoundsException e){}
		try
		{
			m.get(m.nRows(), m.nCols());
			Assert.fail("Exception not thrown");
		} catch (ArrayIndexOutOfBoundsException e){}

		System.out.println("<<<<<<<<<< testGet ----------");
	}

	@Test
	public void testSet()
	{
		System.out.println("---------- testSet >>>>>>>>>>");

		try
		{
			m.set(m.nRows(), 0, 8);
			Assert.fail("Exception not thrown");
		} catch (ArrayIndexOutOfBoundsException e){}
		try
		{
			m.set(0, m.nCols(), 8);
			Assert.fail("Exception not thrown");
		} catch (ArrayIndexOutOfBoundsException e){}
		try
		{
			m.set(m.nRows(), m.nCols(), 8);
			Assert.fail("Exception not thrown");
		} catch (ArrayIndexOutOfBoundsException e){}

		System.out.println("<<<<<<<<<< testSet ----------");
	}

	@Test
	public void testGetColumnMatrixInt()
	{
		System.out.println("---------- testGetColumnMatrixInt >>>>>>>>>>");

		Matrix mc2 = m.getColumnMatrix(2);
		Matrix mcv = new Matrix(new double[]{5, 4, 65, 6}, Matrix.MatrixType.ColumnMatrix);
		Assert.assertTrue(mc2.equals(mcv));
		System.out.println("mc\n" + mc2);
		System.out.println("mcv\n" + mcv);
		
		try
		{
			m.getColumnMatrix(m.nCols());
			Assert.fail("Exception not thrown");
		}
		catch(ArrayIndexOutOfBoundsException e){}

		System.out.println("<<<<<<<<<< testGetColumnMatrixInt ----------");
	}

	@Test
	public void testGetRowMatrixInt()
	{
		System.out.println("---------- testGetRowMatrixInt >>>>>>>>>>");

		Matrix mr3 = m.getRowMatrix(3);
		Matrix mrv = new Matrix(new double[]{58.98, 78, 6, 3, 5}, Matrix.MatrixType.RowMatrix);
		System.out.println("mr3\n" + mr3);
		System.out.println("mrv\n" + mrv);
		Assert.assertTrue(mr3.equals(mrv));

		try
		{
			m.getRowMatrix(m.nRows());
			Assert.fail("Exception not thrown");
		}
		catch(ArrayIndexOutOfBoundsException e){}

		System.out.println("<<<<<<<<<< testGetRowMatrixInt ----------");
	}

	@Test
	public void testGetColumnMatrix()
	{
		System.out.println("---------- testGetColumnMatrix >>>>>>>>>>");

		try
		{
			m.getColumnMatrix();
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e){}
		
		Matrix m1 = new Matrix(new double[][]{{1}, {2}, {3}, {4}, {5}, {6}});
		Matrix m2 = m1.getColumnMatrix();
		
		Assert.assertFalse(m1 == m2);
		Assert.assertTrue(m1.equals(m2));
		Assert.assertEquals(m1.nCols(), 1);
		Assert.assertEquals(m2.nRows(), 6);
		Assert.assertTrue(m1.isIsoDimensional(m2));
		System.out.println("m1\n" + m1);
		System.out.println("m2\n" + m2);

		System.out.println("<<<<<<<<<< testGetColumnMatrix ----------");
	}

	@Test
	public void testGetRowMatrix()
	{
		System.out.println("---------- testGetRowMatrix >>>>>>>>>>");

		try
		{
			m.getRowMatrix();
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e){}
		
		Matrix m1 = new Matrix(new double[][]{{1, 2, 3, 4, 5, 6}});
		Matrix m2 = m1.getRowMatrix();
		
		Assert.assertFalse(m1 == m2);
		Assert.assertTrue(m1.equals(m2));
		Assert.assertEquals(m1.nCols(), 6);
		Assert.assertEquals(m2.nRows(), 1);
		Assert.assertTrue(m1.isIsoDimensional(m2));
		System.out.println("m1\n" + m1);
		System.out.println("m2\n" + m2);

		System.out.println("<<<<<<<<<< testGetRowMatrix ----------");
	}

	@Test
	public void testGetColumnInt()
	{
		System.out.println("---------- testGetColumnInt >>>>>>>>>>");

		double[] ac2 = m.getColumn(2);
		double[] acv = new double[]{5, 4, 65, 6};
		System.out.println("ac2\n" + new Matrix(new double[][]{ac2}));
		System.out.println("acv\n" + new Matrix(new double[][]{acv}));
		for(int i=0; i<ac2.length; i++) Assert.assertEquals(ac2[i], acv[i]);
		for(int i=0; i<acv.length; i++) Assert.assertEquals(acv[i], ac2[i]);
		Assert.assertTrue(ac2.length == acv.length);
		
		try
		{
			m.getColumn(m.nCols());
			Assert.fail("Exception not thrown");
		}
		catch(ArrayIndexOutOfBoundsException e){}
		
		System.out.println("<<<<<<<<<< testGetColumnInt ----------");
	}

	@Test
	public void testGetRowInt()
	{
		System.out.println("---------- testGetRowInt >>>>>>>>>>");

		double[] ar2 = m.getRow(2);
		double[] arv = new double[]{5.0, 21, 65, 908, 56};
		System.out.println("ar2\n" + new Matrix(new double[][]{ar2}));
		System.out.println("arv\n" + new Matrix(new double[][]{arv}));
		for(int i=0; i<ar2.length; i++) Assert.assertEquals(ar2[i], arv[i]);
		for(int i=0; i<arv.length; i++) Assert.assertEquals(arv[i], ar2[i]);
		Assert.assertTrue(ar2.length == arv.length);
		
		try
		{
			m.getRow(m.nRows());
			Assert.fail("Exception not thrown");
		}
		catch(ArrayIndexOutOfBoundsException e){}

		System.out.println("<<<<<<<<<< testGetRowInt ----------");
	}

	@Test
	public void testGetColumn()
	{
		System.out.println("---------- testGetColumn >>>>>>>>>>");

		try
		{
			m.getColumn();
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e){}
		
		Matrix m1 = new Matrix(new double[][]{{1}, {2}, {3}, {4}, {5}, {6}});
		double[] a1 = m1.getColumn();
		double[] a2 = new double[]{1, 2, 3, 4, 5, 6};
		
		System.out.println("a1");
		for(int i=0; i<a1.length; i++)
		{
			Assert.assertEquals(a1[i], a2[i]);
			System.out.print(a1[i] + "\t");
		}
		System.out.println();
		
		System.out.println("a2");
		for(int i=0; i<a2.length; i++)
		{
			Assert.assertEquals(a2[i], a1[i]);
			System.out.print(a2[i] + "\t");
		}
		System.out.println();
		
		Assert.assertTrue(a1.length == a2.length);
		Assert.assertFalse(a1 == a2);

		System.out.println("<<<<<<<<<< testGetColumn ----------");
	}

	@Test
	public void testGetRow()
	{
		System.out.println("---------- testGetRow >>>>>>>>>>");

		try
		{
			m.getRow();
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e){}
		
		Matrix m1 = new Matrix(new double[][]{ {1, 2, 3, 4, 5, 6} });
		double[] b1 = m1.getRow();
		double[] b2 = new double[]{1, 2, 3, 4, 5, 6};
		
		System.out.println("b1");
		for(int i=0; i<b1.length; i++)
		{
			Assert.assertEquals(b1[i], b2[i]);
			System.out.print(b1[i] + "\t");
		}
		System.out.println();
		
		System.out.println("b2");
		for(int i=0; i<b2.length; i++)
		{
			Assert.assertEquals(b2[i], b1[i]);
			System.out.print(b2[i] + "\t");
		}
		System.out.println();
		
		Assert.assertTrue(b1.length == b2.length);
		Assert.assertFalse(b1 == b2);

		System.out.println("<<<<<<<<<< testGetRow ----------");
	}

	@Test
	public void testGetBackingArrayCopy()
	{
		System.out.println("---------- testGetBackingArrayCopy >>>>>>>>>>");
		
		double[][] arrayCopy = m.getBackingArrayCopy();
		
		for(int i=0; i<m.nRows(); i++)
			for(int j=0; j<m.nCols(); j++)
				Assert.assertEquals(array[i][j], arrayCopy[i][j]);

		System.out.println("<<<<<<<<<< testGetBackingArrayCopy ----------");
	}

	@Test
	public void testMaximum()
	{
		System.out.println("---------- testMaximum >>>>>>>>>>");

		System.out.println("M\n" + m);
		System.out.println("max = " + m.maximum());
		Assert.assertEquals(908.0, m.maximum());

		System.out.println("<<<<<<<<<< testMaximum ----------");
	}

	@Test
	public void testMinimum()
	{
		System.out.println("---------- testMinimum >>>>>>>>>>");

		System.out.println("M\n" + m);
		System.out.println("min = " + m.minimum());
		Assert.assertEquals(2.0, m.minimum());

		System.out.println("<<<<<<<<<< testMinimum ----------");
	}

	@Test
	public void testAbs()
	{
		System.out.println("---------- testAbs >>>>>>>>>>");

		Matrix m = new Matrix( new double[][]
		{ {     2,  4, -5,    7,  3},
		  {     3,  2,  4,    5, -2},
		  {   5.0, 21, 65, -908, 56},
		  {-58.98, 78, -6,    3,  5} } );
		
		Matrix aabs = new Matrix( new double[][]
		{ {     2,  4,  5,    7,  3},
		  {     3,  2,  4,    5,  2},
		  {   5.0, 21, 65,  908, 56},
		  { 58.98, 78,  6,    3,  5} } );
		
		System.out.println("a.abs()\n" + m.abs());
		System.out.println("aabs\n" + aabs);
		Assert.assertTrue(aabs.equals( m.abs() ));

		System.out.println("<<<<<<<<<< testAbs ----------");
	}

	@Test
	public void testPlusMatrix()
	{
		System.out.println("---------- testPlusMatrix >>>>>>>>>>");
		
		Matrix a = new Matrix( new double[][]
		{ {     2,  4, -5,    7,  3},
		  {     3,  2,  4,    5, -2},
		  {   5.0, 21, 65, -908, 56},
		  {-58.98, 78, -6,    3,  5} } );
		
		Matrix b = new Matrix( new double[][]
		{ {     2,   4,  5,    7,  3},
		  {    93,   2,  4,    5,  2},
		  {   5.0,  21, 65,  918, 56},
		  { 58.78, -58, 66,   13,  5} } );
		
		Matrix s = new Matrix( new double[][]
		{ {     4,   8,   0,   14,   6},
		  {    96,   4,   8,   10,   0},
		  {  10.0,  42, 130,   10, 112},
		  {  -0.2,  20,  60,   16,  10} } );
		
		System.out.println("a\n" + a);
		System.out.println("b\n" + b);
		System.out.println("a+b\n" + a.plus(b));
		Assert.assertTrue( a.plus(b).setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(s) );
		//con errore di un miliardesimo
		
		System.out.println("<<<<<<<<<< testPlusMatrix ----------");
	}

	@Test
	public void testPlusDouble()
	{
		System.out.println("---------- testPlusDouble >>>>>>>>>>");
		
		Matrix a = new Matrix( new double[][]
		{ {     2,  4, -5,    7,  3},
		  {     3,  2,  4,    5, -2},
		  {   5.0, 21, 65, -908, 56},
		  {-58.98, 78, -6,    3,  5} } );
		
		double b = 10;
		
		Matrix s = new Matrix( new double[][]
		{ {    12, 14,  5,   17, 13},
		  {    13, 12, 14,   15,  8},
		  {  15.0, 31, 75, -898, 66},
		  {-48.98, 88,  4,   13, 15} } );
		
		System.out.println("a\n" + a);
		System.out.println("b\n" + b);
		System.out.println("a+b\n" + a.plus(b));
		Assert.assertTrue( a.plus(b).setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(s) );
		//con errore di un miliardesimo
		
		System.out.println("<<<<<<<<<< testPlusDouble ----------");
	}

	@Test
	public void testMinusMatrix()
	{
		System.out.println("---------- testMinusMatrix >>>>>>>>>>");
		
		Matrix a = new Matrix( new double[][]
		{ {     2,  4, -5,    7,  3},
		  {     3,  2,  4,    5, -2},
		  {   5.0, 21, 65, -908, 56},
		  {-58.98, 78, -6,    3,  5} } );
		
		Matrix b = new Matrix( new double[][]
		{ {     2,   4,  5,    7,  3},
		  {    93,   2,  4,    5,  2},
		  {   5.0,  21, 65,  918, 56},
		  { 58.78, -58, 66,   13,  5} } );
		
		Matrix d = new Matrix( new double[][]
		{ {       0,   0, -10,     0,   0},
		  {     -90,   0,   0,     0,  -4},
		  {       0,   0,   0, -1826,   0},
		  { -117.76, 136, -72,   -10,   0} } );
		
		System.out.println("a\n" + a);
		System.out.println("b\n" + b);
		System.out.println("a-b\n" + a.minus(b));
		Assert.assertTrue( a.minus(b).setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(d) );
		//con errore di un miliardesimo
		
		System.out.println("<<<<<<<<<< testMinusMatrix ----------");
	}

	@Test
	public void testMinusDouble()
	{
		System.out.println("---------- testMinusDouble >>>>>>>>>>");
		
		Matrix a = new Matrix( new double[][]
		{ {     2,  4, -5,    7,  3},
		  {     3,  2,  4,    5, -2},
		  {   5.0, 21, 65, -908, 56},
		  {-58.98, 78, -6,    3,  5} } );
		
		double b = 10;
		
		Matrix d = new Matrix( new double[][]
		{ {    -8, -6, -15,   -3,  -7},
		  {    -7, -8,  -6,   -5, -12},
		  {  -5.0, 11,  55, -918,  46},
		  {-68.98, 68, -16,   -7,  -5} } );
		
		System.out.println("a\n" + a);
		System.out.println("b\n" + b);
		System.out.println("a-b\n" + a.minus(b));
		Assert.assertTrue( a.minus(b).setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(d) );
		//con errore di un miliardesimo
		
		System.out.println("<<<<<<<<<< testMinusDouble ----------");
	}

	@Test
	public void testScalarMultiplication()
	{
		System.out.println("---------- testScalarMultiplication >>>>>>>>>>");
		
		Matrix a = new Matrix( new double[][]
		{ {     2,  4, -5,    7,  3},
		  {     3,  2,  4,    5, -2},
		  {   5.0, 21, 65, -908, 56},
		  {-58.98, 78, -6,    3,  5} } );
		
		double b = 10;
		
		Matrix m = new Matrix( new double[][]
		{ {    20,  40, -50,    70,  30},
		  {    30,  20,  40,    50, -20},
		  {    50, 210, 650, -9080, 560},
		  {-589.8, 780, -60,    30,  50} } );
		
		System.out.println("a\n" + a);
		System.out.println("b\n" + b);
		System.out.println("a*b\n" + a.scalarMultiplication(b));
		Assert.assertTrue( a.scalarMultiplication(b).setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(m) );
		//con errore di un miliardesimo
		
		System.out.println("<<<<<<<<<< testScalarMultiplication ----------");
	}

	@Test
	public void testScalarDivision()
	{
		System.out.println("---------- testScalarDivision >>>>>>>>>>");
		
		Matrix a = new Matrix( new double[][]
		{ {     2,  4, -5,    7,  3},
		  {     3,  2,  4,    5, -2},
		  {   5.0, 21, 65, -908, 56},
		  {-58.98, 78, -6,    3,  5} } );
		
		double b = 10;
		
		Matrix q = new Matrix( new double[][]
		{ {   0.2, 0.4, -0.5,   0.7,  0.3},
		  {   0.3, 0.2,  0.4,   0.5, -0.2},
		  {  0.50, 2.1,  6.5, -90.8,  5.6},
		  {-5.898, 7.8, -0.6,   0.3,  0.5} } );
		
		System.out.println("a\n" + a);
		System.out.println("b\n" + b);
		System.out.println("a/b\n" + a.scalarMultiplication(b));
		Assert.assertTrue( a.scalarDivision(b).setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(q) );
		//con errore di un miliardesimo
		
		System.out.println("<<<<<<<<<< testScalarDivision ----------");
	}
	
	@Test
	public void testMultiply()
	{
		System.out.println("---------- testMultiply >>>>>>>>>>");
		
		{
			Matrix a = new Matrix( new double[][]
			   {{4, 6, 5, 6},
				{7, 8, 9, 6},
				{5, 8, 9, 2} } );
			Matrix b = new Matrix( new double[][]
			   {{4, 4},
				{5, 9},
				{7, 5},
				{3, 1} } );
			
			System.out.println("a\n" + a);
			System.out.println("b\n" + b);
			Matrix c = a.multiply(b);
			
			System.out.println("c\n" + c);
			
			Matrix c_expected = new Matrix( new double[][]
			   {{ 99, 101},
				{149, 151},
				{129, 139} });
			
			System.out.println("c_expected\n" + c_expected);
			
			Assert.assertTrue(c_expected.equals(c));
		}
		
		{
			Matrix a = new Matrix( new double[][]
			   {{4, 6, 5, 6},
				{7, 8, 9, 6},
				{5, 8, 9, 2},
				{3, 4, 2, 1}} );
			Matrix b = new Matrix( new double[][]
			   {{6},
				{3},
				{2},
				{1} } );
			
			System.out.println("a\n" + a);
			System.out.println("b\n" + b);
			Matrix c = a.multiply(b);
			
			System.out.println("c\n" + c);
			
			Matrix c_expected = new Matrix( new double[][]
			   {{58},
				{90},
				{74},
				{35} });
			
			System.out.println("c_expected\n" + c_expected);
			
			Assert.assertTrue(c_expected.equals(c));
		}
		
		System.out.println("<<<<<<<<<< testMultiply ----------");
	}

	@Test
	public void testTotalSum()
	{
		System.out.println("---------- testTotalSum >>>>>>>>>>");
		
		System.out.println("M\n" + m);
		System.out.println("totalsum = " + m.totalSum());
		Assert.assertEquals(1242.98, m.totalSum());
		
		System.out.println("<<<<<<<<<< testTotalSum ----------");
	}

	@Test
	public void testMean()
	{
		System.out.println("---------- testMean >>>>>>>>>>");
		
		System.out.println("M\n" + m);
		System.out.println("mean = " + m.mean());
		Assert.assertEquals(62.149, m.mean());
		
		System.out.println("<<<<<<<<<< testMean ----------");
	}

	@Test
	public void testColumnMean()
	{
		System.out.println("---------- testColumnMean >>>>>>>>>>");
		
		double[] cmeans = new double[]{17.245, 26.25, 20, 230.75, 16.5};
		
		System.out.println("M\n" + m);
		for(int i=0; i<cmeans.length; i++)
			System.out.print("m:" + cmeans[i] + "\t");
		System.out.println();
		
		Assert.assertEquals(cmeans.length, m.nCols());
		for(int i=0; i<cmeans.length; i++)
		{
			Assert.assertTrue(Math.abs( cmeans[i] - m.columnMean(i)) <= 0.000001 );
			System.out.print("e:" + Math.abs( cmeans[i] - m.columnMean(i)) + "\t");
		}
		System.out.println();
		
		System.out.println("<<<<<<<<<< testColumnMean ----------");
	}

	@Test
	public void testRowMean()
	{
		System.out.println("---------- testRowMean >>>>>>>>>>");
		
		double[] rmeans = new double[]{4.2, 3.2, 211, 30.196};
		
		System.out.println("M\n" + m);
		for(int i=0; i<rmeans.length; i++)
			System.out.print("m:" + rmeans[i] + "\t");
		System.out.println();
		
		Assert.assertEquals(rmeans.length, m.nRows());
		for(int i=0; i<rmeans.length; i++)
		{
			Assert.assertTrue(Math.abs( rmeans[i] - m.rowMean(i)) <= 0.000001 );
			System.out.print("e:" + Math.abs( rmeans[i] - m.rowMean(i)) + "\t");
		}
		System.out.println();
		
		System.out.println("<<<<<<<<<< testRowMean ----------");
	}

	@Test
	public void testVariance()
	{
		System.out.println("---------- testVariance >>>>>>>>>>");
		
		System.out.println("M\n" + m);
		System.out.println("variance = " + m.variance());
		Assert.assertTrue( Math.abs(38241.733819 - m.variance()) <= 0.000001);
		System.out.println("error = " + Math.abs(38241.733819 - m.variance()));
		
		System.out.println("<<<<<<<<<< testVariance ----------");
	}

	@Test
	public void testIsSquare()
	{
		System.out.println("---------- testIsSquare >>>>>>>>>>");
		
		Assert.assertFalse( m.isSquare() );
		Assert.assertTrue( new Matrix(8).isSquare() );
		Assert.assertFalse( new Matrix(6,5).isSquare() );
		Assert.assertTrue( new Matrix(new double[][]{{0}}).isSquare() );
		Assert.assertFalse( new Matrix(new double[][]{{0}, {1}}).isSquare() );
		Assert.assertTrue( new Matrix(new double[]{0}, Matrix.MatrixType.ColumnMatrix).isSquare() );
		Assert.assertTrue( new Matrix(new double[]{0}, Matrix.MatrixType.RowMatrix).isSquare() );
		Assert.assertFalse( new Matrix(new double[]{0, 1}, Matrix.MatrixType.RowMatrix).isSquare() );
		
		System.out.println("<<<<<<<<<< testIsSquare ----------");
	}

	@Test
	public void testIsRowMatrix()
	{
		System.out.println("---------- testIsRowMatrix >>>>>>>>>>");
		
		Assert.assertFalse( m.isRowMatrix() );
		Assert.assertFalse( new Matrix(8).isRowMatrix() );
		Assert.assertFalse( new Matrix(6,5).isRowMatrix() );
		Assert.assertTrue( new Matrix(1,5).isRowMatrix() );
		Assert.assertTrue( new Matrix(new double[][]{{0}}).isRowMatrix() );
		Assert.assertFalse( new Matrix(new double[][]{{0}, {1}}).isRowMatrix() );
		Assert.assertTrue( new Matrix(new double[]{0}, Matrix.MatrixType.ColumnMatrix).isRowMatrix() );
		Assert.assertFalse( new Matrix(new double[]{0, 3}, Matrix.MatrixType.ColumnMatrix).isRowMatrix() );
		Assert.assertTrue( new Matrix(new double[]{0}, Matrix.MatrixType.RowMatrix).isRowMatrix() );
		Assert.assertTrue( new Matrix(new double[]{0, 1}, Matrix.MatrixType.RowMatrix).isRowMatrix() );
		
		System.out.println("<<<<<<<<<< testIsRowMatrix ----------");
	}

	@Test
	public void testIsColumnMatrix()
	{
		System.out.println("---------- testIsColumnMatrix >>>>>>>>>>");
		
		Assert.assertFalse( m.isColumnMatrix() );
		Assert.assertFalse( new Matrix(8).isColumnMatrix() );
		Assert.assertFalse( new Matrix(6,5).isColumnMatrix() );
		Assert.assertTrue( new Matrix(7,1).isColumnMatrix() );
		Assert.assertTrue( new Matrix(new double[][]{{0}}).isColumnMatrix() );
		Assert.assertTrue( new Matrix(new double[][]{{0}, {1}}).isColumnMatrix() );
		Assert.assertTrue( new Matrix(new double[]{0}, Matrix.MatrixType.ColumnMatrix).isColumnMatrix() );
		Assert.assertTrue( new Matrix(new double[]{0, 3}, Matrix.MatrixType.ColumnMatrix).isColumnMatrix() );
		Assert.assertTrue( new Matrix(new double[]{0}, Matrix.MatrixType.RowMatrix).isColumnMatrix() );
		Assert.assertFalse( new Matrix(new double[]{0, 1}, Matrix.MatrixType.RowMatrix).isColumnMatrix() );
		
		System.out.println("<<<<<<<<<< testIsColumnMatrix ----------");
	}

	@Test
	public void testSetAdmittableErrorDoubleEqualityErrorAdmissibility()
	{
		System.out.println("---------- testSetAdmittableErrorDoubleEqualityErrorAdmissibility >>>>>>>>>>");
		
		m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.NONE);
		Assert.assertEquals(0.0, m.getAdmittableError());
		
		m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.THOUSANDTH);
		Assert.assertEquals(0.001, m.getAdmittableError());
		
		m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH);
		Assert.assertEquals(0.000000001, m.getAdmittableError());
		
		System.out.println("<<<<<<<<<< testSetAdmittableErrorDoubleEqualityErrorAdmissibility ----------");
	}

	@Test
	public void testSetAdmittableErrorDouble()
	{
		System.out.println("---------- testSetAdmittableErrorDouble >>>>>>>>>>");
		
		m.setAdmittableError(-0.0005);
		Assert.assertEquals(0.0005, m.getAdmittableError());;
		
		System.out.println("<<<<<<<<<< testSetAdmittableErrorDouble ----------");
	}

	@Test
	public void testEqualsMatrix()
	{
		System.out.println("---------- testEqualsMatrix >>>>>>>>>>");
		
		Matrix e = new Matrix( new double[][]
		{ {    2,  4,  5,   7,  3},
		  {    3,  2,  4,   5,  2},
		  {  5.0, 21, 65, 908, 56},
		  {58.98, 78,  6,   3,  5} });
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.THOUSANDTH).equals(e) );
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.MILLIONTH).equals(e) );
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(e) );
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.NONE).equals(e) );
		
		Matrix h = e.plus(e.newLikeThis().fill(new Random()).scalarDivision(1000));
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.THOUSANDTH).equals(h) );
		Assert.assertFalse( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.MILLIONTH).equals(h) );
		Assert.assertFalse( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(h) );
		Assert.assertFalse( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.NONE).equals(h) );
		
		Matrix g = e.plus(e.newLikeThis().fill(new Random()).scalarDivision(1000000));
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.THOUSANDTH).equals(g) );
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.MILLIONTH).equals(g) );
		Assert.assertFalse( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(g) );
		Assert.assertFalse( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.NONE).equals(g) );
		
		Matrix f = e.plus(e.newLikeThis().fill(new Random()).scalarDivision(1000000000));
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.THOUSANDTH).equals(f) );
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.MILLIONTH).equals(f) );
		Assert.assertTrue( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH).equals(f) );
		Assert.assertFalse( m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.NONE).equals(f) );
		
		System.out.println("<<<<<<<<<< testEqualsMatrix ----------");
	}

	@Test
	public void testIsIsoDimensional()
	{
		System.out.println("---------- testIsIsoDimensional >>>>>>>>>>");
		
		Assert.assertFalse( m.isIsoDimensional( new Matrix(8) ));
		Assert.assertTrue( m.isIsoDimensional( new Matrix(4, 5) ));
		
		System.out.println("<<<<<<<<<< testIsIsoDimensional ----------");
	}
	
	@Test
	public void testAreEqualWithinLimitDoubleDouble()
	{
		System.out.println("---------- testAreEqualWithinLimitDoubleDouble >>>>>>>>>>");
		
		m.setAdmittableError(Matrix.DoubleEqualityErrorAdmissibility.BILLIONTH);
		Assert.assertTrue( m.areEqualWithinLimit(1.0, 1.0000000009999) );
		Assert.assertFalse( m.areEqualWithinLimit(1.0, 1.0000000010001) );
		
		System.out.println("<<<<<<<<<< testAreEqualWithinLimitDoubleDouble ----------");
	}
	
	@Test
	public void  testAreEqualWithinLimitDoubleDoubleDouble()
	{
		System.out.println("---------- testAreEqualWithinLimitDoubleDoubleDouble >>>>>>>>>>");
		
		Assert.assertTrue( Matrix.areEqualWithinLimit(1.0, 1.0000000009999, 0.000000001) );
		Assert.assertFalse( Matrix.areEqualWithinLimit(1.0, 1.0000000010001, 0.000000001) );
		
		System.out.println("<<<<<<<<<< testAreEqualWithinLimitDoubleDoubleDouble ----------");
	}
	
	@Test
	public void testDistance()
	{
		System.out.println("---------- testDistance >>>>>>>>>>");
		
		Matrix a = new Matrix( new double[][]
			{ {     2,  4, -5,    7,  3},
			  {     3,  2,  4,    5, -2},
			  {   5.0, 21, 65, -908, 56},
			  {-58.98, 78, -6,    3,  5} } );
				
		Matrix b = new Matrix( new double[][]
			{ {     2,   4,  5,    7,  3},
			  {    93,   2,  4,    5,  2},
			  {   5.0,  21, 65,  918, 56},
			  { 58.78, -58, 66,   13,  5} } );
		
		System.out.println("a\n" + a + "\nb\n" + b);
		
		a.setDistanceType(Matrix.DistanceType.TotalError);
		System.out.println("total error = " + a.distance(b));
		Assert.assertEquals(2265.76, a.distance(b));
		
		a.setDistanceType(Matrix.DistanceType.MaximumError);
		System.out.println("max error = " + a.distance(b));
		Assert.assertEquals(1826.0, a.distance(b));
		
		System.out.println("<<<<<<<<<< testDistance ----------");
	}
}