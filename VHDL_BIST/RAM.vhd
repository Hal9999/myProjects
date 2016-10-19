--DUBBIO 6: va bene implementare la ram con un singolo processo pilotato dal solo clock?
--DUBBIO 7: si può rendere vhdl case-sensitive?
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;

entity RAM is
	generic(
		width : integer :=   8;	--data size
		depth : integer := 256;	--number of cells
		addr  : integer :=   8);
	port(
		clock, enable, r, w : in  std_logic;
		address : in  std_logic_vector( addr-1 downto 0);
		dataIn  : in  std_logic_vector(width-1 downto 0);
		dataOut : out std_logic_vector(width-1 downto 0));
end RAM;

architecture Behavioral of RAM is
	type MemoryType is array(0 to depth-1) of std_logic_vector(width-1 downto 0);
	
	signal memory : MemoryType;
begin
	process(clock)
	begin
		if(clock'event and clock = '1') then
			if enable = '1' then
				if    w = '1' then memory(conv_integer(address)) <= dataIn;
				elsif r = '1' then dataOut <= memory(conv_integer(address));
				end if;
			else dataOut <= (dataOut'range => 'Z');
			end if;
		end if;
	end process;
end Behavioral;