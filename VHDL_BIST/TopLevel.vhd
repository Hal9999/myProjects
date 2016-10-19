library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity TopLevel is
	port(
		reset   : in  std_logic;
		clock   : in  std_logic;
		go      : in  std_logic;
		working : out std_logic;
		result  : out std_logic);
end TopLevel;

architecture Structural of TopLevel is
	component RAM is
		generic(
			width : integer :=   8;	--data size
			depth : integer := 256;	--number of cells
			addr  : integer :=   8);
		port(
			clock, enable, r, w : in  std_logic;
			address : in  std_logic_vector( addr-1 downto 0);
			dataIn  : in  std_logic_vector(width-1 downto 0);
			dataOut : out std_logic_vector(width-1 downto 0));
	end component;
	component BIST is
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
	end component;
	
	signal r, w, enable : std_logic;
	signal address : std_logic_vector(15 downto 0);
	signal dataToRam, dataFromRam : std_logic_vector(15 downto 0);
begin
	ram0  : RAM  generic map(width => 16, depth => 2**16, addr => 16)
  				    port    map(clock, enable, r, w, address, dataToRam, dataFromRam);
	bist0 : BIST generic map(width => 16, depth => 2**16, addrSize => 16)
					 port    map(clock, reset, go, working, result, r, w, enable, address, dataToRam, dataFromRam);
end Structural;

