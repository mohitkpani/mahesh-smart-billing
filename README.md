 <div align="center">
  <img src="assets/mds-logo.jpg" alt="Mahesh Digital Studio Logo" width="150">

  <h1>🧾 Mahesh Smart Billing System</h1>

  <p><b>Smart Invoicing for Mahesh Digital Studio</b></p>

  <img src="https://img.shields.io/badge/Frontend-React-61DAFB?logo=react" alt="React">
  <img src="https://img.shields.io/badge/Backend-Spring%20Boot-6DB33F?logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21">
</div>

---

## 🚀 Live Demo

- 🌐 **Frontend:** [Mahesh Smart Billing](https://mahesh-smart-billing.netlify.app)
- ⚙️ **Backend API:** [Render Backend](https://mahesh-smart-billing-api.onrender.com)

## ✨ Features

- 🧾 Generate and download PDF invoices
- 👤 Enter customer details and address
- 🛒 Add or remove multiple products and services
- 🧮 Automatically calculate item amounts and grand total
- 👀 Preview invoices before downloading
- 📧 Send invoices with PDF attachments by email
- 📱 Responsive interface for desktop and mobile
- 💜 Branded invoices for Mahesh Digital Studio

## 🛠️ Tech Stack

| Technology      | Purpose             |
| --------------- | ------------------- |
| React + Vite    | Frontend            |
| Java 21         | Backend language    |
| Spring Boot     | REST API            |
| OpenPDF         | PDF generation      |
| Sendliberty API | Email delivery      |
| Netlify         | Frontend deployment |
| Render          | Backend deployment  |

## 📁 Project Structure

```text
mahesh-smart-billing/
├── assets/
│   └── mds-logo.jpg
├── shopbill-frontend/
│   ├── src/
│   ├── public/
│   └── package.json
├── shopbill/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/shopbill/
│   │       └── resources/
│   └── pom.xml
└── README.md
```

## 💻 Run Locally

### Prerequisites

- Java 21
- Maven
- Node.js and npm

### 1. Clone the repository

```bash
git clone https://github.com/mohitkpani/mahesh-smart-billing.git
cd mahesh-smart-billing
```

### 2. Run the backend

Configure the `SENDLIBERTY_API_KEY` environment variable before starting the application.

```bash
cd shopbill
mvn spring-boot:run
```

Backend URL: `http://localhost:8080`

### 3. Run the frontend

Open a second terminal from the project root:

```bash
cd shopbill-frontend
npm install
npm run dev
```

Open the local URL displayed in the terminal, usually `http://localhost:5173`.

For local development, set the frontend API base URL to `http://localhost:8080/api/billing`. For production, use the deployed Render API URL.

## 🔐 Security

- Store API keys and credentials in environment variables.
- Never commit passwords, API keys, or other secrets to GitHub.

## 🚀 Future Improvements

- Invoice history and reporting
- Customer and product management
- Persistent invoice storage
- Invoice search and filtering

---

<div align="center">
  <p>Made with ❤️ for <b>Mahesh Digital Studio</b></p>
</div>
