-- phpMyAdmin SQL Dump
-- version 3.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 10, 2012 at 05:29 AM
-- Server version: 5.5.25a
-- PHP Version: 5.4.4

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
-- Table structure for table `log`
--

CREATE TABLE IF NOT EXISTS `log` (
  `idLog` int(11) NOT NULL AUTO_INCREMENT,
  `logtext` varchar(300) NOT NULL,
  `log_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idMember` int(11) NOT NULL,
  PRIMARY KEY (`idLog`),
  KEY `fk_Log_Member1` (`idMember`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=164 ;

--
-- Dumping data for table `log`
--

INSERT INTO `log` (`idLog`, `logtext`, `log_timestamp`, `idMember`) VALUES
(140, 'Ha creato l''ordine con id: 37', '2012-10-10 01:49:50', 10),
(141, 'Ha creato l''ordine con id: 38', '2012-10-10 01:53:40', 10),
(142, 'Ha creato l''ordine con id: 39', '2012-10-10 01:56:05', 10),
(143, 'Ha creato l''ordine con id: 40', '2012-10-10 02:03:04', 10),
(144, 'Ha creato l''ordine con id: 41', '2012-10-10 02:07:41', 10),
(145, 'Ha creato una scheda con id: 24', '2012-10-10 02:11:09', 16),
(146, 'Ha cancellato un prodotto dalla scheda con id: 24', '2012-10-10 02:11:37', 16),
(147, 'Ha provato senza successo a cancellare un prodotto dalla scheda con id: 24', '2012-10-10 02:11:37', 16),
(148, 'Ha cancellato un prodotto dalla scheda con id: 24', '2012-10-10 02:12:44', 16),
(149, 'Ha provato senza successo a cancellare un prodotto dalla scheda con id: 24', '2012-10-10 02:12:46', 16),
(150, 'Ha cancellato un prodotto dalla scheda con id: 24', '2012-10-10 02:12:49', 16),
(151, 'Ha cancellato la scheda con id: 24', '2012-10-10 02:12:49', 16),
(152, 'Ha creato una scheda con id: 25', '2012-10-10 02:18:27', 16),
(153, 'Ha cancellato un prodotto dalla scheda con id: 25', '2012-10-10 02:18:35', 16),
(154, 'Ha provato senza successo a cancellare un prodotto dalla scheda con id: 25', '2012-10-10 02:18:35', 16),
(155, 'Ha cancellato un prodotto dalla scheda con id: 25', '2012-10-10 02:19:04', 16),
(156, 'Ha provato senza successo a cancellare un prodotto dalla scheda con id: 25', '2012-10-10 02:19:04', 16),
(157, 'Ha creato una scheda con id: 26', '2012-10-10 02:36:21', 16),
(158, 'Ha cancellato un prodotto dalla scheda con id: 26', '2012-10-10 02:36:29', 16),
(159, 'Ha modificato la scheda con id: 26', '2012-10-10 03:11:27', 16),
(160, 'Ha modificato la scheda con id: 26', '2012-10-10 03:11:36', 16),
(161, 'Ha modificato la scheda con id: 26', '2012-10-10 03:11:43', 16),
(162, 'Ha modificato la scheda con id: 26', '2012-10-10 03:11:53', 16),
(163, 'Ha modificato la scheda con id: 26', '2012-10-10 03:12:06', 16);

-- --------------------------------------------------------

--
-- Table structure for table `member`
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=44 ;

--
-- Dumping data for table `member`
--

INSERT INTO `member` (`idMember`, `name`, `surname`, `username`, `password`, `reg_code`, `reg_date`, `email`, `address`, `city`, `state`, `cap`, `tel`, `member_type`, `status`, `from_admin`) VALUES
(1, 'Giuseppe', 'Rossi', 'admin', '7815696ecbf1c96e6894b779456d330e', 'bc1bfa4e7cef3a22327bcf0f3f9462f4', '2012-09-27', 'admin@hotmail.it', 'Via Roma, 25', 'Airasca', 'Italia', '10060', '0119909772', 2, 2, 0),
(10, 'Andrea', 'Pistis', 'Resp1', '7815696ecbf1c96e6894b779456d330e', '3fe4e42d50a311f5', '2012-09-27', 'anpis@hotmail.it', 'Corso Giulio Cesare, 22', 'Torino', 'Italia', '10152', '0119944564', 1, 2, 1),
(11, 'Carlo', 'Pisapia', 'Resp2', '7815696ecbf1c96e6894b779456d330e', '3fcc797bd5d55bf8', '2012-09-27', 'carpis@hotmail.it', 'Via Dante di Nanni, 2', 'Torino', 'Italia', '10138', '0112243577', 1, 2, 1),
(12, 'Francesco', 'Seppi', 'Supplier1', '7815696ecbf1c96e6894b779456d330e', '3fdc4fce67dcb576', '2012-09-27', 'frsep@hotmail.it', 'Via Andrea Doria, 25', 'Rho', 'Italia', '20017', '02554788', 3, 2, 1),
(13, 'Luca', 'De Matteo', 'Supplier2', '7815696ecbf1c96e6894b779456d330e', '3fc9cc59fc0b1748', '2012-09-27', 'lude@gmail.com', 'Via Scipio Slataper, 3', 'Brescia', 'Italia', '25128', '0118857655', 3, 2, 1),
(14, 'Matteo', 'Di Matteo', 'Supplier3', '7815696ecbf1c96e6894b779456d330e', '3feb79855e2f75e1', '2012-09-27', 'matdi@gmail.com', 'Via Roma, 25', 'Napoli', 'Italia', '80147', '011445433', 3, 2, 1),
(15, 'Hassan', 'Umbroso', 'Supplier4', '7815696ecbf1c96e6894b779456d330e', '3fee4d614f0e7f32', '2012-09-27', 'hasum@gmail.com', 'Corso Umbria, 21', 'Torino', 'Italia', '10144', '01199065337', 3, 2, 1),
(16, 'Hassan', 'Esam', 'Norm1', '7815696ecbf1c96e6894b779456d330e', '3fe8e3c20fc81669', '2012-09-27', 'hasmet@hotmail.it', 'Largo Augusto, 3', 'Milano', 'Italia', '20122', '0119909554', 0, 2, 0),
(17, 'Francesco', 'Brundu', 'Norm2', '7815696ecbf1c96e6894b779456d330e', '3fe4c8e155a6cbeb', '2012-09-27', 'frbru@hotmail.it', 'Via Sardegna, 2', 'Cinisello Balsamo', 'Italia', '20092', '00203365466', 0, 2, 0),
(18, 'Luca', 'Moretto', 'Norm3', '7815696ecbf1c96e6894b779456d330e', '3fd17dd428057c3c', '2012-09-27', 'lumor@hotmail.it', 'Via Chieri, 30', 'Baldissero Torinese', 'Italia', '10020', '0116647622', 0, 2, 0),
(19, 'Matteo', 'Ferrari', 'Norm4', '7815696ecbf1c96e6894b779456d330e', '3fc767fffdc995c4', '2012-09-27', 'matfer@gmail.com', 'Via Roma, 30', 'Racale', 'Italia', '73055', '0117764322', 0, 2, 0),
(20, 'Luigi', 'Longo', 'Norm5', '7815696ecbf1c96e6894b779456d330e', '3fe21afce4dbb6ab', '2012-09-27', 'luilon@hotmail.it', 'Via Bergamo, 30', 'Monza', 'Italia', '20900', '0117754433', 0, 2, 0),
(21, 'Franco', 'Fidelio', 'Norm6', '7815696ecbf1c96e6894b779456d330e', '3fbfc5d2635d2cd8', '2012-09-27', 'frfid@gmail.com', 'Via Roma, 234', 'Palermo', 'Italia', '10050', '0113386599', 0, 2, 0),
(22, 'Pier Luigi', 'Carletti', 'Norm7', '7815696ecbf1c96e6894b779456d330e', '3fdc9bba519ce05e', '2012-09-27', 'prcarl@yahoo.it', 'Via Candido Giuseppe, 5', 'Lecce', 'Italia', '73100', '0114487599', 0, 2, 0),
(23, 'Carlo', 'Stizi', 'Norm8', '7815696ecbf1c96e6894b779456d330e', '3fd06e0717529cc2', '2012-09-27', 'carstz@hotmail.com', 'Via Lombriasco, 55', 'Roma', 'Italia', '00166', '0113485555', 0, 2, 0),
(24, 'Stefania', 'De Bosco', 'Norm9', '7815696ecbf1c96e6894b779456d330e', '3fba9c6ead4d07c8', '2012-09-27', 'stbos@aruba.it', 'Via Roma, 4', 'Cerveteri', 'Italia', '00052', '0113398577', 0, 2, 0),
(25, 'Nicoletta', 'Luigi', 'Norm10', '7815696ecbf1c96e6894b779456d330e', '3fe5eb667764949e', '2012-09-27', 'niclu@gmail.com', 'Via Colombo, 22', 'Genova', 'Italia', '16121', '0114822789', 0, 2, 0),
(27, 'ssag', 'sagedg', 'Norm100', '7815696ecbf1c96e6894b779456d330e', '3fe312c6e421b53e', '2012-10-07', 'sege@hot.it', 'sdgsadg', 'sdfg', 'India', '12234', '214142141', 0, 2, 0),
(28, 'Hassan', 'Metwalley', 'https://www.google.com/accounts/o8/id?id=AItOawnobDMJrOxySeGlUUuk3IjOooYL50WJzzI', '7815696ecbf1c96e6894b779456d330e', '3fc33e6272f93018', '2012-10-07', 'hasmet87@gmail.com', 'Via Cagliari, 25', 'Torino', 'Italia', '10153', '0118857764', 0, 2, 0),
(29, 'Andrea', 'Galeazzi', 'https://me.yahoo.com/a/Gutp4pNijfEwXG9dUYmihKPlCUw.UQE-#2d88f', '7815696ecbf1c96e6894b779456d330e', '3fb697a800593fc0', '2012-10-07', 'andgal@yahoo.it', 'Via Napoli, 34', 'Roma', 'Italia', '00184', '0213377645', 0, 2, 0),
(30, 'Luca', 'Moretto', 'oauth2:facebook:595304939', 'not set', '3fc60a95b1cc0dc8', '2012-10-09', 'l.moretto88@gmail.com', 'via roma 15b', 'baldissero torinese', 'Italia', '10020', '3475273564', 0, 2, 0),
(38, 'Francesco', 'Brundu', 'https://www.google.com/accounts/o8/id?id=AItOawko4pWXciTG60TocQMoZfaBX6hdmV3E_X0', 'not set', '3fe8ef146b764ea3', '2012-10-09', 'francesco.brundu@gmail.com', 'via Ugo La Malfa, 3b', 'Sassari', 'Italia', '07100', NULL, 0, 2, 0),
(41, 'Matteo', 'Ferrari', 'https://www.google.com/accounts/o8/id?id=AItOawlR3Cx0C2vXXXWy1M8hlkTdAzoFi8yTLUw', 'not set', '3fd3a378aba02cba', '2012-10-10', 'jyxpowa@gmail.com', 'via dei cazzi miei', 'Torino', 'Italia', '10127', '011 233542', 0, 1, 0),
(43, 'maccio', 'capatonda', 'https://me.yahoo.com/a/nyOQk7kKnfqT5mDf5Tqlmm8OpCOPAQ--#41f6d', 'not set', '3fea49cb99657659', '2012-10-10', 'maccio.capatonda@lollino.it', 'via dei cazzi miei', 'Torino', 'Italia', '10127', '011 233542', 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `member_status`
--

CREATE TABLE IF NOT EXISTS `member_status` (
  `idMember_Status` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idMember_Status`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `member_status`
--

INSERT INTO `member_status` (`idMember_Status`, `description`) VALUES
(0, 'Non Verificato'),
(1, 'Verificato - Disabilitato'),
(2, 'Abilitato');

-- --------------------------------------------------------

--
-- Table structure for table `member_type`
--

CREATE TABLE IF NOT EXISTS `member_type` (
  `idMember_Type` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idMember_Type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `member_type`
--

INSERT INTO `member_type` (`idMember_Type`, `description`) VALUES
(0, 'Normale'),
(1, 'Responsabile'),
(2, 'Admin'),
(3, 'Fornitore');

-- --------------------------------------------------------

--
-- Table structure for table `message`
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=20 ;

-- --------------------------------------------------------

--
-- Table structure for table `notify`
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=837 ;

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

CREATE TABLE IF NOT EXISTS `order` (
  `idOrder` int(11) NOT NULL AUTO_INCREMENT,
  `order_name` varchar(100) NOT NULL,
  `date_open` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_close` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `date_delivery` timestamp NULL DEFAULT NULL,
  `idMember_resp` int(11) NOT NULL,
  `idSupplier` int(11) NOT NULL,
  PRIMARY KEY (`idOrder`),
  UNIQUE KEY `order_name` (`order_name`),
  KEY `fk_Order_Member1` (`idMember_resp`),
  KEY `fk_Order_Supplier1` (`idSupplier`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=42 ;

-- --------------------------------------------------------

--
-- Table structure for table `order_product`
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
-- Table structure for table `product`
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=76 ;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`idProduct`, `name`, `description`, `dimension`, `measure_unit`, `unit_block`, `availability`, `transport_cost`, `unit_cost`, `min_buy`, `max_buy`, `imgPath`, `idCategory`, `idSupplier`) VALUES
(22, 'Mela', 'Cesto di mele', 12, 'Kg', 2, 1, 12, 1, 10, 11, 'img/prd/22.jpg', 12, 12),
(23, 'Pera', 'Cesto di pere', 12, 'Kg', 2, 1, 4, 10, 1, 12, 'img/prd/23.jpg', 12, 12),
(24, 'Fragola', 'Sacchetto di fragole', 1, 'Hg', 8, 1, 12, 2, NULL, 30, 'img/prd/24.jpg', 12, 12),
(25, 'Ciliegia', 'Sacchetto di ciliegie', 2, 'Hg', 5, 1, 12, 2, 1, 50, 'img/prd/25.jpg', 12, 12),
(26, 'Insalata', 'Insalata', 1, 'Kg', 3, 1, 16, 2, 2, NULL, 'img/prd/26.jpg', 13, 12),
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
(53, 'Napoli', 'Maglia anno 2013', 1, 'Maglia', 1, 1, 10, 70, 1, 10, 'img/prd/53.bmp', 20, 15);

-- --------------------------------------------------------

--
-- Table structure for table `product_category`
--

CREATE TABLE IF NOT EXISTS `product_category` (
  `idProduct_Category` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`idProduct_Category`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=21 ;

--
-- Dumping data for table `product_category`
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
-- Table structure for table `purchase`
--

CREATE TABLE IF NOT EXISTS `purchase` (
  `idPurchase` int(11) NOT NULL AUTO_INCREMENT,
  `isShipped` tinyint(1) NOT NULL DEFAULT '0',
  `idMember` int(11) NOT NULL,
  `idOrder` int(11) NOT NULL,
  PRIMARY KEY (`idPurchase`),
  KEY `fk_Purchase_Member1` (`idMember`),
  KEY `fk_Purchase_Order1` (`idOrder`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=27 ;

-- --------------------------------------------------------

--
-- Table structure for table `purchase_product`
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
-- Table structure for table `supplier`
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
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`idMember`, `company_name`, `description`, `contact_name`, `fax`, `website`, `payment_method`, `idMemberResp`) VALUES
(12, 'Melinda', 'Vendita frutta e verdura', 'Luigi', '025573389', 'www.melinda.it', 'Postepay', 10),
(13, 'Alex PC', 'Vendita materiale informatico', 'Steve', '025573355', 'www.alex.it', 'Paypal', 10),
(14, 'Carrefour', 'Vendita alimentari', 'Francis', '0114468766', 'www.carrefour.it', 'Mastercard', 11),
(15, 'Sportivando', 'Vendita materiale sportivo', 'Delmonte', '0114478454', 'www.sportivando.it', 'Mastercard', 11);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `log`
--
ALTER TABLE `log`
  ADD CONSTRAINT `fk_Log_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `member`
--
ALTER TABLE `member`
  ADD CONSTRAINT `fk_Member_Member_Status1` FOREIGN KEY (`status`) REFERENCES `member_status` (`idMember_Status`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Member_Member_Type1` FOREIGN KEY (`member_type`) REFERENCES `member_type` (`idMember_Type`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `fk_Message_Member1` FOREIGN KEY (`id_sender`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Member2` FOREIGN KEY (`id_receiver`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Order1` FOREIGN KEY (`Order_idOrder`) REFERENCES `order` (`idOrder`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Message_Product1` FOREIGN KEY (`Product_idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `notify`
--
ALTER TABLE `notify`
  ADD CONSTRAINT `fk_Notify_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `fk_Order_Member1` FOREIGN KEY (`idMember_resp`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Order_Supplier1` FOREIGN KEY (`idSupplier`) REFERENCES `supplier` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `order_product`
--
ALTER TABLE `order_product`
  ADD CONSTRAINT `fk_Order_has_ProductList_Order1` FOREIGN KEY (`idOrder`) REFERENCES `order` (`idOrder`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Order_has_ProductList_ProductList1` FOREIGN KEY (`idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `fk_Product_Product_Category1` FOREIGN KEY (`idCategory`) REFERENCES `product_category` (`idProduct_Category`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Product_Supplier1` FOREIGN KEY (`idSupplier`) REFERENCES `supplier` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `purchase`
--
ALTER TABLE `purchase`
  ADD CONSTRAINT `fk_Purchase_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Purchase_Order1` FOREIGN KEY (`idOrder`) REFERENCES `order` (`idOrder`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `purchase_product`
--
ALTER TABLE `purchase_product`
  ADD CONSTRAINT `fk_Purchase_has_Product_Product1` FOREIGN KEY (`idProduct`) REFERENCES `product` (`idProduct`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Purchase_has_Product_Purchase1` FOREIGN KEY (`idPurchase`) REFERENCES `purchase` (`idPurchase`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `supplier`
--
ALTER TABLE `supplier`
  ADD CONSTRAINT `fk_Supplier_Member1` FOREIGN KEY (`idMember`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Supplier_Member2` FOREIGN KEY (`idMemberResp`) REFERENCES `member` (`idMember`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
