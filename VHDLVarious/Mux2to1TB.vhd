LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY Mux2to1TB IS
END Mux2to1TB;
 
ARCHITECTURE behavior OF Mux2to1TB IS 
    COMPONENT Mux2to1
    PORT(
         i : IN  std_logic_vector(1 downto 0);
         sel : IN  std_logic_vector(0 downto 0);
         o : OUT  std_logic
        );
    END COMPONENT;

   signal i : std_logic_vector(1 downto 0) := (others => '0');
   signal sel : std_logic_vector(0 downto 0) := (others => '0');
   signal o : std_logic;
   
   signal CLK : std_logic;
 
   constant CLK_period : time := 10 ns;
 
BEGIN
 
	-- Instantiate the Unit Under Test (UUT)
   uut: Mux2to1 PORT MAP (
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

	  i <= "01";
	  sel <= "0";
      wait for CLK_period*10;
	  i <= "01";
	  sel <= "1";
      wait for CLK_period*10;

      wait;
   end process;

END;
