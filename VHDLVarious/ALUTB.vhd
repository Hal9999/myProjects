LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;
 
ENTITY ALUtb IS
END ALUtb;
 
ARCHITECTURE behavior OF ALUtb IS 
    -- Component Declaration for the Unit Under Test (UUT)
    COMPONENT ALU
    PORT(
         A : IN  std_logic_vector(7 downto 0);
         B : IN  std_logic_vector(7 downto 0);
         Sel : IN  std_logic_vector(1 downto 0);
         Res : OUT  std_logic_vector(7 downto 0)
        );
    END COMPONENT;
	
   --Inputs
   signal A : std_logic_vector(7 downto 0) := (others => '0');
   signal B : std_logic_vector(7 downto 0) := (others => '0');
   signal Sel : std_logic_vector(1 downto 0) := (others => '0');
 	--Outputs
   signal Res : std_logic_vector(7 downto 0);

   signal  CLK : std_logic;
   constant CLK_period : time := 10 ns;
 
BEGIN
	-- Instantiate the Unit Under Test (UUT)
   uut: ALU PORT MAP (
          A => A,
          B => B,
          Sel => Sel,
          Res => Res
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

      wait for CLK_period*10;
      -- insert stimulus here
	  A <= "00001111";
	  B <= "00000001";
	  Sel <= "00";
	  wait for CLK_period*10;
	  A <= "00001111";
	  B <= "00000001";
	  Sel <= "01";
	  wait for CLK_period*10;
	  A <= "00001111";
	  B <= "00000001";
	  Sel <= "10";
	  wait for CLK_period*10;
	  A <= "00001111";
	  B <= "00000001";
	  Sel <= "11";
      wait;
   end process;

END;
