library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;

entity Adder is
	generic (N: integer := 4);
	port(
			a        : in  std_logic_vector(N-1 downto 0);
			b        : in  std_logic_vector(N-1 downto 0);
			carryIn  : in  std_logic;
			carryOut : out std_logic;
			sum      : out std_logic_vector(N-1 downto 0)
		 );
end Adder;

architecture Behavioral of Adder is
	signal result : std_logic_vector(N downto 0);
begin
	result <= ("0"&A) + ("0"&B) + ("0"&carryIn);
	sum <= result(N-1 downto 0);
	carryOut <= result(N);
end Behavioral;