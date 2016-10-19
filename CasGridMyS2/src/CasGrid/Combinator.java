package CasGrid;

public class Combinator
{
    public static int getCombsNumber(int N, int K)
    {
        int combinationsNumber=1;
        for( int i=0; i<K; i++) combinationsNumber*=N-i;
        for( int i=2; i<=K; i++) combinationsNumber/=i;
        return combinationsNumber;
    }
}