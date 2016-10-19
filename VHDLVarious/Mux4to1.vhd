library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity Mux4to1 is
	port(
		i    : in  std_logic_vector(3 downto 0);
		sel  : in  std_logic_vector(1 downto 0);
		o    : out std_logic
	);
end Mux4to1;

architecture Structural of Mux4to1 is
	component Mux2to1
		port(
			i    : in  std_logic_vector(1 downto 0);
			sel  : in  std_logic_vector(0 downto 0);
			o    : out std_logic
		);
	end component;
	
	signal sig : std_logic_vector(1 downto 0);
	
begin
	m1: Mux2to1 port map (i(1 downto 0), sel(0 downto 0), sig(0));
	m2: Mux2to1 port map (i(3 downto 2), sel(0 downto 0), sig(1));
	m3: Mux2to1 port map (sig, sel(1 downto 1), o);
end architecture Structural;

