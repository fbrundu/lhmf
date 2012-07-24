-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generato il: Lug 24, 2012 alle 18:20
-- Versione del server: 5.5.16
-- Versione PHP: 5.3.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `malnati_project`
--
DROP DATABASE `malnati_project`;
CREATE DATABASE `malnati_project` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `malnati_project`;

-- --------------------------------------------------------

--
-- Struttura della tabella `log`
--

DROP TABLE IF EXISTS `log`;
CREATE TABLE IF NOT EXISTS `log` (
  `idLog` int(11) NOT NULL AUTO_INCREMENT,
  `logtext` varchar(300) NOT NULL,
  `log_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idMember` int(11) NOT NULL,
  PRIMARY KEY (`idLog`),
  KEY `fk_Log_Member1` (`idMember`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- RELATIONS FOR TABLE `log`:
--   `idMember`
--       `member` -> `idMember`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `member`
--

DROP TABLE IF EXISTS `member`;
CREATE TABLE IF NOT EXISTS `member` (
  `idMember` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `username` varchar(250) NOT NULL,
  `password` varchar(32) NOT NULL,
  `reg_code` varchar(64) NOT NULL,
  `reg_date` date NOT NULL,
  `email` varchar(45) NOT NULL,
  `address` varchar(45) NOT NULL,
  `city` varchar(45) NOT NULL,
  `state` varchar(45) NOT NULL,
  `cap` varchar(45) NOT NULL,
  `tel` varchar(45) DEFAULT NULL,
  `member_type` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`idMember`),
  KEY `fk_Member_Member_Type1` (`member_type`),
  KEY `fk_Member_Member_Status1` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- RELATIONS FOR TABLE `member`:
--   `member_type`
--       `member_type` -> `idMember_Type`
--   `status`
--       `member_status` -> `idMember_Status`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `member_status`
--

DROP TABLE IF EXISTS `member_status`;
CREATE TABLE IF NOT EXISTS `member_status` (
  `idMember_Status` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idMember_Status`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `member_status`
--

INSERT INTO `member_status` (`idMember_Status`, `description`) VALUES
(0, 'Non Verificato'),
(1, 'Verificato - Disabilitato'),
(2, 'Abilitato');

-- --------------------------------------------------------

--
-- Struttura della tabella `member_type`
--

DROP TABLE IF EXISTS `member_type`;
CREATE TABLE IF NOT EXISTS `member_type` (
  `idMember_Type` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idMember_Type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `member_type`
--

INSERT INTO `member_type` (`idMember_Type`, `description`) VALUES
(0, 'Normale'),
(1, 'Responsabile'),
(2, 'Admin'),
(3, 'Fornitore');

-- --------------------------------------------------------

--
-- Struttura della tabella `message`
--

DROP TABLE IF EXISTS `message`;
CREATE TABLE IF NOT EXISTS `message` (
  `idMessage` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(300) NOT NULL,
  `message_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isReaded` tinyint(1) NOT NULL DEFAULT '0',
  `id_sender` int(11) NOT NULL,
  `id_receiver` int(11) NOT NULL,
  `Product_idProduct` int(11) NOT NULL,
  `Order_idOrder` int(11) NOT NULL,
  PRIMARY KEY (`idMessage`),
  KEY `fk_Message_Member1` (`id_sender`),
  KEY `fk_Message_Member2` (`id_receiver`),
  KEY `fk_Message_Product1` (`Product_idProduct`),
  KEY `fk_Message_Order1` (`Order_idOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- RELATIONS FOR TABLE `message`:
--   `id_sender`
--       `member` -> `idMember`
--   `id_receiver`
--       `member` -> `idMember`
--   `Product_idProduct`
--       `product` -> `idProduct`
--   `Order_idOrder`
--       `order` -> `idOrder`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `notify`
--

DROP TABLE IF EXISTS `notify`;
CREATE TABLE IF NOT EXISTS `notify` (
  `idNotify` int(11) NOT NULL AUTO_INCREMENT,
  `text` int(11) NOT NULL,
  `notify_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isReaded` tinyint(1) NOT NULL DEFAULT '0',
  `idMember` int(11) NOT NULL,
  PRIMARY KEY (`idNotify`),
  KEY `fk_Notify_Member1` (`idMember`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- RELATIONS FOR TABLE `notify`:
--   `idMember`
--       `member` -> `idMember`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `order`
--

DROP TABLE IF EXISTS `order`;
CREATE TABLE IF NOT EXISTS `order` (
  `idOrder` int(11) NOT NULL,
  `date_open` date NOT NULL,
  `date_close` date NOT NULL,
  `date_delivery` date DEFAULT NULL,
  `idMember_resp` int(11) NOT NULL,
  `idMember_supplier` int(11) NOT NULL,
  PRIMARY KEY (`idOrder`),
  KEY `fk_Order_Member1` (`idMember_resp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- RELATIONS FOR TABLE `order`:
--   `idMember_resp`
--       `member` -> `idMember`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `order_product`
--

DROP TABLE IF EXISTS `order_product`;
CREATE TABLE IF NOT EXISTS `order_product` (
  `idOrder` int(11) NOT NULL,
  `idProduct` int(11) NOT NULL,
  PRIMARY KEY (`idOrder`,`idProduct`),
  KEY `fk_Order_has_ProductList_ProductList1` (`idProduct`),
  KEY `fk_Order_has_ProductList_Order1` (`idOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- RELATIONS FOR TABLE `order_product`:
--   `idOrder`
--       `order` -> `idOrder`
--   `idProduct`
--       `product` -> `idProduct`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `idProduct` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) NOT NULL,
  `dimension` int(11) NOT NULL,
  `measure_unit` varchar(45) NOT NULL,
  `unit_block` int(11) DEFAULT NULL,
  `availability` tinyint(1) NOT NULL DEFAULT '1',
  `transport_cost` float NOT NULL,
  `unit_cost` float NOT NULL,
  `min_buy` int(11) DEFAULT NULL,
  `max_buy` int(11) DEFAULT NULL,
  `imgPath` varchar(100) DEFAULT NULL,
  `idMember_supplier` int(11) NOT NULL,
  `idCategory` int(11) NOT NULL COMMENT '			',
  PRIMARY KEY (`idProduct`),
  KEY `fk_Product_Product_Category1` (`idCategory`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- RELATIONS FOR TABLE `product`:
--   `idCategory`
--       `product_category` -> `idProduct_Category`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `product_category`
--

DROP TABLE IF EXISTS `product_category`;
CREATE TABLE IF NOT EXISTS `product_category` (
  `idProduct_Category` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`idProduct_Category`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `purchase`
--

DROP TABLE IF EXISTS `purchase`;
CREATE TABLE IF NOT EXISTS `purchase` (
  `idPurchase` int(11) NOT NULL AUTO_INCREMENT,
  `isShipped` tinyint(1) NOT NULL DEFAULT '0',
  `idMember` int(11) NOT NULL,
  `idOrder` int(11) NOT NULL,
  PRIMARY KEY (`idPurchase`),
  KEY `fk_Purchase_Member1` (`idMember`),
  KEY `fk_Purchase_Order1` (`idOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- RELATIONS FOR TABLE `purchase`:
--   `idMember`
--       `member` -> `idMember`
--   `idOrder`
--       `order` -> `idOrder`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `purchase_product`
--

DROP TABLE IF EXISTS `purchase_product`;
CREATE TABLE IF NOT EXISTS `purchase_product` (
  `idPurchase` int(11) NOT NULL,
  `idProduct` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  PRIMARY KEY (`idPurchase`,`idProduct`),
  KEY `fk_Purchase_has_Product_Product1` (`idProduct`),
  KEY `fk_Purchase_has_Product_Purchase1` (`idPurchase`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- RELATIONS FOR TABLE `purchase_product`:
--   `idPurchase`
--       `purchase` -> `idPurchase`
--   `idProduct`
--       `product` -> `idProduct`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `supplier`
--

DROP TABLE IF EXISTS `supplier`;
CREATE TABLE IF NOT EXISTS `supplier` (
  `Member_idMember` int(11) NOT NULL,
  `company_name` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  `contact_name` varchar(45) DEFAULT NULL,
  `fax` varchar(45) DEFAULT NULL,
  `website` varchar(45) DEFAULT NULL,
  `payment_method` varchar(45) NOT NULL,
  KEY `fk_Supplier_Member1` (`Member_idMember`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- RELATIONS FOR TABLE `supplier`:
--   `Member_idMember`
--       `member` -> `idMember`
--

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `log`
--
ALTER TABLE `log`
  ADD CONSTRAINT `fk_Log_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `member`
--
ALTER TABLE `member`
  ADD CONSTRAINT `fk_Member_Member_Type1` FOREIGN KEY (`member_type`) REFERENCES `member_type` (`idMember_Type`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Member_Member_Status1` FOREIGN KEY (`status`) REFERENCES `member_status` (`idMember_Status`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `fk_Message_Member1` FOREIGN KEY (`id_sender`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Member2` FOREIGN KEY (`id_receiver`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Product1` FOREIGN KEY (`Product_idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Order1` FOREIGN KEY (`Order_idOrder`) REFERENCES `order` (`idOrder`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `notify`
--
ALTER TABLE `notify`
  ADD CONSTRAINT `fk_Notify_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `fk_Order_Member1` FOREIGN KEY (`idMember_resp`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `order_product`
--
ALTER TABLE `order_product`
  ADD CONSTRAINT `fk_Order_has_ProductList_Order1` FOREIGN KEY (`idOrder`) REFERENCES `order` (`idOrder`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Order_has_ProductList_ProductList1` FOREIGN KEY (`idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `fk_Product_Product_Category1` FOREIGN KEY (`idCategory`) REFERENCES `product_category` (`idProduct_Category`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `purchase`
--
ALTER TABLE `purchase`
  ADD CONSTRAINT `fk_Purchase_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Purchase_Order1` FOREIGN KEY (`idOrder`) REFERENCES `order` (`idOrder`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `purchase_product`
--
ALTER TABLE `purchase_product`
  ADD CONSTRAINT `fk_Purchase_has_Product_Purchase1` FOREIGN KEY (`idPurchase`) REFERENCES `purchase` (`idPurchase`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Purchase_has_Product_Product1` FOREIGN KEY (`idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `supplier`
--
ALTER TABLE `supplier`
  ADD CONSTRAINT `fk_Supplier_Member1` FOREIGN KEY (`Member_idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
