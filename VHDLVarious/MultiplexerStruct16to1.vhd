library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity Multiplexer16to1 is
	port(
		i   : in  std_logic_vector (15 downto 0);
		sel : in  std_logic_vector ( 3 downto 0);
		o   : out std_logic
	);
end entity Multiplexer16to1;

architecture Structural of Multiplexer16to1 is
	 COMPONENT Mux4to1
    PORT(
         i   : IN  std_logic_vector(3 downto 0);
         sel : IN  std_logic_vector(1 downto 0);
         o   : OUT std_logic
        );
    END COMPONENT;
	
	signal sig : std_logic_vector(3 downto 0);
	
begin
	m1: Mux4to1 port map (i(3  downto 0),  sel(1 downto 0), sig(0));
	m2: Mux4to1 port map (i(7  downto 4),  sel(1 downto 0), sig(1));
	m3: Mux4to1 port map (i(11 downto 8),  sel(1 downto 0), sig(2));
	m4: Mux4to1 port map (i(15 downto 12), sel(1 downto 0), sig(3));
	m5: Mux4to1 port map (sig, sel(3 downto 2), o);
end architecture Structural;