--DUBBIO
library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;

entity BIST is
	generic(
		width    : positive :=   8;	--data size
		depth    : positive := 256;	--number of cells
		addrSize : positive :=   8);
	port(
		clock   : in  std_logic;
		reset   : in  std_logic;
		go      : in  std_logic;
		working : out std_logic;
		result  : out std_logic;
		
		r      : out std_logic;
		w      : out std_logic;
		enable : out std_logic;
		address      : out std_logic_vector(addrSize-1 downto 0);
		dataToRam    : out std_logic_vector(   width-1 downto 0);
		dataFromRam  : in  std_logic_vector(   width-1 downto 0));
end BIST;

architecture Behavioral of BIST is
	type State is (Rest, Phase1, Phase2Read, Phase2Write, Phase3Read, Phase3Verify, Finished);
	signal currentState, nextState : State;
	signal tictoc : std_logic := '0';
begin
	process(clock, reset)
	begin
		if(clock = '1' and clock'event) then
			tictoc <= not tictoc;
			if(reset = '1')
				then currentState <= Rest;
				else currentState <= nextState;
			end if;
		end if;
	end process;
	
	process(tictoc)
		variable success : boolean;
		variable count : natural;
	begin
		case currentState is
			when Rest =>
				r <= '0';
				w <= '0';
				address <= (others => '0');
				dataToRam <= (others => '0');
				count := 0;
				working <= '0';
				result <= '0';
				enable <= '0';
				success := true;
				if go = '1'
					then nextState <= Phase1;
					else nextState <= Rest;
				end if;
			when Phase1 =>
				r <= '0';
				w <= '1';
				enable <= '1';
				address <= conv_std_logic_vector(count, addrSize);
				dataToRam <= (others => '0');
				count := count + 1;
				working <= '1';
				result <= '0';
				if count = depth
					then nextState <= Phase2Read; count := 0;
					else nextState <= Phase1;
				end if;
			when Phase2Read =>
				r <= '1';
				w <= '0';
				enable <= '1';
				address <= conv_std_logic_vector(count, addrSize);				
				working <= '1';
				result <= '0';
				nextState <= Phase2Write;
			when Phase2Write =>
				success := success and dataFromRam = (dataFromRam'range => '0');
				r <= '0';
				w <= '1';
				enable <= '1';
				address <= conv_std_logic_vector(count, addrSize);
				dataToRam <= (others => '1');
				working <= '1';
				result <= '0';
				count := count + 1;
				if count = depth
					then nextState <= Phase3Read; count := 0;
					else nextState <= Phase2Read;
				end if;
			when Phase3Read =>
				r <= '1';
				w <= '0';
				enable <= '1';
				address <= conv_std_logic_vector(count, addrSize);				
				working <= '1';
				result <= '0';
				nextState <= Phase3Verify;
			when Phase3Verify =>
				success := success and dataFromRam = (dataFromRam'range => '1');
				working <= '1';
				result <= '0';
				enable <= '1';
				count := count + 1;
				if count = depth
					then nextState <= Finished; count := 0;
					else nextState <= Phase3Read;
				end if;
			when Finished =>
				r <= '0';
				w <= '1';
				enable <= '0';
				if success then result <= '1'; else result <= '0'; end if;
				working <= '0';
				nextState <= Finished;
		end case;
	end process;
end Behavioral;