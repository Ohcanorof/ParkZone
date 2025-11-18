# Parking Garage Management System

**Course:** CS401 Software Engineering  
**Team:** Group 7  
**Project Type:** Java Client-Server Application

## 📋 Project Overview

A software management system for parking garages that allows customers to self-park for a fee. The system tracks available spaces, calculates fees based on vehicle type and duration, enables employee-assisted payment processing, and generates usage reports.

### Technical Stack
- **Language:** Java 11+
- **Architecture:** TCP/IP Client-Server
- **GUI:** Java Swing
- **Storage:** File-based (Java Serialization)
- **No external databases, frameworks, or libraries** (per project requirements)

---

## 🎯 Project Status

### ✅ Phase 1 Complete (Requirements)
- SRS Document with 35+ functional requirements
- 10+ non-functional requirements
- Use case diagrams
- Class candidate identification
- Project schedule and meeting minutes

### ✅ Phase 2 Complete (Design)
- **Class Diagrams:** Complete object-oriented design showing inheritance hierarchies, interfaces, and relationships
- **Sequence Diagrams:** 7 interaction flows covering all use cases
- **Requirements Traceability:** Every sequence flow maps to specific SRS requirements
- **Design Patterns:** Polymorphism, mediator, state, factory, and iterator patterns implemented

### 🚧 Phase 3 In Progress (Implementation)
- Server infrastructure and networking
- Client GUI applications
- Business logic implementation
- File persistence system

---

## 📐 Architecture

### System Components (Multi-Threaded Architecture)

```
┌─────────────┐         TCP/IP          ┌──────────────────────────────┐
│ Client GUI  │◄─────────────────────────►│  ParkingSystemServer         │
│ (Customer)  │      Port 8080           │  - Main acceptLoop() thread  │
└─────────────┘                          │  - Spawns ClientHandler      │
                                         │    threads for each client   │
┌─────────────┐         TCP/IP          └──────────────┬───────────────┘
│ Client GUI  │◄─────────────────────────────────────► │
│ (Customer)  │      Port 8080                Thread 1 │
└─────────────┘                          ┌─────────────▼──────────────┐
                                         │ ClientHandler (Thread 1)   │
┌─────────────┐         TCP/IP          │ - Reads client messages    │
│ Admin GUI   │◄─────────────────────────┤ - Handles authentication  │
│ (Operator)  │      Port 8080           │ - Processes requests       │
└─────────────┘                Thread 2  │ - Sends responses          │
                                         └────────────┬───────────────┘
                                                      │
                         ┌────────────────────────────┼────────────────┐
                         │            ALL THREADS ACCESS               │
                         │                                             │
                    ┌────▼──────────────────────────────────────┐     │
                    │   ParkingSystem (SINGLETON)               │     │
                    │   - users: List<User>                     │     │
                    │   - slots: List<ParkingSlot>              │◄────┘
                    │   - tickets: List<Ticket>                 │
                    │   - Thread-safe operations                │
                    │   - Synchronized methods                  │
                    └───────────────────┬───────────────────────┘
                                        │
                                        ▼
                               ┌─────────────────┐
                               │  File Storage   │
                               │  (.dat files)   │
                               └─────────────────┘
```

### Class Hierarchy

**User Management:**
```
User (abstract)
├── Client - Self-service parking customers
└── Admin - Employee with management privileges
```

**Vehicle Management:**
```
Vehicle (abstract) implements Taxable
├── Car - Standard vehicle ($5/hour)
├── Truck - Large vehicle ($10/hour)
├── Bus - Commercial vehicle ($12/hour)
├── Van - Medium vehicle ($7/hour)
├── Bike - Two-wheeler ($2/hour)
├── EV - Electric vehicle ($4/hour with green discount)
└── Scooter - Small two-wheeler ($2/hour)
```

**Core Operations:**
- `ParkingSlot` - Manages individual parking space state
- `Ticket` - Tracks parking sessions and calculates fees
- `Report` - Generates analytics and usage reports
- `System` - Central coordinator for all operations

---

## 🔄 Key Workflows

### Customer Workflows

1. **Registration & Login**
   - Create account with email/password
   - Register vehicle(s) with plate number, type, color, brand, model
   - Secure authentication with password hashing

2. **Park Vehicle**
   - View real-time available slots
   - Select parking slot
   - System generates ticket with unique ID
   - Records entry time automatically

3. **Exit & Payment**
   - Search ticket by plate number or slot number
   - System calculates total fee based on vehicle type and duration
   - Process payment
   - System releases parking slot
   - Ticket archived to history

### Admin Workflows

1. **System Management**
   - Add new parking slots to system
   - View all active tickets across all users
   - Monitor parking lot capacity in real-time

2. **Manual Payment Processing**
   - Assist customers at exit terminals
   - Search tickets by multiple criteria
   - Override and process payments manually
   - Release slots and update system state

3. **Reporting & Analytics**
   - Generate usage reports for date ranges
   - Calculate total revenue from tickets
   - Analyze parking duration patterns
   - View vehicle type distribution
   - Access all registered vehicles in system

---

## 🎨 Design Patterns

Our implementation demonstrates key software engineering patterns:

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Polymorphism** | Vehicle fee calculation via `Taxable` interface | Different vehicle types calculate fees differently |
| **Inheritance** | User→Client/Admin, Vehicle→Car/Truck/etc | Code reuse and logical hierarchy |
| **Mediator** | System class coordinates all operations | Reduces coupling between components |
| **State** | Ticket lifecycle (active→paid→archived) | Clean state management |
| **Factory** | System creates User, Vehicle, Ticket objects | Centralized object creation |
| **Iterator** | Admin loops through Users to aggregate data | Clean collection traversal |

### Polymorphic Fee Calculation Example

```java
// Each vehicle type implements its own fee logic
class Car extends Vehicle {
    @Override
    public double calculateFee(Duration duration) {
        return duration.toHours() * 5.00;
    }
}

class Truck extends Vehicle {
    @Override
    public double calculateFee(Duration duration) {
        return duration.toHours() * 10.00;
    }
}

// Ticket doesn't need to know vehicle type details
class Ticket {
    public double calculateTotalFee() {
        return vehicle.calculateFee(parkingDuration); // Polymorphic call
    }
}
```

---

## 📊 Requirements Traceability

### Core Functional Requirements

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| FR-001: User account creation | ✅ | Flow 1: User Registration |
| FR-004: User authentication | ✅ | Flow 2: Login |
| FR-008: Vehicle registration | ✅ | Flow 2: Vehicle Registration |
| FR-010: Display available slots | ✅ | Flow 3: Park Vehicle |
| FR-012: Generate unique ticket IDs | ✅ | Flow 3: Ticket Generation |
| FR-014: Calculate fees by vehicle type | ✅ | Flow 3 & 4: Polymorphic calculation |
| FR-016: Ticket lookup | ✅ | Flow 4: Exit & Payment |
| FR-018: Process payments | ✅ | Flow 4 & 6: Payment processing |
| FR-026: Admin add parking slots | ✅ | Flow 6: Slot Management |
| FR-028: Manual payment processing | ✅ | Flow 6: Admin Payment |
| FR-031: Generate usage reports | ✅ | Flow 7: Reporting |

### Non-Functional Requirements

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| NFR-001: Encrypt sensitive data | ✅ | Password hashing in registration flow |
| NFR-005: Strong password policies | ✅ | Validation in user creation |
| NFR-006: Role-based access control | ✅ | User/Client/Admin hierarchy |
| No database requirement | ✅ | File-based collections (activeTickets, history) |
| Java + TCP/IP architecture | ✅ | Client-server design in all flows |


---

## 🎓 Academic Integrity

This project is submitted as coursework for CS401 Software Engineering. All work is original and completed by team members listed above. External code snippets (if any) are properly attributed.

**Course Instructor:** Professor Smith  
**Institution:** California State University, East Bay 
**Semester:** Fall 2025

---

## 📝 License

This project is submitted for academic purposes only. All rights reserved by the team members.

---

---

## 📝 Note

Some dates might look wierd, due to inexperience with GitHub, code was committed on later dates, but was being done a week before the first commits.

---

**Last Updated:** October 20, 2025  
**Version:** 2.0 (Phase 2 Complete)

