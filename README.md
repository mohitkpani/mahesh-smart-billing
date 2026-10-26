# Mahesh Smart Billing System

A web-based billing application developed for **Mahesh Digital Studio** to create professional invoices, calculate product totals, generate PDF bills, and send invoices to customers through email.

## Live Demo

- **Frontend:** https://mahesh-smart-billing.netlify.app
- **Backend API:** https://mahesh-smart-billing-api.onrender.com

## Features

- Create customer invoices with customer name, mobile number, email, and address.
- Add multiple products or services.
- Enter product prices and quantities.
- Automatically calculate item amounts and the grand total.
- Preview invoices before downloading.
- Generate and download PDF invoices.
- Send PDF invoices to customers by email.
- Responsive React user interface.
- Branded invoice design with the Mahesh Digital Studio logo.

## Technologies Used

### Frontend

- React.js
- JavaScript
- HTML5
- CSS3
- Vite

### Backend

- Java 21
- Spring Boot
- Maven
- OpenPDF for PDF generation
- Sendliberty API for email delivery

## Project Structure

```text
mahesh-smart-billing/
├── shopbill-frontend/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── assets/
│   ├── public/
│   └── package.json
│
├── shopbill/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/shopbill/
│   │       └── resources/
│   │           └── static/images/
│   └── pom.xml
│
└── README.md
```

## Run Locally

### Prerequisites

- Java 21 or compatible JDK
- Node.js and npm
- Maven

### 1. Clone the repository

```bash
git clone https://github.com/mohitkpani/mahesh-smart-billing.git
cd mahesh-smart-billing
```

### 2. Start the backend

Configure the Sendliberty API key as an environment variable named `SENDLIBERTY_API_KEY`.

From the project root, run:

```bash
cd shopbill
mvn spring-boot:run
```

The backend runs at `http://localhost:8080` by default.

### 3. Start the frontend

Open a second terminal from the project root:

```bash
cd shopbill-frontend
npm install
npm run dev
```

Open the local URL shown in your terminal, usually `http://localhost:5173`.

For local development, configure the frontend API base URL to use `http://localhost:8080/api/billing`. For production, use the deployed Render API URL.

## Deployment

- **Frontend:** Deployed on Netlify.
- **Backend:** Deployed on Render.
- **Email delivery:** Integrated with Sendliberty.

Configure secrets such as `SENDLIBERTY_API_KEY` through environment variables in your local run configuration and hosting service. Never commit API keys, passwords, or other credentials to GitHub.

## Future Improvements

- Invoice history and reporting.
- Customer and product management.
- Persistent storage for invoices.
- Improved invoice search and filtering.

## Author

Developed for **Mahesh Digital Studio**.
