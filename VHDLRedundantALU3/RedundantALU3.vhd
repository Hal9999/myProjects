library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity RedundantALU3 is
	port(
		A  	    : in  std_logic_vector(15 downto 0);
		B         : in  std_logic_vector(15 downto 0);
		C		    : in  std_logic_vector(15 downto 0);
		D   		 : in  std_logic_vector(15 downto 0);
		E		    : in  std_logic_vector(15 downto 0);
		F   		 : in  std_logic_vector(15 downto 0);
		opCode    : in  std_logic_vector( 1 downto 0);
		output    : out std_logic_vector(15 downto 0);
		dataValid : out std_logic
	);
end RedundantALU3;

architecture Behavioral of RedundantALU3 is
	component ALU
		generic (N: integer := 4);
		port(
				A   : in  std_logic_vector(N-1 downto 0);
				B   : in  std_logic_vector(N-1 downto 0);
				Sel : in  std_logic_vector(  1 downto 0);
				Res : out std_logic_vector(N-1 downto 0)
			 );
	end component;
	
	signal result0, result1, result2 : std_logic_vector(15 downto 0);
begin
	ALU0 : ALU generic map(N=>16) port map(A, B, opCode, result0);
	ALU1 : ALU generic map(N=>16) port map(C, D, opCode, result1);
	ALU2 : ALU generic map(N=>16) port map(E, F, opCode, result2);
	
	process(result0, result1, result2)
	begin
		dataValid <= '1';
		if    result0 = result1 or result0 = result2 then output <= result0;
		elsif result1 = result0 or result1 = result2 then output <= result1;
	 --elsif result2 = result0 or result2 = result1 then output <= result2;
		else
			dataValid <= '0';
			output <= (others => 'Z');
		end if;
	end process;
end Behavioral;

