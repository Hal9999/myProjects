public class Solution
{
	public int[] array;
	public int[] shuffledArray;
	
	public Solution(int[] nums)
	{
		this.array = nums;
		shuffledArray = nums.clone(); 
	}

	/** Resets the array to its original configuration and return it. */
	public int[] reset()
	{
		shuffledArray = array.clone();
		return shuffledArray;
	}

	/** Returns a random shuffling of the array. */
    public int[] shuffle()
    {
    	for(int i=0; i<shuffledArray.length; i++)
    	{
    		int pos = (int)Math.floor((Math.random()*shuffledArray.length));
    		int tmp = shuffledArray[i];
    		shuffledArray[i] = shuffledArray[pos];
    		shuffledArray[pos] = tmp;
    		
    	}
        return shuffledArray;
    }
    
    public boolean isPowerOfFour(int x)
    {
    	return (x!=0 && ((x & x-1) == 0)) ? (x & 0x5555) != 0 : false;
//        if((x & 0x5555) != 0) return false;
//        return (x!=0 && ((x & x-1) == 0));
    }
    
    public boolean isPowerOfFour2(int x)
    {
    	int arr[] = {1, 4,16,64,256,1024,4096,16384,65536,262144,1048576,4194304, 16777216,67108864,268435456,1073741824};
    	for (int i : arr)
			if(i==x) return true;
    	return false;
    }
    
}

/**
 * Your Solution object will be instantiated and called as such: Solution obj =
 * new Solution(nums); int[] param_1 = obj.reset(); int[] param_2 =
 * obj.shuffle();
 */