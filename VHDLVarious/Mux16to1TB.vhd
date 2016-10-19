LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY Mux16to1TB IS
END Mux16to1TB;
 
ARCHITECTURE behavior OF Mux16to1TB IS 
    COMPONENT Multiplexer16to1
    PORT(
         i : IN  std_logic_vector(15 downto 0);
         sel : IN  std_logic_vector(3 downto 0);
         o : OUT  std_logic
        );
    END COMPONENT;

   signal i : std_logic_vector(15 downto 0) := (others => '0');
   signal sel : std_logic_vector(3 downto 0) := (others => '0');

   signal o : std_logic;

   signal CLK : std_logic;
   constant CLK_period : time := 10 ns;
 
BEGIN
   uut: Multiplexer16to1 PORT MAP (
          i => i,
          sel => sel,
          o => o
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
      wait for 100 ns;	

	  i <= "1110011001010111";
	  sel <= "0001";
      wait for CLK_period*10;
	  sel <= "0010";
      wait for CLK_period*10;
	  sel <= "0011";
      wait for CLK_period*10;
	  sel <= "0100";
      wait for CLK_period*10;
	  sel <= "0101";
      wait for CLK_period*10;
	  sel <= "0110";
      wait for CLK_period*10;
	  sel <= "0111";
      wait for CLK_period*10;
	  sel <= "1000";
      wait for CLK_period*10;
	  sel <= "1001";
      wait for CLK_period*10;
	  sel <= "1010";
      wait for CLK_period*10;
	  sel <= "1011";
      wait for CLK_period*10;
	  sel <= "1100";
      wait for CLK_period*10;
	  sel <= "1101";
      wait for CLK_period*10;
	  sel <= "1110";
      wait for CLK_period*10;
	  sel <= "1111";
      wait for CLK_period*10;
	  sel <= "0000";
      wait for CLK_period*10;

      wait;
   end process;

END;
