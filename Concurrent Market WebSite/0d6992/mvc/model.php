<?php
	class Crypto
	{
		const fixedSalt = '67898872359862354615';
		public static function hash($text, $salt)
		{
			//$salt=md5($salt); //FIXME rifare il db con questo sale.
			return md5($text . $salt . self::fixedSalt);
		}
	}
	
	class Database
	{
		const host = 'localhost';
		const user = 's219668';
		const password = 'dlecuria';
		const databaseName = 's219668';
		private $connection;
		
		function __construct()
		{
			mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
			$this->connection = new mysqli(self::host, self::user, self::password, self::databaseName);
			$this->connection->set_charset("utf8");
			if($this->connection->connect_error) throw new Exception("DB connection failed");
		}
		
		function getConnection() { return $this->connection; }
		
		function __destruct() { $this->connection->close(); }
	}
	
	class StockMarket
	{
		private $database;
		
		public function __construct($dbConnection) { $this->database = $dbConnection->getConnection(); }
		
		public function getBuys() { return $this->getStockValues(1); }
		
		public function getSells() { return $this->getStockValues(0); }
		
		private function getStockValues($type)
		{
			$result = array();
				
			if($type===1)
				$sql = "SELECT `price`, `quantity` FROM `buys` ORDER BY `price` DESC LIMIT 5";
			else
				$sql = "SELECT `price`, `quantity` FROM `sells` ORDER BY `price` ASC LIMIT 5";
			
			if(($query = $this->database->query($sql))===false) throw new Exception("Query Failed");
			
			while($row = $query->fetch_assoc())
				$result[] = $row;
			
			for($i=count($result); $i<5; $i++)
				$result[] = array('price' => 0, 'quantity' => 0);
			
			$query->free_result();
				
			return $result;
		}
		
		public function sell($userId, $sellingStocks)
		{
			mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
			$userId = $this->database->real_escape_string($userId);		//FIXME forse è eccessivo perchè l'id è generato dal dbms
			$result = 0;
			
			try
			{
				$this->database->autocommit(false);
				$query = $this->database->query("SELECT `price` FROM `buys` FOR UPDATE");	//FIXME va fatto?
				$query->free_result();
				//$this->database->query("LOCK TABLES `buys` WRITE, `users` WRITE, `history` WRITE");
				
				$query = $this->database->query("SELECT `stocks`, `euros` FROM `users` WHERE `id`='" . $userId . "' FOR UPDATE");
				$row = $query->fetch_assoc();
				$stocks = $row['stocks'];
				$oldBalance = $balance = $row['euros'];
				$query->free_result();
				if($stocks < $sellingStocks) throw new Exception(-2);		//l'utente non ha abbastanza azioni
				
				$rowsToUpdate = array();
				$rowsToDelete = array();
				
				$highestPrice = PHP_INT_MAX;								//per scegliere la prossima riga da consumare
				$toSellCount = $sellingStocks;								//azioni da vendere
				
				do{
					$query = $this->database->query("SELECT `price`, `quantity` FROM `buys` WHERE `price`<$highestPrice ORDER BY `price` DESC LIMIT 1 FOR UPDATE");
					if($query->num_rows != 1) throw new Exception(-1);		//se siamo qui significa che ci servono più righe, ma se non ce ne sono ho un errore
					$row = $query->fetch_assoc();
					$highestPrice = $row['price'];
					
					$remainingRowStocks = $row['quantity'] - $toSellCount;
					if($remainingRowStocks>=0)								//allora questa riga è sufficiente: dobbiamo o dropparla o aggiornarla e questo è l'ultimo giro di while
					{
						$balance += $toSellCount * $highestPrice;
						$stocks -= $toSellCount;
						$toSellCount = 0;
						
						if($remainingRowStocks==0)
							$rowsToDelete[] = $highestPrice;
						else
							$rowsToUpdate[] = array('price' => $highestPrice, 'quantity' => $remainingRowStocks);
					}
					else													//consumo completamente questa riga e me ne serve un'altra
					{
						$balance += $row['quantity'] * $highestPrice;
						$stocks -= $row['quantity'];
						$toSellCount -= $row['quantity'];
						$rowsToDelete[] = $highestPrice;
					}
					$query->free_result();
				} while($toSellCount>0);
				
				//ora ho i dati sulle righe da aggiornare o cancellare, i dati sul saldo dell'utente e posso anche inserire una riga nello storico delle operazioni
				foreach($rowsToDelete as $price)
					$this->database->query("DELETE FROM `buys` WHERE `price`=$price");
				
				foreach($rowsToUpdate as $update)
					$this->database->query("UPDATE `buys` SET `quantity`=" . $update['quantity'] . " WHERE `price`=" . $update['price']);
				
				$this->database->query("UPDATE `users` SET `stocks`=$stocks, `euros`=$balance WHERE `id`='" . $userId . "'");
				
				$this->database->query("INSERT INTO `history` (`userId`, `type`, `stocks`, `euros`) VALUES ('" . $userId . "' , 0, -$sellingStocks, " . ($balance-$oldBalance) . ")" );
				
				if(!$this->database->commit()) throw Exception(-3);
				$this->database->autocommit(true);
			}
			catch(Exception $e)
			{
				$this->database->rollback();
				$result = $e->getMessage();
			}
			
			$this->database->autocommit(true);
			return $result;
		}
		
		public function buy($userId, $buyingStocks)
		{
			mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
			$userId = $this->database->real_escape_string($userId);		//FIXME forse è eccessivo perchè l'id è generato dal dbms
			$result = 0;
			
			try
			{
				$this->database->autocommit(false);
				$query = $this->database->query("SELECT `price` FROM `sells` FOR UPDATE");	//FIXME va fatto?
				$query->free_result();
				//$this->database->query("LOCK TABLES `sells` WRITE, `users` WRITE, `history` WRITE");
				
				$query = $this->database->query("SELECT `stocks`, `euros` FROM `users` WHERE `id`='" . $userId . "' FOR UPDATE");
				$row = $query->fetch_assoc();
				$stocks = $row['stocks'];
				$oldBalance = $balance = $row['euros'];
				$query->free_result();
				
				$rowsToUpdate = array();
				$rowsToDelete = array();
				
				$lowestPrice = 0;												//per scegliere la prossima riga da consumare
				$toBuyCount = $buyingStocks;									//azioni da vendere
				
				do{
					$query = $this->database->query("SELECT `price`, `quantity` FROM `sells` WHERE `price`>$lowestPrice ORDER BY `price` ASC LIMIT 1 FOR UPDATE");
					if($query->num_rows != 1) throw new Exception(-2);			//se siamo qui significa che ci servono più righe, ma se non ce ne sono ho un errore
					$row = $query->fetch_assoc();
					$lowestPrice = $row['price'];
					$stockQuantity = $row['quantity'];
						
					$remainingRowStocks = $stockQuantity - $toBuyCount;
					if($remainingRowStocks>=0)									//allora questa riga è sufficiente: dobbiamo o dropparla o aggiornarla e questo è l'ultimo giro di while
					{
						$balance -= $toBuyCount * $lowestPrice;
						$stocks += $toBuyCount;
						$toBuyCount = 0;
		
						if($remainingRowStocks==0)
							$rowsToDelete[] = $lowestPrice;
						else
							$rowsToUpdate[] = array('price' => $lowestPrice, 'quantity' => $remainingRowStocks);
					}
					else														//consumo completamente questa riga e me ne serve un'altra
					{
						$balance -= $stockQuantity * $lowestPrice;
						$stocks += $stockQuantity;
						$toBuyCount -= $stockQuantity;
						$rowsToDelete[] = $lowestPrice;
					}
					$query->free_result();
				} while($toBuyCount>0);
				
				if($balance<0) throw new Exception(-1);							//l'utente non ha abbastanza fondi
				
				//ora ho i dati sulle righe da aggiornare o cancellare, i dati sul saldo dell'utente e posso anche inserire una riga nello storico delle operazioni
				foreach($rowsToDelete as $price)
					$this->database->query("DELETE FROM `sells` WHERE `price`=$price");
				
				foreach($rowsToUpdate as $update)
					$this->database->query("UPDATE `sells` SET `quantity`=" . $update['quantity'] . " WHERE `price`=" . $update['price']);
				
				$this->database->query("UPDATE `users` SET `stocks`=$stocks, `euros`=$balance WHERE `id`='" . $userId . "'");
				
				$this->database->query("INSERT INTO `history` (`userId`, `type`, `stocks`, `euros`) VALUES ('" . $userId . "' , 1, $buyingStocks, " . ($balance-$oldBalance) . ")" );
				
				if(!$this->database->commit()) throw Exception(-3);
				$this->database->autocommit(true);
			}
			catch(Exception $e)
			{
				$this->database->rollback();
				$result = $e->getMessage();
			}
			
			$this->database->autocommit(true);
			return $result;
		}
	}
	
	class User
	{
		private $database;
		
		private $loggedIn = false;
		private $id;
		private $name;
		private $surname;
		private $email;
		private $euros;
		private $stocks;
		
		public function __construct($dbConnection) { $this->database = $dbConnection->getConnection(); }
		
		public function getUserById($id)
		{
			$id = $this->database->real_escape_string($id);				//FIXME forse è eccessivo perchè l'id è generato dal dbms
			$this->loggedIn = false;
			
			$sql = "SELECT `email`, `name`, `surname`, `euros`, `stocks` FROM `users` WHERE `id`='" . $id . "'"; //FIXME è sicura questa query?
				
			$query = $this->database->query($sql);
				
			if($query == false) throw new Exception("Query Failed");	//FIXME in caso torno false e basta
			if($query->num_rows != 1) return false;
				
			$row = $query->fetch_assoc();
				
			$this->loggedIn = true;
			$this->id = $id;
			$this->name = $row['name'];
			$this->surname = $row['surname'];
			$this->email = $row['email'];
			$this->euros = $row['euros'];
			$this->stocks = $row['stocks'];
			
			$query->free_result();
			
			return true;
		}
		
		public function getUserByAuthentication($email, $password)
		{
			$email = $this->database->real_escape_string($email);
			$password = $this->database->real_escape_string($password);
			
			$this->loggedIn = false;
			$password = Crypto::hash($password, $email);
			$sql = "SELECT `id`, `name`, `surname`, `euros`, `stocks` FROM `users` WHERE `email`='" . $email . "' AND `password`='" . $password . "'";
			
			$query = $this->database->query($sql);
			
			if($query == false) throw new Exception("Query Failed");
			if($query->num_rows != 1) return false;
			
			$row = $query->fetch_assoc();
			
			$this->loggedIn = true;
			$this->id = $row['id'];
			$this->name = $row['name'];
			$this->surname = $row['surname'];
			$this->email = $email;
			$this->euros = $row['euros'];
			$this->stocks = $row['stocks'];
			
			$query->free_result();

			return true;
		}
		
		public function createUser($email, $name, $surname, $password)
		{
			$email = $this->database->real_escape_string($email);
			$name = $this->database->real_escape_string($name);
			$surname = $this->database->real_escape_string($surname);
			$password = $this->database->real_escape_string($password);
			
			$sql = "SELECT `id` FROM `users` WHERE `email`='" . $email ."'";
			
			$query = $this->database->query($sql);
			
			if($query===false) throw new Exception("Query Failed");
			if($query->num_rows !=0 ) return false;
			
			$query->free_result();
			
			$password = Crypto::hash($password, $email);
			$query = $this->database->query("INSERT INTO `users` (`email`, `name`, `surname`, `password`) VALUES ('" . $email . "', '" . $name . "', '" . $surname . "', '" . $password . "')" );
			if($query===false) return false;
			
			return true;
		}
		
		public function isLoggedIn() { return $this->loggedIn; }
		
		public function getId() { return $this->id; }
		
		public function getName() { return $this->name; }
		
		public function getSurname() { return $this->surname; }
		
		public function getEmail() { return $this->email; }
		
		public function getEuros() { return $this->euros; }
		
		public function getStocks() { return $this->stocks; }
		
		public function getTransactionHistory()
		{
			if(!$this->loggedIn) throw new Exception("Invalid user object state");
			
			$transactions = array();
			
			$sql = "SELECT `timestamp`, `type`, `stocks`, `euros` FROM `history` WHERE `userId`='" . $this->id . "' ORDER BY `timestamp` ASC";
			
			$query = $this->database->query($sql);
				
			if($query==false) throw new Exception("Query Failed");
			
			while($row = $query->fetch_assoc())
				$transactions[] = $row;
			
			$query->free_result();
			return $transactions;
		}
	}
?>