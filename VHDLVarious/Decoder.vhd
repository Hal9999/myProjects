library IEEE;
use ieee.std_logic_1164.ALL;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity Decoder is
	port (
			A : in   std_logic_vector(2 downto 0);
			O : out  std_logic_vector(7 downto 0)
		 );
end Decoder;

architecture Behavioral of Decoder is
begin
	process(A)
	begin
		case A is
			when "000" => O <= "00000001";
			when "001" => O <= "00000010";
			when "010" => O <= "00000100";
			when "011" => O <= "00001000";
			when "100" => O <= "00010000";
			when "101" => O <= "00100000";
			when "110" => O <= "01000000";
			when "111" => O <= "10000000";
			when others => O <= "XXXXXXXX";
		end case;
	end process;
end Behavioral;

