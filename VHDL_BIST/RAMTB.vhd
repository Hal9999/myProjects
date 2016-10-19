library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;
 
ENTITY RAMTB IS
END RAMTB;
 
ARCHITECTURE behavior OF RAMTB IS 

    COMPONENT RAM
	generic( width : integer :=   8;	--data size
				depth : integer := 256;	--number of cells
				addr  : integer :=   8);
	port(
		clock, enable, r, w    : in  std_logic;
		address  : in  std_logic_vector( addr-1 downto 0);
		dataIn   : in  std_logic_vector(width-1 downto 0);
		dataOut  : out std_logic_vector(width-1 downto 0)
	);
    END COMPONENT;

   signal clock : std_logic := '0';
   signal enable : std_logic := '0';
   signal r : std_logic := '0';
   signal w : std_logic := '0';
   signal address : std_logic_vector(7 downto 0) := (others => '0');
   signal dataIn : std_logic_vector(15 downto 0) := (others => '0');

   signal dataOut : std_logic_vector(15 downto 0);

   constant clock_period : time := 4 ns;
 
BEGIN
   uut: RAM generic map( width => 16, depth => 256, addr => 8)
	PORT MAP (
          clock => clock,
          enable => enable,
          r => r,
          w => w,
          address => address,
          dataIn => dataIn,
          dataOut => dataOut
        );

   clock_process :process
   begin
		clock <= '0';
		wait for clock_period/2;
		clock <= '1';
		wait for clock_period/2;
   end process;

   stim_proc: process
   begin		
wait for Clock_period;	
		
		for i in 0 to 255 loop
			enable <= '1';
			w <= '1';
			r <= '0';
			address <= conv_std_logic_vector(i, 8);
			dataIn <= (others => '0');
			wait for Clock_period;
		end loop;
		
		for i in 0 to 255 loop
			enable <= '1';
			w <= '0';
			r <= '1';
			address <= conv_std_logic_vector(i, 8);
			wait for Clock_period;
			assert dataOut = x"0000";
			
			enable <= '1';
			w <= '1';
			r <= '0';
			address <= conv_std_logic_vector(i, 8);
			dataIn <= (others => '1');
			wait for Clock_period;
		end loop;
		
		for i in 0 to 255 loop
			enable <= '1';
			w <= '0';
			r <= '1';
			address <= conv_std_logic_vector(i, 8);
			wait for Clock_period;
			assert dataOut = x"FFFF";
		end loop;

      wait;
   end process;

END;
