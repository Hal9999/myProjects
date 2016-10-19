library IEEE;
use ieee.std_logic_1164.ALL;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity Comparator is
	port (
			A       : in  std_logic_vector(7 downto 0);
			B       : in  std_logic_vector(7 downto 0);
			less    : out std_logic;
			equal   : out std_logic;
			greater : out std_logic
		 );
end Comparator;

architecture Behavioral of Comparator is
begin
	process(A, B)
	begin
		if (A<B) then
			greater <= '1'; equal <= '0'; less <= '0';
		elsif (A=B) then
			greater <= '0'; equal <= '1'; less <= '0';
		else
			greater <= '0'; equal <= '0'; less <= '1';
		end if;
	end process;
end Behavioral;

