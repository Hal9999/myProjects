<?php
	include 'model.php';
	
	abstract class Action
	{
		const Login = 0;
		const Logout = 1;
		const GenericHomepage = 4;
		const PersonalHomePage = 5;
		const Register = 6;
		const Buy = 7;
		const Sell = 8;
		const Error = 9;
		const CookieCheck = 10;
	}
	
	abstract class Result
	{
		const OK = 0;
		const Failed = 1;
		const Incomplete = 2;
		const Empty_ = 3;
		const MarketDeficit = 4;
		const UserDeficit = 5;
	}
	
	class Validators
	{
		private static function sanitize($text)
		{
			$text = strip_tags($text);
			$text = trim($text);
			$text = stripslashes($text);
			$text = htmlspecialchars($text);
			return $text;
		}
		
		public static function sanitizeAction($text)
		{
			$text = Validators::sanitize($text);
			switch($text)
			{
				case 'login': return Action::Login;
				case 'logout': return Action::Logout;
				case 'register': return Action::Register;
				case 'buy': return Action::Buy;
				case 'sell': return Action::Sell;
				case 'error': return Action::Error;
				case 'cookieCheck' : return Action::CookieCheck;
				default: return false;
			}
		}
		
		public static function sanitizePositiveInteger($text)
		{
			$text = Validators::sanitize($text);
			if(preg_match("/^\d+$/", $text) && settype($quantity, 'integer') && $text>0)
				return $text;
			else return false;
		}
		
		public static function sanitizeEmail($text)
		{
			$text = Validators::sanitize($text);
			return preg_match("/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$/", $text) ? $text : false;
		}
		
		public static function sanitizeName($text)
		{
			$text = Validators::sanitize($text);
			return preg_match("/^[a-zA-Z']+$/", $text) ? $text : false;
		}
		
		public static function sanitizeSurname($text)
		{
			return Validators::sanitizeName($text);
		}
		
		public static function sanitizePassword($text)
		{
			$text = Validators::sanitize($text);
			return preg_match("/^[a-zA-Z0-9]{2,}$/", $text) ? $text : false;
		}
	}
	
	class Controller
	{
		private $viewAddress;
		private $logged = false;
		
		public function __construct($viewAddress)
		{
			$this->viewAddress = $viewAddress;
		}
		
		private static function redirectToHTTPS()
		{
			if($_SERVER['HTTPS']!="on")
			{
				$redirect = "https://".$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];
				header("Location:$redirect");
			}
		}
		
		private function redirect($action='')
		{
			header('HTTP/1.1 307 temporary redirect');
			if($action==='')
				header('Location: ' . $this->viewAddress);
			else
				header('Location: ' . $this->viewAddress . '?action='.urlencode($action));
			exit;
		}
		
		private function resetSession()
		{
			$_SESSION = array();
			if(ini_get("session.use_cookies"))
			{
				$parameters = session_get_cookie_params();
				setcookie(session_name(), '', time()-3600*24, $parameters['path'], $parameters['domain'], $parameters['secure'], $parameters['httponly']);
			}
			session_destroy();
		}
		
		public function isLogged() { return $this->logged; }
		
		public function getAction()
		{
			Controller::redirectToHTTPS();
			
			$status = array();
			
			$action = isset($_REQUEST['action']) ? $action=Validators::sanitizeAction($_REQUEST['action']) : false;
			if(isset($_REQUEST['action']) && $action===false) Controller::redirect();
			
			session_start();
			
			if($action===Action::CookieCheck)
			{
				if(isset($_COOKIE['foo']) && $_COOKIE['foo'] == 'bar')
				{
					Controller::redirect();
				}
				else
				{
					$status['action'] = Action::CookieCheck;
					$status['result'] = Result::Failed;
					return $status;
				}
			}
			
			if(count($_COOKIE) == 0)
			{
				setcookie('foo', 'bar', time() + 3600);		//set a cookie to test
				Controller::redirect('cookieCheck');		//redirecting to the same page to check
				//header("location: errors.php?check=true");	
			}
			
			if(isset($_SESSION['s219668_userID']))
			{
				//allora ho uno userId e quindi una sessione: controlo se è ancora valida
				if(time()-$_SESSION['s219668_lastOperationTime'] > 2*60)	//sessione scaduta
				{
					//FIXME potrei restituire un certo valore cosicchè la view mostra un messaggio di sessione scaduta
					Controller::resetSession();
					if(isset($_REQUEST['action'])) Controller::redirect($_REQUEST['action']);
					else Controller::redirect();
				}
				else
				{
					$_SESSION['s219668_lastOperationTime'] = time();
					$this->logged = true;
				}
			}
			
			if($action===Action::Error)
			{
				$status['action'] = Action::Error;
			}
			else if($this->logged===true && $action===Action::Logout)			//sono loggato e mi voglio sloggare
			{
				Controller::resetSession();
				Controller::redirect();
			}
// 			if($this->logged===true && $action===Action::Login)
// 			{
// 				Controller::redirect();
// 			}
			else if($this->logged===false && $action===Action::Login)		//non sono loggato e mi voglio loggare
			{
				$status['action'] = Action::Login;
				
				$email = isset($_REQUEST['userEmail']) ? Validators::sanitizeEmail($_REQUEST['userEmail']) : false;
				$password = isset($_REQUEST['userPassword']) ? Validators::sanitizePassword($_REQUEST['userPassword']) : false;
				
				if(!isset($_REQUEST['userEmail']) && !isset($_REQUEST['userPassword']))
					$status['result'] = Result::Empty_;
				else if((isset($_REQUEST['userEmail']) && $email===false) || (isset($_REQUEST['userPassword']) && $password===false))
					$status['result'] = Result::Incomplete;
				else if($email!==false && $password!==false)
				{
					$db = new Database();
					$user = new User($db);
					
					if($user->getUserByAuthentication($email, $password))
					{
						$_SESSION['s219668_userID'] = $user->getId();
						$_SESSION['s219668_lastOperationTime'] = time();
						Controller::redirect();
					}
					else $status['result'] = Result::Failed;
				}
			}
			else if($this->logged===true && $action===Action::Register)	//sono loggato: mi sloggo e poi registro
			{
				Controller::resetSession();
				Controller::redirect('register');
			}
			else if($this->logged===false && $action===Action::Register)//sono sloggato e voglio registrare
			{
				$status['action'] = Action::Register;
				
				$email = isset($_REQUEST['userEmail']) ? Validators::sanitizeEmail($_REQUEST['userEmail']) : false;
				$name = isset($_REQUEST['userName']) ? Validators::sanitizeName($_REQUEST['userName']) : false;
				$surname = isset($_REQUEST['userSurname']) ? Validators::sanitizeSurname($_REQUEST['userSurname']) : false;
				$password = isset($_REQUEST['userPassword']) ? Validators::sanitizePassword($_REQUEST['userPassword']) : false;
				
				if(!isset($_REQUEST['userEmail']) && !isset($_REQUEST['userName']) && !isset($_REQUEST['userSurname']) && !isset($_REQUEST['userPassword']))
					$status['result'] = Result::Empty_;
				else if((isset($_REQUEST['userEmail']  ) && $email  ===false) || (isset($_REQUEST['userName']    ) && $name    ===false) ||
						(isset($_REQUEST['userSurname']) && $surname===false) || (isset($_REQUEST['userPassword']) && $password===false))
					$status['result'] = Result::Incomplete;
				else if($email!==false && $name!==false && $surname!==false && $password!==false)
				{
					$db = new Database();
					$user = new User($db);
					
					if($user->createUser($email, $name, $surname, $password))
						$status['result'] = Result::OK;
					else
						$status['result'] = Result::Failed;
				}
			}
			else if($this->logged===false && $action===Action::Buy)
			{
				Controller::redirect('login');
			}
			else if($this->logged===false && $action===Action::Sell)
			{
				Controller::redirect('login');
			}
			else if($this->logged===true && $action===Action::Buy)
			{
				$status['action'] = Action::Buy;
				
				$quantity = isset($_REQUEST['quantity']) ? Validators::sanitizePositiveInteger($_REQUEST['quantity']) : false;
				
				if(!isset($_REQUEST['quantity']))
					$status['result'] = Result::Empty_;
				else if(isset($_REQUEST['quantity']) && $quantity===false)
					$status['result'] = Result::Incomplete;
				else
				{
					$db = new Database();
					$market = new StockMarket($db);
					
					$result = $market->buy($_SESSION['s219668_userID'], $quantity);
					
					if($result==-1) $status['result'] = Result::UserDeficit;
					else if($result==-2) $status['result'] = Result::MarketDeficit;
					else if($result==0) $status['result'] = Result::OK;
					else $status['result'] = Result::Failed;
				}
			}
			else if($this->logged===true && $action===Action::Sell)
			{
				$status['action'] = Action::Sell;
				
				$quantity = isset($_REQUEST['quantity']) ? Validators::sanitizePositiveInteger($_REQUEST['quantity']) : false;
				
				if(!isset($_REQUEST['quantity']))
					$status['result'] = Result::Empty_;
				else if(isset($_REQUEST['quantity']) && $quantity===false)
					$status['result'] = Result::Incomplete;
				else
				{
					$db = new Database();
					$market = new StockMarket($db);
					
					$result = $market->sell($_SESSION['s219668_userID'], $quantity);
					
					if($result==-1) $status['result'] = Result::MarketDeficit;
					else if($result==-2) $status['result'] = Result::UserDeficit;
					else if($result==0) $status['result'] = Result::OK;
					else $status['result'] = Result::Failed;
				}
			}
			else if($this->logged===true)
			{
				$status['action'] = Action::PersonalHomePage;				
				$status['s219668_userID'] = $_SESSION['s219668_userID'];
			}
			else if($this->logged===false)
			{
				$status['action'] = Action::GenericHomepage;
			}
			else
				$status['action'] = Action::Error;
			
			return $status;
		}
	}
?>