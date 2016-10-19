<?php
	function exception_handler($exception) { echo "<div class=\"error\">Errore interno del server. Riprova pi&ugrave tardi, grazie.</div>"; exit; }
    set_exception_handler('exception_handler');
    
	include 'mvc/controller.php';
	
	$controller = new Controller('index.php');
	try
	{
		$action = $controller->getAction();
	}
	catch(Exception $e)
	{
		$action['action'] = Action::Error;
		//$action['result'] = $e->getFile() . ": " . $e->getLine() . ": " . $e->getMessage();
	}
?>
<!DOCTYPE html>
<html>
    <head>
        <script type="text/javascript" src="libs/jquery-2.1.4.min.js"></script>
        <script type="text/javascript" src="validators.js"></script>
        <meta charset="UTF-8">
        <title>Stock Market</title>
        <link rel="stylesheet" href="styles/style.css" type="text/css"/>
        <link rel="icon" href="images/favicon.png" type="image/png" />
    </head>
    <body>
    <?php include('header.html'); ?>
    <div id="main">
		<div id="sidebar">
			<?php if($controller->isLogged()): ?>
				<a href="index.php">Home</a><br/><br/>
				<a href="index.php?action=buy">Compra</a><br/><br/>
				<a href="index.php?action=sell">Vendi</a><br/><br/>
				<a href="index.php?action=logout">Logout</a><br/><br/>
			<?php else: ?>
				<a href="index.php">Home</a><br/><br/>
				<a href="index.php?action=login">Autenticazione</a><br/><br/>
				<a href="index.php?action=register">Registrazione</a><br/><br/>
			<?php endif;?>
    	</div>
    	<div id="mainContent">
			<?php
				if($action['action']===Action::Login)
				{
					?>
					<form id="loginForm" action="index.php" method="post">
						<fieldset>
							<p class="fieldsetTitle">Login</p>
							<label for="userEmailField">E-mail:</label><br>
							<input type="text" name="userEmail" required id="userEmailField"/><br>
							<label for="userPasswordField">Password:</label><br>
							<input type="password" name="userPassword" required id="userPasswordField"/><br>
							<input type="hidden" name="action" value="login">
							<button type="submit" id="loginSubmit">Login</button>
						</fieldset>
					</form>
					<?php
					if($action['result']===Result::Incomplete)
						echo "<div class=\"error\">Dati immessi non corretti.</div>";
					else if($action['result']===Result::Failed)
						echo "<div class=\"error\">Credenziali non corrette.</div>";
				}
				else if($action['action']===Action::Logout)
				{}
				else if($action['action']===Action::GenericHomepage)
				{
					try
					{
						$db = new Database();
						$market = new StockMarket($db);
						$buys = $market->getBuys();
						$sells = $market->getSells();
					}
					catch(Exception $e){ $action['action'] = Action::Error; }
					if($action['action']!==Action::Error): ?>
						<div class="fieldset">
							<table>
							<caption>Compravendita azioni</caption>
							<tr><th>Quantit&agrave; in Acquisto</th><th>Prezzo in Acquisto</th><th>Quantit&agrave; in vendita</th><th>Prezzo in Vendita</th></tr>
							<?php
								for($i=0; $i<5; $i++)
								{
									echo "<tr>";
									if($buys[$i]['price']==0)
										echo "<td></td><td></td>";
									else
										echo "<td>" . $buys[$i]['quantity'] . "</td><td>" . $buys[$i]['price'] . "</td>";
									
									if($sells[$i]['price']==0)
										echo "<td></td><td></td>";
									else
										echo "<td>" . $sells[$i]['quantity'] . "</td><td>" . $sells[$i]['price'] . "</td>";
								
									echo "</tr>";
								}
							?>
							</table>
						</div>
					<?php endif;
				}
				else if($action['action']===Action::PersonalHomePage)
				{
					try
					{
						$db = new Database();
						
						$market = new StockMarket($db);
						$buys = $market->getBuys();
						$sells = $market->getSells();
						
						$user = new User($db);
						$user->getUserById($action['s219668_userID']);
						if($user===false)
							throw new Exception("Model and Controller are out of sync.");
						$history = $user->getTransactionHistory();
					}
					catch(Exception $e){ $action['action'] = Action::Error; }
					if($action['action']!==Action::Error): ?>
						<div class="fieldset">
							<div class="fieldsetTitle">Homepage utente</div>
							Utente: <em><?php echo $user->getName();   ?> <?php echo $user->getName(); ?></em><br/>
							E-mail: <em><?php echo $user->getEmail();  ?></em><br/>
							Saldo:  <em><?php echo $user->getEuros();  ?></em><br/>
							Azioni: <em><?php echo $user->getStocks(); ?></em><br/>
							<div class="fieldsetTitle">Compravendita azioni</div>
							<table>
							<tr><th>Quantit&agrave; in Acquisto</th><th>Prezzo in Acquisto</th><th>Quantit&agrave; in vendita</th><th>Prezzo in Vendita</th></tr>
							<?php
								for($i=0; $i<5; $i++)
								{
									echo "<tr>";
									if($buys[$i]['price']==0)
										echo "<td></td><td></td>";
									else
										echo "<td>" . $buys[$i]['quantity'] . "</td><td>" . $buys[$i]['price'] . "</td>";
									
									if($sells[$i]['price']==0)
										echo "<td></td><td></td>";
									else
										echo "<td>" . $sells[$i]['quantity'] . "</td><td>" . $sells[$i]['price'] . "</td>";
								
									echo "</tr>";
								}
							?>
							</table><br/>
							<?php if(count($history)==0): ?>
								Nessuna transazione presente.</div>
							<?php else: ?>
								<div class="fieldsetTitle">Storico transazioni</div>
								<table>
								<tr><th>Tipo</th><th>Data</th><th>Azioni</th><th>Importo &euro;</th></tr>
								<?php
									foreach($history as $row)
									{
										echo "<tr>";
										echo "<td>" . ($row['type']==0 ? "Vendita" : "Acquisto") . "</td><td>" . $row['timestamp'] . "</td><td>" . $row['stocks'] . "</td><td>" . $row['euros'] . "</td>";
										echo "</tr>";
									}
								?>
								</table></div>
							<?php endif;
						endif;
				}
				else if($action['action']===Action::Register)
				{
					if($action['result']===Result::OK)
						echo "<div class=\"good\">Registrazione completata.</div>";
					else
					{
						if($action['result']===Result::Incomplete)
							echo "<div class=\"error\">Dati immessi non corretti.</div>";
						else if($action['result']===Result::Failed)
							echo "<div class=\"error\">Registrazione fallita: e-mail gi&agrave; in uso.</div>";
						
						?>
						<form id="registerForm" action="index.php" method="post">
							<fieldset>
								<p class="fieldsetTitle">Registrazione</p>
								<label for="userEmailField">E-mail:</label><br>
								<input type="text" name="userEmail" required id="userEmailField"/><br>
								<label for="userNameField">Nome:</label><br>
								<input type="text" name="userName" required id="userNameField"/><br>
								<label for="userSurnameField">Cognome:</label><br>
								<input type="text" name="userSurname" required id="userSurnameField"/><br>
								<label for="userPasswordField">Password:</label><br>
								<input type="password" name="userPassword" required id="userPasswordField"/><br>
								<label for="userPasswordField2">Ripeti password:</label><br>
								<input type="password" required id="userPasswordField2"/><br>
								<input type="hidden" name="action" value="register">
								<button type="submit" id="registerSubmit">Registra</button>
							</fieldset>
						</form>
						<?php
					}
				}
				else if($action['action']===Action::Buy && ($action['result']===Result::Incomplete || $action['result']===Result::Empty_))
				{
					if($action['result']===Result::Incomplete)
						echo "<div class=\"error\">Numero non valido, riprovare.</div>";
					?>
						<form id="buyForm" action="index.php" method="post">
							<fieldset>
								<p class="fieldsetTitle">Acquisto</p>
								<label for="quantityField">Quantit&agrave;:</label><br>
								<input type="number" name="quantity" required id="buyQuantityField"/><br>
								<input type="hidden" name="action" value="buy">
								<button type="submit" id="buySubmit">Compra</button>
							</fieldset>
						</form>
					<?php		
				}
				else if($action['action']===Action::Buy)
				{
					if($action['result']===Result::MarketDeficit)
						echo "<div class=\"error\">Azioni nel mercato non sufficienti per completare l'acquisto.</div>";
					else if($action['result']===Result::UserDeficit)
						echo "<div class=\"error\">Fondi dell'utente non sufficienti per completare l'acquisto.</div>";
					else if($action['result']===Result::OK)
						echo "<div class=\"good\">Acquisto eseguito con successo.</div>";
					else
						$action['action'] = Action::Error;
				}
				else if($action['action']===Action::Sell && ($action['result']===Result::Incomplete || $action['result']===Result::Empty_))
				{
					if($action['result']===Result::Incomplete)
						echo "<div class=\"error\">Numero non valido, riprovare.</div>";
					?>
						<form id="sellForm" action="index.php" method="post">
							<fieldset>
								<p class="fieldsetTitle">Vendita</p>
								<label for="quantityField">Quantit&agrave;:</label><br>
								<input type="number" name="quantity" required id="sellQuantityField"/><br>
								<input type="hidden" name="action" value="sell">
								<button type="submit" id="sellSubmit">Vendi</button>
							</fieldset>
						</form>
					<?php		
				}
				else if($action['action']===Action::Sell)
				{
					if($action['result']===Result::MarketDeficit)
						echo "<div class=\"error\">Richiesta del mercato non sufficiente per completare la vendita.</div>";
					else if($action['result']===Result::UserDeficit)
						echo "<div class=\"error\">Azioni possedute dell'utente non sufficienti per completare la vendita.</div>";
					else if($action['result']===Result::OK)
						echo "<div class=\"good\">Vendita eseguita con successo.</div>";
					else
						$action['action'] = Action::Error;
				}
				else if($action['action']===Action::CookieCheck)
				{
					if($action['result']===Result::Failed)
						echo "<div class=\"error\">I cookie sono disattivati e questo sito ne richiede l'uso. Attivali, grazie.</div>";
				}
				
				if($action['action']===Action::Error)
				{
					echo "<div class=\"error\">Errore interno del server. Riprova pi&ugrave tardi, grazie.</div>";
				}
			?>
		<noscript>
			<div class="error">Javascript disattivato o non supportato: il sito potrebbe non funzionare correttamente.</div>
		</noscript>
		</div>
	</div>
	<?php include('footer.html'); ?>
    </body>
</html>