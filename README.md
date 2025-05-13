### 📬 Charity boxes management application

Application provides a REST API to manage collection boxes and fundraising events.
Project also includes in total 20 unit tests & 29 integration tests.

---

#### 🔥 Core Functionalities

#### 1. REST API for managing Collection Boxes 
- Add new collection box
- List all collection boxes with information if every box is empty and assigned to event
- Remove collection box
- Assign collection box
- Put money to the collection box
- Transfer money from the box to the fundraising event

#### 2. REST API for managing Fundraising Events
- Create new fundraising event
- Display a financial report (with events and their account status)

#### 3. Validation Rules
- Only empty box can be added to the event
- Box can be assigned to one event at time
- Empty box cannot transfer money to the event
- Not assigned box cannot transfer money
- When box is removed it doesnt transfer money to the event
- There is a predetermined currency list that are supported
- It is checked whether given JSON has correct format (event & box request)

#### 4. Other functionalities
- Collection box can store many various currencies
  - Adding money to the box in new currency -> adds new currency with specific amount
  - Adding money to the box in already existing currency -> increase amount of that currency 
- Fundraising event stores money in one specific currency
    - While transferring money from the box to the event account, all currencies are converted to that specific currency
---

### 📌 API Endpoints

#### 🔻 Collection Box Controller
POST: ```/collection-boxes```
- Register a new collection box initialized with the specified list of currencies and zero balances

GET: ```/collection-boxes```
- Retrieve all collection boxes, each indicating whether it is assigned to an event and whether it is empty.

DELETE: ```/collection-boxes/{id}```
- Remove a collection box by its ID without transferring any funds

PUT: ```/collection-boxes/{boxId}/assignTo/{eventId}```
-  Assign an empty collection box to a specific fundraising event

POST: ```/collection-boxes/{id}/putMoney```
- Add a given amount of money in a specified currency to the collection box

PUT: ```/collection-boxes/{id}/transfer```
- Transfer all funds from the collection box to its assigned event’s account, converting currencies as needed

#### 🔻 Fundraising Event Controller
POST: ```/fundraising-events```
- Create a new fundraising event with an initial account balance of zero in the chosen currency

GET: ```/fundraising-events/report```
- Return a financial report listing each event and its current account balance

---

### 🚀 Getting Started

#### Running locally
1. Build the project
```
./mvnw clean install
```
2. Run it with following command
```
java -jar target/CharityBoxes-0.0.1-SNAPSHOT.jar
```

#### Running with Docker (you need to have Docker installed)
1. Clone this repository<br>
```
git clone https://github.com/BeeBercik/charity-boxes.git
```
2. Move to the root directory<br>
```
cd charity-boxes
``` 
3. Run via Docker Compose:<br>
```
docker-compose up -d --build
```

4. Verify It’s Running
- The API should be available at http://localhost:8080.
- Use e.g. Postman to test endpoints.

---

### ✅ Testing

Project includes 49 tests in total.
- **Unit Tests:** Verifying both services logic.
- **Integration Tests:** Validating end-to-end application behavior using in-memory database.

💡 All tests are run using JUnit 5, with MockMvc for controller testing and a test database for integration tests.

#### 🔻 Running Tests
1. **Move to the root directory**<br>
```
cd charity-boxes
```
2. **Run all tests inside the project directory:**<br>
```
./mvnw clean verify
```

💡 You do not need to have maven installed on your machine, maven wrapper is included in project files.

--- 

#### 🛠️  Technology Stack
- **Java 17** + **Spring Boot**
- **Spring Data JPA** for persistence
- **Maven** for build automation
- **H2 memory db** for storing data and testing
- **Docker & Docker Compose** for containerization
- **JUnit 5, Mockito** for unit and integration tests
