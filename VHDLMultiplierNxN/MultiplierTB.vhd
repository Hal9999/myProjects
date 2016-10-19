LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY MultiplierTB IS
END MultiplierTB;
 
ARCHITECTURE behavior OF MultiplierTB IS 
    COMPONENT Multiplier
    PORT(
         a : IN  std_logic_vector(15 downto 0);
         b : IN  std_logic_vector(15 downto 0);
         product : OUT  std_logic_vector(31 downto 0)
        );
    END COMPONENT;
   
   signal a : std_logic_vector(15 downto 0) := (others => '0');
   signal b : std_logic_vector(15 downto 0) := (others => '0');

   signal product : std_logic_vector(31 downto 0);
   
   constant CLK_period : time := 10 ns;
	signal CLK : std_logic;
 
BEGIN
   uut: Multiplier PORT MAP (
          a => a,
          b => b,
          product => product
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
		
      a <= x"DEFE";
		b <= x"8956";
		wait for 5 ns;
		assert product = x"77A0D754";
      wait for CLK_period;
		
		a <= x"FFFF";
		b <= x"0001";
		wait for 5 ns;
		assert product = x"0000FFFF";
      wait for CLK_period;
		
		a <= x"FFFF";
		b <= x"FFFF";
		wait for 5 ns;
		assert product = x"FFFE0001";
      wait for CLK_period;
		
		a <= x"0001";
		b <= x"0001";
		wait for 5 ns;
		assert product = x"00000001";
      wait for CLK_period;
		
		a <= x"FFFF";
		b <= x"0002";
		wait for 5 ns;
		assert product = x"0001FFFE";
      wait for CLK_period;
		
		a <= x"5252";
		b <= x"5252";
		wait for 5 ns;
		assert product = x"1A78A244";
      wait for CLK_period;

      wait;
   end process;

END;
