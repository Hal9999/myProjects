library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;
ENTITY ComparatorTB IS
END ComparatorTB;
 
ARCHITECTURE behavior OF ComparatorTB IS 
    -- Component Declaration for the Unit Under Test (UUT)
    COMPONENT Comparator
    PORT(
         A : IN  std_logic_vector(7 downto 0);
         B : IN  std_logic_vector(7 downto 0);
         less : OUT  std_logic;
         equal : OUT  std_logic;
         greater : OUT  std_logic
        );
    END COMPONENT;

	--Inputs
   signal A : std_logic_vector(7 downto 0) := (others => '0');
   signal B : std_logic_vector(7 downto 0) := (others => '0');
 	--Outputs
   signal less : std_logic;
   signal equal : std_logic;
   signal greater : std_logic;
   -- No clocks detected in port list. Replace CLK below with 
   -- appropriate port name 
   signal CLK : std_logic;
   constant CLK_period : time := 10 ns;

BEGIN
	-- Instantiate the Unit Under Test (UUT)
   uut: Comparator PORT MAP (
          A => A,
          B => B,
          less => less,
          equal => equal,
          greater => greater
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
	  
	  -- insert stimulus here
      wait for CLK_period*10;
	  A <= "00010000";
	  B <= "00010000";
	  wait for CLK_period*10;
	  A <= "00010000";
	  B <= "00010011";
	  wait for CLK_period*10;
	  A <= "00010100";
	  B <= "00010000";
      wait;
   end process;

END;
