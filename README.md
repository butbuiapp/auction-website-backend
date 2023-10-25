## Requirement

**Secure Online Auction System**

Online auction however is a different business model where the items are sold through price bidding. Usually bidding have start price and ending time. Potential buyers in auction and the winner is the one who bids the item for highest price. We treat the fraud detection with a binary classification. For buying product online user have to provide his personal details like email address, license number,etc. Only the valid user will have authority to bid. This prevents various frauds according in online shopping.

**Modules**

Customer Module:

- Customer register: customer will be provided with a personal account through registration
- Customer must provide email address, license number. Email cannot be duplicated
- After registration, customer will receive verification email with unique verification code along with login page URL.
- Verification code only valid in 10 minutes
- When the customer first time tries to login the website, it'll redirect customer to verification page, only the verification code matches the one in the email, customer can logs in successfully. Customer can choose to resend the verification code. If fails 3 times, redirect to login page again.
- Profile Verification: The customers profile verified by the admin for the auction bid participation.
- Without being verified by admin, customer cannot bid any product.
- Customer Login: Login to the system with valid username and password.
- If customer forget password, can reset password. Customer can only update password when verification code in email matches.
- View auction product:
- Customer can view products page with [pagination or infinite loop]
- Customer can view product detail page
- Search Product
- Auction Products: Only verified customer can able to view auction to bidding for product.
- Customer must make a deposit before bidding, the deposit is 10% of the starting price by default. As a seller, it can set a deposit amount.
- View bid history including all bidding prices, bidding item, etc. bidding history is organized monthly, yearly.
- As winner of a bidding, be able to print the invoice.

Seller Module

The seller module includes different sellers who wish to sell their products. A seller can add or delete or modify information about different items. The different functionalities for seller are

- Register as a seller: the same as "Customer" Module. Only verified seller can sell product.
- Can add a new a product, a product can be in several categories, multiple product images, description, bid starting price, deposit, bid due date, bidding payment due date.
- When add a new product, seller can choose "save without release", or "save and release".
- If choose save without release, seller can update all information about the product and delete the product. And customer cannot see the product.
- If choose save and release, customer can see the product and bid. And seller are no longer available to update the product.
- Can delete a product if only in save status. Or no one bid on this product.
- Can modify information related to the product such as price, basic information only save or release but no one bid yet.
- Be able to view monthly earning report.

Admin Module

The administrative module includes an admin who acts as an intermediator between seller and the customer.

- Admin Login: Login with authorized username and passwords(can be hardcoded).
- Must use admin login page different from regular user
- Verify Customers & Sellers : The Administrator verifies new users when the online auctioning also approve authorized seller/customer after registration.
- You can choose to connect to DMV datasource API, or fake checking
- Admin can CRUD product categories
- Cannot delete a product category if any product assigned to this category.

Bidding System

- Only verified customer can bid product, seller cannot bid their own products.
- Customer can only increase the price, not decrease
- Always display the current highest bid,
- Display how many customer bid on the product
- After the bid due date, close the bidding system
- Pick the highest bidder as the winner
- Return deposit to all bidders except the winner
- Customer must pay full amount of the bidding price in the certain days which is configured by the seller.
- If not paid, charge deposit only
- Seller must ship the item in 3 days after customer pays. If not shipped, return deposit. Seller receives the full amount of money only after customer receive the item or in 30 days after shipping.

## Prerequisites

Before you begin, ensure you have the following installed:

- Java Development Kit (JDK)
- Apache Maven
- MySQL installation with schema named "auction"

## Build the Application

To build the Spring Boot application and create a JAR file, run the following Maven command in the project directory:

```
mvn clean package
```

## Run the Application

To run the Spring Boot application as executable jar file, use the following command:

```
java -jar target/online-auction-api-1.0.0.jar
```

To run the Spring Boot application with mvn plugin (mvn package not required)

```
mvn spring-boot:run
```

