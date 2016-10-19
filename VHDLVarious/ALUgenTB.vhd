LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY ALUTB IS
END ALUTB;
 
ARCHITECTURE behavior OF ALUTB IS 
    COMPONENT ALU
    PORT(
         A : IN  std_logic_vector(3 downto 0);
         B : IN  std_logic_vector(3 downto 0);
         Sel : IN  std_logic_vector(1 downto 0);
         Res : OUT  std_logic_vector(3 downto 0)
        );
    END COMPONENT;

	signal A : std_logic_vector(3 downto 0) := (others => '0');
   signal B : std_logic_vector(3 downto 0) := (others => '0');
   signal Sel : std_logic_vector(1 downto 0) := (others => '0');

   signal Res : std_logic_vector(3 downto 0);
	signal CLK : std_logic;
   constant CLK_period : time := 10 ns;
 
BEGIN
   uut: ALU PORT MAP (
          A => A,
          B => B,
          Sel => Sel,
          Res => Res
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
      -- hold reset state for 100 ns.
      wait for 100 ns;	

      wait for CLK_period*10;

      -- insert stimulus here 

      wait;
   end process;

END;
