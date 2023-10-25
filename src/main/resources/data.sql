INSERT IGNORE INTO `user` (id, email, name, password, license_number, current_balance) VALUES (1, 'admin@gmail.com', 'Auction Ninja Admin','$2a$10$bg4htGxk4Lq7F8jABitWtedREpNFyGgOSFtaXNjr9D3.8YusaNQ26', '11111111', 10000); -- password admin
INSERT IGNORE INTO `user` (id, email, name, password, license_number, current_balance) VALUES (2, 'seller@gmail.com', 'Bid Dynasty','$2a$10$FNY7zLAN8iXKtyzWvLkdW.Zt4JOABpOa7HQ7wPnzcdg/o2NS6i9Ha', '22222222', 10000); -- password seller
INSERT IGNORE INTO `user` (id, email, name, password, license_number, current_balance) VALUES (3, 'customer@gmail.com', 'Savvy Shopper','$2a$12$Pi4hgQI2xLd942yc6S5f/uFY0dDCzQqiyjtxv3lMhDu9pYPP3PsHG', '33333333', 10000); -- password customer
INSERT IGNORE INTO `user` (id, email, name, password, license_number, current_balance) VALUES (4, 'customer2@gmail.com', 'Bid Buddy', '$2a$12$Pi4hgQI2xLd942yc6S5f/uFY0dDCzQqiyjtxv3lMhDu9pYPP3PsHG', '44444444', 10000); -- password customer


INSERT IGNORE INTO `role` VALUES (1, 'ADMIN');
INSERT IGNORE INTO `role` VALUES (2, 'SELLER');
INSERT IGNORE INTO `role` VALUES (3, 'CUSTOMER');


INSERT IGNORE INTO `user_role` VALUES (1, 1, 1);
INSERT IGNORE INTO `user_role` VALUES (2, 2, 2);
INSERT IGNORE INTO `user_role` VALUES (3, 3, 3);
INSERT IGNORE INTO `user_role` VALUES (4, 3, 4);
