library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity Multiplexer is
	port (
				I   : in  std_logic_vector(7 downto 0);
				sel : in  std_logic_vector(2 downto 0);
				O   : out std_logic
		  );
end Multiplexer;

architecture Behavioral of Multiplexer is
begin
	process(I, sel)
	begin
		case sel is
			when "000" => O <= I(0);
			when "001" => O <= I(1);
			when "010" => O <= I(2);
			when "011" => O <= I(3);
			when "100" => O <= I(4);
			when "101" => O <= I(5);
			when "110" => O <= I(6);
			when "111" => O <= I(7);
			when others => O <= 'X';
		end case;
	end process;
end Behavioral;

