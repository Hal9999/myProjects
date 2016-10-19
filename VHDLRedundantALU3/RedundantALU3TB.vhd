LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY RedundantALU3TB IS
END RedundantALU3TB;
 
ARCHITECTURE behavior OF RedundantALU3TB IS 
    COMPONENT RedundantALU3
    PORT(
         A : IN  std_logic_vector(15 downto 0);
         B : IN  std_logic_vector(15 downto 0);
         C : IN  std_logic_vector(15 downto 0);
         D : IN  std_logic_vector(15 downto 0);
         E : IN  std_logic_vector(15 downto 0);
         F : IN  std_logic_vector(15 downto 0);
         opCode : IN  std_logic_vector(1 downto 0);
         output : OUT  std_logic_vector(15 downto 0);
         dataValid : OUT  std_logic
        );
    END COMPONENT;
    
   signal A : std_logic_vector(15 downto 0) := (others => '0');
   signal B : std_logic_vector(15 downto 0) := (others => '0');
   signal C : std_logic_vector(15 downto 0) := (others => '0');
   signal D : std_logic_vector(15 downto 0) := (others => '0');
   signal E : std_logic_vector(15 downto 0) := (others => '0');
   signal F : std_logic_vector(15 downto 0) := (others => '0');
   signal opCode : std_logic_vector(1 downto 0) := (others => '0');

   signal output : std_logic_vector(15 downto 0);
   signal dataValid : std_logic;

   constant CLK_period : time := 10 ns;
	signal CLK : std_logic;
 
BEGIN
   uut: RedundantALU3 PORT MAP (
          A => A,
          B => B,
          C => C,
          D => D,
          E => E,
          F => F,
          opCode => opCode,
          output => output,
          dataValid => dataValid
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

		A <= x"AABB";
		B <= x"BBAA";
		C <= x"AABB";
		D <= x"BBAA";
		E <= x"AABB";
		F <= x"BBAA";
		opCode <= "00";
		wait for CLK_period/2;
			assert dataValid = '1' report "Data validity bit is wrong." severity error;
			assert output = x"6665" report "Output is wrong." severity error;
      wait for CLK_period;
		
		A <= x"AABB";
		B <= x"BBAA";
		C <= x"A0BB";
		D <= x"BBAA";
		E <= x"AACB";
		F <= x"BBAA";
		opCode <= "00";
		wait for CLK_period/2;
			assert dataValid = '0' report "Data validity bit is wrong." severity error;
			assert output = "ZZZZZZZZZZZZZZZZ" report "Output is wrong." severity error;
      wait for CLK_period;
		
		A <= "ZZZZZZZZZZZZZZZZ";
		B <= "ZZZZZZZZZZZZZZZZ";
		C <= x"1122";
		D <= x"1133";
		E <= x"1122";
		F <= x"1133";
		opCode <= "00";
		wait for CLK_period/2;
			assert dataValid = '1' report "Data validity bit is wrong." severity error;
			assert output = x"2255" report "Output is wrong." severity error;
      wait for CLK_period;

      wait;
   end process;

END;
