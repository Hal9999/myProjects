library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity SIMD16x4ALU is
	port(
		A   		  : in  std_logic_vector(63 downto 0);
		B   		  : in  std_logic_vector(63 downto 0);
		nibbleMask : in  std_logic_vector( 3 downto 0);
		Sel 		  : in  std_logic_vector( 1 downto 0);
		output	  : out std_logic_vector(63 downto 0)
	);
end SIMD16x4ALU;

architecture Behavioral of SIMD16x4ALU is
	component ALU
		generic (N: integer := 4);
		port(
			A      : in  std_logic_vector(N-1 downto 0);
			B      : in  std_logic_vector(N-1 downto 0);
			Sel    : in  std_logic_vector(  1 downto 0);
			enable : in  std_logic;
			output : out std_logic_vector(N-1 downto 0)
		 );
	end component;
begin
	ALU0 : ALU generic map(N => 16) port map( A(15 downto  0), B(15 downto  0), Sel, nibbleMask(0), output(15 downto  0) );
	ALU1 : ALU generic map(N => 16) port map( A(31 downto 16), B(31 downto 16), Sel, nibbleMask(1), output(31 downto 16) );
	ALU2 : ALU generic map(N => 16) port map( A(47 downto 32), B(47 downto 32), Sel, nibbleMask(2), output(47 downto 32) );
	ALU3 : ALU generic map(N => 16) port map( A(63 downto 48), B(63 downto 48), Sel, nibbleMask(3), output(63 downto 48) );
end Behavioral;

