LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY Mux4to1TB IS
END Mux4to1TB;
 
ARCHITECTURE behavior OF Mux4to1TB IS 
    COMPONENT Mux4to1
    PORT(
         i : IN  std_logic_vector(3 downto 0);
         sel : IN  std_logic_vector(1 downto 0);
         o : OUT  std_logic
        );
    END COMPONENT;

	signal i : std_logic_vector(3 downto 0) := (others => '0');
   signal sel : std_logic_vector(1 downto 0) := (others => '0');

   signal o : std_logic;

   signal CLK : std_logic;
   constant CLK_period : time := 10 ns;
 
BEGIN
 
	-- Instantiate the Unit Under Test (UUT)
   uut: Mux4to1 PORT MAP (
          i => i,
          sel => sel,
          o => o
        );

   -- Clock process definitions
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

	  i <= "1010";
	  sel <= "00";
      wait for CLK_period*10;
	  i <= "1010";
	  sel <= "01";
      wait for CLK_period*10;
	  i <= "1010";
	  sel <= "10";
      wait for CLK_period*10;
	  i <= "1010";
	  sel <= "11";
      wait for CLK_period*10;
	  i <= "1100";
	  sel <= "00";
      wait for CLK_period*10;
	  i <= "1100";
	  sel <= "01";
      wait for CLK_period*10;
	  i <= "1100";
	  sel <= "10";
      wait for CLK_period*10;
	  i <= "1100";
	  sel <= "11";
      wait for CLK_period*10;

      wait;
   end process;

END;
