library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity ALU is
	generic (N: integer := 4);
	port(
			A      : in  std_logic_vector(N-1 downto 0);
			B      : in  std_logic_vector(N-1 downto 0);
			Sel    : in  std_logic_vector(  1 downto 0);
			enable : in  std_logic;
			output : out std_logic_vector(N-1 downto 0)
		 );
end ALU;

architecture Behavioral of ALU is
begin
	process(A, B, Sel, enable)
	begin
		if enable = '0' then output <= (others => 'Z');
		else case Sel is
				--ADD
				when "00" => output <= A + B;
				--SUB
				when "01" => output <= A + (not B) + 1;
				--OR
				when "10" => output <= A or B;
				--AND
				when "11" => output <= A and B;
				when others => output <= (others => 'Z');
			end case;
		end if;
	end process;
end Behavioral;