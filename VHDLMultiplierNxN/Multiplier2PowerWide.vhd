library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity Multiplier2PowerWide is
	generic( N : natural := 4);
	port(
		AB   : in  std_logic_vector ( 2**N-1 downto 0);
		CD   : in  std_logic_vector ( 2**N-1 downto 0);
		ZXYT : out std_logic_vector ((2**(N+1))-1 downto 0)
	);
end Multiplier2PowerWide;

architecture Structural of Multiplier2PowerWide is
	component Adder is
		generic (N: integer := 4);
		port(
				a        : in  std_logic_vector(N-1 downto 0);
				b        : in  std_logic_vector(N-1 downto 0);
				carryIn  : in  std_logic;
				carryOut : out std_logic;
				sum      : out std_logic_vector(N-1 downto 0)
			 );
	end component;
	component Multiplier2PowerWide is
		generic( N : natural := 4);
		port(
			AB   : in  std_logic_vector ( 2**N-1 downto 0);
			CD   : in  std_logic_vector ( 2**N-1 downto 0);
			ZXYT : out std_logic_vector ((2**(N+1))-1 downto 0)
		);
	end component;
begin
	recursiveGen:
		if N >= 1 generate
			signal EF, GH, IJ, KL : std_logic_vector(2**N-1 downto 0);
			signal Z, X, Y, T, s0, s1, s2 : std_logic_vector(2**(N-1)-1 downto 0);
			signal c0, c1, c2, c3 : std_logic;
			begin
			M0 : Multiplier2PowerWide generic map(N => N-1) port map( AB((2**(N-1))-1 downto 0       ), CD((2**(N-1))-1 downto 0       ), EF ); --EF=B*D
			M1 : Multiplier2PowerWide generic map(N => N-1) port map( AB( 2**N-1      downto 2**(N-1)), CD((2**(N-1))-1 downto 0       ), GH ); --GH=A*D
			M2 : Multiplier2PowerWide generic map(N => N-1) port map( AB((2**(N-1))-1 downto 0       ), CD( 2**N-1      downto 2**(N-1)), IJ ); --IJ=B*C
			M3 : Multiplier2PowerWide generic map(N => N-1) port map( AB( 2**N-1      downto 2**(N-1)), CD( 2**N-1      downto 2**(N-1)), KL ); --KL=A*C
			T <= EF((2**(N-1))-1 downto 0);
			A0 : Adder generic map(N => 2**(N-1)) port map( EF( 2**N-1      downto 2**(N-1)), GH((2**(N-1))-1 downto 0       ) , '0', c0, s0 );
			A1 : Adder generic map(N => 2**(N-1)) port map( IJ((2**(N-1))-1 downto 0       ), s0, '0', c1, Y  );
			A2 : Adder generic map(N => 2**(N-1)) port map( GH( 2**N-1      downto 2**(N-1)), IJ( 2**N-1      downto 2**(N-1)) ,  c0, c2, s1 );
			A3 : Adder generic map(N => 2**(N-1)) port map( KL((2**(N-1))-1 downto 0       ), s1,  c1, c3, X  );
			s2(0) <= c2;
			s2((2**(N-1))-1 downto 1) <= (others => '0');
			A4 : Adder generic map(N => 2**(N-1)) port map( KL( 2**N-1      downto 2**(N-1)), s2, c3, open, Z );
			ZXYT <= Z&X&Y&T;
		end generate;
	recursiveGenExit:
		if N = 0 generate
			ZXYT(0) <= AB(0) and CD(0);
			ZXYT(1) <= '0';
		end generate;
end Structural;