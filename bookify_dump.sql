-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bookify
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bookedAt` datetime(6) NOT NULL,
  `status` enum('CANCELLED','CONFIRMED') NOT NULL,
  `type` enum('EVENT','FLIGHT','HOTEL') NOT NULL,
  `listing_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `checkIn` date DEFAULT NULL,
  `checkOut` date DEFAULT NULL,
  `numGuests` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpft2easulf9x9l18rabap7g59` (`listing_id`),
  KEY `FKeyog2oic85xg7hsu2je2lx3s6` (`user_id`),
  CONSTRAINT `FKeyog2oic85xg7hsu2je2lx3s6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpft2easulf9x9l18rabap7g59` FOREIGN KEY (`listing_id`) REFERENCES `listings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (9,'2025-05-17 22:29:25.010148','CONFIRMED','FLIGHT',4,4,'2025-06-10','2025-06-15',3),(12,'2025-05-20 14:13:22.000000','CANCELLED','FLIGHT',1002,1,'2025-06-19','2025-06-26',2),(13,'2025-05-20 17:28:03.893158','CONFIRMED','FLIGHT',1002,1,NULL,NULL,2),(14,'2025-05-20 17:28:03.896484','CANCELLED','HOTEL',1001,1,'2025-06-19','2025-06-26',2),(15,'2025-05-20 17:28:04.142975','CONFIRMED','EVENT',1003,1,NULL,NULL,2),(16,'2025-05-20 17:32:00.999565','CANCELLED','EVENT',1006,1,NULL,NULL,2),(17,'2025-05-20 17:32:01.000558','CANCELLED','HOTEL',1004,1,'2025-06-16','2025-06-29',2),(25,'2025-05-20 18:32:03.166057','CONFIRMED','HOTEL',1001,1,'2025-05-20','2025-05-27',2),(26,'2025-05-20 18:33:06.335095','CONFIRMED','HOTEL',1007,1,'2025-05-20','2025-05-27',2),(28,'2025-05-20 18:36:33.521502','CANCELLED','HOTEL',1010,1,'2025-05-20','2025-05-27',2),(30,'2025-05-20 18:42:45.108533','CONFIRMED','HOTEL',1010,4,'2025-05-20','2025-05-27',2),(31,'2025-05-20 18:51:12.217650','CONFIRMED','HOTEL',2,4,NULL,NULL,1),(32,'2025-05-20 19:00:31.437401','CONFIRMED','HOTEL',2,4,NULL,NULL,2),(33,'2025-05-20 19:00:57.513349','CONFIRMED','FLIGHT',1002,4,NULL,NULL,3),(41,'2025-05-20 21:39:06.761344','CONFIRMED','HOTEL',1007,10,'2025-05-20','2025-05-27',2),(42,'2025-05-20 21:39:09.119767','CANCELLED','HOTEL',1007,10,'2025-05-20','2025-05-27',2),(44,'2025-05-20 22:43:00.345759','CONFIRMED','HOTEL',1001,10,NULL,NULL,2),(45,'2025-05-21 20:09:13.961801','CONFIRMED','EVENT',1012,9,NULL,NULL,4),(46,'2025-05-21 20:09:14.004801','CONFIRMED','HOTEL',1010,9,'2025-06-18','2025-06-29',4),(47,'2025-05-21 20:11:56.389001','CONFIRMED','FLIGHT',1008,9,'2025-06-15','2025-06-28',3),(48,'2025-05-21 20:47:05.919292','CONFIRMED','FLIGHT',1112,12,'2025-07-07','2025-07-24',13),(50,'2025-05-21 23:15:15.306024','CONFIRMED','HOTEL',1010,12,'2025-05-21','2025-05-28',2),(51,'2025-05-24 16:06:34.991094','CONFIRMED','HOTEL',2,10,'2025-06-20','2025-06-27',2),(52,'2025-05-24 16:06:35.182113','CONFIRMED','EVENT',1006,10,NULL,NULL,2),(53,'2025-05-24 16:06:35.182113','CONFIRMED','FLIGHT',1005,10,NULL,NULL,2),(56,'2025-05-25 20:31:47.582067','CONFIRMED','FLIGHT',1008,14,'2025-05-28','2025-06-25',1),(57,'2025-05-25 20:32:31.174517','CONFIRMED','FLIGHT',1005,14,'2025-05-28','2025-06-25',1);
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `eventDate` datetime(6) NOT NULL,
  `venue` varchar(255) NOT NULL,
  `id` bigint NOT NULL,
  `ticketCapacity` int NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKsndjgmg18n0m5o25ukh1ofb8n` FOREIGN KEY (`id`) REFERENCES `listings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES ('2025-08-15 20:00:00.000000','Pier Amphitheater',5,6),('2025-06-23 20:00:00.000000','Eiffel Tower',1003,197),('2025-06-24 18:00:00.000000','Parc Güell',1006,246),('2025-06-25 15:00:00.000000','Colosseum',1009,298),('2025-06-26 19:30:00.000000','Ammoudi Bay',1012,116),('2025-06-29 20:00:00.000000','Broadway Theatre',1018,298),('2025-06-30 09:00:00.000000','Sugarloaf Mountain',1021,150),('2025-07-07 20:00:00.000000','Port de Solférino',1103,180),('2025-08-13 09:00:00.000000','Versailles',1106,220),('2025-07-05 21:30:00.000000','Tablao Cordobés',1113,200),('2025-09-08 18:00:00.000000','Ibiza Port',1116,250);
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flights`
--

DROP TABLE IF EXISTS `flights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flights` (
  `airline` varchar(255) NOT NULL,
  `arrivalTime` datetime(6) NOT NULL,
  `departure` varchar(255) NOT NULL,
  `departureTime` datetime(6) NOT NULL,
  `id` bigint NOT NULL,
  `arrival` varchar(255) NOT NULL,
  `seatCapacity` int NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKmmn4ttfrm9l8yt7slgo5kfaqi` FOREIGN KEY (`id`) REFERENCES `listings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flights`
--

LOCK TABLES `flights` WRITE;
/*!40000 ALTER TABLE `flights` DISABLE KEYS */;
INSERT INTO `flights` VALUES ('Oceanic','2025-07-10 22:00:00.000000','JFK','2025-07-10 10:00:00.000000',4,'LHR',11),('Bookify Air','2025-06-27 18:00:00.000000','Bucharest (OTP)','2025-06-20 12:00:00.000000',1002,'Paris (CDG)',173),('Bookify Air','2025-06-28 18:15:00.000000','Bucharest (OTP)','2025-06-21 12:30:00.000000',1005,'Barcelona (BCN)',177),('Bookify Air','2025-06-29 17:10:00.000000','Bucharest (OTP)','2025-06-22 11:45:00.000000',1008,'Rome (FCO)',174),('Bookify Air','2025-06-30 16:50:00.000000','Bucharest (OTP)','2025-06-23 09:20:00.000000',1011,'Santorini (JTR)',160),('Bookify Air','2025-07-01 21:30:00.000000','Bucharest (OTP)','2025-06-24 14:00:00.000000',1014,'Tokyo (HND)',250),('Bookify Air','2025-07-02 22:45:00.000000','Bucharest (OTP)','2025-06-25 08:10:00.000000',1017,'New York (JFK)',260),('Bookify Air','2025-07-03 21:00:00.000000','Bucharest (OTP)','2025-06-26 07:30:00.000000',1020,'Rio (GIG)',220),('Bookify Air','2025-07-04 19:10:00.000000','Bucharest (OTP)','2025-06-27 13:40:00.000000',1023,'Phuket (HKT)',200),('Bookify Air','2025-07-11 17:30:00.000000','Bucharest','2025-07-04 10:00:00.000000',1102,'Paris',180),('Bookify Air','2025-08-17 18:10:00.000000','Bucharest','2025-08-10 11:20:00.000000',1105,'Paris',180),('Bookify Air','2025-07-09 18:10:00.000000','Bucharest','2025-07-02 12:00:00.000000',1112,'Barcelona',167),('Bookify Air','2025-09-13 19:40:00.000000','Bucharest','2025-09-06 13:10:00.000000',1115,'Ibiza',180);
/*!40000 ALTER TABLE `flights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotels`
--

DROP TABLE IF EXISTS `hotels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotels` (
  `address` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `starRating` int DEFAULT NULL,
  `id` bigint NOT NULL,
  `totalRooms` int NOT NULL,
  `availableFrom` date NOT NULL,
  `availableTo` date NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK6rayolplxmvxe996892sixp4k` FOREIGN KEY (`id`) REFERENCES `listings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotels`
--

LOCK TABLES `hotels` WRITE;
/*!40000 ALTER TABLE `hotels` DISABLE KEYS */;
INSERT INTO `hotels` VALUES ('13 Beach Blvd','Barcelona',4,2,2,'2025-06-01','2025-06-30'),('1 Champ de Mars','Paris',4,1001,36,'2025-05-20','2025-11-20'),('10 Carrer de Mallorca','Barcelona',4,1004,33,'2025-05-20','2025-11-20'),('2 Via dei Fori Imperiali','Rome',4,1007,35,'2025-05-20','2025-11-20'),('1 Oia Cliffside','Oia',5,1010,21,'2025-05-20','2025-11-20'),('1 Shibuya Crossing','Tokyo',4,1013,60,'2025-05-20','2025-11-20'),('1560 Broadway','New York',4,1016,54,'2025-05-20','2025-11-20'),('Avenida Atlântica 1702','Rio',5,1019,44,'2025-05-20','2025-11-20'),('99 Beach Road','Phuket',4,1022,50,'2025-05-20','2025-11-20'),('12 Rue du Louvre','Paris',4,1101,32,'2025-07-05','2025-08-31'),('99 La Rambla','Barcelona',4,1111,30,'2025-07-03','2025-07-31'),('Playa d’en Bossa 1','Ibiza',4,1114,40,'2025-09-05','2025-10-10'),('Clujului','Cluj',4,1119,100,'2025-05-29','2025-06-25');
/*!40000 ALTER TABLE `hotels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `listings`
--

DROP TABLE IF EXISTS `listings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `listings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(1000) DEFAULT NULL,
  `listingType` enum('EVENT','FLIGHT','HOTEL') NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `title` varchar(255) NOT NULL,
  `created_by_id` bigint DEFAULT NULL,
  `country` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa2vf5r0813tsiqq62f98catxl` (`created_by_id`),
  CONSTRAINT `FKa2vf5r0813tsiqq62f98catxl` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1125 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `listings`
--

LOCK TABLES `listings` WRITE;
/*!40000 ALTER TABLE `listings` DISABLE KEYS */;
INSERT INTO `listings` VALUES (2,'Ocean views & breakfast','HOTEL',120.00,'Seaside Inn',1,'ES'),(4,'NYC → London','FLIGHT',83.00,'Transatlantic Flight',1,'ES'),(5,'Live band by the pier','EVENT',39.99,'Summer Concert',1,'ES'),(1001,'Stay right next to the Eiffel Tower.','HOTEL',150.00,'Eiffel-Tower Hotel',1,'FR'),(1002,'Return flight Bucharest ↔ Paris','FLIGHT',310.00,'Flight to Paris',1,'FR'),(1003,'Guided tour & dinner at the Tower.','EVENT',75.00,'Eiffel-Tower Experience',1,'FR'),(1004,'Steps from La Sagrada Familia.','HOTEL',140.00,'Sagrada Hotel',1,'ES'),(1005,'Return flight Bucharest ↔ Barcelona','FLIGHT',195.00,'Flight to Barcelona',1,'ES'),(1006,'Discover Gaudí´s masterpieces.','EVENT',65.00,'Gaudí Walking Tour',1,'ES'),(1007,'Boutique stay by the Colosseum.','HOTEL',155.00,'Colosseum Suites',1,'IT'),(1008,'Return flight Bucharest ↔ Rome','FLIGHT',185.00,'Flight to Rome',1,'IT'),(1009,'Arena floor tour & forum pass.','EVENT',60.00,'Ancient Rome Pass',1,'IT'),(1010,'Infinity-pool views of the caldera.','HOTEL',165.00,'Caldera View Hotel',1,'GR'),(1011,'Return flight Bucharest ↔ Santorini','FLIGHT',220.00,'Flight to Santorini',1,'GR'),(1012,'Dinner cruise around the island.','EVENT',70.00,'Sunset Catamaran',1,'GR'),(1013,'Panoramic rooms above Shibuya.','HOTEL',190.00,'Shibuya Sky Hotel',1,'JP'),(1014,'Return flight Bucharest ↔ Tokyo','FLIGHT',620.00,'Flight to Tokyo',1,'JP'),(1015,'Roll & taste with a local chef.','EVENT',85.00,'Sushi Masterclass',1,'JP'),(1016,'Heart of the action in NYC.','HOTEL',205.00,'Times Square Hotel',1,'US'),(1017,'Return flight Bucharest ↔ NYC','FLIGHT',650.00,'Flight to NYC',1,'US'),(1018,'Evening performance ticket.','EVENT',95.00,'Broadway Show',1,'US'),(1019,'Beachfront icon in Rio.','HOTEL',175.00,'Copacabana Palace',1,'BR'),(1020,'Return flight Bucharest ↔ Rio','FLIGHT',540.00,'Flight to Rio',1,'BR'),(1021,'Guided hike + cable-car descent.','EVENT',70.00,'Sugarloaf Hike',1,'BR'),(1022,'Swim-up bar & sunset views.','HOTEL',135.00,'Patong Beach Resort',1,'TH'),(1023,'Return flight Bucharest ↔ Phuket','FLIGHT',510.00,'Flight to Phuket',1,'TH'),(1101,'Artist loft near the Louvre','HOTEL',162.00,'Louvre Loft',1,'FR'),(1102,'','FLIGHT',210.00,'Flight Bucharest-Paris (July)',1,'FR'),(1103,'Live music on the Seine','EVENT',58.00,'Seine Jazz Cruise',1,'FR'),(1105,'','FLIGHT',215.00,'Flight Bucharest-Paris (Aug)',1,'FR'),(1106,'Palace & gardens tour','EVENT',72.00,'Versailles Day Trip',1,'FR'),(1111,'Central suites on Las Ramblas','HOTEL',145.00,'Las Ramblas Suites',1,'ES'),(1112,'','FLIGHT',198.00,'Flight Bucharest-Barcelona (July)',1,'ES'),(1113,'Flamenco show & tapas','EVENT',55.00,'Flamenco Night',1,'ES'),(1114,'Balearic vibes & pool','HOTEL',175.00,'Beachfront Ibiza Hotel',1,'ES'),(1115,'','FLIGHT',225.00,'Flight Bucharest-Ibiza (Sept)',1,'ES'),(1116,'Sunset DJ cruise','EVENT',70.00,'Ibiza Boat Party',1,'ES'),(1119,'Cel mai tare loc din centrul Clujului.','HOTEL',100.00,'Continental Cluj',1,'RO');
/*!40000 ALTER TABLE `listings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `paidAt` datetime DEFAULT NULL,
  `status` enum('FAILED','PENDING','SUCCESSFUL') NOT NULL,
  `transactionId` varchar(255) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc52o2b1jkxttngufqp3t7jr3h` (`booking_id`),
  CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (4,150.00,NULL,'PENDING',NULL,14),(5,210.00,NULL,'PENDING',NULL,13),(6,75.00,NULL,'PENDING',NULL,15),(7,65.00,NULL,'PENDING',NULL,16),(8,140.00,NULL,'PENDING',NULL,17),(16,150.00,NULL,'PENDING',NULL,25),(17,155.00,NULL,'PENDING',NULL,26),(19,165.00,NULL,'PENDING',NULL,28),(21,165.00,NULL,'PENDING',NULL,30),(22,120.00,NULL,'PENDING',NULL,31),(23,120.00,NULL,'PENDING',NULL,32),(24,210.00,NULL,'PENDING',NULL,33),(32,155.00,NULL,'PENDING',NULL,41),(33,155.00,NULL,'PENDING',NULL,42),(35,150.00,NULL,'PENDING',NULL,44),(36,165.00,'2025-05-21 20:09:14','SUCCESSFUL','55076a37-d2aa-4221-995f-397598d94ad4',46),(37,70.00,'2025-05-21 20:09:14','SUCCESSFUL','ce8ff481-e3af-48e0-8273-8299bd91e05f',45),(38,185.00,'2025-05-21 20:11:56','SUCCESSFUL','51de65cd-6b83-4101-a53d-86c359d31f10',47),(39,198.00,'2025-05-21 20:47:06','SUCCESSFUL','d482f78d-1306-4e70-9ba2-df7de741a7ec',48),(41,165.00,'2025-05-21 23:15:15','SUCCESSFUL','b893f131-8f97-4a5b-9d9c-4212b4c91dd1',50),(42,120.00,'2025-05-24 16:06:35','SUCCESSFUL','06d37164-c282-4f03-9604-a98db32dec64',51),(43,65.00,'2025-05-24 16:06:35','SUCCESSFUL','f0bf49ab-aab3-4eeb-9bf9-863df658c04c',52),(44,195.00,'2025-05-24 16:06:35','SUCCESSFUL','7d58e60c-342a-455c-ad45-e50fc9c0d76f',53),(46,185.00,'2025-05-25 20:31:48','SUCCESSFUL','92227b21-d402-4680-8179-80333726e81d',56),(47,195.00,'2025-05-25 20:32:31','SUCCESSFUL','4b9ba903-054a-4087-a6bc-1c0c04511315',57);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(2000) DEFAULT NULL,
  `createdAt` datetime(6) NOT NULL,
  `moderated` bit(1) NOT NULL,
  `moderationRemarks` varchar(255) DEFAULT NULL,
  `rating` int NOT NULL,
  `booking_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK28an517hrxtt2bsg93uefugrm` (`booking_id`),
  CONSTRAINT `FK28an517hrxtt2bsg93uefugrm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (8,'Perfect experience!','2025-05-20 22:18:19.500393',_binary '',NULL,5,41),(9,'It was great!','2025-05-20 22:52:21.326797',_binary '',NULL,5,44),(10,'Beautifull!','2025-05-21 20:10:56.276261',_binary '',NULL,5,45),(11,'Fine!','2025-05-21 20:11:04.357252',_binary '\0','No motivation!',2,46),(12,'Acceptable! Little delay!','2025-05-21 20:36:38.369298',_binary '\0',NULL,3,47),(13,'Acceptable','2025-05-21 20:47:33.034384',_binary '\0','Why?',4,48),(14,'Mi-a placut maxim!','2025-05-21 23:42:43.864539',_binary '',NULL,5,50),(16,'So cool!','2025-05-25 20:32:04.305166',_binary '\0',NULL,3,56),(17,'Best flight ever!','2025-05-25 20:32:47.858049',_binary '',NULL,5,57);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age` bigint NOT NULL,
  `email` varchar(255) NOT NULL,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `resetPasswordToken` varchar(255) DEFAULT NULL,
  `resetTokenExpiry` datetime(6) DEFAULT NULL,
  `role` enum('ROLE_ADMIN','ROLE_USER') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,22,'calina.borzan18@yahoo.com','Calin','Borzan','$2a$10$BHQddswcxOfEdj.EMYCP3O8Vgy7shACVLbxDwnpXmN2kwTTNL3vwi',NULL,NULL,'ROLE_ADMIN','calinaborzan'),(4,19,'calinaborzan@gmail.com','Calina','Borzan','$2a$10$WNA0RfQOi/3cgXAV3TeXE.80.R4wj/gv5lIrJQFtqYX/Fr1.3S9n.',NULL,NULL,'ROLE_USER','calina'),(6,21,'gozmanpopmaria@gmail.com','Maria','Gozman','$2a$10$tjqIGHuECLZIa8K//mNM1OVcn5kvC/b.K2NzvJTEavqcIDdnKryXq',NULL,NULL,'ROLE_USER','mariag2'),(9,22,'calinaborzan7@gmail.com','Calina','Borzan','$2a$10$E7ga9sqkNLR3UdbCGIcPg.lxG/BKwthyf6Zh.Ndno3DXeQpckKlvG',NULL,NULL,'ROLE_USER','calinaborzan4'),(10,54,'carmen_borzan@yahoo.com','Carmena','Borzan','$2a$10$b/ZaP/Mfj8IopFSR7hT0ZOORiYMMq5FZzp3US4c9nIxhRnCksTR8C',NULL,NULL,'ROLE_USER','carmenborzan'),(11,22,'murimoro67@gmail.com','Andrei','Muresan','$2a$10$hNcj.m15taw/Q6gAA6PFZuooVPZyuLXLvTbGfBA2MPcfINa3IFmKO',NULL,NULL,'ROLE_USER','muri'),(12,54,'nicusor@yahoo.com','Nicusor','Dan','$2a$10$mcQ9YLi3hD9yLhqmmnExWOj9YQ4KVg6lB2okWK131g3ufTMcGVryO',NULL,NULL,'ROLE_USER','nicu3'),(14,22,'bia@yahoo.com','Bianca','Horvat','$2a$10$v5uoKmepuX2/pjltaHe6BeDAnQ7na2GwAiM1hh.4dXq/D8RSvk9EK',NULL,NULL,'ROLE_USER','bia');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-25 20:42:55
