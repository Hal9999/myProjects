library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity ALU is
	port(
			A   : in  std_logic_vector(7 downto 0);
			B   : in  std_logic_vector(7 downto 0);
			Sel : in  std_logic_vector(1 downto 0);
			Res : out std_logic_vector(7 downto 0)
		 );
end ALU;

architecture Behavioral of ALU is
begin
	process(A, B, Sel)
	begin
		case Sel is
			--ADD
			when "00" => Res <= A + B;
			--SUB
			when "01" => Res <= A + (not B) + 1;
			--OR
			when "10" => Res <= A or B;
			--AND
			when "11" => Res <= A and B;
			when others => Res <= "ZZZZZZZZ";
		end case;
	end process;
end Behavioral;