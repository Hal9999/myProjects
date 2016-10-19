LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY TopLevelTB IS
END TopLevelTB;
 
ARCHITECTURE behavior OF TopLevelTB IS 
    COMPONENT TopLevel
    PORT(
         reset : IN  std_logic;
         clock : IN  std_logic;
         go : IN  std_logic;
         working : OUT  std_logic;
         result : OUT  std_logic
        );
    END COMPONENT;

   signal reset : std_logic := '0';
   signal clock : std_logic := '0';
   signal go : std_logic := '0';

   signal working : std_logic;
   signal result : std_logic;

   constant clock_period : time := 5 ns;
 
BEGIN
   uut: TopLevel PORT MAP (
          reset => reset,
          clock => clock,
          go => go,
          working => working,
          result => result
        );

   clock_process :process
   begin
		clock <= '1';
		wait for clock_period/2;
		clock <= '0';
		wait for clock_period/2;
   end process;
 
   stim_proc: process
   begin		
      reset <= '1';
		wait for clock_period;
		reset <= '0';
		go <= '1';
		wait for clock_period;
		go <= '0';
		
      wait;
   end process;

END;
