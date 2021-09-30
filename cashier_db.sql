-- phpMyAdmin SQL Dump
-- version 5.0.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 21, 2021 at 12:16 PM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.4.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cashier_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `id` int(11) NOT NULL,
  `nama_barang` varchar(100) DEFAULT NULL,
  `harga` int(20) DEFAULT NULL,
  `stok` int(20) DEFAULT NULL,
  `satuan` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`id`, `nama_barang`, `harga`, `stok`, `satuan`) VALUES
(1, 'Indomie Ayam Geprek', 12000, 9, 'Ikat'),
(2, 'Air Mineral', 2000, 4, 'Botol'),
(3, 'Mie Goreng', 3000, 3, 'Bungkus'),
(4, 'Pocky Chocolate', 7000, 8, 'Bungkus'),
(5, 'Nissin Crackers', 8000, 7, 'Bungkua'),
(6, 'Rinso Anti Noda', 11700, 6, 'Bungkus');

-- --------------------------------------------------------

--
-- Table structure for table `det_transaksi`
--

CREATE TABLE `det_transaksi` (
  `id` int(11) NOT NULL,
  `id_barang` int(11) DEFAULT NULL,
  `harga` int(11) DEFAULT NULL,
  `jumlah` int(11) DEFAULT NULL,
  `subtotal` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `det_transaksi`
--

INSERT INTO `det_transaksi` (`id`, `id_barang`, `harga`, `jumlah`, `subtotal`) VALUES
(1, 2, 2000, 5, 10000),
(2, 3, 3000, 5, 15000),
(3, 1, 12000, 1, 12000),
(4, 3, 3000, 10, 30000),
(5, 1, 12000, 1, 12000),
(5, 2, 2000, 1, 2000),
(5, 3, 3000, 2, 6000),
(5, 4, 7000, 2, 14000),
(5, 5, 8000, 1, 8000),
(5, 6, 11700, 1, 11700);

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `id` int(11) NOT NULL,
  `role` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`id`, `role`) VALUES
(1, 'Admin'),
(2, 'Petugas');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id` int(11) NOT NULL,
  `id_detail` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `tgl` date DEFAULT NULL,
  `jam` time DEFAULT NULL,
  `total` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id`, `id_detail`, `id_user`, `tgl`, `jam`, `total`) VALUES
(1, 1, 1, '2021-02-05', '08:37:30', 10000),
(2, 2, 2, '2021-02-09', '08:26:19', 15000),
(3, 3, 1, '2021-02-16', '12:56:22', 12000),
(4, 4, 1, '2021-02-16', '13:03:22', 30000),
(5, 5, 1, '2021-02-16', '13:08:35', 53700);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `nama` varchar(50) DEFAULT NULL,
  `username` varchar(25) DEFAULT NULL,
  `password` varchar(10) DEFAULT NULL,
  `role` enum('Admin','Petugas') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `nama`, `username`, `password`, `role`) VALUES
(1, 'Ari Kurniawan', 'ari', '123', 'Admin'),
(2, 'Andre Nugroho', 'andre13', '321', 'Petugas'),
(3, 'Dimas Sulistiyo', 'dimas', '231', 'Admin');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `det_transaksi`
--
ALTER TABLE `det_transaksi`
  ADD KEY `id_barang` (`id_barang`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD KEY `role_id` (`role`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `role`
--
ALTER TABLE `role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `det_transaksi`
--
ALTER TABLE `det_transaksi`
  ADD CONSTRAINT `det_transaksi_ibfk_1` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id`);

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
