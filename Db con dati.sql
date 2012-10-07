-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generato il: Ott 07, 2012 alle 16:40
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

-- --------------------------------------------------------

--
-- Struttura della tabella `log`
--

CREATE TABLE IF NOT EXISTS `log` (
  `idLog` int(11) NOT NULL AUTO_INCREMENT,
  `logtext` varchar(300) NOT NULL,
  `log_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idMember` int(11) NOT NULL,
  PRIMARY KEY (`idLog`),
  KEY `fk_Log_Member1` (`idMember`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=51 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `member`
--

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
  `from_admin` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idMember`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_Member_Member_Type1` (`member_type`),
  KEY `fk_Member_Member_Status1` (`status`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=26 ;

--
-- Dump dei dati per la tabella `member`
--

INSERT INTO `member` (`idMember`, `name`, `surname`, `username`, `password`, `reg_code`, `reg_date`, `email`, `address`, `city`, `state`, `cap`, `tel`, `member_type`, `status`, `from_admin`) VALUES
(1, 'Giuseppe', 'Rossi', 'admin', '7815696ecbf1c96e6894b779456d330e', 'bc1bfa4e7cef3a22327bcf0f3f9462f4', '2012-09-27', 'admin@hotmail.it', 'Via Roma, 25', 'Airasca', 'Italia', '10060', '0119909772', 2, 2, 0),
(10, 'Andrea', 'Pistis', 'Resp1', '7815696ecbf1c96e6894b779456d330e', '3fe4e42d50a311f5', '2012-09-27', 'anpis@hotmail.it', 'Corso Giulio Cesare, 22', 'Torino', 'Italia', '10152', '0119944564', 1, 2, 1),
(11, 'Carlo', 'Pisapia', 'Resp2', '7815696ecbf1c96e6894b779456d330e', '3fcc797bd5d55bf8', '2012-09-27', 'carpis@hotmail.it', 'Via Dante di Nanni, 2', 'Torino', 'Italia', '10138', '0112243577', 1, 2, 1),
(12, 'Francesco', 'Seppi', 'Supplier1', '7815696ecbf1c96e6894b779456d330e', '3fdc4fce67dcb576', '2012-09-27', 'frsep@hotmail.it', 'Via Andrea Doria, 25', 'Rho', 'Italia', '20017', '02554788', 3, 2, 1),
(13, 'Luca', 'De Matteo', 'Supplier2', '7815696ecbf1c96e6894b779456d330e', '3fc9cc59fc0b1748', '2012-09-27', 'lude@gmail.com', 'Via Scipio Slataper, 3', 'Brescia', 'Italia', '25128', '0118857655', 3, 2, 1),
(14, 'Matteo', 'Di Matteo', 'Supplier3', '7815696ecbf1c96e6894b779456d330e', '3feb79855e2f75e1', '2012-09-27', 'matdi@gmail.com', 'Via Roma, 25', 'Napoli', 'Italia', '80147', '011445433', 3, 2, 1),
(15, 'Hassan', 'Umbroso', 'Supplier4', '7815696ecbf1c96e6894b779456d330e', '3fee4d614f0e7f32', '2012-09-27', 'hasum@gmail.com', 'Corso Umbria, 21', 'Torino', 'Italia', '10144', '01199065337', 3, 2, 1),
(16, 'Hassan', 'Metwalley', 'Norm1', '7815696ecbf1c96e6894b779456d330e', '3fe8e3c20fc81669', '2012-09-27', 'hasmet@hotmail.it', 'Largo Augusto, 3', 'Milano', 'Italia', '20122', '0119909554', 0, 2, 0),
(17, 'Francesco', 'Brundu', 'Norm2', '7815696ecbf1c96e6894b779456d330e', '3fe4c8e155a6cbeb', '2012-09-27', 'frbru@hotmail.it', 'Via Sardegna, 2', 'Cinisello Balsamo', 'Italia', '20092', '00203365466', 0, 2, 0),
(18, 'Luca', 'Moretto', 'Norm3', '7815696ecbf1c96e6894b779456d330e', '3fd17dd428057c3c', '2012-09-27', 'lumor@hotmail.it', 'Via Chieri, 30', 'Baldissero Torinese', 'Italia', '10020', '0116647622', 0, 2, 0),
(19, 'Matteo', 'Ferrari', 'Norm4', '7815696ecbf1c96e6894b779456d330e', '3fc767fffdc995c4', '2012-09-27', 'matfer@gmail.com', 'Via Roma, 30', 'Racale', 'Italia', '73055', '0117764322', 0, 2, 0),
(20, 'Luigi', 'Longo', 'Norm5', '7815696ecbf1c96e6894b779456d330e', '3fe21afce4dbb6ab', '2012-09-27', 'luilon@hotmail.it', 'Via Bergamo, 30', 'Monza', 'Italia', '20900', '0117754433', 0, 2, 0),
(21, 'Franco', 'Fidelio', 'Norm6', '7815696ecbf1c96e6894b779456d330e', '3fbfc5d2635d2cd8', '2012-09-27', 'frfid@gmail.com', 'Via Roma, 234', 'Palermo', 'Italia', '10050', '0113386599', 0, 2, 0),
(22, 'Pier Luigi', 'Carletti', 'Norm7', '7815696ecbf1c96e6894b779456d330e', '3fdc9bba519ce05e', '2012-09-27', 'prcarl@yahoo.it', 'Via Candido Giuseppe, 5', 'Lecce', 'Italia', '73100', '0114487599', 0, 2, 0),
(23, 'Carlo', 'Stizi', 'Norm8', '7815696ecbf1c96e6894b779456d330e', '3fd06e0717529cc2', '2012-09-27', 'carstz@hotmail.com', 'Via Lombriasco, 55', 'Roma', 'Italia', '00166', '0113485555', 0, 2, 0),
(24, 'Stefania', 'De Bosco', 'Norm9', '7815696ecbf1c96e6894b779456d330e', '3fba9c6ead4d07c8', '2012-09-27', 'stbos@aruba.it', 'Via Roma, 4', 'Cerveteri', 'Italia', '00052', '0113398577', 0, 2, 0),
(25, 'Nicoletta', 'Luigi', 'Norm10', '7815696ecbf1c96e6894b779456d330e', '3fe5eb667764949e', '2012-09-27', 'niclu@gmail.com', 'Via Colombo, 22', 'Genova', 'Italia', '16121', '0114822789', 0, 2, 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `member_status`
--

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

CREATE TABLE IF NOT EXISTS `message` (
  `idMessage` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(300) DEFAULT NULL,
  `message_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isReaded` tinyint(1) NOT NULL DEFAULT '0',
  `id_sender` int(11) DEFAULT NULL,
  `id_receiver` int(11) NOT NULL,
  `Product_idProduct` int(11) DEFAULT NULL,
  `Order_idOrder` int(11) DEFAULT NULL,
  `message_category` int(11) NOT NULL,
  PRIMARY KEY (`idMessage`),
  KEY `fk_Message_Member1` (`id_sender`),
  KEY `fk_Message_Member2` (`id_receiver`),
  KEY `fk_Message_Product1` (`Product_idProduct`),
  KEY `fk_Message_Order1` (`Order_idOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `notify`
--

CREATE TABLE IF NOT EXISTS `notify` (
  `idNotify` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(300) NOT NULL,
  `notify_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isReaded` tinyint(1) NOT NULL DEFAULT '0',
  `idMember` int(11) NOT NULL,
  `notify_category` int(11) NOT NULL,
  PRIMARY KEY (`idNotify`),
  KEY `fk_Notify_Member1` (`idMember`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=74 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `order`
--

CREATE TABLE IF NOT EXISTS `order` (
  `idOrder` int(11) NOT NULL AUTO_INCREMENT,
  `order_name` varchar(100) NOT NULL,
  `date_open` date NOT NULL,
  `date_close` date NOT NULL,
  `date_delivery` date DEFAULT NULL,
  `idMember_resp` int(11) NOT NULL,
  `idSupplier` int(11) NOT NULL,
  PRIMARY KEY (`idOrder`),
  UNIQUE KEY `order_name` (`order_name`),
  KEY `fk_Order_Member1` (`idMember_resp`),
  KEY `fk_Order_Supplier1` (`idSupplier`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `order_product`
--

CREATE TABLE IF NOT EXISTS `order_product` (
  `idOrder` int(11) NOT NULL,
  `idProduct` int(11) NOT NULL,
  `failed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idOrder`,`idProduct`),
  KEY `fk_Order_has_ProductList_ProductList1` (`idProduct`),
  KEY `fk_Order_has_ProductList_Order1` (`idOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `product`
--

CREATE TABLE IF NOT EXISTS `product` (
  `idProduct` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) NOT NULL,
  `dimension` int(11) NOT NULL,
  `measure_unit` varchar(45) NOT NULL,
  `unit_block` int(11) NOT NULL DEFAULT '1',
  `availability` tinyint(1) NOT NULL DEFAULT '1',
  `transport_cost` float NOT NULL,
  `unit_cost` float NOT NULL,
  `min_buy` int(11) DEFAULT NULL,
  `max_buy` int(11) DEFAULT NULL,
  `imgPath` varchar(100) DEFAULT NULL,
  `idCategory` int(11) NOT NULL COMMENT '			',
  `idSupplier` int(11) NOT NULL,
  PRIMARY KEY (`idProduct`),
  KEY `fk_Product_Product_Category1` (`idCategory`),
  KEY `fk_Product_Supplier1` (`idSupplier`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=68 ;

--
-- Dump dei dati per la tabella `product`
--

INSERT INTO `product` (`idProduct`, `name`, `description`, `dimension`, `measure_unit`, `unit_block`, `availability`, `transport_cost`, `unit_cost`, `min_buy`, `max_buy`, `imgPath`, `idCategory`, `idSupplier`) VALUES
(22, 'Mela', 'Cesto di mele', 12, 'Kg', 2, 1, 12, 1, 10, 11, 'img/prd/22.jpg', 12, 12),
(23, 'Pera', 'Cesto di pere', 12, 'Kg', 2, 1, 4, 10, 1, 12, 'img/prd/23.jpg', 12, 12),
(24, 'Fragola', 'Sacchetto di fragole', 1, 'Hg', 8, 1, 12, 2, 1, 30, 'img/prd/24.jpg', 12, 12),
(25, 'Ciliegia', 'Sacchetto di ciliegie', 2, 'Hg', 5, 1, 12, 2, 1, 50, 'img/prd/25.jpg', 12, 12),
(26, 'Insalata', 'Insalata', 1, 'Kg', 3, 1, 16, 2, 2, 50, 'img/prd/26.jpg', 13, 12),
(27, 'Cavolfiore', 'Cassetta di cavolfiori', 1, 'Kg', 2, 1, 12, 1, 2, 8, 'img/prd/27.jpg', 13, 12),
(29, 'Pomodoro', 'Cassetta di pomodori', 2, 'Kg', 2, 1, 12, 2, 1, 10, 'img/prd/29.jpg', 13, 12),
(30, 'Mora', 'Cesto di more', 1, 'Hg', 3, 1, 5, 20, 2, 10, 'img/prd/30.jpg', 12, 12),
(31, 'Confezione di Cd', 'Confezione da 10 cd', 1, 'Cd', 10, 1, 12, 20, 1, 10, 'img/prd/31.jpg', 14, 13),
(32, 'Dvd', 'Dvd singolo', 1, 'Dvd', 1, 1, 10, 5, 1, 90, 'img/prd/32.jpg', 14, 13),
(33, 'Microsoft Windows XP', 'Windows XP 64 bit', 1, 'Dvd', 1, 1, 12, 90, 1, 10, 'img/prd/33.jpg', 15, 13),
(34, 'Microsoft Windows Server', 'Windows Server 2008 64 bit', 1, 'Dvd', 1, 1, 12, 150, 1, 5, 'img/prd/34.jpg', 15, 13),
(35, 'Microsoft Windows 7', 'Windows 7 64 bit', 1, 'Dvd', 1, 1, 12, 140, 1, 6, 'img/prd/35.png', 15, 13),
(36, 'Apple MacOs X', 'MacOs X Lion', 1, 'Dvd', 1, 1, 9, 120, 1, 4, 'img/prd/36.png', 15, 13),
(37, 'Asus Nexus 7', 'Nexus 7 16 Gb', 1, 'Tablet', 1, 1, 12, 250, 15, 34, 'img/prd/37.jpg', 16, 13),
(38, 'Samsung Galaxy Tab 2', 'Galaxy Tab 2 10 16 Gb', 1, 'Tablet', 1, 1, 10, 500, 1, 6, 'img/prd/38.jpg', 16, 13),
(39, 'Farfalle', 'Confezione di Farfalle', 1, 'Kg', 5, 1, 12, 5, 1, 50, 'img/prd/39.jpg', 17, 14),
(40, 'Penne', 'Confezione di penne', 1, 'Kg', 2, 1, 12, 3, 1, 20, 'img/prd/40.jpg', 17, 14),
(41, 'Pan Di Stelle', 'Confezione di pan di stelle', 1, 'Kg', 2, 1, 12, 3, 1, 20, 'img/prd/41.jpg', 18, 14),
(43, 'Abbracci', 'Confezione di abbracci', 1, 'Kg', 2, 1, 10, 5, 1, 5, 'img/prd/43.JPG', 18, 14),
(44, 'Macine', 'Confezione di macine', 1, 'Kg', 2, 1, 11, 2, 1, 20, 'img/prd/44.jpg', 18, 14),
(45, 'Campagnole', 'Confezione di campagnole', 500, 'Hg', 2, 1, 12, 1, 1, 40, 'img/prd/45.png', 18, 14),
(46, 'Scarpa Nike', 'Scarpa numero 45', 1, 'Scarpa', 2, 1, 12, 100, 1, 30, 'img/prd/46.jpg', 19, 15),
(47, 'Scarpa Calcio', 'Scarpa da calcio Nike', 1, 'Scarpa', 2, 1, 10, 100, 2, 9, 'img/prd/47.JPG', 19, 15),
(48, 'Scarpa Adidas', 'Scarpa Adidas modello 40', 1, 'Scarpa', 2, 1, 10, 130, 1, 100, 'img/prd/48.jpg', 19, 15),
(49, 'Scarpa Asics', 'Scarpa Asics numero 40', 1, 'Scarpa', 2, 1, 20, 70, 1, 20, 'img/prd/49.jpeg', 19, 15),
(50, 'Juventus', 'Maglia Anno 2013', 1, 'Maglia', 1, 1, 10, 90, 1, 20, 'img/prd/50.jpg', 20, 15),
(51, 'Milan', 'Maglia anno 2013', 1, 'Maglia', 1, 1, 10, 100, 1, 20, 'img/prd/51.jpg', 20, 15),
(52, 'Inter', 'Maglia anno 2010', 1, 'Maglia', 1, 1, 20, 40, 1, 20, 'img/prd/52.jpg', 20, 15),
(53, 'Napoli', 'Maglia anno 2013', 1, 'Maglia', 1, 1, 10, 70, 1, 10, 'img/prd/53.bmp', 20, 15),
(54, 'Lecce', 'Maglia anno 2013', 1, 'Maglia', 1, 1, 10, 40, 1, 5, 'img/prd/54.jpg', 20, 15),
(55, 'Sampdoria', 'Maglia anno 2012', 1, 'Maglia', 1, 1, 11, 20, 1, 3, 'img/prd/55.gif', 20, 15),
(56, 'Torino', 'Maglia anno 2008', 1, 'Maglia', 1, 1, 10, 50, 1, 4, 'img/prd/56.gif', 20, 15);

-- --------------------------------------------------------

--
-- Struttura della tabella `product_category`
--

CREATE TABLE IF NOT EXISTS `product_category` (
  `idProduct_Category` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`idProduct_Category`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=21 ;

--
-- Dump dei dati per la tabella `product_category`
--

INSERT INTO `product_category` (`idProduct_Category`, `description`) VALUES
(12, 'Frutta'),
(13, 'Verdura'),
(14, 'Misto'),
(15, 'Sistemi Operativi'),
(16, 'Tablet'),
(17, 'Pasta'),
(18, 'Biscotti'),
(19, 'Scarpe'),
(20, 'Maglie Calcio');

-- --------------------------------------------------------

--
-- Struttura della tabella `purchase`
--

CREATE TABLE IF NOT EXISTS `purchase` (
  `idPurchase` int(11) NOT NULL AUTO_INCREMENT,
  `isShipped` tinyint(1) NOT NULL DEFAULT '0',
  `idMember` int(11) NOT NULL,
  `idOrder` int(11) NOT NULL,
  PRIMARY KEY (`idPurchase`),
  KEY `fk_Purchase_Member1` (`idMember`),
  KEY `fk_Purchase_Order1` (`idOrder`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

-- --------------------------------------------------------

--
-- Struttura della tabella `purchase_product`
--

CREATE TABLE IF NOT EXISTS `purchase_product` (
  `idPurchase` int(11) NOT NULL,
  `idProduct` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `inserted_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idPurchase`,`idProduct`),
  KEY `fk_Purchase_has_Product_Product1` (`idProduct`),
  KEY `fk_Purchase_has_Product_Purchase1` (`idPurchase`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `supplier`
--

CREATE TABLE IF NOT EXISTS `supplier` (
  `idMember` int(11) NOT NULL,
  `company_name` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  `contact_name` varchar(45) DEFAULT NULL,
  `fax` varchar(45) DEFAULT NULL,
  `website` varchar(45) DEFAULT NULL,
  `payment_method` varchar(45) NOT NULL,
  `idMemberResp` int(11) NOT NULL,
  PRIMARY KEY (`idMember`),
  KEY `fk_Supplier_Member1` (`idMember`),
  KEY `fk_Supplier_Member2` (`idMemberResp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `supplier`
--

INSERT INTO `supplier` (`idMember`, `company_name`, `description`, `contact_name`, `fax`, `website`, `payment_method`, `idMemberResp`) VALUES
(12, 'Melinda', 'Vendita frutta e verdura', 'Luigi', '025573389', 'www.melinda.it', 'Postepay', 10),
(13, 'Alex PC', 'Vendita materiale informatico', 'Steve', '025573355', 'www.alex.it', 'Paypal', 10),
(14, 'Carrefour', 'Vendita alimentari', 'Francis', '0114468766', 'www.carrefour.it', 'Mastercard', 11),
(15, 'Sportivando', 'Vendita materiale sportivo', 'Delmonte', '0114478454', 'www.sportivando.it', 'Mastercard', 11);

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
  ADD CONSTRAINT `fk_Member_Member_Status1` FOREIGN KEY (`status`) REFERENCES `member_status` (`idMember_Status`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Member_Member_Type1` FOREIGN KEY (`member_type`) REFERENCES `member_type` (`idMember_Type`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `fk_Message_Member1` FOREIGN KEY (`id_sender`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Member2` FOREIGN KEY (`id_receiver`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Order1` FOREIGN KEY (`Order_idOrder`) REFERENCES `order` (`idOrder`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Product1` FOREIGN KEY (`Product_idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `notify`
--
ALTER TABLE `notify`
  ADD CONSTRAINT `fk_Notify_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `fk_Order_Member1` FOREIGN KEY (`idMember_resp`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Order_Supplier1` FOREIGN KEY (`idSupplier`) REFERENCES `supplier` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

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
  ADD CONSTRAINT `fk_Product_Product_Category1` FOREIGN KEY (`idCategory`) REFERENCES `product_category` (`idProduct_Category`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Product_Supplier1` FOREIGN KEY (`idSupplier`) REFERENCES `supplier` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

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
  ADD CONSTRAINT `fk_Purchase_has_Product_Product1` FOREIGN KEY (`idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Purchase_has_Product_Purchase1` FOREIGN KEY (`idPurchase`) REFERENCES `purchase` (`idPurchase`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limiti per la tabella `supplier`
--
ALTER TABLE `supplier`
  ADD CONSTRAINT `fk_Supplier_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Supplier_Member2` FOREIGN KEY (`idMemberResp`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
