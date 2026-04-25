-- employeeData.cities definition

CREATE TABLE `cities` (
  `cityID` int NOT NULL,
  `cityName` varchar(25) NOT NULL,
  PRIMARY KEY (`cityID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cities';


-- employeeData.employees definition

CREATE TABLE `employees` (
  `empid` int NOT NULL,
  `Fname` varchar(65) NOT NULL,
  `Lname` varchar(65) NOT NULL,
  `email` varchar(65) NOT NULL,
  `HireDate` date DEFAULT NULL,
  `Salary` decimal(10,2) NOT NULL,
  `SSN` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`empid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- employeeData.job_titles definition

CREATE TABLE `job_titles` (
  `job_title_id` int NOT NULL,
  `job_title` varchar(125) NOT NULL,
  PRIMARY KEY (`job_title_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- employeeData.states definition

CREATE TABLE `states` (
  `stateID` int NOT NULL,
  `state` varchar(2) NOT NULL,
  PRIMARY KEY (`stateID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='States';


-- employeeData.addresses definition

CREATE TABLE `addresses` (
  `addressID` int NOT NULL,
  `street` varchar(50) NOT NULL,
  `cityID` int NOT NULL,
  `stateID` int NOT NULL,
  `postalCode` varchar(15) NOT NULL,
  `empID` int NOT NULL,
  PRIMARY KEY (`addressID`),
  KEY `cityID` (`cityID`),
  KEY `stateID` (`stateID`),
  KEY `empID` (`empID`),
  CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`cityID`) REFERENCES `cities` (`cityID`),
  CONSTRAINT `addresses_ibfk_2` FOREIGN KEY (`stateID`) REFERENCES `states` (`stateID`),
  CONSTRAINT `addresses_ibfk_3` FOREIGN KEY (`empID`) REFERENCES `employees` (`empid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Addresses';


-- employeeData.demographics definition

CREATE TABLE `demographics` (
  `empID` int NOT NULL,
  `DOB` date DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`empID`),
  CONSTRAINT `demographics_ibfk_1` FOREIGN KEY (`empID`) REFERENCES `employees` (`empid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- employeeData.division definition

CREATE TABLE `division` (
  `divID` int NOT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `cityID` int NOT NULL,
  `addressLine1` varchar(50) NOT NULL,
  `addressLine2` varchar(50) DEFAULT NULL,
  `stateID` int DEFAULT NULL,
  `country` varchar(50) NOT NULL,
  `postalCode` varchar(15) NOT NULL,
  PRIMARY KEY (`divID`),
  KEY `cityID` (`cityID`),
  KEY `stateID` (`stateID`),
  CONSTRAINT `division_ibfk_1` FOREIGN KEY (`cityID`) REFERENCES `cities` (`cityID`),
  CONSTRAINT `division_ibfk_2` FOREIGN KEY (`stateID`) REFERENCES `states` (`stateID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='company divisions';


-- employeeData.employee_division definition

CREATE TABLE `employee_division` (
  `empid` int NOT NULL,
  `div_ID` int NOT NULL,
  PRIMARY KEY (`empid`,`div_ID`),
  KEY `div_ID` (`div_ID`),
  CONSTRAINT `employee_division_ibfk_1` FOREIGN KEY (`empid`) REFERENCES `employees` (`empid`),
  CONSTRAINT `employee_division_ibfk_2` FOREIGN KEY (`div_ID`) REFERENCES `division` (`divID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- employeeData.employee_job_titles definition

CREATE TABLE `employee_job_titles` (
  `empid` int NOT NULL,
  `job_title_id` int NOT NULL,
  PRIMARY KEY (`empid`,`job_title_id`),
  KEY `job_title_id` (`job_title_id`),
  CONSTRAINT `employee_job_titles_ibfk_1` FOREIGN KEY (`empid`) REFERENCES `employees` (`empid`),
  CONSTRAINT `employee_job_titles_ibfk_2` FOREIGN KEY (`job_title_id`) REFERENCES `job_titles` (`job_title_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- employeeData.login definition

CREATE TABLE `login` (
  `empid` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('HR_ADMIN','GENERAL_EMPLOYEE') DEFAULT 'GENERAL_EMPLOYEE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`empid`),
  UNIQUE KEY `username` (`username`),
  CONSTRAINT `login_ibfk_1` FOREIGN KEY (`empid`) REFERENCES `employees` (`empid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- employeeData.payroll definition

CREATE TABLE `payroll` (
  `payID` int NOT NULL,
  `pay_date` date DEFAULT NULL,
  `earnings` decimal(8,2) DEFAULT NULL,
  `fed_tax` decimal(7,2) DEFAULT NULL,
  `fed_med` decimal(7,2) DEFAULT NULL,
  `fed_SS` decimal(7,2) DEFAULT NULL,
  `state_tax` decimal(7,2) DEFAULT NULL,
  `retire_401k` decimal(7,2) DEFAULT NULL,
  `health_care` decimal(7,2) DEFAULT NULL,
  `empid` int NOT NULL,
  PRIMARY KEY (`payID`),
  KEY `empid` (`empid`),
  CONSTRAINT `payroll_ibfk_1` FOREIGN KEY (`empid`) REFERENCES `employees` (`empid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;