SET FOREIGN_KEY_CHECKS=0;
INSERT INTO employeeData.employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES
	 (1,'Snoopy','Beagle','Snoopy@example.com','2022-08-01',45000.00,'111-11-1111'),
	 (2,'Charlie','Brown','Charlie@example.com','2022-07-01',48000.00,'111-22-1111'),
	 (3,'Lucy','Doctor','Lucy@example.com','2022-07-03',55000.00,'111-33-1111'),
	 (4,'Pepermint','Patti','Peppermint@example.com','2022-08-02',98000.00,'111-44-1111'),
	 (5,'Linus','Blanket','Linus@example.com','2022-09-01',43000.00,'111-55-1111'),
	 (6,'PigPin','Dusty','PigPin@example.com','2022-10-01',33000.00,'111-66-1111'),
	 (7,'Scooby','Doo','Scooby@example.com','1973-07-03',78000.00,'111-77-1111'),
	 (8,'Shaggy','Rodgers','Shaggy@example.com','1973-07-11',77000.00,'111-88-1111'),
	 (9,'Velma','Dinkley','Velma@example.com','1973-07-21',82000.00,'111-99-1111'),
	 (10,'Daphne','Blake','Daphne@example.com','1973-07-30',59000.00,'111-00-1111');
INSERT INTO employeeData.employees (empid,Fname,Lname,email,HireDate,Salary,SSN) VALUES
	 (11,'Bugs','Bunny','Bugs@example.com','1934-07-01',18000.00,'222-11-1111'),
	 (12,'Daffy','Duck','Daffy@example.com','1935-04-01',16000.00,'333-11-1111'),
	 (13,'Porky','Pig','Porky@example.com','1935-08-12',16550.00,'444-11-1111'),
	 (14,'Elmer','Fudd','Elmer@example.com','1934-08-01',15500.00,'555-11-1111'),
	 (15,'Marvin','Martian','Marvin@example.com','1937-05-01',28000.00,'777-11-1111');
INSERT INTO employeeData.cities (cityID,cityName) VALUES
	 (1,'Atlanta'),
	 (2,'New York');
INSERT INTO employeeData.states (stateID,state) VALUES
	 (1,'GA'),
	 (2,'NY');
INSERT INTO employeeData.division (divID,Name,cityID,addressLine1,addressLine2,stateID,country,postalCode) VALUES
	 (1,'Technology Engineering',1,'200 17th Street NW','',1,'USA','30363'),
	 (2,'Marketing',1,'200 17th Street NW','',1,'USA','30363'),
	 (3,'Human Resources',2,'45 West 57th Street','',2,'USA','00034'),
	 (999,'HQ',2,'45 West 57th Street','',2,'USA','00034');
INSERT INTO employeeData.employee_division (empid,div_ID) VALUES
	 (7,1),
	 (10,1),
	 (1,999),
	 (2,999),
	 (3,999);
INSERT INTO employeeData.employee_job_titles (empid,job_title_id) VALUES
	 (7,100),
	 (5,101),
	 (4,102),
	 (8,102),
	 (9,102),
	 (10,102),
	 (14,103),
	 (15,103),
	 (11,200),
	 (6,201);
INSERT INTO employeeData.employee_job_titles (empid,job_title_id) VALUES
	 (12,201),
	 (13,202),
	 (2,900),
	 (3,901),
	 (1,902);
INSERT INTO employeeData.job_titles (job_title_id,job_title) VALUES
	 (100,'software manager'),
	 (101,'software architect'),
	 (102,'software engineer'),
	 (103,'software developer'),
	 (200,'marketing manager'),
	 (201,'marketing associate'),
	 (202,'marketing assistant'),
	 (900,'Chief Exec. Officer'),
	 (901,'Chief Finn. Officer'),
	 (902,'Chief Info. Officer');
INSERT INTO employeeData.login (empid,username,password_hash,`role`,created_at,updated_at) VALUES
	 (1,'admin1','Ybjsm+iZ9gldZB0EzxKVrg==','HR_ADMIN','2026-04-24 22:29:56','2026-04-24 22:29:56'),
	 (2,'snoopy','RnIZ+y15oOdi01OUMadtPQ==','GENERAL_EMPLOYEE','2026-04-24 22:29:56','2026-04-24 22:29:56'),
	 (3,'charlie','RnIZ+y15oOdi01OUMadtPQ==','GENERAL_EMPLOYEE','2026-04-24 22:29:56','2026-04-24 22:29:56'),
	 (4,'lucy','RnIZ+y15oOdi01OUMadtPQ==','GENERAL_EMPLOYEE','2026-04-24 22:29:56','2026-04-24 22:29:56'),
	 (5,'peppermint','RnIZ+y15oOdi01OUMadtPQ==','HR_ADMIN','2026-04-24 22:29:56','2026-04-24 22:29:56');
INSERT INTO employeeData.payroll (payID,pay_date,earnings,fed_tax,fed_med,fed_SS,state_tax,retire_401k,health_care,empid) VALUES
	 (1,'2026-01-31',865.38,276.92,12.55,53.65,103.85,3.46,26.83,1),
	 (2,'2025-12-31',923.08,295.38,13.38,57.23,110.77,3.69,28.62,1),
	 (3,'2026-01-31',923.08,295.38,13.38,57.23,110.77,3.69,28.62,2),
	 (4,'2025-12-31',923.08,295.38,13.38,57.23,110.77,3.69,28.62,2),
	 (5,'2026-01-31',1057.69,338.46,15.34,65.58,126.92,4.23,32.79,3),
	 (6,'2025-12-31',1057.69,338.46,15.34,65.58,126.92,4.23,32.79,3),
	 (7,'2026-01-31',1884.62,603.08,27.33,116.85,226.15,7.54,58.42,4),
	 (8,'2025-12-31',826.92,264.62,11.99,51.27,99.23,3.31,25.63,4),
	 (9,'2026-01-31',826.92,264.62,11.99,51.27,99.23,3.31,25.63,5),
	 (10,'2025-12-31',826.92,264.62,11.99,51.27,99.23,3.31,25.63,5);
INSERT INTO employeeData.payroll (payID,pay_date,earnings,fed_tax,fed_med,fed_SS,state_tax,retire_401k,health_care,empid) VALUES
	 (11,'2026-01-31',634.62,203.08,9.20,39.35,76.15,2.54,19.67,6),
	 (12,'2025-12-31',634.62,203.08,9.20,39.35,76.15,2.54,19.67,6);
SET FOREIGN_KEY_CHECKS=1;