-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generato il: Set 12, 2016 alle 19:24
-- Versione del server: 5.5.50
-- Versione PHP: 5.3.10-1ubuntu3.24

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `s219668`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `buys`
--

DROP TABLE IF EXISTS `buys`;
CREATE TABLE IF NOT EXISTS `buys` (
  `quantity` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  PRIMARY KEY (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `buys`
--

INSERT INTO `buys` (`quantity`, `price`) VALUES
(8, 800),
(3, 900),
(4, 950),
(10, 960),
(2, 1000);

-- --------------------------------------------------------

--
-- Struttura della tabella `history`
--

DROP TABLE IF EXISTS `history`;
CREATE TABLE IF NOT EXISTS `history` (
  `userId` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type` tinyint(4) NOT NULL,
  `stocks` int(11) NOT NULL,
  `euros` int(11) NOT NULL,
  UNIQUE KEY `userId` (`userId`,`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `history`
--

INSERT INTO `history` (`userId`, `timestamp`, `type`, `stocks`, `euros`) VALUES
(17, '2016-09-12 13:33:34', 1, 5, -5190),
(17, '2016-09-12 13:33:39', 1, 7, -7350),
(17, '2016-09-12 13:33:42', 0, -2, 2000),
(18, '2016-09-12 13:34:04', 1, 3, -3200),
(18, '2016-09-12 13:34:12', 1, 10, -11150),
(18, '2016-09-12 13:34:16', 0, -3, 2880);

-- --------------------------------------------------------

--
-- Struttura della tabella `sells`
--

DROP TABLE IF EXISTS `sells`;
CREATE TABLE IF NOT EXISTS `sells` (
  `price` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `sells`
--

INSERT INTO `sells` (`price`, `quantity`) VALUES
(1030, 3),
(1050, 11),
(1100, 8),
(1150, 6),
(1200, 15);

-- --------------------------------------------------------

--
-- Struttura della tabella `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(80) NOT NULL,
  `name` varchar(80) NOT NULL,
  `surname` varchar(80) NOT NULL,
  `password` varchar(80) NOT NULL,
  `euros` int(11) NOT NULL DEFAULT '50000',
  `stocks` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=19 ;

--
-- Dump dei dati per la tabella `users`
--

INSERT INTO `users` (`id`, `email`, `name`, `surname`, `password`, `euros`, `stocks`) VALUES
(17, 'u1@p.it', 'u', 'u', '1813567525c529e3dc1251ae1f06251d', 39460, 10),
(18, 'u2@p.it', 'u', 'u', '07f537543ab28f738ca2ce0372491369', 38530, 10);

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `history`
--
ALTER TABLE `history`
  ADD CONSTRAINT `userIdForeignKey` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
