library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity Mux2to1 is
	port(
		i    : in  std_logic_vector(1 downto 0);
		sel  : in  std_logic_vector(0 downto 0);
		o    : out std_logic
	);
end Mux2to1;

architecture Structural of Mux2to1 is
	component OR_ent
		port(
			x: in std_logic;
			y: in std_logic;
			F: out std_logic
		);
	end component;
	component NOR_ent
		port(
			x: in std_logic;
			y: in std_logic;
			F: out std_logic
		);
	end component;
	component AND_ent
		port(
			x: in std_logic;
			y: in std_logic;
			F: out std_logic
		);
	end component;
	
	signal s1, s2, s3 : std_logic;
	
begin
	and1: AND_ent port map (i(1), sel(0), s3);
	nor1: NOR_ent port map (sel(0), sel(0), s1);
	and2: AND_ent port map (s1, i(0), s2);
	or1:  OR_ent  port map (s2, s3, o);
end Structural;

