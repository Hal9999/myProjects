LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY Multiplier2PowerWideTB IS
END Multiplier2PowerWideTB;
 
ARCHITECTURE behavior OF Multiplier2PowerWideTB IS 
    COMPONENT Multiplier2PowerWide
	 generic( N : natural := 4);
    PORT(
         AB : IN  std_logic_vector(31 downto 0);
         CD : IN  std_logic_vector(31 downto 0);
         ZXYT : OUT  std_logic_vector(63 downto 0)
        );
    END COMPONENT;
   
   signal AB : std_logic_vector(31 downto 0) := (others => '0');
   signal CD : std_logic_vector(31 downto 0) := (others => '0');

   signal ZXYT : std_logic_vector(63 downto 0);
 
   constant CLK_period : time := 10 ns;
	signal CLK : std_logic;
 
BEGIN
   uut: Multiplier2PowerWide generic map( N => 5 )
			PORT MAP (
          AB => AB,
          CD => CD,
          ZXYT => ZXYT
        );
		  
   CLK_process :process
   begin
		CLK <= '0';
		wait for CLK_period/2;
		CLK <= '1';
		wait for CLK_period/2;
   end process;

   stim_proc: process
   begin		
      wait for 10 ns;	

		AB <= x"DEAFDAEA";
		CD <= x"89746644";
		wait for 5 ns;
		assert ZXYT = x"77915CCBA3F76228";
      wait for CLK_period;
		
		AB <= x"FFFFFFFF";
		CD <= x"FFFFFFFF";
		wait for 5 ns;
		assert ZXYT = x"FFFFFFFE00000001";
      wait for CLK_period;
		
		AB <= x"FFFFFFFF";
		CD <= x"00000000";
		wait for 5 ns;
		assert ZXYT = x"0000000000000000";
      wait for CLK_period;
		
		AB <= x"FFFFFFFF";
		CD <= x"00000001";
		wait for 5 ns;
		assert ZXYT = x"00000000FFFFFFFF";
      wait for CLK_period;
		
		AB <= x"FEFEEFEF";
		CD <= x"10100101";
		wait for 5 ns;
		assert ZXYT = x"0FFFDFEDECDEDEEF";
      wait for CLK_period;
		
      wait;
   end process;

END;
