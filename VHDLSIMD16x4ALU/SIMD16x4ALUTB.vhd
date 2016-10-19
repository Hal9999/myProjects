library IEEE;
USE ieee.std_logic_1164.ALL;
 
ENTITY SIMD16x4ALUTB IS
END SIMD16x4ALUTB;
 
ARCHITECTURE behavior OF SIMD16x4ALUTB IS 
    COMPONENT SIMD16x4ALU
    PORT(
         A : IN  std_logic_vector(63 downto 0);
         B : IN  std_logic_vector(63 downto 0);
         nibbleMask : IN  std_logic_vector(3 downto 0);
         Sel : IN  std_logic_vector(1 downto 0);
         output : OUT  std_logic_vector(63 downto 0)
        );
    END COMPONENT;

   signal A : std_logic_vector(63 downto 0) := (others => '0');
   signal B : std_logic_vector(63 downto 0) := (others => '0');
   signal nibbleMask : std_logic_vector(3 downto 0) := (others => '0');
   signal Sel : std_logic_vector(1 downto 0) := (others => '0');

   signal output : std_logic_vector(63 downto 0);
 
   constant CLK_period : time := 10 ns;
	signal CLK : std_logic;
 
BEGIN
   uut: SIMD16x4ALU PORT MAP (
          A => A,
          B => B,
          nibbleMask => nibbleMask,
          Sel => Sel,
          output => output
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
		
		A <= x"1111222233334444";
		B <= x"1010202030304040";
		nibbleMask <= "0101";
		Sel <= "00";
		wait for 5 ns;
			assert output(63 downto 48) = "ZZZZZZZZZZZZZZZZ" report "1st output nibble is wrong." severity error;
			assert output(47 downto 32) = x"4242" report "2nd output nibble is wrong." severity error;
			assert output(31 downto 16) = "ZZZZZZZZZZZZZZZZ" report "3rd output nibble is wrong." severity error;
			assert output(15 downto  0) = x"8484" report "4th output nibble is wrong." severity error;
      wait for CLK_period;
		
		A <= x"1111222233334444";
		B <= x"1010202030304040";
		nibbleMask <= "0011";
		Sel <= "00";
		wait for 5 ns;
			assert output(63 downto 48) = "ZZZZZZZZZZZZZZZZ" report "1st output nibble is wrong." severity error;
			assert output(47 downto 32) = "ZZZZZZZZZZZZZZZZ" report "2nd output nibble is wrong." severity error;
			assert output(31 downto 16) = x"6363" report "3rd output nibble is wrong." severity error;
			assert output(15 downto  0) = x"8484" report "4th output nibble is wrong." severity error;
      wait for CLK_period;
		
		A <= x"1111222233334444";
		B <= x"1010202030304040";
		nibbleMask <= "0000";
		Sel <= "00";
		wait for 5 ns;
			assert output(63 downto 48) = "ZZZZZZZZZZZZZZZZ" report "1st output nibble is wrong." severity error;
			assert output(47 downto 32) = "ZZZZZZZZZZZZZZZZ" report "2nd output nibble is wrong." severity error;
			assert output(31 downto 16) = "ZZZZZZZZZZZZZZZZ" report "3rd output nibble is wrong." severity error;
			assert output(15 downto  0) = "ZZZZZZZZZZZZZZZZ" report "4th output nibble is wrong." severity error;
      wait for CLK_period;
		
		A <= x"1111222233334444";
		B <= x"1010202030304040";
		nibbleMask <= "1111";
		Sel <= "00";
		wait for 5 ns;
			assert output(63 downto 48) = x"2121" report "1st output nibble is wrong." severity error;
			assert output(47 downto 32) = x"4242" report "2nd output nibble is wrong." severity error;
			assert output(31 downto 16) = x"6363" report "3rd output nibble is wrong." severity error;
			assert output(15 downto  0) = x"8484" report "4th output nibble is wrong." severity error;
			assert output = x"2121424263638484";
      wait for CLK_period;
		
		A <= x"FFFFFFFFFFFFFFFF";
		B <= x"1010202030304040";
		nibbleMask <= "1111";
		Sel <= "00";
		wait for 5 ns;
			assert output(63 downto 48) = x"100F" report "1st output nibble is wrong." severity error;
			assert output(47 downto 32) = x"201F" report "2nd output nibble is wrong." severity error;
			assert output(31 downto 16) = x"302F" report "3rd output nibble is wrong." severity error;
			assert output(15 downto  0) = x"403F" report "4th output nibble is wrong." severity error;
			assert output = x"100F201F302F403F";
      wait for CLK_period;

      wait;
   end process;

END;
