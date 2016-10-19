LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;
 
ENTITY MultiplexerTB IS
END MultiplexerTB;
 
ARCHITECTURE behavior OF MultiplexerTB IS 
    COMPONENT Multiplexer
    PORT(
         I : IN  std_logic_vector(7 downto 0);
         sel : IN  std_logic_vector(2 downto 0);
         O : OUT  std_logic
        );
    END COMPONENT;

   signal I : std_logic_vector(7 downto 0) := (others => '0');
   signal sel : std_logic_vector(2 downto 0) := (others => '0');
   signal O : std_logic;

   signal CLK : std_logic;
   constant CLK_period : time := 10 ns;
 
BEGIN
   uut: Multiplexer PORT MAP (
          I => I,
          sel => sel,
          O => O
        );

   CLK_process :process
   begin
		CLK <= '0';
		wait for CLK_period/2;
		CLK <= '1';
		wait for CLK_period/2;
   end process;
 
   -- Stimulus process
   stim_proc: process
   begin		
      -- hold reset state for 100 ns.
      wait for 100 ns;	

      wait for CLK_period*10;
	  I <= "10011101";
	  sel <= "000";
	  wait for CLK_period*10;
	  I <= "10011101";
	  sel <= "001";
	  wait for CLK_period*10;
	  I <= "10011101";
	  sel <= "010";
	  wait for CLK_period*10;
	  I <= "10011101";
	  sel <= "111";
	  wait for CLK_period*10;
	  I <= "10011101";
	  sel <= "110";

      wait;
   end process;

END;
