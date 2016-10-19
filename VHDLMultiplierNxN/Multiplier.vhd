library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.MATH_REAL.ALL;

entity Multiplier is
	generic( N : positive := 16);
	port(
		a       : in  std_logic_vector (  N-1 downto 0);
		b       : in  std_logic_vector (  N-1 downto 0);
		product : out std_logic_vector (2*N-1 downto 0)
	);
end Multiplier;

architecture Structural of Multiplier is
	component Multiplier2PowerWide is
		generic( N : natural := 4);
		port(
			AB   : in  std_logic_vector ( 2**N-1 downto 0);
			CD   : in  std_logic_vector ( 2**N-1 downto 0);
			ZXYT : out std_logic_vector ((2**(N+1))-1 downto 0)
		);
	end component;
	
	constant width : integer := integer(ceil(log2(real(N))));
	signal extA, extB : std_logic_vector(2**width-1     downto 0);
	signal extProduct : std_logic_vector(2**(width+1)-1 downto 0);
begin
	extA(N-1 downto 0) <= a;
	extA(2**width-1 downto N) <= (others => '0');
	extB(N-1 downto 0) <= b;
	extB(2**width-1 downto N) <= (others => '0');
	MUL : Multiplier2PowerWide generic map(N => width) port map(extA, extB, extProduct);
	product <= extProduct(2*N-1 downto 0);
end Structural;